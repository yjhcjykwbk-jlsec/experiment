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
package nu.fw.jeti.plugins.filetransfer.ibb;

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
public class IBBHandler extends ExtensionHandler
{
	private boolean close;
	private boolean open;
	private String sid;
	private int sequence;
	private int blockSize;
	private String data;
	
	public void startHandling(Attributes attr)
	{
		close=open=false;
		open=true;
		sid=null;
		data=null;
		blockSize=4096;
		sid= attr.getValue("sid");
		try{
			blockSize = Integer.parseInt(attr.getValue("block-size"));
			open=true;
		}
		catch(NumberFormatException e)
		{
			open=false;
		}
		try{
			sequence =  Integer.parseInt(attr.getValue("seq"));
			close = false;
		}
		catch(NumberFormatException e)
		{
			if(!open)close=true;
		}
	}
	
//	public void endElement(String name)
//	{
//		System.out.println(name);
//		data = getText();
//		clearCurrentChars();
//	}

	public Extension build()
	{
		data = getText();
		clearCurrentChars();
		return new IBBExtension(open,close,sid,blockSize,sequence,data);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
