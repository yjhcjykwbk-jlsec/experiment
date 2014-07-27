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
 *
 *	Created on 28-dec-2003
 */
 
package nu.fw.jeti.applet;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import nu.fw.jeti.backend.LoginInfo;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.DiscoItem;
import nu.fw.jeti.ui.LoginStatusWindow;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.QueryServers;

/**
 * @author E.S. de Boer
 * @author Published under the terms and conditions of the
 *    		GNU General Public License
 */

public class LoginWindow extends JFrame
{
	private JTextField txtUser = new JTextField();
	private JTextField txtPassword = new JPasswordField();
	private JComboBox cmbServer = new JComboBox();
	private JTextField txtResource = new JTextField();
	private JPanel jPanel1 = new JPanel();
    private JCheckBox chkSSL = new JCheckBox();
    //private JCheckBox chkHideLogin = new JCheckBox();
    private JPanel jPanel2 = new JPanel();
    private JTextField txtPort = new JTextField("5222");
	private JButton btnLogin = new JButton();
	private JButton btnRegister = new JButton();
	private Backend backend;

	public LoginWindow(Backend backend,LoginInfo info)
	{//wrong password
		setTitle(I18N.gettext("main.login.Wrong_Password_Try_again"));
		this.backend = backend;
		txtUser.setText(info.getUsername());
		txtResource.setText(info.getResource());
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		cmbServer.setSelectedItem(info.getServer());
		pack();
		setLocationRelativeTo(null);
	}
		
	public LoginWindow(Backend backend)
	{		
		this.backend = backend; 
		setTitle(I18N.gettext("main.login.Login"));
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if (Jeti.loginInfo!=null)
		{
			txtUser.setText(Jeti.loginInfo.getUsername());
			txtResource.setText(Jeti.loginInfo.getResource());
			cmbServer.setSelectedItem(Jeti.loginInfo.getServer());
		}
		pack();
		setLocationRelativeTo(null);

	}

	
	private void jbInit() throws Exception
	{
		I18N.setTextAndMnemonic("main.EditProfile.Register",btnRegister);
		btnRegister.setToolTipText(I18N.gettext("main.EditProfile.Request_new_account"));
		txtResource.setText("JetiApplet");
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		getRootPane().setDefaultButton(btnLogin);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		this.setCursor(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		JLabel label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Jabber_Server",label);
		label.setLabelFor(cmbServer);
        this.getContentPane().add(label, BorderLayout.NORTH);
        this.getContentPane().add(cmbServer);
        
        // load server list from servers.xml
        cmbServer.addItem("");
		for(Iterator i = QueryServers.getServers().iterator();i.hasNext();)
		{
			DiscoItem item = (DiscoItem)i.next();
			cmbServer.addItem(item.getJID().getDomain());
		}        
       		
		cmbServer.setAlignmentX((float) 0.0);		
		cmbServer.setEditable(true);
        
		label = new JLabel();
		I18N.setTextAndMnemonic("main.EditProfile.Username",label);
		label.setLabelFor(txtUser);
		getContentPane().add(label);
		getContentPane().add(txtUser);
		label = new JLabel();
		I18N.setTextAndMnemonic("main.EditProfile.Password",label);
		label.setLabelFor(txtPassword);
		getContentPane().add(label);
		getContentPane().add(txtPassword);
		label = new JLabel();
		I18N.setTextAndMnemonic("main.EditProfile.Resource",label);
		label.setLabelFor(txtResource);
		getContentPane().add(label);
		getContentPane().add(txtResource, null);
		
		I18N.setTextAndMnemonic("main.EditProfile.Use_SSL",chkSSL);
        //chkHideLogin.setText("Hide Status Window");
        //chkHideLogin.setToolTipText("Hide the login status window");
		label = new JLabel();
        I18N.setTextAndMnemonic("main.EditProfile.Port",label);
        jPanel2.add(label);
        label.setLabelFor(txtPort);
        jPanel2.add(txtPort);
        jPanel2.add(chkSSL);
        //jPanel2.add(chkHideLogin);
        jPanel2.setAlignmentX((float) 0.0);
        txtPort.setPreferredSize(new Dimension(80, 21));       
		getContentPane().add(jPanel2);
		
		       
        btnRegister.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnRegister_actionPerformed(e);
            }
        });
       // btnLogin.setMnemonic('L'); // TODO: Mnemonic here
		btnLogin.setText(I18N.gettext("main.login.Login"));
		getRootPane().setDefaultButton(btnLogin);
		btnLogin.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnLogin_actionPerformed(e);
            }
        });
		jPanel1.setAlignmentX((float) 0.0);
		jPanel1.add(btnRegister);
		jPanel1.add(btnLogin);
		getContentPane().add(jPanel1);
	}

    void btnLogin_actionPerformed(ActionEvent e)
    {
    	if(!(txtUser.getText().equals("") || cmbServer.getSelectedItem().equals("")))
		{
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
			String resource = txtResource.getText();
			if(resource.equals("")) resource = "JetiApplet";
			LoginInfo info = new LoginInfo((String)cmbServer.getSelectedItem(),null,txtUser.getText(),txtPassword.getText(),resource,port,chkSSL.isSelected());
			new LoginStatusWindow(info,backend,1);
			this.dispose();
		}
	}
	   
    void btnRegister_actionPerformed(ActionEvent e)
    {
    	String server = (String)cmbServer.getSelectedItem();
    	if(server==null || server.equals("")) server = JOptionPane.showInputDialog(this, I18N.gettext("main.EditProfile.Jabber_Server"), I18N.gettext("main.EditProfile.Create_New_Account"), JOptionPane.QUESTION_MESSAGE);
		if (server == null || server.equals("")) return;
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		backend.newAccount(server,txtUser.getText(),txtPassword.getText());
    }
       
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
