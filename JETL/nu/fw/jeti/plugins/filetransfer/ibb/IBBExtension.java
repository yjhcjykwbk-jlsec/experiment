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
package nu.fw.jeti.plugins.filetransfer.ibb;

import java.util.HashMap;
import java.util.Map;

import nu.fw.jeti.backend.XExecutableExtension;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Packet;
import nu.fw.jeti.jabber.elements.XMPPError;
import nu.fw.jeti.jabber.elements.XMPPErrorTag;
import nu.fw.jeti.plugins.filetransfer.GetFileWindow;
import nu.fw.jeti.plugins.filetransfer.Plugin;
import nu.fw.jeti.util.Log;

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
	private String base64data;
	private static Map fileWindows = new HashMap(10);

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
		System.out.println(getSid());
		GetFileWindow w= (GetFileWindow)fileWindows.get(sid);
		if(w==null)
		{
			Log.xmlReceivedError("ibb stream from unknown");
		}
		else
		{
			IBBReceive r= (IBBReceive)w.getStreamReceive();
			r.addData(getData());
		}
	}

	public void execute(InfoQuery iq, Backend backend)
	{
		if(iq.getType().equals("set"))
		{
			//Plugin.ibb(iq.getFrom(),this);
						
			if(isOpen())
			{
				GetFileWindow w= Plugin.getGetFile(iq.getFrom());
				if(w!=null)
				{//file accepted by filetransfer protocol;
					System.out.println(getSid());
					w.startDownloading(new IBBReceive(iq.getFrom(),getSid(),backend,w));
					fileWindows.put(getSid(),w);
					backend.send(new InfoQuery(iq.getFrom(),"result",iq.getID(),null));
					Log.xmlReceivedError("ibb stream init from unknown");
				}
				else
				{
					XMPPError e = new XMPPError("cancel",501);
					e.addError(new XMPPErrorTag("feature-not-implemented"));
					backend.send(new InfoQuery(iq.getFrom(),iq.getID(),e));
				}
			}
			if(isClose())
			{
				GetFileWindow w=  (GetFileWindow) fileWindows.remove(sid);
				if(w!=null)((IBBReceive)w.getStreamReceive()).stopDownloading();
				System.out.println(" close  " + sid);
				backend.send(new InfoQuery(iq.getFrom(),"result",iq.getID(),null));
			}
		}
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
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
