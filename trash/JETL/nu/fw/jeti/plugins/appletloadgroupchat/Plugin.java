/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2002 E.S. de Boer  
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
 *	Created on 10-aug-2004
 */
 
package nu.fw.jeti.plugins.appletloadgroupchat;



import javax.swing.JOptionPane;

import nu.fw.jeti.applet.Jeti;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.groupchat.GroupchatSignin;
import nu.fw.jeti.plugins.groupchat.GroupchatWindow;
import nu.fw.jeti.plugins.groupchat.elements.XMUC;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, StatusChangeListener
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "appletloadgroupchat.Loads_a_groupchat_on_login";
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String NAME = "appletloadgroupchat";
	public final static String ABOUT = "by E.S. de Boer";
	private static Plugin plugin;
	private Backend backend;
	
	
	public Plugin(Backend backend) 
	{
		this.backend = backend;
	}
        
    public static void init(Backend backend)
    {
    	//System.out.println("applet loaded");
    	Plugin.plugin = new Plugin(backend);
    	backend.addListener(StatusChangeListener.class,plugin);
    }
           
    
    public void unload(){}
    
    public static void unload(Backend backend)
    {
    	backend.removeListener(StatusChangeListener.class,plugin);
    }
    
    public void connectionChanged(boolean online)
    {
    	//System.out.println(Jeti.groupchatServer);
    	if(online)
    	{
    		if(Jeti.groupchatServer != null && Jeti.groupchatRoom !=null)
    		{
    			String nick = null;
    			if(Jeti.randomName)
    			{
    				nick = JOptionPane.showInputDialog(backend.getMainWindow(),"Enter your nickname");
    			}
    			if(nick==null) nick = backend.getMyJID().getUser();
    			JID jid = new JID(Jeti.groupchatRoom,Jeti.groupchatServer,nick);
        		GroupchatWindow gcw = nu.fw.jeti.plugins.groupchat.Plugin.getGroupchat(jid,backend);
        		backend.send(new Presence(jid,"available",new XMUC()));
        		gcw.show();
    		}
    		else if(Jeti.groupchatServer != null)
    		{
    			GroupchatSignin g = new GroupchatSignin(backend);
    			g.setChatRoomServer(Jeti.groupchatServer);
    			g.show();
    		}
    	}
    }
    
    public void exit(){}
    
    public void ownPresenceChanged(int presence,String statusmessage){}
    
    
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
