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
 *  or mail me at eric@jeti.tk
 */

// Created on 18-sept-2004
package nu.fw.jeti.plugins.filetransfer.socks5;

import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUser;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 */
public class Socks5Handler extends ExtensionHandler
{
	private String sid;
	private JID streamHost;
	private List streamHosts;
	private JID activate;
	private JID jid;
	private String host;
	private int port;
	private String zeroConf;
	
	public void startHandling(Attributes attr)
	{
		sid=null;
		streamHost=null;
		streamHosts=null;
		activate=null;
		host=zeroConf=null;
		port=0;
		jid=null;
		sid= attr.getValue("sid");
	}
	
	public void startElement(String name,Attributes attr)
	{
		if(name.equals("streamhost-used"))
		{
			try
			{
				streamHost=JID.checkedJIDFromString(attr.getValue("jid"));
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(name.equals("streamhost"))
		{
			try
			{
				jid=JID.checkedJIDFromString(attr.getValue("jid"));
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			host = attr.getValue("host");
			try{
				port = Integer.parseInt(attr.getValue("port"));
			}catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			zeroConf = attr.getValue("zeroconf");
		}
	}
	
	public void endElement(String name)
	{
		if(name.equals("activate"))
		{
			try
			{
				activate =JID.checkedJIDFromString(getText());
			} catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(name.equals("streamhost"))
		{
			StreamHost h = new StreamHost(jid,host,port,zeroConf);
			if(streamHosts==null)streamHosts= new LinkedList();
			streamHosts.add(h);
			host=zeroConf=null;
			port=0;
			jid=null;
		}
		clearCurrentChars();
	}

	public Extension build()
	{
		return new Socks5Extension(sid,streamHost,streamHosts,activate);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
