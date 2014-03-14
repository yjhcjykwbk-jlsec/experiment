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
 *	Created on 13-okt-2003
 */

package nu.fw.jeti.plugins.groupchat;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.groupchat.elements.XConference;
import nu.fw.jeti.plugins.groupchat.handlers.XConferenceHandler;
import nu.fw.jeti.plugins.groupchat.handlers.XMUCAdminHandler;
import nu.fw.jeti.plugins.groupchat.handlers.XMUCOwnerHandler;
import nu.fw.jeti.plugins.groupchat.handlers.XMUCUserHandler;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, MessageListener
{
	public final static String VERSION = "0.2";
	public final static String DESCRIPTION = "groupchat.groupchat";
	public final static String MIN_JETI_VERSION = "0.5.1";
	public final static String NAME = "groupchat";
	public final static String ABOUT = "by M. Forssen and E.S. de Boer";
	private static Map groupchatWindows = new HashMap();
	private JMenu menu;//save menu to remove it
	private Bookmarks bookmarks;
	private static Plugin plugin;
		
	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}

	public Plugin(final Backend backend)
	{
		//groupchat menu item
		menu = new JMenu();
		I18N.setTextAndMnemonic("groupchat.groupchat",menu);
		JMenuItem menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("groupchat.Join/Create", menuItem, true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new GroupchatSignin(backend).show();
			}
		});
		menu.add(menuItem);
		backend.getMain().addToMenu(menu);
		backend.getMain().addToRosterMenu(I18N.gettext("groupchat.Invite_for_groupchat") + "...",new RosterMenuListener()
		{
			public void actionPerformed(JIDStatus jidStatus,nu.fw.jeti.backend.roster.JIDStatusGroup group)
			{
                GroupchatInvite.inviteUser(backend, jidStatus.getJID(),
                                           groupchatWindows);
			}
		});
		backend.addListener(MessageListener.class, this);
        backend.addExtensionHandler("http://jabber.org/protocol/muc#user",
                                    new XMUCUserHandler());
        backend.addExtensionHandler("http://jabber.org/protocol/muc#owner",
                                    new XMUCOwnerHandler());
        backend.addExtensionHandler("http://jabber.org/protocol/muc#admin",
                                    new XMUCAdminHandler());
        backend.addExtensionHandler("jabber:x:conference",
                                    new XConferenceHandler());
		bookmarks = new Bookmarks(backend, menu);
	}
		
	public static synchronized void addGroupchat(JID jid, GroupchatWindow gcw)
	{
		setChatWindowPosition(gcw);
		groupchatWindows.put(jid,gcw); 
	}
	
	/**
     * Gets the groupchat window for the groupchat JID jid, if there is no
     * window yet it will be created.
	 * @param jid The groupchat JID
	 * @param backend
	 * @return The Groupchat window for jid
	 */
    public static synchronized GroupchatWindow getGroupchat(JID jid,
                                                            Backend backend) {
		GroupchatWindow gcw =(GroupchatWindow) groupchatWindows.get(jid);
        if(gcw==null) {
			gcw = new GroupchatWindow(backend,jid);
		}
		return gcw;
	}
		
    public static synchronized void removeGroupchat(JID jid) {
		GroupchatWindow gcw =(GroupchatWindow) groupchatWindows.remove(jid);
		if(groupchatWindows.isEmpty())
		{// save window pos if it is the last window
			Preferences.putInteger("groupchat","chatPosX",gcw.getX());
			Preferences.putInteger("groupchat","chatPosY",gcw.getY());
		}
	}
	
    private static void setChatWindowPosition(GroupchatWindow chatWindow) {
		if(groupchatWindows.isEmpty())
		{
			int posX = Preferences.getInteger("groupchat","chatPosX",100);
			int posY = Preferences.getInteger("groupchat","chatPosY",100);
            int screenX =
                (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            int screenY =
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			if(posX>screenX) posX=100;
			if(posY>screenY) posY=100;
			chatWindow.setLocation(posX,posY);
		}
		else
		{
			chatWindow.setLocationRelativeTo(null);
			//ChatWindow oldWindow = (GroupchatWindow)groupchatWindows. get(groupchatWindows.size()-1);
			//chatWindow.setLocation(oldWindow.getX()+50,oldWindow.getY()+50);
		}
	}
	
	public static void addBookmark(JID jid, String nick)
	{
		plugin.bookmarks.addBookmark(jid,nick);
	}
		
	public void message(Message message)
	{
		if(message.getType().equals("groupchat"))
		{
			GroupchatWindow gcw= (GroupchatWindow) groupchatWindows.get(message.getFrom());
			if(gcw != null)
			{
				gcw.appendMessage(message);
			}
			else System.out.println(message.getBody());
		}
	}

	public void unload() {}
	
	public static void unload(Backend backend)
	{
		backend.removeExtensionHandler("http://jabber.org/protocol/muc#user");
		backend.removeExtensionHandler("http://jabber.org/protocol/muc#owner");
		backend.removeExtensionHandler("jabber:x:conference");
		backend.removeListener(MessageListener.class, plugin);
		backend.removeListener(StatusChangeListener.class,plugin.bookmarks);
		backend.getMain().removeFromMenu(plugin.menu);
		backend.getMain().removeFromRosterMenu(I18N.gettext("groupchat.Invite_for_groupchat"));
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
