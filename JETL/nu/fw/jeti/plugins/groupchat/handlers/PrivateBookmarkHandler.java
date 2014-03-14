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
 *	Created on 15-nov-2003
 */
 
package nu.fw.jeti.plugins.groupchat.handlers;

import java.util.ArrayList;
import java.util.List;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.groupchat.Bookmarks;
import nu.fw.jeti.plugins.groupchat.elements.Conference;
import nu.fw.jeti.plugins.groupchat.elements.PrivateBookmarkExtension;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;


public class PrivateBookmarkHandler extends ExtensionHandler
{
	private List urls;
	private List conferences;
	private String name;
	private JID jid;
	private String nick;
	private String password;
	private boolean autoJoin;
	private Bookmarks bookmarks;

	public PrivateBookmarkHandler(Bookmarks bookmarks)
	{
		this.bookmarks = bookmarks;
	}
	
	public void startHandling(Attributes attributes)
	{
		jid=null;
		name=null;
		autoJoin=false;
		nick=null;
		password=null;
		urls = new ArrayList(10);
		conferences = new ArrayList(10);
	}

	public void startElement(String name,Attributes attributes)
	{
		if(name.equals("conference"))
		{
			this.name = attributes.getValue("name");
			try
			{
				jid = JID.checkedJIDFromString(attributes.getValue("jid"));
			} catch (InstantiationException e)
			{
				Log.notParsedXML("bookmark, invalid JID");
			}
			String aj = attributes.getValue("autojoin");
			if (aj!=null && aj.equals("1")) autoJoin = true;
			else autoJoin =false;
		}
		else if (name.equals("url"))
		{
			urls.add(new String[]{attributes.getValue("name"),attributes.getValue("url")});
		}
		//else Log.notParsedXML(" bookmarks " + name);
	}
	
	public void endElement(String name)
	{
		if(name.equals("conference"))
		{
			if(jid!=null)conferences.add(new Conference(this.name,jid,autoJoin,nick,password));
			jid=null;
			name=null;
			autoJoin=false;
			nick=null;
			password=null;
		}
		else if(name.equals("nick"))
		{
			nick = getText();
		}
		else if(name.equals("password"))
		{
			password = getText();
		}
		clearCurrentChars();
	}


	public Extension build()
	{
		jid=null;
		name=null;
		autoJoin=false;
		nick=null;
		password=null;
		return new PrivateBookmarkExtension(urls,conferences,bookmarks);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
