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

package nu.fw.jeti.plugins.alertwindow;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

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
	
	private JTextField txtPopupTime;
	private JSlider slider;
	private JButton btnColor;
	private JButton btnForegroundColor;
	private JButton btnDemo;
	private Color color;
	private Color foregroundColor;

	public PrefPanel(final Backend backend)
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);
		c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = 1;
		
		txtPopupTime = new JTextField(Preferences.getString("alertwindow","popuptime", "4"));
		add(new JLabel(I18N.gettext("alertwindow.Time_to_show_alert")),c);
		//txtScrollSpeed.setPreferredSize(new Dimension(100,30));
		c.weightx = 1;
		add(txtPopupTime,c);
		c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(I18N.gettext("alertwindow.sec")),c);
		slider = new JSlider(5,100,Preferences.getInteger("alertwindow","alpha", 100));
		c.anchor=GridBagConstraints.FIRST_LINE_END;
		if(Plugin.supportsAlpha)
		{	
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setLabelTable(slider.createStandardLabels(10,10));
            c.gridwidth = 1;
			add(new JLabel(I18N.gettext("alertwindow.Transparancy,_100%_is_opaque")),c);
			//txtScrollTime.setPreferredSize(new Dimension(100,30));
			add(slider,c);
            c.gridwidth = GridBagConstraints.REMAINDER;
			add(new JLabel("%"),c);
		}
		foregroundColor = new Color(Preferences.getInteger("alertwindow","foregroundcolor",SystemColor.controlText.getRGB()));
		btnForegroundColor = new JButton(I18N.gettext("alertwindow.Foreground_color"));
		btnForegroundColor.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Color c = JColorChooser.showDialog(PrefPanel.this.getTopLevelAncestor() ,I18N.gettext("alertwindow.Color"), color);
				if (c != null)
				{	
					foregroundColor =c;
					btnForegroundColor.setForeground(foregroundColor);
				}
			}
		});
		btnForegroundColor.setForeground(foregroundColor);
		add(btnForegroundColor,c);
		
		color = new Color(Preferences.getInteger("alertwindow","backgroundcolor",SystemColor.info.getRGB()));
		btnColor = new JButton(I18N.gettext("alertwindow.Background_color"));
		btnColor.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Color c = JColorChooser.showDialog(PrefPanel.this.getTopLevelAncestor() ,I18N.gettext("alertwindow.Color"), color);
				if (c != null)
				{	
					color =c;
					btnColor.setBackground(color);
				}
			}
		});
		btnColor.setBackground(color);
		add(btnColor,c);
		
		
		btnDemo = new JButton(I18N.gettext("alertwindow.Demonstration"));
		btnDemo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(!txtPopupTime.getText().equals(""))
				{	
					try 
					{
						int popupTime = Integer.parseInt(txtPopupTime.getText());
						if(popupTime<1)popupTime=1;
						if(popupTime>3600)popupTime=3600;//max 1 hour popup
						Plugin plugin = new Plugin(new Frame());
						plugin.demo(color,foregroundColor,slider.getValue(), popupTime);
					}
					catch(NumberFormatException e) {}
				}
			}
		});
		add(btnDemo,c);

        c.gridwidth = 3;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences()
	{
		if(!txtPopupTime.getText().equals(""))
		{	
			try 
			{
				int scrollTime = Integer.parseInt(txtPopupTime.getText());
				if(scrollTime<1)scrollTime=1;
				if(scrollTime>3600)scrollTime=3600;//max 1 hour popup
				Preferences.putInteger("alertwindow","popuptime", scrollTime);
			}
			catch(NumberFormatException e) {}
		}
		Preferences.putInteger("alertwindow","alpha",slider.getValue());
		Preferences.putInteger("alertwindow","backgroundcolor",color.getRGB());
		Preferences.putInteger("alertwindow","foregroundcolor",foregroundColor.getRGB());
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
