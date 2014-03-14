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
package nu.fw.jeti.plugins.fontsize;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.Preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 */

public class PrefPanel extends PreferencesPanel
{
    private JSpinner size;

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3, 5, 0, 3);
        c.gridwidth = GridBagConstraints.REMAINDER;

 		add(new JLabel(I18N.gettext("fontsize.The_default_font_size_in_chat_windows")), c);

		SpinnerModel model = new SpinnerNumberModel(
            Preferences.getInteger("fontsize", "font-size", 14),
            4, 100, 1);
        size = new JSpinner(model);
        c.gridwidth = 1;
 		add(new JLabel(I18N.gettext("fontsize.Font_Size")), c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(size, c);

        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences() {
        Preferences.putInteger("fontsize", "font-size",
                               ((Integer)size.getValue()).intValue());
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
