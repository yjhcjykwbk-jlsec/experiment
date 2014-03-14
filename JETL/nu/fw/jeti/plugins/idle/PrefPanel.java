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

package nu.fw.jeti.plugins.idle;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


//21-3-2004
/**
 * @author E.S. de Boer
 */

public class PrefPanel extends PreferencesPanel
{
	private JTextField txtMinutesAway = new JTextField();
	private JTextField txtAwayMessage = new JTextField();
	private JTextField txtMinutesXA = new JTextField();
	private JTextField txtXAMessage = new JTextField();

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);

		txtMinutesAway.setText(Preferences.getString("idle","minutesAway", "5"));
		txtAwayMessage.setText(Preferences.getString("idle","awayMessage", "Idle"));
		txtMinutesXA.setText(Preferences.getString("idle","minutesXA", "20"));
		txtXAMessage.setText(Preferences.getString("idle","XAMessage", "Idle"));
		add(new JLabel(I18N.gettext("idle.Minutes_of_no_activity_before_auto_Away")), c);
		add(txtMinutesAway, c);
		add(new JLabel(I18N.gettext("idle.Auto_Away_Message")), c);
		add(txtAwayMessage, c);
		add(new JLabel(I18N.gettext("idle.Minutes_of_no_activity_before_auto_Extended_Away")), c);
		add(txtMinutesXA, c);
		add(new JLabel(I18N.gettext("idle.Auto_Extended_Away_Message")), c);
		add(txtXAMessage, c);

        c.gridwidth = 1;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences()
	{
		int minutesAway=0;
		int minutesXA=1;
		if(!txtMinutesAway.getText().equals(""))
		{	
			try 
			{
				minutesAway = Integer.parseInt(txtMinutesAway.getText());
				if(minutesAway<0) minutesAway=0;
				Preferences.putInteger("idle","minutesAway", minutesAway);
			}
			catch(NumberFormatException e) {}
		}
		if(!txtMinutesXA.getText().equals(""))
		{	
			try 
			{
				minutesXA = Integer.parseInt(txtMinutesXA.getText());
				if(minutesXA<minutesAway) minutesXA = minutesAway;
				Preferences.putInteger("idle","minutesXA", minutesXA);
			}
			catch(NumberFormatException e) {}
		}
		if(!txtAwayMessage.getText().equals(""))
		{	
			Preferences.putString("idle","awayMessage", txtAwayMessage.getText());
		}
		if(!txtXAMessage.getText().equals(""))
		{	
			Preferences.putString("idle","XAMessage", txtXAMessage.getText());
		}
		Plugin.setParameters(minutesAway, txtAwayMessage.getText(), minutesXA, txtXAMessage.getText());
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
