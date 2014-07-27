/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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
 *
 *	Created on 21-nov-2003
 */
 
package nu.fw.jeti.plugins.servertolog;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.IQXExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;

/**
 * @author E.S. de Boer
 *
 */
public class ServerLogExtension extends Extension implements IQXExtension
{
    private List logs;
	private String xmlVer;
	public final static String XML_VERSION ="v1";

	public ServerLogExtension(){}

	public ServerLogExtension(List logs)
	{
		this.logs =logs;
		xmlVer = XML_VERSION;
	}
	
		
//	public ServerLogExtension(String[] openGroups, String xmlVersion)
//	{
//		this.openGroups = openGroups;
//		xmlVer = xmlVersion;
//	}

    public List getLogs()
    {
	    return logs;
    }
    
    public boolean isCorrectVersion()
    {
	    return XML_VERSION.equals(xmlVer);
	}


	public String getXmlVersion()
    {
	    return xmlVer;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			boolean error = false;
			for (Iterator i = logs.iterator(); i.hasNext(); ) 
			{
				Log log = (Log) i.next();
				try {
					storeLog(log);
				}catch(RuntimeException e)
				{
					error = true;
				}
			}
			if(!error)backend.send(new InfoQuery("set",new IQPrivate(new ServerLogExtension())));
		}
		else if (iq.getType().equals("error"))
		{
			System.out.println(iq.getErrorDescription());
		}
	}
	
	public void storeLog(final Log log)
	{
		Thread worker = new Thread() 
		{
			public void run()
			{
				JID jid = log.getFrom();
				String name = null;
				JIDStatus jidStatus = Backend.getJIDStatus(jid);
				if (jidStatus != null)
				{
					if(jidStatus.getType().equals("unknown") || jidStatus.getType().equals("jabber"))name = jid.toStringNoResource();
					else name = jid.getUser() + "." + jidStatus.getType(); 
				}
				else name = jid.toStringNoResource();

				BufferedWriter writer = null;
				String file = Start.path + "newlogs" + File.separator + name + ".txt";
				try
				{
					writer = new BufferedWriter(new FileWriter(file, true));
				}
				catch (IOException e2)
				{
					nu.fw.jeti.util.Popups.errorPopup(file + " could not be opend in write mode", "Logfile Error");
					throw new RuntimeException();
					//return;
				}
				try
				{
					writer.newLine();
					writer.newLine();
					writer.write("----------------" + log.getStartTime() + "----------------");
					writer.newLine();
					writer.newLine();
					String text = log.getText();
					text = text.replaceAll("\n",System.getProperty("line.separator"));
					writer.write(text);
					writer.close();
				}
				catch (IOException e2)
				{
					nu.fw.jeti.util.Popups.errorPopup(file + " could not be written", "Logfile Error");
					throw new RuntimeException();
				}
			}
		};
		worker.start();
	}
	
		
	public void appendToXML(StringBuffer retval)
	{
		retval.append("<jeti xmlns='jeti:serverlog'");
		appendAttribute(retval,"xmlVersion",xmlVer);
		retval.append(">");
		if(logs != null)
		{
			for (Iterator i=logs.iterator();i.hasNext();)
			{
				Log log = (Log)i.next();
				retval.append("<log");
				appendAttribute(retval,"from",log.getFrom());
				appendAttribute(retval,"date",log.getStartTime());
				retval.append(">");
				appendElement(retval,"body",log.getText());
				retval.append("</log>");
			}
		}
		retval.append("</jeti>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
