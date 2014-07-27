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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ProgressMonitor;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.util.I18N;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

/**
 * @author E.S. de Boer
 *
 */
public class IndexOldLogs
{
	private String from;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
	private Date date;
	private StringBuffer text;
	private IndexWriter indexWriter;
		
	public IndexOldLogs()
	{
		//File indexDir =new File(Start.path + "logs" + File.separator);
		//File indexDir =new File("e:\\data\\java\\jeti\\index\\");
		File indexDir =new File(Start.path + "index");
		if (!indexDir.exists())
		{
			indexDir.mkdir();		
		}
		try
		{
			indexWriter = new IndexWriter(FSDirectory.getDirectory(indexDir,false), new StandardAnalyzer(), true);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void index() 
	{
		Thread thread = new Thread() 
		{
			
			public void run()
			{
				try
				{
					//indexDirectory(new File("e:\\data\\java\\jeti\\logs\\"));
					indexDirectory(new File(Start.path + "logs"));
					
					indexWriter.optimize();
					indexWriter.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	private void indexDirectory(File dir) throws IOException
	{
		File[] files = dir.listFiles();
		ProgressMonitor progress = new ProgressMonitor(null,I18N.gettext("Indexing log files"),"", 0, files.length);
		for (int i=0; i < files.length; i++) 
	    {
			if(progress.isCanceled()) break;
			File f = files[i];
	        if (f.getName().endsWith(".txt")) 
	        {
	        	progress.setNote(f.getName());
	        	progress.setProgress(i);
	        	parseLog(f);
	        }
	    }
		progress.close();
	}
	
	public void parseLog(final File file) throws IOException
	{
		from = file.getName();
		System.out.println(from);
		from = from.substring(0,from.length()-4);
		BufferedReader reader=null;
		reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) !=null)
		{
			if(line.equals("")) continue;
			else if(line.startsWith("----------------") && line.length() > 50  && line.endsWith("----------------"))
			{
				String temp = line.substring(16);
				temp = temp.substring(0,temp.length()- 16);
				try
				{
					Date newdate = dateFormat.parse(temp);
					//System.out.println(date);
					if(text!=null)
					{
						indexFile();
					}
					text = new StringBuffer();
					date = newdate;
				} catch (ParseException e2)
				{
					text.append(line +'\n');
				}
				//System.out.println(date);
			}
			else text.append(line +'\n');
		}
		indexFile(); //index last message
		text=null;
	}
	
	private void indexFile() throws IOException
	{
	    Document doc = new Document();
	    doc.add(Field.Keyword("from",from));
	    doc.add(Field.Keyword("date",DateField.dateToString(date)));
	    doc.add(Field.Text("contents",text.toString()));
	   // System.out.println(text.toString());
	    //doc.add(Field.Keyword("filename", f.getCanonicalPath()));
		indexWriter.addDocument(doc);
	}
	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
