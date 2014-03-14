/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2001 E.S. de Boer  
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

import java.awt.event.*;

import javax.swing.*;

import nu.fw.jeti.backend.LoginInfo;
import nu.fw.jeti.backend.ProfileInfo;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 */

public class LoginWindow extends JFrame
{
	private JPasswordField txtPassword = new JPasswordField();
	private Backend backend;
    private JComboBox cmbProfile;
    private JPanel jPanel1 = new JPanel();
    private JButton btnEdit = new JButton();
    private JButton btnLogin = new JButton();
    private JButton btnNew = new JButton();
	private ProfileInfo profileInfo;
	private static LoginWindow loginWindow;


	public LoginWindow(Backend back,String error)
	{
		this(back);
		this.getContentPane().add(new JLabel(error));
		pack(); 
	}

	private LoginWindow(Backend back)
	{
		profileInfo = new ProfileInfo();
		cmbProfile = new JComboBox(profileInfo.getProfilesList());
		backend = back;
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
		txtPassword.requestFocus(); 
	}
	
	public static void createLoginWindow(Backend back)
	{
		if(loginWindow==null) loginWindow = new LoginWindow(back);
		loginWindow.setVisible(true);
	}
	
	private void jbInit() throws Exception
	{
		getRootPane().setDefaultButton(btnLogin);
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.login.Login"));
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		
		this.setCursor(null);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				LoginWindow.this.dispose();
				loginWindow=null;
			}
		});
				
		this.setResizable(false);

		cmbProfile.setAlignmentX(0.0f);
		String profileName = Preferences.getString("jeti","profileName",null);
		if(profileName!=null)
		{	
			LoginInfo info = profileInfo.getProfile(profileName);
			if(info != null)
			{	
				cmbProfile.setSelectedItem(profileName);
				if(info.getPassword() == null) txtPassword.setEditable(true);
				else txtPassword.setEditable(false);
			}
		}
		cmbProfile.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				String profileName = (String)e.getItem();
				LoginInfo info = profileInfo.getProfile(profileName);
				if(info != null)
				{	
					if(info.getPassword() == null) txtPassword.setEditable(true);
					else txtPassword.setEditable(false);
				}
			}
		});
        I18N.setTextAndMnemonic("main.login.Edit",btnEdit);
        btnEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnEdit_actionPerformed(e);
            }
        });
        I18N.setTextAndMnemonic("main.login.Login",btnLogin);
        btnLogin.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnLogin_actionPerformed(e);
            }
    	});
    	txtPassword.addKeyListener(new java.awt.event.KeyAdapter()
    	{		
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
				{
					btnLogin.doClick();  
				}
			}
    	});
        jPanel1.setAlignmentX(0.0f);
        I18N.setTextAndMnemonic("main.login.New",btnNew);
        btnNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnNew_actionPerformed(e);
            }
        });
        JLabel label = new JLabel();
        I18N.setTextAndMnemonic("main.login.Profile",label);
        getContentPane().add(label);
        label.setLabelFor(cmbProfile);
        getContentPane().add(cmbProfile);
        label = new JLabel();
        I18N.setTextAndMnemonic("main.login.Password",label);
        label.setLabelFor(txtPassword);
        getContentPane().add(label);
		getContentPane().add(txtPassword);
		getContentPane().add(jPanel1);
        jPanel1.add(btnLogin, null);
        jPanel1.add(btnEdit, null);
        jPanel1.add(btnNew, null);
		pack();
		setLocationRelativeTo(null);
	}

	public void update()
	{
		cmbProfile.setModel(new DefaultComboBoxModel(profileInfo.getProfilesList()));
	}

    void btnNew_actionPerformed(ActionEvent e)
    {
		new EditProfileWindow(this,profileInfo,backend).setVisible(true);
    }

    void btnEdit_actionPerformed(ActionEvent e)
    {
		new EditProfileWindow(this,profileInfo,(String)cmbProfile.getSelectedItem()).setVisible(true);
    }

    void btnLogin_actionPerformed(ActionEvent e)
    {
		if(!txtPassword.isEditable() || !(txtPassword.getText().equals("")))
		{
			String profileName = (String)cmbProfile.getSelectedItem();
			LoginInfo info = profileInfo.getProfile(profileName);
			if(info == null)return;
			if(info.getPassword() == null) info.setPassword(txtPassword.getText());
			new LoginStatusWindow(info,backend,1);
			Preferences.putString("jeti","profileName",profileName);
			loginWindow=null;
			dispose();
		}
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
