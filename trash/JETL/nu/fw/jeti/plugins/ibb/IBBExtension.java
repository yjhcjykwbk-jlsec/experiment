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

// Created on 8-sept-2004
package nu.fw.jeti.plugins.ibb;

import nu.fw.jeti.backend.XExecutableExtension;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 */
public class IBBExtension extends Extension implements IQExtension, XExecutableExtension
{
	private boolean close;
	private boolean open;
	private String sid;
	private int sequence;
	private int blockSize=4096;
	//private byte[] data;
	String base64data;

	public IBBExtension(String sid,int blockSize)
	{
		open=true;
		this.blockSize = blockSize;
		this.sid=sid;
	}
	
	public IBBExtension(String sid)
	{
		close=true;
		this.sid=sid;
	}
	
	
	public IBBExtension(String sid,int sequence, String data)
	{
		this.sid = sid;
		this.sequence = sequence;
		base64data = data;
	}
	
	public IBBExtension(boolean open,boolean close, String sid, int blockSize,int sequence, String data)
	{
		this.open=open;
		this.close = close;
		this.sid = sid;
		this.blockSize=blockSize;
		this.sequence = sequence;
		base64data = data;
	}
	
	public String getData()
	{
		return base64data;
	}
	
	public String getSid()
	{
		return sid;
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public boolean isClose()
	{
		return close;
	}
	
	
	public void execute(Packet iq, Backend backend)
	{
		Plugin.ibb(iq.getFrom(),this);
	}

	public void execute(InfoQuery iq, Backend backend)
	{
		Plugin.ibb(iq.getFrom(),this);
	}
	
	public void appendToXML(StringBuffer xml)
	{
		if(open)
		{
			xml.append("<open xmlns= 'http://jabber.org/protocol/ibb'");
			appendAttribute(xml,"sid",sid);
			appendAttribute(xml,"block-size",String.valueOf(blockSize));
			xml.append("/>");
		}
		else if (close)
		{
			xml.append("<close xmlns= 'http://jabber.org/protocol/ibb'");
			appendAttribute(xml,"sid",sid);
			xml.append("/>");
		}
		else
		{
			xml.append("<data xmlns= 'http://jabber.org/protocol/ibb'");
			appendAttribute(xml,"sid",sid);
			appendAttribute(xml,"seq",String.valueOf(sequence));
			xml.append(">");
			//addData(data,xml);
			xml.append(base64data);
			xml.append("</data>");
			//TODO change to AMP protocol instead of hardcoding
			xml.append("<amp xmlns='http://jabber.org/protocol/amp'>");
			xml.append("<rule condition='deliver-at' value='stored' action='error'/>");
			xml.append("<rule condition='match-resource' value='exact' action='error'/>");
			xml.append("</amp>");
		}
	}
	
//	  private void addData(byte[] arr, StringBuffer xml)
//	  {
//	        for (int i = 0; i < arr.length; ++i) xml.append((char) arr[i]);
//	  }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
