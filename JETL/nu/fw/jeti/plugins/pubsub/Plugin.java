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
 *	Created on 9-aug-2003
 */

package nu.fw.jeti.plugins.pubsub;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.groupchat.elements.XConference;
import nu.fw.jeti.plugins.groupchat.handlers.XConferenceHandler;
import nu.fw.jeti.plugins.groupchat.handlers.XMUCOwnerHandler;
import nu.fw.jeti.plugins.groupchat.handlers.XMUCUserHandler;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "pubsub.publish_and_subscribe";
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String NAME = "pubsub";
	public final static String ABOUT = "by E.S. de Boer";
	private JMenuItem menuItem;//save menu to remove it
	private static Plugin plugin;
	
	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}

	public Plugin(final Backend backend)
	{
		//groupchat menu item
		menuItem = new JMenuItem(I18N.gettext("pubsub.Pubsub_(beta)"));
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new PubSubBrowse(backend).show();
			}
		});
		backend.getMain().addToMenu(menuItem);
		
		//backend.addExtensionHandler("http://jabber.org/protocol/muc#user",new XMUCUserHandler());
		//backend.addExtensionHandler("http://jabber.org/protocol/muc#owner",new XMUCOwnerHandler());
		//backend.addExtensionHandler("jabber:x:conference",new XConferenceHandler());
	}
	
	public void unload() {}
	
	public static void unload(Backend backend)
	{
		//backend.removeExtensionHandler("http://jabber.org/protocol/muc#user");
		//backend.removeExtensionHandler("http://jabber.org/protocol/muc#owner");
		//backend.removeExtensionHandler("jabber:x:conference");
		backend.getMain().removeFromMenu(plugin.menuItem);
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
