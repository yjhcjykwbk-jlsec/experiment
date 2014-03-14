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

package nu.fw.jeti.plugins.sound;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.SpringUtilities;
import nu.fw.jeti.ui.StdPreferencesPanel;


//10-sept-2004
/**
 * @author E.S. de Boer
 */

public class PrefPanel extends PreferencesPanel
{
	private JTextField blockTime = new JTextField();
	List sounds = new LinkedList();
	JSlider slider;
    GridBagConstraints c = new GridBagConstraints();
	private JCheckBox chkBeep;
	private boolean oldBeep;

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);

		add(new JLabel(I18N.gettext("sound.Enable")), c);
        c.weightx = 1;
		add(new JLabel(I18N.gettext("sound.Path_to_sound")), c);
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(I18N.gettext("sound.Choose")), c);
		
		addSoundChooser("message");
		addSoundChooser("online");
		addSoundChooser("offline");
		addSoundChooser("own_online");
		addSoundChooser("own_offline");
		
        c.gridwidth = 1;
		add(new JLabel(I18N.gettext("sound.Volume")), c);
		JPanel panel = new JPanel();
		panel.add(new JLabel(I18N.gettext("sound.Min")));
		int max = 15;
		if(System.getProperty("java.version").startsWith("1.5"))max =6;
		int volume = Preferences.getInteger("sound","volume",0);
		if(volume>max)volume=max;
		slider = new JSlider(-5,max,volume);
		panel.add(slider);
		panel.add(new JLabel(I18N.gettext("sound.Max")));
		add(panel, c);
		JButton button = new JButton();
		I18N.setTextAndMnemonic("sound.Test",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Preferences.putInteger("sound","volume",slider.getValue());
				Plugin.test();
			}
		});
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(button, c);		

		blockTime.setText(Preferences.getString("sound", "block", "2"));
        JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(I18N.gettext("sound.Block_repeats_of_sound_within") + " "), BorderLayout.LINE_START);
        p.add(blockTime, BorderLayout.CENTER);
		p.add(new JLabel(" (" + I18N.gettext("sound.seconds") + ")"),
              BorderLayout.LINE_END);
		add(p, c);
		
        c.gridwidth = GridBagConstraints.REMAINDER;
		chkBeep = new JCheckBox(I18N.gettext("main.options.standard.Beep_on_new_message"));
		chkBeep.setToolTipText(I18N.gettext("main.options.standard.tooltip.Beep_on_new_message_arrival")); 
		oldBeep = Preferences.getBoolean("jeti","beep",true);
		chkBeep.setSelected(oldBeep);
        add(chkBeep, c);

        c.gridwidth = 3;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);

        new ButtonLinker(chkBeep, StdPreferencesPanel.chkBeep);
	}
	
	private void addSoundChooser(String name)
	{
        c.gridwidth = 1;
		String path = Preferences.getString("sound",name,null);
		JCheckBox box = new JCheckBox(I18N.gettext("sound."+ name));
		box.setSelected(Preferences.getBoolean("sound",name+".enabled",true));
		add(box, c);
		final JTextField text = new JTextField(path);
		add(text, c);
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileFilter()
				{
					public boolean accept(File f)
					{
						if(f.isDirectory()) return true;
						String ext = getExtension(f);
						if(ext!=null)
						{
							if(ext.equals("wav") ||
							   ext.equals("aiff") ||
							   ext.equals("au"))
							return true;
						}
						return false;
					}

					public String getDescription()
					{
						return "Audio files";
					}
					
					private String getExtension(File f) {
				        String ext = null;
				        String s = f.getName();
				        int i = s.lastIndexOf('.');

				        if (i > 0 &&  i < s.length() - 1) {
				            ext = s.substring(i+1).toLowerCase();
				        }
				        return ext;
				    }
				});
				int res = chooser.showOpenDialog(PrefPanel.this);
				if (res == JFileChooser.APPROVE_OPTION) 
				{
		            text.setText(chooser.getSelectedFile().getPath());
				}
			}
		});
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(button, c);
		sounds.add(new Object[]{name,box,text});
	}
	

	public void savePreferences()
	{
		for(Iterator i = sounds.iterator();i.hasNext();)
		{
			Object[] temp = (Object[]) i.next();
			String name =(String) temp[0];
			Preferences.putBoolean("sound",name+".enabled",((JCheckBox)temp[1]).isSelected());
			String path = ((JTextField)temp[2]).getText();
			if(path.equals("")) {
                Preferences.putString("sound",name,null);
			} else {
                Preferences.putString("sound",name,path);
            }
		}
        Plugin.reloadSounds();
		int vol =slider.getValue();
		if(Preferences.getInteger("sound","volume",0)!=vol)
		{
			Preferences.putInteger("sound","volume",vol);
		}
		if (!blockTime.getText().equals("")) {
			try {
				int i = Integer.parseInt(blockTime.getText());
				Preferences.putInteger("sound", "block", i);
			} catch (NumberFormatException e){}
		}
	}


    private class ButtonLinker implements ChangeListener {
        private AbstractButton but1;
        private AbstractButton but2;
        private boolean ignore = false;

        public ButtonLinker(AbstractButton but1, AbstractButton but2) {
            this.but1 = but1;
            this.but2 = but2;

            but1.addChangeListener(this);
            but2.addChangeListener(this);
        }

        public synchronized void stateChanged(ChangeEvent e) {
            if (ignore) {
                return;
            }
            ignore = true;
            if (but1.isSelected() != but2.isSelected()) {
                if (e.getSource().equals(but1)) {
                    but2.setSelected(but1.isSelected());
                } else {
                    but1.setSelected(but2.isSelected());
                }
            }
            ignore = false;
        }
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
