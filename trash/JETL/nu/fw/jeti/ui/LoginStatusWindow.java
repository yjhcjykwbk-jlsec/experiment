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

// Created on 7-jul-2003
package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.*;

import nu.fw.jeti.backend.LoginInfo;
import nu.fw.jeti.backend.Start;
import nu.fw.jeti.events.LoginListener;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.util.I18N;

/**
 * show login status
 * @author E.S. de Boer
 *
 */
public class LoginStatusWindow extends JFrame implements LoginListener
{
	private Backend backend;
	private JLabel lblStatus; 
	private JButton button;
	private JLabel lblInfo;
	private JLabel[] ligths = new JLabel[5];
	private int tryCounter =0;
	private LoginInfo info;
	
	public LoginStatusWindow(final LoginInfo info, final Backend backend,int tries)
	{
		this.info = info;
		setCursor(Cursor.getDefaultCursor());
		this.backend = backend;
		if(tries>1)tryCounter = tries+1;
		setIconImage(StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.loginstatus.Logging_in"));
		JPanel panel = new JPanel(new BorderLayout());
		lblInfo = new JLabel(I18N.gettext("main.loginstatus.Logging_in")+"..."); 
		panel.add(lblInfo,BorderLayout.NORTH);
		JPanel pnlLigths = new JPanel();
		for(int i=0;i<5;i++) 
		{
			ligths[i] = new JLabel(StatusIcons.getOfflineIcon());
			pnlLigths.add(ligths[i]);  
		}
		panel.add(pnlLigths,BorderLayout.CENTER);
		button = new JButton();
		I18N.setTextAndMnemonic("Abort",button);
		panel.add(button,BorderLayout.SOUTH);
		getContentPane().add(panel,BorderLayout.CENTER);
		
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				backend.removeListener(LoginListener.class,LoginStatusWindow.this);
				dispose();
			}
		});
		
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(button.getText().equals(I18N.gettext("Abort")))				{ 
					backend.abortLogin();
					tryCounter =0;
					reset(); 
				}
				else
				{
					//button.setText(I18N.gettext("Abort"));
					I18N.setTextAndMnemonic("Abort",button);
					lblInfo.setText(I18N.gettext("main.loginstatus.Logging_in")+"...");
					backend.login(info);
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			}
		});
		lblStatus = new JLabel(I18N.gettext("main.loginstatus.Logging_in_on_your_jabber_account")+".......");
		getContentPane().add(lblStatus,BorderLayout.SOUTH);
		backend.addListener(LoginListener.class,this);
		pack();
		setLocationRelativeTo(null);
		if(tries >1) backend.autoLogin(info,tries);  
		else backend.login(info);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if(!info.hideStatusWindow()) show();
	}
		
	private void reset()
	{
		for(int i=0;i<5;i++) 
		{
			ligths[i].setIcon(StatusIcons.getOfflineIcon());
		}
		if(tryCounter==0)
		{
			setCursor(Cursor.getDefaultCursor());
		 	I18N.setTextAndMnemonic("Retry",button);
		}
	}
	
	
	public void loginMessage(final String message)
	{
		Runnable updateAComponent = new Runnable()
		{
			public void run()
			{
				lblStatus.setText(message);
			}
		};
		SwingUtilities.invokeLater(updateAComponent);	
	}
	
	public void loginStatus(final int count)
	{
		if(count == 5)
		{
			setCursor(Cursor.getDefaultCursor()); 
			backend.removeListener(LoginListener.class,this);
			dispose();
			return;
		}
		Runnable updateAComponent = new Runnable()
		{
			public void run()
			{
				if(count==0 && button.getText().equals(I18N.gettext("Retry")))
				{
					I18N.setTextAndMnemonic("Abort",button);
					lblInfo.setText(I18N.gettext("main.loginstatus.Logging_in")+"...");
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
				ligths[count].setIcon(StatusIcons.getStatusIcon(Presence.AVAILABLE)); 
			}
		};
		SwingUtilities.invokeLater(updateAComponent);	
	}
	
	public void loginError(final String message)
	{
		Runnable updateAComponent = new Runnable()
		{
			public void run()
			{
				reset();
				if(tryCounter !=0)
				{
					lblInfo.setText(MessageFormat.format(I18N.gettext("main.loginstatus.An_error_has_occured,_trying_again_{0}_X"),new Object[]{new Integer(tryCounter)}));
					tryCounter--;
				}
				else lblInfo.setText(I18N.gettext("main.loginstatus.An_error_has_occured_while_logging_in"));
				lblStatus.setText(message);
				
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
		show();//show if not visible
	}
	
	public void unauthorized()
	{
		if (!Start.applet)new LoginWindow(backend,I18N.gettext("main.login.Wrong_Password_Try_again")).show();
		else new nu.fw.jeti.applet.LoginWindow(backend,info).show();
		backend.removeListener(LoginListener.class,this);
		dispose();  
	}

	public void abort()
	{
		backend.removeListener(LoginListener.class,this);
		dispose();  
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
