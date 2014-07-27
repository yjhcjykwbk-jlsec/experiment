/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 2-mei-2004
 */
 
package nu.fw.jeti.plugins.searchlogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.SpringUtilities;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;


/**
 * @author E.S. de Boer
 *
 */
public class SearchWindow extends JFrame
{
	private IndexSearcher searcher;
	private JList results = new JList(new DefaultListModel());
	
	public SearchWindow()
	{
		setTitle(I18N.gettext("Log search"));	
		//File indexDir =new File("e:\\data\\java\\jeti\\index\\");
		File indexDir =new File(Start.path + "index");
		if (!indexDir.exists())
		{
			new IndexOldLogs().index();	
		}
		try
		{
			searcher = new IndexSearcher(FSDirectory.getDirectory(indexDir, false));
		} catch (IOException e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		panel.add(new JLabel(I18N.gettext("From contact:")));
		final JTextField txtContact = new JTextField();
		panel.add(txtContact);
		panel.add(new JLabel(I18N.gettext("Chat contains:")));
		final JTextField txtContains = new JTextField();
		panel.add(txtContains);
		
		panel.add(new JLabel(I18N.gettext("Start on:")));
		panel.add(new JLabel(I18N.gettext("End on:")));
		Calendar calendar = new GregorianCalendar();
		Date latestDate = calendar.getTime();
		calendar.add(Calendar.MONTH,-6);
		Date earliestDate = calendar.getTime();
		final SpinnerDateModel startDate = new SpinnerDateModel(earliestDate,null,latestDate,Calendar.MONTH);
	    SimpleDateFormat simpleDateFormat;
	    simpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
	    JSpinner spinner = new JSpinner(startDate);
	    spinner.setEditor(new JSpinner.DateEditor(spinner, simpleDateFormat.toPattern()));
	    spinner.setMaximumSize(new Dimension(20,10));
		panel.add(spinner);
		final SpinnerDateModel endDate = new SpinnerDateModel(latestDate,null,null,Calendar.MONTH);
		spinner = new JSpinner(endDate);
		spinner.setEditor(new JSpinner.DateEditor(spinner, simpleDateFormat.toPattern()));
		panel.add(spinner);
		
		JButton button = new JButton("Index");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new IndexOldLogs().index();
			}	
		});
		panel.add(button);
		button = new JButton(I18N.gettext("Search"));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				try {
					search(txtContact.getText(),txtContains.getText(),startDate.getDate(),endDate.getDate());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}	
		});
		getRootPane().setDefaultButton(button);
		panel.add(button);
		SpringUtilities.makeCompactGrid(panel,5,2,6,6,1,1);	
		getContentPane().add(panel,BorderLayout.NORTH);
		JScrollPane listScroller = new JScrollPane(results);
		listScroller.setPreferredSize(new Dimension(300, 400));
		getContentPane().add(listScroller,BorderLayout.CENTER);
		results.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() == false && results.getSelectedIndex() != -1)
				{
					JFrame window = new JFrame();
					JScrollPane scrollpane = new JScrollPane();
					window.getContentPane().add(scrollpane, BorderLayout.CENTER);
					scrollpane.getViewport().add(new JTextArea(results.getSelectedValue().toString()));
					window.setSize(500, 500);
					window.show();
				}
			}
		});
		
		pack();
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try {
					searcher.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SearchWindow.this.dispose();  
			}
		});
	}
	
	private void search(String contacts, String contents,Date startDate,Date endDate) throws IOException
	{
	    Query query=null;
		try
		{
			Query query1=null,query2 = null;
			if(!contents.equals("")) query1 = QueryParser.parse(contents, "contents", new StandardAnalyzer());
			if(!contacts.equals("")) query2 = QueryParser.parse(contacts, "from", new StandardAnalyzer());
			if(query1!=null)
			{
				if(query2==null) query=query1;
				else
				{
					BooleanQuery tempQuery = new BooleanQuery();
					tempQuery.add(query1,true,false);
					tempQuery.add(query2,true,false);
					query = tempQuery;
				}
			}
			else if (query2==null)return;
			else query = query2; 
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Filter dateFilter = new DateFilter("date",startDate,endDate);
		Hits hits = searcher.search(query,dateFilter);
	    System.out.println("Found " + hits.length() + " document(s) that matched query '" + query + "':");
	    DefaultListModel model =(DefaultListModel) results.getModel();
		model.clear();
		 for (int i = 0; i < hits.length(); i++) {
	        Document doc = hits.doc(i);
			model.addElement(doc.get("from") + " | " + doc.get("contents"));
	        //System.out.println(doc.get("contents"));
	    }
	}

	
	
	public static void main(String[] args) 
	{
		Start.path = "e:\\data\\java\\jeti\\";
		new SearchWindow().show();
	}
	

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
