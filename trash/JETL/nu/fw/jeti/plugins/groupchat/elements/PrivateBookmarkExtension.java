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
 *	Created on 3-sept-2004
 */
 
package nu.fw.jeti.plugins.groupchat.elements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQXExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.groupchat.Bookmarks;
import nu.fw.jeti.plugins.groupchat.GroupchatSignin;
import nu.fw.jeti.plugins.groupchat.GroupchatWindow;
import nu.fw.jeti.plugins.groupchat.Plugin;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class PrivateBookmarkExtension extends Extension implements IQXExtension
{
    private List conferences;
	private List urls;
	private Bookmarks bookmarks;
	

	public PrivateBookmarkExtension(){}

	public PrivateBookmarkExtension(List urls, List conferences)
	{
		this.urls = urls;
		this.conferences = conferences;
	}
	
	public PrivateBookmarkExtension(List urls, List conferences,Bookmarks bookmarks)
	{
		this.urls = urls;
		this.conferences = conferences;
		this.bookmarks = bookmarks;
	}
		
    public List getConferences()
    {
	    return conferences;
    }
    
	public List getURLs()
    {
	    return urls;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			bookmarks.newBookmarks(this);
		}
		else if (iq.getType().equals("error"))
		{
			System.err.println(iq.getErrorDescription());
		}
	}
	
	public void appendToXML(StringBuffer retval)
	{
		retval.append("<storage xmlns='storage:bookmarks'");
		if (conferences == null && urls==null)
		{ //short cut
			retval.append("/>");
			return;
		}
		retval.append(">");
		if(urls != null)
		{
			for (Iterator i= urls.iterator();i.hasNext();)
			{
				retval.append("<url");
				String[] temp =(String[])i.next();
				appendAttribute(retval,"name",temp[0]);
				appendAttribute(retval,"url",temp[1]);
				retval.append("/>");
			}
		}
		if(conferences != null)
		{
			for (Iterator i= conferences.iterator();i.hasNext();)
			{
				((Conference)i.next()).appendToXML(retval);
			}
		}
		retval.append("</storage>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
