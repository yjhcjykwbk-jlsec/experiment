/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2001 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk
 */

package nu.fw.jeti.util;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.events.JavaErrorListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Packet;

/**
 * @author E.S. de Boer
 */

public class Log
{
	private static Date date = new Date();
	private static DateFormat dateFormat = DateFormat.getTimeInstance();
	private static LinkedList xml = new LinkedList();
	private static LinkedList xmlErrors = new LinkedList();
	private static volatile LinkedList errors = new LinkedList();
	private static Backend backend;
	private static PrintStream diskLog = null;
	
	public Log(Backend backend) 
	{
		Log.backend= backend; 
		//pipe err stream to log screen

		catchExceptions();
		 if (Preferences.getBoolean("jeti","debugToFile",false)) {
            String name = Preferences.getString("jeti","debugFile", "out.log");
            try {
                diskLog = new PrintStream(new FileOutputStream(name, true));
            } catch (IOException e) {
                System.err.println("Failed to open debug log '" + name + "': " + e);
            }
        }
	}

	private void catchExceptions()
	{
		try
		{
			Thread t = new Thread()
			{
				PipedInputStream piNormal = new PipedInputStream();
				PipedOutputStream poNormal = new PipedOutputStream(piNormal);
				public void run()
				{
                    try {
                    	System.setErr(new PrintStream(poNormal, true));
                    } catch (Exception e) {System.out.println("Error logging setup failed");}
					try
					{
						BufferedReader bin = new BufferedReader(new InputStreamReader(piNormal));
						String output = null;
						while ((output = bin.readLine()) != null)
						{
							System.out.println(output);
							synchronized(errors)
							{
								errors.add(gettime() + " " + output);
							}
//							//do only once? ipv every line 
							try
							{//try catch because exceptions thrown here cause deadlock
								for(Iterator j = backend.getListeners(JavaErrorListener.class);j.hasNext();)
								{//show error in ui
									((JavaErrorListener)j.next()).error(); 
								}
							}
							catch(Exception e)
							{
								System.out.println(e.getMessage());	
							}
						}
						bin.close();
						piNormal.close();
					}
					catch (IOException ioe)
					{
						//write end dead then set system err again
						try
						{
							piNormal.close();
							poNormal.close();
						}
						catch (IOException e)
						{
							System.out.println("foutje");
						}
						catchExceptions();   
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}catch (IOException i){System.out.println("foutje");}
	}
	
	private static String gettime()
	{
		date.setTime(System.currentTimeMillis());
		return dateFormat.format(date);
	}

    private static void loggedAdd(LinkedList list, String line) {
        list.add(line);
        if (diskLog != null) {
            diskLog.println(line);
        }
    }
    
    public static void clear()
	{
    	synchronized(xml)
		{
    		xml.clear();
		}
	}

	//***********xml log*******************\\
	public static void xmlPacket(Packet packet)
	{
		synchronized(xml)
		{
			loggedAdd(xml, gettime() + " - p: " + packet);
		}
	}

	public static void completeXML(StringBuffer message)
	{
		synchronized(xml)
		{
			loggedAdd(xml, gettime() + " - c: " + message);
		}
	}

	public static void sendXML(String message)
	{
		synchronized(xml)
		{
			loggedAdd(xml, gettime() + " +  : " + message);
		}
	}

	public static List getXML()
	{
		synchronized(xml)
		{
			return (List) xml.clone();
		}
	}

	//*********xml errors***********\\
	public static void notParsedXML(String message)
	{
		synchronized(xmlErrors)
		{
			xmlErrors.add(gettime() + " not parsed: " + message);
		}
	}

	public static void xmlParseException(Exception e)
	{
		synchronized(xmlErrors)
		{
			xmlErrors.add(gettime() + " " + e.getMessage());
		}
	}

	public static void xmlReceivedError(String message)
	{
		synchronized(xmlErrors)
		{
			xmlErrors.add(gettime() + " " + message);
		}
	}

	public static List getXMLErrors()
	{
		synchronized(xmlErrors)
		{
			return (List) xmlErrors.clone();
		}
	}

	//***********errors***************\\
	public static void notSend(String message)
	{
		synchronized(errors)
		{
			errors.add(gettime() + " not Send " + message);
		}
	}

	public static void error(String message)
	{
		synchronized(errors)
		{
			errors.add(gettime() + " error " + message);
		}
	}

	public static List getErrors()
	{
		synchronized(errors)
		{
			return (List) errors.clone();
		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
