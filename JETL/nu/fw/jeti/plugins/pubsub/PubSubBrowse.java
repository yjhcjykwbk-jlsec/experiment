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
 *	Created on 9-aug-2003
 */


package nu.fw.jeti.plugins.pubsub;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.fw.jeti.events.BrowseListener;
import nu.fw.jeti.events.DiscoveryListener;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.DiscoveryInfo;
import nu.fw.jeti.jabber.elements.DiscoveryItem;
import nu.fw.jeti.jabber.elements.IQBrowse;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.groupchat.elements.XMUC;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.util.I18N;

public class PubSubBrowse extends JFrame implements DiscoveryListener 
{
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JTextField txtRoom = new JTextField();
    private JPanel jPanel2 = new JPanel();
    private JTextField txtNick = new JTextField();
    private JLabel jLabel2 = new JLabel();
    private JPanel jPanel3 = new JPanel();
    private JTextField txtServer = new JTextField();
    private JLabel jLabel3 = new JLabel();
    private JPanel jPanel4 = new JPanel();
    private JButton jButton1 = new JButton();
    private JButton btnOK = new JButton();
	private Backend backend;
	//private Jeti main;
	private JTextArea txtBrowseResult = new JTextArea();  

    public PubSubBrowse(Backend backend)
    {
		this.backend = backend;
		//this.main = main;
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void jbInit() throws Exception
    {
    	setIconImage(StatusIcons.getImageIcon("jeti").getImage());
		//jLabel1.setText(I18N.gettext("Room","groupchat"));
    	I18N.setTextAndMnemonic("groupchat.Room",jLabel1);
    	jLabel1.setLabelFor(txtRoom);
        txtNick.setPreferredSize(new Dimension(200, 21));
        //jLabel2.setText(I18N.gettext("nickname","groupchat"));
        I18N.setTextAndMnemonic("groupchat.Nickname",jLabel2);
        jLabel2.setLabelFor(txtNick);
        //jLabel3.setText(I18N.gettext("Server","groupchat"));
        I18N.setTextAndMnemonic("groupchat.Server",jLabel3);
        jLabel3.setLabelFor(txtServer);
		//this.getContentPane().setLayout(new im.ui.VerticalLayout(5,VerticalLayout.BOTH));
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        
        Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				jButton1_actionPerformed(e);
			}
		};
		jButton1.setAction(cancelAction);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
       
        btnOK.setText(I18N.gettext("OK"));
        getRootPane().setDefaultButton(btnOK);
        btnOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnOK_actionPerformed(e);
            }
        });
//		JButton btnInstantRoom = new JButton("Aknowledge Instant Room"); 
//		btnInstantRoom.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(txtServer.getText() != "" && txtRoom.getText() !="") backend.send(new InfoQuery(new JID(txtRoom.getText(),txtServer.getText()),"set",new IQMUCOwner())); 
//					//backend.send(new InfoQuery(new JID("private.jabber.org"),"get",new IQBrowse()));
//			}
//		});
        
		//JButton btnBrowse = new JButton(I18N.gettext("Available rooms","groupchat")); 
		//btnBrowse.setMnemonic('A'); 
        JButton btnBrowse = new JButton();
        I18N.setTextAndMnemonic("groupchat.Available_rooms",btnBrowse);
		btnBrowse.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!txtServer.getText().equals(""))
				{
					//backend.browseNotCached(new JID(txtServer.getText()),bl);
					if(!txtRoom.getText().equals(""))
					backend.getItems(new JID(txtServer.getText()),txtRoom.getText(), PubSubBrowse.this);
					else backend.getItems(new JID(txtServer.getText()),PubSubBrowse.this);
					
					
					//backend.send(new InfoQuery(new JID(txtServer.getText()),"get",new IQDiscoItem()));
				}
					
			}
		});
        txtRoom.setPreferredSize(new Dimension(200, 21));
        txtServer.setPreferredSize(new Dimension(200, 21));
        this.getContentPane().add(jPanel3);
		this.getContentPane().add(jPanel1);
        jPanel1.add(jLabel1, null);
        jPanel1.add(txtRoom, null);
        this.getContentPane().add(jPanel2);
        jPanel2.add(jLabel2, null);
        jPanel2.add(txtNick, null);
        jPanel3.add(jLabel3, null);
        jPanel3.add(txtServer, null);
		//this.getContentPane().add(btnInstantRoom);
		this.getContentPane().add(btnBrowse);
		//txtBrowseResult.
		JScrollPane scroll = new JScrollPane(txtBrowseResult);
		this.getContentPane().add(scroll);
		this.getContentPane().add(jPanel4, null);
        jPanel4.add(btnOK, null);
        jPanel4.add(jButton1, null);
		pack();
    }
    
    public void discoveryInfoResult(JID jid, DiscoveryInfo browseResult){}
    
    public void discoveryItemResult(JID jid, DiscoveryItem browseResult)
    {
    	StringBuffer text = new StringBuffer(); 
    	if (browseResult.hasItems())
    	{
    		for (Iterator i = browseResult.getItems(); i.hasNext();)
			{
    			DiscoItem  item = (DiscoItem) i.next();
				text.append(item.getName() + "  #  " + item.getJID() + " : " + item.getNode()+ "\n");
			}
			txtBrowseResult.setText(text.toString());	
    	}
    	pack();
    }

    void btnOK_actionPerformed(ActionEvent e)
    {
//		//backend.setPresence("available",new JID(txtRoom.getText(),txtServer.getText(),null));
//		//backend.setPresence("available",new JID(txtRoom.getText(),txtServer.getText(),txtNick.getText()));
//		String nick = txtNick.getText();
//		if(nick.equals("")) nick = backend.getMyJID().getUser();
//		System.out.println(nick);
//		JID jid = new JID(txtRoom.getText(),txtServer.getText(),nick);
//		
//		GroupchatWindow gcw = new GroupchatWindow(backend,jid);
//		backend.send(new Presence(jid,"available",new XMUC()));
//		gcw.show(); 
		dispose();
    }

    void jButton1_actionPerformed(ActionEvent e)
    {
		dispose();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
