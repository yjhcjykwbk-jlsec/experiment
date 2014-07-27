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

package nu.fw.jeti.plugins.ibb;



import java.util.HashMap;
import java.util.Map;

import nu.fw.jeti.events.OOBListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 * @version 1.0
 */
//Created on 8-sept-2004
public class Plugin implements Plugins
{
	private static Backend backend;
	private static Plugin plugin;
	private Map fileWindows = new HashMap(10);
	public final static String VERSION = "1.1";
	public final static String DESCRIPTION = "ibb.File_Transfer_using_the_jabber_server";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "ibb";
	public final static String ABOUT = "by E.S. de Boer, uses a simple webserver by David Brown";
	
	
	public static void init(final Backend backend)
    {
		backend.getMain().addToRosterMenu(I18N.gettext("ibb.Transfer_Small_File")+ "...",new RosterMenuListener()
		{
			public void actionPerformed(JIDStatus jidStatus,nu.fw.jeti.backend.roster.JIDStatusGroup group)
			{
				new WebServer(backend,jidStatus.getCompleteJID()).show();
			}
		});
		plugin = new Plugin();
		Plugin.backend = backend;
		backend.addExtensionHandler("http://jabber.org/protocol/ibb",new IBBHandler());
    }

	public static void ibb(JID jid,IBBExtension ibb)
	{
		String sid = ibb.getSid();
		GetFileWindow w =(GetFileWindow) plugin.fileWindows.get(sid);
		if(w==null) 
		{
			if(ibb.isOpen())
			{
				w = new GetFileWindow(jid,backend,ibb);
				w.show();
				plugin.fileWindows.put(sid,w);
			}
		}
		else if(ibb.isClose())w.stopDownloading();
		else w.addData(ibb.getData());
		
		//System.out.println("oop in plugin");
	    
	}
	
	public static void unload(Backend backend)
	{
		//make remove from menu
		//backend.getMain().rem
		
		backend.getMain().removeFromRosterMenu(I18N.gettext("ibb.Transfer_File")+ "...");
		backend.removeExtensionHandler("http://jabber.org/protocol/ibb");
		plugin = null;
	}
	
	public void unload() {}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
