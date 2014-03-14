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

// Created on 28-okt-2004
package nu.fw.jeti.plugins.filetransfer.socks5;

import java.util.Iterator;
import java.util.List;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.filetransfer.GetFileWindow;
import nu.fw.jeti.plugins.filetransfer.Plugin;
import nu.fw.jeti.util.Log;
import nu.fw.jeti.util.Popups;

/**
 * @author E.S. de Boer
 *
 */
public class Socks5Extension extends Extension implements IQExtension
{
	private String sid;
	private JID streamHost;
	private List streamHosts;
	private JID activate;

	public Socks5Extension(String sid, JID streamHost, List streamHosts, JID activate)
	{
		this.sid = sid;
		this.streamHost = streamHost;
		this.streamHosts = streamHosts;
		this.activate = activate;
	}
	public Socks5Extension(String sid,List streamhosts)
	{
		this.sid=sid;
		this.streamHosts = streamhosts;
	}
	
	public Socks5Extension(String sid,JID activation)
	{
		this.sid=sid;
		this.activate = activation;
	}
	
	public Socks5Extension(JID streamHost,String sid)
	{
		this.sid=sid;
		this.streamHost = streamHost;
	}
	
	public String getSid()
	{
		return sid;
	}
	
	public JID getStreamHostUsed()
	{
		return streamHost;
	}
	
	public boolean hasStreamHosts()
	{
		return streamHosts!=null;
	}
	
	public Iterator getStreamHosts()
	{
		return streamHosts.iterator();
	}

	public void execute(InfoQuery iq, Backend backend)
	{
		if(iq.getType().equals("set"))
		{
			GetFileWindow w = Plugin.getGetFile(iq.getFrom());
			if(w==null) Log.xmlReceivedError("Socks stream from unknown");
			else 
			{
				w.startDownloading(new ReceiveSocks5(backend,iq,w));
			}
		}
	}
	
	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns= 'http://jabber.org/protocol/bytestreams'");
		appendAttribute(xml,"sid",sid);
		xml.append(">");
		appendElement(xml,"activate",activate);
		if(streamHost!=null)
		{
			xml.append("<streamhost-used");
			appendAttribute(xml,"jid",streamHost);
			xml.append("/>");
		}
		if(streamHosts!=null)
		{
			for(Iterator i=streamHosts.iterator();i.hasNext();)
			{
				((StreamHost)i.next()).appendToXML(xml);
			}
		}
		
		xml.append("</query>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
