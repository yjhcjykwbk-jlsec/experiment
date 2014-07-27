/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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

package nu.fw.jeti.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import sun.java2d.pipe.TextPipe;

import nu.fw.jeti.backend.LoginInfo;
import nu.fw.jeti.backend.ProfileInfo;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.DiscoItem;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.QueryServers;

/**
 * @author E.S. de Boer
 * @author Published under the terms and conditions of the
 *    		GNU General Public License
 */

public class EditProfileWindow extends JFrame
{
	private JTextField txtUser = new JTextField();
	private JTextField txtPassword = new JPasswordField();
	private ProfileInfo profileInfo;
	private JComboBox cmbServer = new JComboBox();
	private JTextField txtResource = new JTextField();
	private JPanel jPanel1 = new JPanel();
	private JButton btnCancel = new JButton();
	private JButton btnOnce = new JButton();
	private JButton btnSave = new JButton();
    private JTextField txtProfiel = new JTextField();
    private JCheckBox chkSSL = new JCheckBox();
    private JPanel jPanel2 = new JPanel();
    private JCheckBox chkHideLogin = new JCheckBox();
    private JTextField txtPort = new JTextField();
    private JPanel jPanel3 = new JPanel();
    private JTextField txtHost = new JTextField();
    private JPanel jPanel4 = new JPanel();
    private JCheckBox chkProxy = new JCheckBox();
    private JTextField txtProxyHost = new JTextField();
    private JTextField txtProxyPort = new JTextField();
    private JTextField txtProxyUserName = new JTextField();
    private JTextField txtProxyPassword = new JTextField();
    private JButton btnRemove = new JButton();
    private JSpinner spinner = new JSpinner(new SpinnerNumberModel(0,-128,127,1));
	private LoginWindow loginWindow;
	private Backend backend;

