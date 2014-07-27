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
 *	Created on 28-okt-2004
 */
 
package nu.fw.jeti.plugins.filetransfer.socks5;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;

/**
 * @author E.S. de Boer
 *
 */
public class StreamHost extends Extension
{
	private JID jid;
	private String host;
	private int port;
	private String zeroConf;
	
	public StreamHost(JID jid, String host, int port, String zeroConf)
	{
		this.jid = jid;
		this.host = host;
		this.port = port;
		this.zeroConf = zeroConf;
	}
	
	public String getHost()
	{
		return host;
	}
	public JID getJID()
	{
		return jid;
	}
	public int getPort()
	{
		return port;
	}
	public String getZeroConf()
	{
		return zeroConf;
	}
	public void appendToXML(StringBuffer xml)
	{
		xml.append("<streamhost");
		appendAttribute(xml,"jid",jid);
		appendAttribute(xml,"host",host);
		appendAttribute(xml,"port",String.valueOf(port));
		appendAttribute(xml,"zeroconf",zeroConf);
		xml.append("/>");
	}
	
}
