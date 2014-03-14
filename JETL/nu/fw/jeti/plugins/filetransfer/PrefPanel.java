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

package nu.fw.jeti.plugins.filetransfer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.plugins.filetransfer.socks5.StreamHost;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


//7-2-2004
/**
 * @author E.S. de Boer
 */

public class PrefPanel extends PreferencesPanel
{
	private JComboBox cmbIP;
	private JTextField txtPort = new JTextField();
	private JCheckBox chkUseLocal;
	private JCheckBox chkCloseOnComplete;

	public PrefPanel(Backend backend)
	{//TODO add network config wizard with auto network detection (connect to php)
		setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 0, 3);

        String choosen = Preferences.getString("filetransfer","ip",null);
        String ip = Plugin.getIP();
        String[] ips;
        if(choosen!=null && !choosen.equals("automatic"))
        {
        		if(ip!=null) ips = new String[]{choosen,ip
        				,I18N.gettext("filetransfer.automatic")+ " (" + ip + ")"};
        		else ips = new String[]{choosen};
        }
        else if (ip!=null) ips = new String[]{I18N.gettext("filetransfer.automatic")+ " (" + ip + ")",ip};
        else ips = new String[0];
        cmbIP = new JComboBox(ips);
        cmbIP.setEditable(true);
        
        //cmbIP.setText();
		
        
        
		txtPort.setText(Preferences.getString("filetransfer","port", "7777"));
		chkUseLocal = new JCheckBox(I18N.gettext("filetransfer.behind_firewall_or_NAT"));
		chkUseLocal.setSelected(!Preferences.getBoolean("filetransfer", "useLocalIP", true));
		add(chkUseLocal,c);
				
		c.gridwidth = 1;
		c.anchor=GridBagConstraints.LINE_START;
		add(new JLabel(I18N.gettext("filetransfer.ip")),c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(cmbIP,c);
		c.gridwidth = 1;
        add(new JLabel(I18N.gettext("filetransfer.port")),c);
        c.gridwidth = GridBagConstraints.REMAINDER;
		add(txtPort,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
        chkCloseOnComplete = new JCheckBox(I18N.gettext("filetransfer.close_download_windows_when_download_ready"));
        chkCloseOnComplete.setSelected(Preferences.getBoolean("filetransfer", "closeOnComplete", false));
		add(chkCloseOnComplete,c);
		c.gridwidth = 2;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences()
	{
		Preferences.putBoolean("filetransfer", "useLocalIP", !chkUseLocal.isSelected());
		Preferences.putBoolean("filetransfer", "closeOnComplete", chkCloseOnComplete.isSelected());
		if(cmbIP.getSelectedIndex()!=-1 && !cmbIP.getSelectedItem().equals(""))
		{	
			String ip =cmbIP.getSelectedItem().toString();
			if(ip!=null)
			{
				if(ip.startsWith(I18N.gettext("filetransfer.automatic")))ip = "automatic";
				Preferences.putString("filetransfer","ip",ip);
			}
		}
		if(!txtPort.getText().equals(""))
		{	
			try 
			{
				int scrollTime = Integer.parseInt(txtPort.getText());
				Preferences.putInteger("titlescroller","scrolltime", scrollTime);
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
