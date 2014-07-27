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
package nu.fw.jeti.plugins.groupchat;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.I18N;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * @author M Forssen
 */
public class PrefPanel extends PreferencesPanel {
    private JCheckBox chkLinkStatus;
    private JCheckBox showPresence;

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);

		chkLinkStatus = new JCheckBox(
            I18N.gettext("groupchat.options.Link_Status_Buttons"));
		chkLinkStatus.setToolTipText(I18N.gettext("groupchat.options.tooltip.Changing_status_in_one_window_affects_all_windows"));

		boolean old = Preferences.getBoolean("jeti","statusLinked", true);
		chkLinkStatus.setSelected(old);

        add(chkLinkStatus, c);

		showPresence = new JCheckBox(
            I18N.gettext("groupchat.options.Show_presence_messages_in_chat_window"));
		chkLinkStatus.setToolTipText(I18N.gettext("groupchat.options.tooltip.Insert_a_message_in_the_chat_window_each_time_a_participant_changes_availability"));
		old = Preferences.getBoolean("groupchat","showPresence", true);
		showPresence.setSelected(old);

        add(showPresence, c);

        c.gridwidth = 1;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences() {
		Preferences.putBoolean("jeti", "statusLinked",
                               chkLinkStatus.isSelected());
		Preferences.putBoolean("groupchat", "showPresence",
                               showPresence.isSelected());
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
