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
 
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

/**
 * @author E.S. de Boer
 *
 */
public class ConvertLogs
{
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
	private static Map  map = new HashMap();
	private static String path;
	
	public static void main(String[] args)
	{
		getPath();
		indexDirectory(new File(path + "logs"));
		saveLogs();
		System.exit(0);
	}
	
	private static String getPath()
	{
		// Now, search for and get the URL for this class.
		URL url = ConvertLogs.class.getResource("ConvertLogs.class");
		//System.out.println(url);
		String urlString = null;

		try
		{ //remove %20 from program files etc
			urlString = URLDecoder.decode(url.toString(), "UTF8"); //encode if to url? probaly not
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} //1.2 error

		//System.out.println(url.getPath()  ); //werkt niet in 1.2
		//System.out.println(url);
		if (url.getProtocol().equals("jar"))
		{
			// Strip off JarURL-specific syntax.
			urlString = urlString.substring(4);
			urlString = urlString.substring(0, urlString.lastIndexOf("!"));
			urlString = urlString.substring(0, urlString.lastIndexOf("/") + 1);
		}
		else urlString = ".....e:\\data\\java\\jeti\\";//urlString.substring(0, urlString.lastIndexOf("/jeti") + 1);
		//System.out.println("df" + urlString);
		path = urlString.substring(5);
		return urlString;
		/*
		try
		{
			localURL = new URL(urlString);
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
		}
		if(System.getProperty("file.separator").equals("/"))//unix? if mac then bug
		{
			path = urlString.substring(5);
		}
		else path = urlString.substring(6);
		path = path.replace('/',File.separatorChar);
		System.out.println(path);
		System.out.println(urlString);
		System.out.println(localURL);
		*/
	}

	private static void indexDirectory(File dir) 
	{
		File[] files = dir.listFiles();
		if(files==null)System.exit(1);
		ProgressMonitor progress = new ProgressMonitor(null,"Indexing log files","", 0, files.length);
		for (int i=0; i < files.length; i++) 
	    {
			if(progress.isCanceled()) break;
			File f = files[i];
			String name = f.getName();
			//System.out.println(name);
			if (name.endsWith(".txt")) 
	        {
				int atLocation = name.indexOf("@");
				if (atLocation<0)
				{//no @ do nothing with it
					continue;
				}
				progress.setNote(name);
	        	progress.setProgress(i);
	        	if(name.startsWith("msn.",atLocation+1))
	        	{
	        		readLog(f,name.substring(0,atLocation),"msn");
	        		
	        	}
	        	else if(name.startsWith("icq.",atLocation+1))
	        	{
	        		readLog(f,name.substring(0,atLocation),"icq");
	        		
	        	}
	        	else if(name.startsWith("aim.",atLocation+1))
	        	{
	        		readLog(f,name.substring(0,atLocation),"aim");
	        		
	        	}
	        	//else readLog(f,name.substring(0,atLocation),name.substring(atLocation+1,name.length()-4));
	         }
	    }
		progress.close();
	}
	
	private static void readLog(File file, String adres, String type)
	{
		String name = adres +"@"+ type +".txt";
		
		BufferedReader log=null;
		try
		{
			log = new BufferedReader(new FileReader(file));
			parseLog(log,name);
		}
		catch (FileNotFoundException e2)
		{
			return;
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
		finally
		{
			if(log!=null)
				try
				{
					log.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
		}
		file.delete();
	}
	
	
	
	private static void parseLog(BufferedReader reader, String name) throws IOException
	{
		StringBuffer text=null;
		String line;
		Date date=null;
		List adresList =(List) map.get(name);
		if(adresList==null)
		{
			adresList = new ArrayList();
			map.put(name,adresList);
		}
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
						adresList.add(new DateText(date, text.toString()));
					}
					text = new StringBuffer();
					date = newdate;
				} catch (ParseException e2)
				{
					text.append(line + System.getProperty("line.separator"));
				}
				//System.out.println(date);
			}
			else text.append(line + System.getProperty("line.separator"));
		}
		adresList.add(new DateText(date, text.toString()));
	}
	
	private static void saveLogs()
	{
		for(Iterator i=map.entrySet().iterator();i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			String filename = (String) entry.getKey();
			List texts = (List) entry.getValue();
			Collections.sort(texts);
			
			String file = path + "logs" + File.separator + filename;
			PrintWriter log=null;
			try
			{
				log = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file, true)), true);
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
				log.close();
				continue;
			}
			for(Iterator j = texts.iterator();j.hasNext();)
			{
				((DateText)j.next()).save(log);
			}
			log.close();
		}
	}
}

class DateText implements Comparable
{
	private Date date;
	private String text;
	
	public DateText(Date date, String text)
	{
		this.date = date;
		this.text = text;
	}
	
	public int compareTo(Object o)
	{
		return date.compareTo(((DateText)o).date);
	}
	
	public void save(PrintWriter log)
	{
		log.println();
		log.println("----------------" + date.toString() + "----------------");
		log.println();
		log.println(text);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
