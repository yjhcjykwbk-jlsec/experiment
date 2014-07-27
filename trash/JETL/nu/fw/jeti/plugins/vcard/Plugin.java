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

// Created on 21-nov-2004
package nu.fw.jeti.plugins.vcard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.util.I18N;


/**
 * @author E.S. de Boer
 *
 */
public class Plugin extends JFrame implements Plugins
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "vcard.shows_contact_details";
	public final static String MIN_JETI_VERSION = "0.6";
	public final static String NAME = "vcard";
	public final static String ABOUT = "by E.S. de Boer";
	private static JMenuItem menuitem;
			
	public static void init(final Backend backend)
	{
		backend.getMain().addToRosterMenu(I18N.gettext("vcard.Show_Details"),new RosterMenuListener ()
		{
			public void actionPerformed(JIDStatus jidStatus,nu.fw.jeti.backend.roster.JIDStatusGroup group)
			{
				backend.send(new InfoQuery(jidStatus.getJID(),"get",new VCard()));
			}
		});
		menuitem = new JMenuItem(I18N.gettext("vcard.Edit_Details"));
		menuitem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				backend.send(new InfoQuery("get", new VCard()));
			}
		});
		backend.getMain().addToMenu(menuitem);
		backend.addExtensionHandler("vcard-temp",new VCardHandler());
	}
		
	public static void unload(Backend backend)
	{
		backend.getMain().removeFromRosterMenu(I18N.gettext("vcard.Show_Details"));
		backend.getMain().removeFromMenu(menuitem);
		menuitem=null;
		backend.removeExtensionHandler("vcard-temp");
	}
	
	public void unload()
	{
		
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