	public EditProfileWindow(LoginWindow window,ProfileInfo profileInfo,Backend backend)
	{//new
		loginWindow = window;
		this.profileInfo = profileInfo;
		txtPort.setText("5222");
		txtResource.setText("JETI");
		setTitle(I18N.gettext("main.EditProfile.New_Profile_Info"));
		//btnRemove.setMnemonic('R'); 
		I18N.setTextAndMnemonic("main.EditProfile.Register",btnRemove);
		btnRemove.setToolTipText(I18N.gettext("main.EditProfile.Request_new_account"));
		this.backend = backend; 
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public EditProfileWindow(LoginWindow window,ProfileInfo profileInfo,String profileName)
	{//edit
		loginWindow = window;
		txtProfiel.setText(profileName);
		txtProfiel.setEditable(false);
		setTitle(I18N.gettext("main.EditProfile.Edit_Profile_Info"));
		//btnRemove.setMnemonic('R'); 
		I18N.setTextAndMnemonic("main.EditProfile.Remove",btnRemove);
		btnRemove.setToolTipText(I18N.gettext("main.EditProfile.Remove_this_ profile"));
		this.profileInfo = profileInfo;
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		LoginInfo temp = profileInfo.getProfile(profileName);
		if (temp != null)
		{
			txtUser.setText(temp.getUsername());
			txtPassword.setText(temp.getPassword());
			txtResource.setText(temp.getResource());
			txtPort.setText(String.valueOf(temp.getPort()));
            txtHost.setText(temp.getHost());
			chkSSL.setSelected(temp.isSSl());
			chkHideLogin.setSelected(temp.hideStatusWindow());
			cmbServer.setSelectedItem(temp.getServer());
			spinner.setValue(new Integer(temp.getPriority()));		
			if(temp.useProxy())
			{
				chkProxy.setSelected(true);
				txtProxyHost.setText(temp.getProxyServer());
				txtProxyPort.setText(temp.getProxyPort());
				txtProxyUserName.setText(temp.getProxyUsername());
				txtProxyPassword.setText(temp.getProxyPassword());
			}
		}
	}

	private void jbInit() throws Exception
	{
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		this.setCursor(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		JLabel label = new JLabel();
		
		I18N.setTextAndMnemonic("main.EditProfile.Profile_name",label);
		getContentPane().add(label);
		label.setLabelFor(txtProfiel);
		//getContentPane().add(new JLabel(I18N.gettext("main.EditProfile.Profile_name")));
		
		this.getContentPane().add(txtProfiel);
        //this.getContentPane().add(new JLabel(I18N.gettext("main.EditProfile.Jabber_Server")), BorderLayout.NORTH);
        label = new JLabel();
        
        I18N.setTextAndMnemonic("main.EditProfile.Jabber_Server",label);
		label.setLabelFor(cmbServer);
		getContentPane().add(label);
        this.getContentPane().add(cmbServer);
        
        cmbServer.addItem("");
		for(Iterator i = QueryServers.getServers().iterator();i.hasNext();)
		{
			DiscoItem item = (DiscoItem)i.next();
			cmbServer.addItem(item.getJID().getDomain());
		}       
        
		cmbServer.setAlignmentX((float) 0.0);
		cmbServer.setEditable(true);
        
		//getContentPane().add(new JLabel(I18N.gettext("main.EditProfile.Username")));
		label = new JLabel();
		
		I18N.setTextAndMnemonic("main.EditProfile.Username",label);
		label.setLabelFor(txtUser);
		getContentPane().add(label);
		getContentPane().add(txtUser);
		
	
		label = new JLabel();
		I18N.setTextAndMnemonic("main.EditProfile.Password_(leave_blank_to_ask)",label);
		label.setToolTipText(I18N.gettext("main.EditProfile.Only_fill_in_password_on_private_computers,_to_prevent_theft_of_password"));
		getContentPane().add(label);
		label.setLabelFor(txtPassword);
		
		getContentPane().add(txtPassword);
		label = new JLabel();
		
	
		I18N.setTextAndMnemonic("main.EditProfile.Resource",label);
		label.setLabelFor(txtResource);
		getContentPane().add(label);
		
		getContentPane().add(txtResource);

		JButton button = new JButton();
		I18N.setTextAndMnemonic("main.EditProfile.Advanced",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(jPanel3.isVisible())
				{
					jPanel3.setVisible(false);
					pack();
					setLocationRelativeTo(null);
				}
				else
				{
					jPanel3.setVisible(true);
					pack();
					setLocationRelativeTo(null);
				}

			}
		});
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(button);
		panel.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
		
		panel.setAlignmentX((float) 0.0);
		getContentPane().add(panel);
		
		jPanel3.setAlignmentX((float) 0.0);
        jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.PAGE_AXIS));
        advancedConfig();
        getContentPane().add(jPanel3);
        jPanel3.setVisible(false);
        
        btnRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnRemove_actionPerformed(e);
            }
        });
        Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				btnCancel_actionPerformed(e);
			}
		};
		btnCancel.setAction(cancelAction);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
		       
	
		jPanel1.setAlignmentX((float) 0.0);
	
		I18N.setTextAndMnemonic("Save",btnSave);
		btnSave.setToolTipText(I18N.gettext("main.EditProfile.Save_profile"));
        btnSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnSave_actionPerformed(e);
            }
        });
        
		jPanel1.add(btnRemove);
		jPanel1.add(btnSave);
		jPanel1.add(btnCancel);
        getContentPane().add(jPanel1);
        pack();
		setLocationRelativeTo(null);

	}

    
	private void advancedConfig()
	{
		JLabel label;
		I18N.setTextAndMnemonic("main.EditProfile.Use_SSL",chkSSL);
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Port",label);
        label.setLabelFor(txtPort);
        jPanel2.add(label);
        jPanel2.add(txtPort);
        jPanel2.add(chkSSL);
        
        jPanel2.setAlignmentX((float) 0.0);
        txtPort.setPreferredSize(new Dimension(80, 21));       
        jPanel3.add(jPanel2);
        
        JPanel panel= new JPanel();
        panel.setAlignmentX((float) 0.0);
        label = new JLabel();
		I18N.setTextAndMnemonic("main.EditProfile.Priority",label);
		label.setLabelFor(spinner);
       /// spinner = new JSpinner(new SpinnerNumberModel(0,-128,127,1));
        panel.add(label);
        panel.add(spinner);
        
        I18N.setTextAndMnemonic("main.EditProfile.Hide_Status_Window",chkHideLogin);
        chkHideLogin.setToolTipText(I18N.gettext("main.EditProfile.Hide_the_login_status_window"));
        panel.add(chkHideLogin);
        jPanel3.add(panel);
        
        
        jPanel4.setAlignmentX((float) 0.0);
        jPanel4.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(142, 142, 142)),I18N.gettext("main.EditProfile.Proxy")));
        jPanel4.setLayout(new BoxLayout(jPanel4 ,BoxLayout.Y_AXIS));
        //chkProxy.setText(I18N.gettext("main.EditProfile.Use_Socks_Proxy"));
        I18N.setTextAndMnemonic("main.EditProfile.Use_Socks_Proxy",chkProxy);
        jPanel4.add(chkProxy);
        //jPanel4.add(new JLabel(I18N.gettext("main.EditProfile.Proxy_Host")));
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Proxy_Host",label);
        jPanel4.add(label);
        label.setLabelFor(txtProxyHost);
        jPanel4.add(txtProxyHost);
        //jPanel4.add(new JLabel(I18N.gettext("main.EditProfile.Proxy_Port")));
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Proxy_Port",label);
        jPanel4.add(label);
        label.setLabelFor(txtProxyPort);
        jPanel4.add(txtProxyPort);
        //jPanel4.add(new JLabel(I18N.gettext("main.EditProfile.Username")));
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Username",label);
        jPanel4.add(label);
        label.setLabelFor(txtProxyUserName);
        jPanel4.add(txtProxyUserName);
        //jPanel4.add(new JLabel(I18N.gettext("main.EditProfile.Password")));
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Password",label);
        jPanel4.add(label);
        label.setLabelFor(txtProxyPassword);
        jPanel4.add(txtProxyPassword);
        jPanel3.add(jPanel4);
        
        label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Connect_to",label);
        label.setLabelFor(txtHost);
       // label.setAlignmentX(0.0f);
        //txtHost.setAlignmentX(0.0f);
        jPanel3.add(label);
        jPanel3.add(txtHost);
        
        I18N.setTextAndMnemonic("main.EditProfile.Use_this_profile_only_for_this_session",btnOnce);
		btnOnce.setToolTipText(I18N.gettext("main.EditProfile.Use_this_profile_only_to_login_this_time")); 
        btnOnce.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnLogin_actionPerformed(e);
            }
        });
        jPanel3.add(btnOnce);
	}

	void btnLogin_actionPerformed(ActionEvent e)
    {
		if(saveCurrent())this.dispose();
    }

	private boolean saveCurrent()
	{
		if(!(txtProfiel.getText().equals("") ||txtUser.getText().equals("") || cmbServer.getSelectedItem().equals("")))
		{
			String resource = txtResource.getText();
			if(resource.equals("")) resource = "JETI";
			int port;
			try
			{
				port = Integer.parseInt(txtPort.getText());
			}
			catch (NumberFormatException ex)
			{
				if(chkSSL.isSelected()) port = 5223;
				else port = 5222;
			}
            LoginInfo info = new LoginInfo((String)cmbServer.getSelectedItem(),
                                           txtHost.getText(),
                                           txtUser.getText(),
                                           txtPassword.getText(),
                                           resource,port,chkSSL.isSelected(),
                                           ((Number)spinner.getValue()).intValue(),
										   chkProxy.isSelected(),
                                           chkHideLogin.isSelected(),
                                           txtProxyHost.getText(),
                                           txtProxyUserName.getText(),
                                           txtProxyPassword.getText(),
                                           txtProxyPort.getText());
			profileInfo.setProfile(txtProfiel.getText(),info);
			loginWindow.update();
			return true;
		}
		else return false;
	}

    void btnSave_actionPerformed(ActionEvent e)
    {
		if(saveCurrent())
		{
			profileInfo.save();
			this.dispose();
		}
    }

    void btnCancel_actionPerformed(ActionEvent e)
    {
		this.dispose();
    }

    void btnRemove_actionPerformed(ActionEvent e)
    {
    	if(btnRemove.getText().equals(I18N.gettext("main.EditProfile.Register")))
    	{
			//String server = JOptionPane.showInputDialog(this, I18N.gettext("main.EditProfile.Jabber_Server","jeti"), I18N.gettext("main.EditProfile.Create_New_Account","jeti"), JOptionPane.QUESTION_MESSAGE);
			//if (server == null || server.equals("")) return;
    		String server = (String) cmbServer.getSelectedItem();
    		if(server==null || server.equals(""))
    		{
    			JOptionPane.showMessageDialog(this,I18N.gettext("main.EditProfile.Please_select_a_server_first"));
    			return;
    		}
    		String username = txtUser.getText();
    		if(username.equals(""))
    		{
    			JOptionPane.showMessageDialog(this,I18N.gettext("main.EditProfile.Please_enter_a_username_first"));
    			return;
    		}
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			backend.newAccount(server,username ,txtPassword.getText());
    	}
    	else
    	{
			if (JOptionPane.showConfirmDialog(this,MessageFormat.format(I18N.gettext("main.EditProfile.Really_remove_{0}?"),new Object[]{txtProfiel.getText()}),I18N.gettext("Remove"),JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				profileInfo.remove(txtProfiel.getText());
				loginWindow.update();
				this.dispose();
			}
    	}
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
