package nu.fw.jeti.plugins.groupchat;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.fw.jeti.backend.Start;
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
import nu.fw.jeti.util.QueryServers;


/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class GroupchatSignin extends JFrame
{
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JComboBox cmbRoom = new JComboBox();
    private Vector rooms = new Vector();
    private JPanel jPanel2 = new JPanel();
    private JTextField txtNick = new JTextField();
    private JLabel jLabel2 = new JLabel();
    private JPanel jPanel3 = new JPanel();
    private JComboBox cmbChatRoomServer = new JComboBox();
    private JLabel jLabel3 = new JLabel();
    private JPanel jPanel4 = new JPanel();
    private JButton jButton1 = new JButton();
    private JButton btnOK = new JButton();
	private Backend backend;
    private RoomListener roomListener = new RoomListener();
    private ServerListener serverListener = new ServerListener();
    private NicListener nicListener = new NicListener();
    private boolean takeFocus = true;

    public GroupchatSignin(Backend backend)
    {
		this.backend = backend;
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        JID server = new JID(backend.getMyJID().getDomain());
        backend.getItems(server, serverListener);
    }

    private void jbInit() throws Exception
    {
    	setTitle(I18N.gettext("groupchat.Choose_groupchat_room"));
    	setIconImage(StatusIcons.getImageIcon("jeti").getImage());
    	I18N.setTextAndMnemonic("groupchat.Room",jLabel1);
    	jLabel1.setLabelFor(cmbRoom);
        txtNick.setPreferredSize(new Dimension(200, 21));
        I18N.setTextAndMnemonic("groupchat.Nickname",jLabel2);
        jLabel2.setLabelFor(txtNick);
        I18N.setTextAndMnemonic("groupchat.Chat_Server",jLabel3);
        jLabel3.setLabelFor(cmbChatRoomServer);
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
        
        JComboBox cmbServer = new JComboBox();
        JLabel label = new JLabel();
        I18N.setTextAndMnemonic("groupchat.Server",label);
		label.setLabelFor(cmbServer);
		        
        cmbServer.addItem("");
        cmbServer.addItem(backend.getMyJID().getDomain());
		for(Iterator i = QueryServers.getServers().iterator();i.hasNext();)
		{
			DiscoItem item = (DiscoItem)i.next();
			cmbServer.addItem(item.getJID().getDomain());
		}
		cmbServer.setSelectedItem(backend.getMyJID().getDomain());
		
		cmbServer.setPreferredSize(new Dimension(200, 21));
		cmbServer.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	String server = e.getItem().toString();
                	if(JID.isValidServer(server))
                	{
                		cmbChatRoomServer.removeAllItems();
                		backend.getItems(new JID(server), serverListener);
                   	}
                }
			}
		});
        cmbServer.setEditable(true);
        
        
        
        cmbRoom.setPreferredSize(new Dimension(200, 21));
        cmbRoom.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    getRoomNic(e.getItem().toString());
                }
			}
        });
        cmbRoom.setEditable(true);
					
        cmbChatRoomServer.setPreferredSize(new Dimension(200, 21));
        cmbChatRoomServer.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	 Object o = cmbChatRoomServer.getSelectedItem();
                	 if(o!=null)
                	 {
	                	 if(o instanceof ChatServer)
	                	 {
	                	 	ChatServer cs = (ChatServer)o;
							setChatRoomServer(cs.getServer());
	                	 }
	                	 else if (!o.equals(""))setChatRoomServer(o.toString());
                	 }
                }
			}
		});
        cmbChatRoomServer.setEditable(true);
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(label);
        panel.add(cmbServer);
        this.getContentPane().add(panel);
        this.getContentPane().add(jPanel3);
        jPanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel3.add(jLabel3, null);
        jPanel3.add(cmbChatRoomServer, null);
		this.getContentPane().add(jPanel1);
        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel1.add(jLabel1, null);
        jPanel1.add(cmbRoom, null);
        this.getContentPane().add(jPanel2);
        jPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel2.add(jLabel2, null);
        jPanel2.add(txtNick, null);
		this.getContentPane().add(jPanel4, null);
        jPanel4.add(btnOK, null);
        jPanel4.add(jButton1, null);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void setChatRoomServer(String server)
    {
    	backend.getItems(new JID(server),roomListener, false);
    }
    
    
    void btnOK_actionPerformed(ActionEvent e)
    {
		String nick = txtNick.getText();
		if(nick.equals("")) {
            nick = backend.getMyJID().getUser();
        }

        String room;
        int selectedIndex = cmbRoom.getSelectedIndex();
        if (-1 != selectedIndex) {
            room = ((JID)rooms.elementAt(selectedIndex)).getUser();
        } else {
            room = cmbRoom.getSelectedItem().toString();
            if (!JID.isValidUser(room)) {
                JOptionPane.showMessageDialog(
                    this, 
                    I18N.getTextWithAmp("Username_contains_illegal_chars_(see_english_translation)"),
                    I18N.gettext("groupchat.Illegal_group_name"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        Object o = cmbChatRoomServer.getSelectedItem();
        if(o==null)return;
        String server;
	   	if(o instanceof ChatServer)
	   	{
	   	 	ChatServer cs = (ChatServer)o;
			server = cs.getServer();
	   	}
	   	else 
	   	{
	   		if(o.equals(""))return;
	   		server = o.toString();
	   	}
         
        JID jid = new JID(room, server, nick);
		
        GroupchatWindow gcw = Plugin.getGroupchat(jid,backend);
		backend.send(new Presence(jid,"available",new XMUC()));
		gcw.show(); 
		dispose();
    }

    void jButton1_actionPerformed(ActionEvent e)
    {
		dispose();
	}
    
    private void getRoomNic(String room)
	{
	// Do nothing since the params is not implemented XXX
	//String server = cmbServer.getSelectedItem().toString();
	//backend.getInfo(new JID(room, server),"x-roomuser-item",nicListener);
	}

	private class RoomListener implements DiscoveryListener
	{
		public void discoveryInfoResult(JID jid, DiscoveryInfo browseResult)
		{}

		public void discoveryItemResult(JID jid, DiscoveryItem browseResult)
		{
			cmbRoom.removeAllItems();
			rooms.clear();
			if (browseResult.hasItems())
			{
				for (Iterator i = browseResult.getItems(); i.hasNext();)
				{
					DiscoveryItem item = (DiscoveryItem) i.next();
					cmbRoom.addItem(item.getName());
					rooms.add(item.getJID());
				}
				if (takeFocus)
				{
					cmbRoom.setSelectedIndex(0);
					cmbRoom.requestFocusInWindow();
					cmbRoom.getEditor().selectAll();
					takeFocus = false;
				}
			}
			
		}
	}

	private class ServerListener implements DiscoveryListener
	{
		public void discoveryInfoResult(JID jid, DiscoveryInfo item)
		{
//			not all servers support feature yet so use conference category
			if("conference".equals(item.getCategory()))cmbChatRoomServer.addItem(new ChatServer(jid,item.getName()));
			
//			if (item.hasFeatures())
//			{
//				for (Iterator i = item.getFeatures(); i.hasNext();)
//				{
//					String feature = (String) i.next();
//					System.out.println(feature);
//					if (feature.equals("http://jabber.org/protocol/muc"))
//					{
//						cmbChatRoomServer.addItem(new ChatServer(jid,item.getName()));
//					}
//				}
//			}
		}

		public void discoveryItemResult(JID jid, DiscoveryItem browseResult)
		{
			if (browseResult.hasItems())
			{
				for (Iterator i = browseResult.getItems(); i.hasNext();)
				{
					DiscoveryItem item = (DiscoveryItem) i.next();
					backend.getInfo(item.getJID(), this);
				}
			}
		}
	}

	private class NicListener implements DiscoveryListener
	{
		public void discoveryInfoResult(JID jid, DiscoveryInfo item)
		{
		// Handle nic identity packet
		}

		public void discoveryItemResult(JID jid, DiscoveryItem browseResult)
		{}
	}
	
	class ChatServer
	{
		private String name;
		private String server;
		
		public ChatServer(JID jid, String name)
		{
			server = jid.getDomain();
			if(name==null || name.equals("")) this.name = server;
			else this.name = name;
		}
		
		public String getServer()
		{
			return server;
		}
		
		public String toString()
		{
			return name;
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
