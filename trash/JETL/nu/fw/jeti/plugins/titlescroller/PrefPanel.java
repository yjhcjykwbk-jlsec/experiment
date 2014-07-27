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

package nu.fw.jeti.plugins.titlescroller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


//7-2-2004
/**
 * @author E.S. de Boer
 */

public class PrefPanel extends PreferencesPanel
{
	private JTextField txtScrollSpeed = new JTextField();
	private JTextField txtScrollTime = new JTextField();

	public PrefPanel(Backend backend)
	{
		setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);

		txtScrollSpeed.setText(Preferences.getString("titlescroller","scrollspeed", "200"));
		txtScrollTime.setText(Preferences.getString("titlescroller","scrolltime", "120"));
		add(new JLabel(I18N.gettext("titlescroller.Scrolling_speed")),c);
		c.weightx = 1;
		add(txtScrollSpeed,c);
		c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(I18N.gettext("titlescroller.ms")),c);
        c.gridwidth = 1;
		c.anchor=GridBagConstraints.FIRST_LINE_END;
		add(new JLabel(I18N.gettext("titlescroller.Time_to_scroll_(0_for_no_time_limit)")),c);
		add(txtScrollTime,c);
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(I18N.gettext("titlescroller.s")),c);
						
        c.gridwidth = 3;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences()
	{
		if(!txtScrollTime.getText().equals(""))
		{	
			try 
			{
				int scrollTime = Integer.parseInt(txtScrollTime.getText());
				Preferences.putInteger("titlescroller","scrolltime", scrollTime);
			}
			catch(NumberFormatException e) {}
		}
		if(!txtScrollSpeed.getText().equals(""))
		{	
			try 
			{
				int scrollSpeed = Integer.parseInt(txtScrollSpeed.getText());
				Preferences.putInteger("titlescroller","scrollspeed", scrollSpeed);
			}
			catch(NumberFormatException e) {}
		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
