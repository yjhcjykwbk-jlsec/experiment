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
 
package nu.fw.jeti.plugins.messagelog;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.SwingUtilities;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.jabber.JIDStatus;

/**
 * @author E.S. de Boer
 *
 */
public class IndexOldLogs
{
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
	private static Map  map;
		
		
	public static void index(final JIDStatus jidStatus,final MessageLogWindow window) 
	{
		map = new HashMap();
		Thread thread = new Thread() 
		{
			
			public void run()
			{
				if(jidStatus instanceof PrimaryJIDStatus)
				{
					PrimaryJIDStatus primaryJIDStatus = (PrimaryJIDStatus)jidStatus;
					parseJIDStatus(primaryJIDStatus.getJIDPrimaryStatus());
					if(primaryJIDStatus.hasMultiple())
					{
						for (Iterator i = primaryJIDStatus.getOtherJidStatussen(); i.hasNext(); ) 
						{
							JIDStatus j = (JIDStatus) i.next();
							parseJIDStatus(j);
						}
					}
				}
				else parseJIDStatus(jidStatus);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						window.addData(map);
					}
				});
			}
		};
		thread.start();
		//thread.run();
	}
	
	private static void parseJIDStatus(JIDStatus jidStatus)
	{
		String name =null;
		if(jidStatus.getType().equals("unknown") || jidStatus.getType().equals("jabber"))name = jidStatus.getJID().toStringNoResource();
		else name = jidStatus.getJID().getUser() + "." + jidStatus.getType(); 
		BufferedReader log=null;
		try
		{
			log = new BufferedReader(new FileReader(Start.path + "logs" + File.separator + name + ".txt"));
			//log = new BufferedReader(new FileReader("e:\\data\\Java\\JETI\\" + "logs" + File.separator + name + ".txt"));
			parseLog(log);
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
	}
		
	private static void parseLog(BufferedReader reader) throws IOException
	{
		StringBuffer text=null;
		String line;
		Date date=null;
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
					//System.out.println(newdate + " " + temp);
					
					if(text!=null)
					{
						map.put(date, text.toString());
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
		map.put(date, text.toString());//add last message
	}
		
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
