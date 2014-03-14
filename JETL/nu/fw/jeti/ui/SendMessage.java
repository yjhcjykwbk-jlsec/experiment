package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class SendMessage extends JFrame
{
    private static final int MIN_WIDTH  = 300;
    private static final int MIN_HEIGHT = 200;
	private JButton jButton1 = new JButton();
	private Backend  backend;
	private JID jid;
	private String user;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextArea jTextArea1 = new JTextArea();
    private JTextField txtSubject = new JTextField();
    //private boolean enterSends;


	/*
	public SendMessage(ConnectToBack backend,JIDStatus jidStatus)
	{//send
		this.backend = backend;
		jid = jidStatus.getJID();
		setTitle(jidStatus.getJID().getUsername());
		try
		{
			jbInit();
			jButton1.setText("Send");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//jTextArea1.setText(UIManager.getLookAndFeelDefaults().toString() );
		setSize(300,200);
	}
	*/

	public SendMessage(Backend backend,JID jid, String user)
	{//send
		this.backend = backend;
		this.jid = jid;
		//enterSends = Preferences.getBoolean("jeti","enterSends",true);
		setTitle(user);
		try
		{
			jbInit();
			jButton1.setText(I18N.gettext("Send"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		jTextArea1.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				txtInvoer_keyPressed(e);
			}
		});
        fixSize();
	}

	public SendMessage(Backend backend,Message messageElement)
	{//receive
		this.backend = backend;

        if (messageElement.getType().equals("error")) {
            handleError(messageElement);
            return;
        }

		getRootPane().setDefaultButton(jButton1);
		JIDStatus jidStatus = backend.getJIDStatus(messageElement.getFrom());
		jid = messageElement.getFrom();
		if (jidStatus == null || jidStatus.getNick() == null ) {
            if (jid.getResource() != null) {
                user = jid.getUser() + "/" + jid.getResource();
            } else {
                user = jid.getUser();
            }
		} else {
            user = jidStatus.getNick();
        }
		String subject = messageElement.getSubject(); 
		if (subject == null) {
            setTitle(user);
		} else {
            setTitle(user +" | " + subject);
        }

		try
		{
			jbInit();
			//jButton1.setMnemonic('R');
			I18N.setTextAndMnemonic("main.popup.Reply",jButton1);
			String message = messageElement.getBody();
			jTextArea1.setText(message);
			jTextArea1.setEditable(false);
			txtSubject.setText(subject);
			txtSubject.setEditable(false);
			if(message.indexOf(System.getProperty("line.separator")) == -1)
			{// if enters message is expected to be well formated, otherwise format
				int naamLengte = (getFontMetrics((Font)UIManager.get("TextArea.font"))).stringWidth(message);
				//System.out.println(naamLengte);
				if(naamLengte > 400)
				{//message is to long enable autowrap
					setSize(400,300);
					jTextArea1.setLineWrap(true);
					return;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(getHeight() > screenSize.getHeight() || getWidth() > screenSize.getWidth())
		{
			setSize((int)screenSize.getWidth()-50,(int)screenSize.getHeight()-50);
		}
        fixSize();
	}

	private void jbInit() throws Exception
	{
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		//getRootPane().setDefaultButton(jButton1);
		jButton1.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jButton1_actionPerformed(e);
			}
		});
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jTextArea1.setWrapStyleWord(true);
        this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTextArea1, null);
		//    this.getContentPane().add(jTextArea1, BorderLayout.CENTER);

		this.getContentPane().add(jButton1, BorderLayout.SOUTH);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(I18N.gettext("main.popup.Subject")), BorderLayout.NORTH);
		panel.add(txtSubject, BorderLayout.CENTER);
		panel.add(new JLabel(I18N.gettext("main.popup.Message")), BorderLayout.SOUTH);
		this.getContentPane().add(panel,BorderLayout.NORTH);
		setLocationRelativeTo(null);
	}

    private void fixSize()
    {
        Dimension d = getSize();

        if (d.height < MIN_HEIGHT) {
            d.height = MIN_HEIGHT;
        }
        if (d.width < MIN_WIDTH) {
            d.width = MIN_WIDTH;
        }
        if (d != getSize()) {
            setSize(d);
        }
    }

	void jButton1_actionPerformed(ActionEvent e)
	{
		if(jButton1 .getText().equals(I18N.gettext("Send")))
		{
			String subject =txtSubject.getText();
			if(subject.equals("")) subject = null;
		    backend.sendMessage(new Message(jTextArea1.getText(),subject,jid));
		    this.dispose();
		}
		else new SendMessage(backend ,jid,user).show();
	}
	
	void txtInvoer_keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
//			if (enterSends)
//			{
//				if ((e.getModifiers() == KeyEvent.SHIFT_MASK) || (e.getModifiers() == KeyEvent.CTRL_MASK))
//				{
//					//txtInvoer.setText(txtInvoer.getText() +"\n");
//					Document doc = jTextArea1.getDocument();
//					try
//					{
//						doc.insertString(jTextArea1.getCaretPosition(), "\n", null);
//					}
//					catch (BadLocationException e3)
//					{}
//				}
//				else
//				{
//					//send();
//					jButton1.doClick();
//					e.consume();
//				}
//			}
//			else
			{
				if ((e.getModifiers() == KeyEvent.SHIFT_MASK) || (e.getModifiers() == KeyEvent.CTRL_MASK))
				{
					//send();
					jButton1.doClick();
				}
			}
		}
	}	

	private void handleError(Message message) {
        String error;

        switch (message.getErrorCode()) {
        case 404:
            error = MessageFormat.format(
                I18N.gettext("main.error.User_{0}_could_not_be_found"),
                new Object[] {message.getFrom()});
            break;
        default:
            error = I18N.gettext("main.error.Error_in_chat:")
                + " " + message.getBody();
            break;
        }
        JOptionPane.showMessageDialog(backend.getMainWindow(), error,
                                      I18N.gettext("main.error.Error"),
                                      JOptionPane.ERROR_MESSAGE);
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
