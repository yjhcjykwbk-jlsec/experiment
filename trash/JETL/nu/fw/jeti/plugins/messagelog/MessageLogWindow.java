package nu.fw.jeti.plugins.messagelog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.util.I18N;

/**
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * 
 * @author E.S. de Boer
 */

public class MessageLogWindow extends JFrame
{
	private JSplitPane splitPane = new JSplitPane();
	private JList list;
	private StringBuffer text = new StringBuffer();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JScrollPane jScrollPane2 = new JScrollPane();
	private JTextArea jTextArea1;
	private JTextField txtSearch = new JTextField();
	private Map logs;
	private Date[] dates;
	private int logNumber=0;
	private int logPosition=0;

	public MessageLogWindow(JIDStatus jidStatus)
	{
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		splitPane.setBorder(null);
		if (jidStatus == null) return;

		jTextArea1 = new JTextArea(text.toString());
		jTextArea1.setEditable(false);
		splitPane.add(jScrollPane1,JSplitPane.RIGHT);
		splitPane.add(jScrollPane2,JSplitPane.LEFT);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT,2,0));
		panel2.setBorder(null);
		//JButton button = new JButton("Search");
		//button.setMnemonic('S');
		JButton button = new JButton();
		I18N.setTextAndMnemonic("messagelog.Search",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				search(0,0);
			}
		});
		panel2.add(button);
		//button = new JButton("Search Next");
		//button.setMnemonic('N');
		button = new JButton();
		I18N.setTextAndMnemonic("messagelog.Search_Next",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				search(logNumber,logPosition);
			}
		});
		panel2.add(button);
		button = new JButton();
		
		Action cancelAction = new AbstractAction(I18N.gettext("Close"))
		{
			public void actionPerformed(ActionEvent e)
			{
				MessageLogWindow.this.dispose();
			}
		};
		button.setAction(cancelAction);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
			
		
		panel2.add(button);
		panel.add(txtSearch,BorderLayout.CENTER);
		panel.add(panel2,BorderLayout.EAST);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		
		
		jScrollPane1.getViewport().add(jTextArea1, null);
//		pack();
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		if (getHeight() > screenSize.getHeight() || getWidth() > screenSize.getWidth())
//		{
//			setSize((int) screenSize.getWidth() - 50, (int) screenSize.getHeight() - 50);
//		}
		setSize(600, 600);
		splitPane.setDividerLocation(150);
		show();
		IndexOldLogs.index(jidStatus, this);
	}

	public void addData(final Map map)
	{
		logs = map;
		dates =(Date[]) map.keySet().toArray(new Date[map.size()]);
		Arrays.sort(dates,new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((Comparable)o2).compareTo(o1);
			}
		});
		list = new JList(dates);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(list.getSelectedValue()!=null)
				jTextArea1.setText((String)map.get(list.getSelectedValue()));
				jTextArea1.setCaretPosition(0);

			}
		});
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			DateFormat dateFormat = DateFormat.getDateTimeInstance();
			
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel t =(JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
								
				t.setText(dateFormat.format(value));
				return t;
			}
		});
		
		jScrollPane2.getViewport().add(list);
	}
	
	private void search(int logNum , int logPos)
	{//use thread??
		String toSearch = txtSearch.getText();
		if (toSearch.equals("")) return;
		for(int i = logNum;i<dates.length;i++)
		{
			Object date = dates[i];
			String log = (String) logs.get(date);
			int index = log.indexOf(toSearch,logPos);
			if(index!=-1)
			{
				list.setSelectedIndex(i);
				//jTextArea1.setText(log);
				jTextArea1.requestFocus();
				jTextArea1.setCaretPosition(index);
				int searchEnd = index+toSearch.length();
				jTextArea1.moveCaretPosition(searchEnd);
				logNumber = i;
				logPosition = searchEnd;
				return;
			}
		}
		logPosition=0;
		logNumber = 0;
		if(JOptionPane.showConfirmDialog(this,MessageFormat.format(I18N.gettext("main.popup.{0}_not_found_until_the_end_of_this_file,_start_again_from_the_beginning?"),new Object[]{toSearch})) == JOptionPane.OK_OPTION)
		{
			search(0,0);
		}
		
	}

		
//	public static void main(String[] d)
//	{
//		new MessageLogWindow(new NormalJIDStatus(new JID("tjverweij","charente.de"),"Tim"));
//	}
//	
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
