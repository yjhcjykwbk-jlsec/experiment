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

package nu.fw.jeti.plugins.autoloadgroupchat;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.plugins.groupchat.GroupchatSignin;
import nu.fw.jeti.plugins.xhtml.fontchooser.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


public class PrefPanel extends PreferencesPanel
{
    private JLabel jLabel1 = new JLabel();
    private JTextField txtRoom = new JTextField();
    private JTextField txtNick = new JTextField();
    private JLabel jLabel2 = new JLabel();
    private JTextField txtServer = new JTextField();
    private JLabel jLabel3 = new JLabel();

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);
        GridBagConstraints cEnd = (GridBagConstraints)c.clone();
        cEnd.gridwidth = GridBagConstraints.REMAINDER;
		
    	I18N.setTextAndMnemonic("groupchat.Room",jLabel1);
    	jLabel1.setLabelFor(txtRoom);
        I18N.setTextAndMnemonic("groupchat.Nickname",jLabel2);
        jLabel2.setLabelFor(txtNick);
        I18N.setTextAndMnemonic("groupchat.Server",jLabel3);
        jLabel3.setLabelFor(txtServer);

        add(jLabel1, c);
        add(txtRoom, cEnd);
        add(jLabel2, c);
        add(txtNick, cEnd);
        add(jLabel3, c);
        add(txtServer, cEnd);

        c.gridwidth = 2;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);

		txtRoom.setText(Preferences.getString("autoloadgroupchat", "room", "room"));
		txtServer.setText(Preferences.getString("autoloadgroupchat", "server", "groupchatserver"));
		txtNick.setText(Preferences.getString("autoloadgroupchat", "nick", ""));
		
	}

	public void savePreferences()
	{
		if (!txtRoom.getText().equals(""))
		{
			Preferences.putString("autoloadgroupchat", "room", txtRoom.getText());
		}
		if (!txtNick.getText().equals(""))
		{
			Preferences.putString("autoloadgroupchat", "nick", txtNick.getText());
		}
		if (!txtServer.getText().equals(""))
		{
			Preferences.putString("autoloadgroupchat", "server", txtServer.getText());
		}
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
