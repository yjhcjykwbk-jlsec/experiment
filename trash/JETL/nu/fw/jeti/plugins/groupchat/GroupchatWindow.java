package nu.fw.jeti.plugins.groupchat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

import nu.fw.jeti.backend.roster.NormalJIDStatus;
import nu.fw.jeti.backend.roster.Roster;
import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.jabber.elements.MessageBuilder;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCOwnerConfigure;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCSetList;
import nu.fw.jeti.plugins.groupchat.elements.XMUCPassword;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUser;
import nu.fw.jeti.ui.AddContact;
import nu.fw.jeti.ui.ChatSplitPane;
import nu.fw.jeti.ui.SendMessage;
import nu.fw.jeti.ui.StatusButton;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 * @author Martin Forssen
 *
 * Does not implement
 *  - History (6.3.12)
 *  - Registering with a room (6.11)
 *  - Nic discovery (6.12)
 *
 * Nice to implement
 *  - Nicer showing of date for old messages, means changing ChatSplitPane
 * add discovery of groupchat room features (6.1)
 */

// TODO
// Bookmark management

public class GroupchatWindow extends JFrame implements PresenceListener 
{
    private static final int CHAT_DIVIDER_DEFAULT = 280;

	private ChatSplitPane chatSplitPane;
    private JSplitPane splitPane;
	private JID roomJID;
	private JID fullRoomJID;
	private String me;
	private Backend backend;
	private String toName;
	private String type;
	private MyListModel model;
	private List userNames = new ArrayList();
    private JMenu ownerMenu = new JMenu();
    private JMenu userMenu = new JMenu();
    private JMenu membersMenu = new JMenu();
    private JList lstActors;
    private JPopupMenu actionMenu;
    private JButton btnSend;

    private StatusButton btnStatus;
	private String message;
	private int status;
    private XMUCUser myXMUC = null;
    private boolean isOwner = false;
    private boolean mayManage = false;
    private JTextField txtSubject = new JTextField(I18N.gettext("groupchat.Subject"));
	private boolean isMUCRoom=true;
		
	public GroupchatWindow(Backend backend, JID jid)
	{
		Plugin.addGroupchat(jid,this);
		fullRoomJID =jid;
		roomJID = new JID(jid.getUser(),jid.getDomain());
		backend.addPresenceListener(roomJID,this);
		toName = roomJID.toString(); 
		this.backend = backend;

		me = jid.getResource();
		type = "images";
		chatSplitPane = new ChatSplitPane(backend, roomJID,toName
        									, me, null, true, this,userMenu);
        init();
	}

	public void appendMessage(final Message message)
	{
		String name = message.getFrom().getResource();
		if(message.getSubject() != null)
		{
			Runnable updateAComponent = new Runnable()
			{
				public void run()
				{
					txtSubject.setText(MessageFormat.format(I18N.gettext("groupchat.Subject {0}"),new Object[]{message.getSubject()}));
				}
			};
			SwingUtilities.invokeLater(updateAComponent);
		}
		else if (name == null)
		{
		 	chatSplitPane.appendSystemMessage(message.getBody());
		} else {
            chatSplitPane.appendMessage(message,name);
        }
	}
	

	private void init()
	{
		txtSubject.setEditable(false);
		txtSubject.setBorder(null);
		txtSubject.setHorizontalAlignment(SwingConstants.CENTER);
		createMenu();
		
		setIconImage(StatusIcons.getStatusIcon(Presence.NONE,type).getImage());
        btnSend = new JButton();
        I18N.setTextAndMnemonic("groupchat.Send",btnSend);
        btnSend.setEnabled(false);
        chatSplitPane.getTextInput().setEnabled(false);
        btnSend.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				chatSplitPane.send(); 
			}
		});
		//chat panel
		JPanel pnlChat = new JPanel(new BorderLayout());
		pnlChat.setBorder(null);
		JPanel jPanel1 = new JPanel();
        jPanel1.add(btnSend, null);
		//JPanel jPanel2 = new JPanel();
		//jPanel2.add(txtSubject); 
		pnlChat.add(txtSubject, BorderLayout.NORTH);
		pnlChat.add(jPanel1, BorderLayout.SOUTH);
		pnlChat.add(chatSplitPane, BorderLayout.CENTER);
		 
		
		//control + online panel
		final JPanel pnlStatus = new JPanel(new BorderLayout());
		pnlStatus.setMinimumSize(new Dimension(0,22));
		pnlStatus.setBorder(null);
		//list of persons in groupchat
		model = new MyListModel(); 
        lstActors = new JList(model); 
        lstActors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstActors.setCellRenderer(new MyListRenderer());
		lstActors.setFocusable(false);
		ToolTipManager.sharedInstance().registerComponent(lstActors);
		JScrollPane scrollpane = new JScrollPane(lstActors);
		pnlStatus.add(scrollpane,BorderLayout.CENTER);
		
		final Box pnlControl = Box.createVerticalBox(); 
	
        actionMenu = new JPopupMenu();
        MouseListener popupListener = new MyPopupListener();
        lstActors.addMouseListener(popupListener);
		
        btnStatus = new StatusButton(backend, fullRoomJID);
		pnlControl.add(btnStatus); 
		pnlStatus.add(pnlControl,BorderLayout.SOUTH); 				 

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   pnlChat, pnlStatus);
		splitPane.setBorder(null);  
		getContentPane().add(splitPane,BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doLeave();
            }
        });
		
		chatSplitPane.getTextInput().addKeyListener(new KeyAdapter()
		{
			private String searchText="jeti";
			private int lastLocation=0;
			
			public void keyPressed(KeyEvent e)
			{
				if ((e.getModifiers() == KeyEvent.CTRL_MASK))
				{
					if (e.getKeyCode() == KeyEvent.VK_N)
					{
						Actor a = (Actor)lstActors.getSelectedValue();
						if(a!=null)	addTextToSend("@" + a.getJID().getResource() + ": ");				
					}
					else if (e.getKeyCode() == KeyEvent.VK_M)
					{
						try
						{
							JTextPane text = chatSplitPane.getTextInput();
							int wordEnd =text.getCaretPosition();
							
							Document doc = text.getDocument();
							Element line = Utilities.getParagraphElement(text, wordEnd);
							int lineStart = line.getStartOffset();
							//int lineEnd = Math.min(line.getEndOffset(), doc.getLength());
							String s = doc.getText(lineStart, wordEnd);
							int wordStart =s.length()-1;
							for(;wordStart>=0;wordStart--)
							{
								char c = s.charAt(wordStart);
								if(Character.isSpace(c))break;
							}
							wordStart = lineStart+wordStart+1;
							//System.out.println(wordStart);
							//TODO 
							//int wordStart = Utilities.getWordStart(text,wordEnd);
							
							
							String word = doc.getText(wordStart, wordEnd-wordStart);
							//word = word.trim();
							if(word.length() == 0)
							{
								//Document d = chatSplitPane.getTextInput().getDocument();
								//chatSplitPane.getTextInput().setCaretPosition(d.getLength());
								//chatSplitPane.getTextInput().moveCaretPosition(d.getLength());
								Actor a = (Actor)lstActors.getSelectedValue();
								if(a!=null)	addTextToSend(a.getJID().getResource());
							}
							else
							{
								word = word.toLowerCase();
								if(!word.startsWith(searchText))
								{
									searchText=word;
									lastLocation=0;
								}
								searchNick(wordStart,wordEnd);
							}
						}catch(BadLocationException e2)
						{
							e2.printStackTrace();
						}
						
						
					}
				}
			}
			
			private void searchNick(int wordStart, int wordEnd)
			{
				if(lastLocation >=userNames.size())lastLocation=0;
				for(int i =lastLocation;i<userNames.size();i++)
				{
					String nick = (String)userNames.get(i);
					
					if(nick.toLowerCase().startsWith(searchText))
					{
						Document d = chatSplitPane.getTextInput().getDocument();
						try
						{
							d.remove(wordStart,wordEnd-wordStart);
							d.insertString(wordStart,nick,null);
							//chatSplitPane.getTextInput().setCaretPosition(d.getLength());
							//chatSplitPane.getTextInput().moveCaretPosition(d.getLength());
						} catch (BadLocationException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						lastLocation=i+1;
						return;
					}
				}
				//Document d = chatSplitPane.getTextInput().getDocument();
				//chatSplitPane.getTextInput().setCaretPosition(d.getLength());
				//chatSplitPane.getTextInput().moveCaretPosition(d.getLength());
				lastLocation =0;
			}
		});
		
		
        setSize(Preferences.getInteger("groupchat","chatWidth",590),
                Preferences.getInteger("groupchat","chatHeigth",460));
        chatSplitPane.setDividerLocation(
            Preferences.getInteger("groupchat","chatDivider",CHAT_DIVIDER_DEFAULT));
        splitPane.setDividerLocation(
            Preferences.getInteger("groupchat","divider",460));
    }

 	private void createMenu()
	{
		JMenuBar menuBar =new JMenuBar(); 
		JMenu menu = new JMenu();
		I18N.setTextAndMnemonic("menu", menu);
		//User menu
        I18N.setTextAndMnemonic("groupchat.User",userMenu);
        
        Map menuItems = backend.getMain().getRosterMenuItems();
        if (menuItems != null)
   		{
        	String menuString = I18N.gettext("messagelog.Show_MessageLog");
			if(menuItems.containsKey(menuString))
        	{
        		JMenuItem item = new JMenuItem(menuString); 
   				final RosterMenuListener listener = 
   					(RosterMenuListener) menuItems.get(menuString);
   				item.addActionListener(new java.awt.event.ActionListener()
   				{
   					public void actionPerformed(ActionEvent e)
   					{
   						listener.actionPerformed(
   								new NormalJIDStatus(roomJID,null) , null);
   					}
   				});
   				userMenu.add(item);
   			}
   		}
   		JMenuItem menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Change_nickname",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nick = JOptionPane.showInputDialog(
                    GroupchatWindow.this,
                    I18N.gettext("groupchat.Change_nickname_to"),
                    I18N.gettext("groupchat.Change_nickname"),
                    JOptionPane.QUESTION_MESSAGE);
				if(nick == null) return;
				
				if(!isMUCRoom)
				{//old groupchat room hack
					me = nick;
					fullRoomJID = new JID(roomJID.getUser(),roomJID.getDomain(),nick);
					btnStatus.setJID(fullRoomJID);
				}
				
                backend.send(new Presence(new JID(roomJID.getUser(),
                                                  roomJID. getDomain(),
                                                  nick), "available"));
			}
		});
        menuItem.setEnabled(false);
        // AppGate
        userMenu.add(menuItem);

		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("groupchat.Change_Subject",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String subject = JOptionPane.showInputDialog(
                    GroupchatWindow.this,
                    I18N.gettext("groupchat.Change_room_subject_to"),
                    I18N.gettext("groupchat.Change_subject_to"),
                    JOptionPane.QUESTION_MESSAGE);
				if(subject == null) return;
				MessageBuilder builder = new MessageBuilder();
				builder.subject = subject;
				builder.to = roomJID; 
				builder.type = "groupchat";
				backend.send(builder.build()); 
                // Maybe we should handle any errors returned by this. But
                // Jeti currently throws away all error Messages. And the
                // current behavior that the change simply does not happen
                // is not all that bad either.
			}
		});
        menuItem.setEnabled(false);
        userMenu.add(menuItem);
        userMenu.addSeparator();

        menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Invite_user",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GroupchatInvite.inviteToRoom(backend, roomJID);
            }
        });
        menuItem.setEnabled(false);
        userMenu.add(menuItem);
        userMenu.addSeparator();

		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("groupchat.Bookmark_room",
                                menuItem, true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Plugin.addBookmark(roomJID,fullRoomJID.getResource());
			}
		});
        userMenu.add(menuItem);

		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("groupchat.Exit_Room",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                doLeave();
			}
		});
        userMenu.add(menuItem);					
        menuBar.add(userMenu); 

        //members menu
        I18N.setTextAndMnemonic("groupchat.Members", membersMenu);
        menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Manage_Members",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ManageMembers(backend, roomJID, model).show();
            }
        });
        membersMenu.add(menuItem);
        menuItem.setEnabled(false);
        menuBar.add(membersMenu);

		//Owner menu
        I18N.setTextAndMnemonic("groupchat.Owner",ownerMenu);
		menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Configure_Room",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new IQMUCOwnerConfigure(backend, roomJID);
            }
        });
        menuItem.setEnabled(false);
        ownerMenu.add(menuItem);
        menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Destroy_Room",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DestroyDialog(backend, roomJID).show();
			}
		});
        menuItem.setEnabled(false);
        ownerMenu.add(menuItem);
        menuBar.add(ownerMenu);
        setJMenuBar(menuBar);
	}

	private void doLeave() 
    {
		Plugin.removeGroupchat(roomJID);
		backend.send(new Presence(fullRoomJID,"unavailable" )); 
		Preferences.putInteger("groupchat","chatHeigth",getHeight());
		Preferences.putInteger("groupchat","chatWidth",getWidth());
		if(btnSend.isEnabled())
		{
			Preferences.putInteger("groupchat","chatDivider",chatSplitPane.getDividerLocation());
		}
        Preferences.putInteger("groupchat","divider",splitPane.getDividerLocation());
		dispose();
	}

    /*
     * Potential entries:
     *  Grant/revoke voice
     *  Kick user
     *  Ban user
     *  Revoke membership
     *  Grant/revoke moderator
     *  Grant/revoke admin
     *  Grant/revoke owner
     */
    private void configureActionMenu() {
        actionMenu.removeAll();

        final Actor a = (Actor)lstActors.getSelectedValue();
        String objectName;
        if (a.getFullJID() != null) {
            objectName = a.getFullJID().toStringNoResource();
        } else {
            objectName = a.getJID().getResource();
        }
        actionMenu.setLabel(objectName);

        JMenuItem item;

        item = new JMenuItem(objectName);

        if (me.equals(a.getJID().getResource())) {
            item = new JMenuItem(
                I18N.gettext("groupchat.Myself"));
            actionMenu.add(item);
            return;
	}
	
        // Send private message
        item = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Private_message",
                                item, true);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JID jid = a.getJID();
                new SendMessage(backend, jid,
                                jid.getUser() + "/" + jid.getResource()
                    ).show(); 
            }
        });
        actionMenu.add(item);
        
        item = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Private_chat",item,true);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.getMain().startChat(a.getJID());
			}
		});
		actionMenu.add(item);
		
       
        item = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.rostermenu.Local_Time",
                                item, true);
        item.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                backend.send(new InfoQuery(a.getJID(), "get", new IQTime()));
                
            }
        });
        actionMenu.add(item);
   
        item = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.rostermenu.Local_Version",
                                item, true);
        item.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                backend.send(new InfoQuery(a.getJID(), "get", new IQVersion()));
            }
        });
        actionMenu.add(item);
       
        
        

        if (a.getFullJID() != null
            && null == backend.getJIDStatus(a.getFullJID())) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("main.main.jetimenu.Add_Contact",
                                    item,true);
            item.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    new AddContact(a.getFullJID(), GroupchatWindow.this,
                                   backend).show();
                }
            });
            actionMenu.add(item);
        }
        Map menuItems = backend.getMain().getRosterMenuItems();
        if (menuItems != null)
		{
			for (Iterator i = menuItems.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				if(((String) entry.getKey()).equals(I18N.gettext("filetransfer.Transfer_File")+ "...")) continue; //fix filetransfer with groupchat
				item = new JMenuItem((String) entry.getKey());
				final RosterMenuListener listener = (RosterMenuListener) entry.getValue();
				item.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						listener.actionPerformed(a , null);
					}
				});
				actionMenu.add(item);
			}
		}

		//add nick to text output
	    item = new JMenuItem();
        I18N.setTextAndMnemonic("groupchat.Add_Nickname_to_Output",item);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addTextToSend("@" + a.getJID().getResource() + ": ");
			}
		});
		actionMenu.add(item);
		
		
        // Grant/Revoke Voice
        String nick = a.getJID().getResource();
        if (mayManage && a.getRole() == XMUCUser.VISITOR) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Grant_Voice",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, false, XMUCUser.PARTICIPANT));
            actionMenu.add(item);
        } else if (mayManage
                   && a.getRole() == XMUCUser.PARTICIPANT
                   && a.getAffiliation() == XMUCUser.NONE) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Revoke_Voice",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, false, XMUCUser.VISITOR));
            actionMenu.add(item);
        }

        // Kick user
        if (mayManage && a.getAffiliation() == XMUCUser.NONE
            && a.getRole() != XMUCUser.MODERATOR) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Kick_User",item,true);
            item.addActionListener(
                new ActionMenuListener(nick, false, XMUCUser.NONE));
            actionMenu.add(item);
        }

        // Ban user
        if (mayManage && a.getAffiliation() != XMUCUser.OWNER
            && a.getAffiliation() != XMUCUser.ADMIN
            && a.getFullJID() != null) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Ban_User", item,true);
            item.addActionListener(
                new ActionMenuListener(a.getFullJID(), true,XMUCUser.OUTCAST));
            actionMenu.add(item);
        }

        // Revoke membership
        if (mayManage
            && a.getAffiliation() == XMUCUser.MEMBER) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Revoke_Membership",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, true, XMUCUser.NONE));
            actionMenu.add(item);
        }

        // Grant/revoke moderator
        if (mayManage
            && (a.getRole() == XMUCUser.PARTICIPANT
                || a.getRole() == XMUCUser.VISITOR)) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Grant_Moderator",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, false, XMUCUser.MODERATOR));
            actionMenu.add(item);
        } else if (mayManage && a.getRole() == XMUCUser.MODERATOR
            && (a.getAffiliation() == XMUCUser.NONE
                || a.getAffiliation() == XMUCUser.MEMBER)) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Revoke_Moderator",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, false,
                                       XMUCUser.PARTICIPANT));
            actionMenu.add(item);
        }

        // Grant/revoke admin
        if (mayManage && a.getFullJID() != null
            && (a.getAffiliation() == XMUCUser.NONE
                || a.getAffiliation() == XMUCUser.MEMBER)) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Grant_Admin",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(a.getFullJID(), true,
                                       XMUCUser.ADMIN));
            actionMenu.add(item);
        } else if (mayManage && a.getAffiliation() == XMUCUser.ADMIN) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Revoke_Admin",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(nick, true, XMUCUser.MEMBER));
            actionMenu.add(item);
        }

        // Grant/revoke owner
        if (isOwner && a.getFullJID() != null
            && (a.getAffiliation() == XMUCUser.NONE
                || a.getAffiliation() == XMUCUser.MEMBER
                || a.getAffiliation() == XMUCUser.ADMIN)) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Grant_Owner",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(a.getFullJID(), true,
                                       XMUCUser.OWNER));
            actionMenu.add(item);
        } else if (isOwner && a.getAffiliation() == XMUCUser.OWNER
            && a.getFullJID() != null) {
            item = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Revoke_Owner",
                                    item,true);
            item.addActionListener(
                new ActionMenuListener(a.getFullJID(), true,
                                       XMUCUser.ADMIN));
            actionMenu.add(item);
        }
        
        if (0 == actionMenu.getComponentCount()) {
            item = new JMenuItem(
                I18N.gettext("groupchat.No_Actions"));
            actionMenu.add(item);
        }
    }
   	
    private void doPasswordEntry() {
        String pw = JOptionPane.showInputDialog(
            GroupchatWindow.this,
            I18N.gettext("groupchat.Password_required_to_join_this_room"),
            I18N.gettext("groupchat.Password"),
            JOptionPane.QUESTION_MESSAGE);
        if (pw == null || pw.length() == 0) {
            Plugin.removeGroupchat(roomJID);
            dispose();
            return;
        }
        backend.send(new Presence(fullRoomJID,"available",
                                  new XMUCPassword(pw)));
	}
	
	public void presenceChanged(final Presence presence)
	{
        if(presence.getType().equals("error")) {
            if (401 == presence.getErrorCode()) {
                doPasswordEntry();
                return;
            }
			chatSplitPane.appendSystemMessage(presence.getErrorDescription());
			return;	
		}
		final JID jid = presence.getFrom();
		XMUCUser temp=null;
        if(presence.hasExtensions()) {
            for(Iterator i = presence.getExtensions();i.hasNext();) {
				Object o = i.next();
                if(o instanceof XMUCUser) {
					temp =(XMUCUser)o;
					break; 
				}				
			}
		}
		final XMUCUser xMucUser = temp;
        if (me.equals(jid.getResource())) {//temp
            if(temp!=null && temp.getStatusCode()==303) {//own nickname change
				//me = temp.getJID().getResource();
            	me = temp.getNick();
                fullRoomJID = new JID(roomJID.getUser(),
                                      roomJID.getDomain(),me);
                btnStatus.setJID(fullRoomJID);
            } else {
                ownPresenceChanged(presence, xMucUser);
                if (temp!=null && temp.getStatusCode()==201) {
                    new IQMUCOwnerConfigure(backend, roomJID);
                }
            }
        }
        if(presence.getType().equals("unavailable")) {
            Runnable updateAComponent = new Runnable() {
                public void run() {
                    model.removeElement(new Actor(jid));
                    userNames.remove(jid.getResource());
                    if (Preferences.getBoolean("groupchat","showPresence",true)) {
                        String text = MessageFormat.format(
                            I18N.gettext("groupchat.{0}_has_left"),
                            new Object[]{jid.getResource()});
                        chatSplitPane.appendSystemMessage(text);
                    }
                }
            };
            SwingUtilities.invokeLater(updateAComponent);
        } else {
            Runnable updateAComponent = new Runnable() {
                public void run() {
                    Actor status = new Actor(jid);
                    int index = model.indexOf(status);
                    String text;
                    if(index == -1) {
                        status.update(presence,xMucUser);
                        model.addElement(status);
                        userNames.add(jid.getResource());
                        text = I18N.gettext(
                            "groupchat.{0}_has_entered_the_room");
                    } else {
                        status =(Actor) model.getElementAt(index);
                        text = getChangeMessage(status, presence);
                        status.update(presence,xMucUser);
                        model.updateElement(index);
                    }
                    if (text != null
                        && Preferences.getBoolean("groupchat","showPresence",
                                                  true)) {
                        chatSplitPane.appendSystemMessage(
                            MessageFormat.format(
                                text, new Object[]{jid.getResource()}));
                    }
                }
            };
            SwingUtilities.invokeLater(updateAComponent);
        }
    }
	
    private String getChangeMessage(Actor old, Presence n) {
        if (old.getShow() != n.getShow()) {
            String text = MessageFormat.format(
                I18N.gettext("groupchat.{0}_changed_state_to_{1}"),
                new Object[]{"{0}", Presence.toLongShow(n.getShow())});
            if (n.getStatus() != null) {
                text += '\n' + MessageFormat.format(
                    I18N.gettext("groupchat.with_status_message_{0}")
								 , new Object[]{n.getStatus()});
            }
            return text;
        }
        if(old.getStatus()!=null && !(old.getStatus().equals(n.getStatus())))
        {
        	String text = MessageFormat.format(
        	I18N.gettext("groupchat.{0}_changed_status_message_to_{1}"),
            new Object[]{"{0}", n.getStatus()});
        	return text;
        }
        return null;
    }

    public void ownPresenceChanged(Presence presence, XMUCUser xMucUser)
	{
		status = presence.getShow();   
		message = presence.getStatus();
        btnStatus.ownPresenceChanged(status, message);
        myXMUC = xMucUser;
        Runnable updateAComponent = new Runnable() {
            public void run() {
                chatSplitPane.getTextInput().requestFocusInWindow();
 				ImageIcon icon = StatusIcons.getStatusIcon(status);
                if (status == Presence.UNAVAILABLE) {
                    backend.removePresenceListener(roomJID);
                }
 				setIconImage(icon.getImage());
                if (myXMUC != null) {
                    if (status == Presence.UNAVAILABLE
                        && myXMUC.getStatusCode() == 307) {
                        notifyKicked();
                    }
                    isOwner = false;
                    if (XMUCUser.OWNER == myXMUC.getAffiliation()) {
                        isOwner = true;
                    }
                    for (int i=0; i<ownerMenu.getItemCount(); i++) {
                        ownerMenu.getItem(i).setEnabled(isOwner);
                    }

                    mayManage = false;
                    if (isOwner
                        || XMUCUser.ADMIN == myXMUC.getAffiliation()
                        || XMUCUser.MODERATOR == myXMUC.getRole()) {
                        mayManage = true;
                    }
                    for (int i=0; i<membersMenu.getItemCount(); i++) {
                        membersMenu.getItem(i).setEnabled(mayManage);
                    }

                    for (int i=0; i<userMenu.getItemCount(); i++) {
                        if (userMenu.getItem(i) != null) {
                            userMenu.getItem(i).setEnabled(true);
                        }
                    }

                    boolean sendEnabled = true;
                    if (XMUCUser.VISITOR == myXMUC.getRole()
                        || XMUCUser.NONE == myXMUC.getRole()) {
                        sendEnabled = false;
                    }
                    setSendEnabled(sendEnabled);
                }
                else
                {//non muc room
                	isMUCRoom=false;
                	for(int i=0;i<userMenu.getItemCount();i++)
                	{
                		if(userMenu.getItem(i)!=null)
                		{
                			userMenu.getItem(i).setEnabled(true);
                		}
                	}
                	boolean sendEnabled = true;
	                setSendEnabled(sendEnabled);
                }
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
	}
	
    private void notifyKicked() {
        String reason = I18N.gettext("groupchat.None");
        String title = I18N.gettext("groupchat.Thrown_out_from_room");

        if (myXMUC != null) {
            if (301 == myXMUC.getStatusCode()) {
                title = I18N.gettext("groupchat.Banned_from_room");
            } 
            if (myXMUC.getReason() != null) {
                reason = myXMUC.getReason();
            }
        }
        String fmtReason = 
            MessageFormat.format(I18N.gettext("groupchat.Reason_{0}"),
                                 new Object[] {reason});
        Popups.messagePopup(fmtReason, roomJID + ": " + title);
        chatSplitPane.appendSystemMessage(title + ". " + fmtReason);
        return;
	}
    
    
    public void setSendEnabled(boolean enabled) 
	{
        if (enabled == btnSend.isEnabled()) {
            return;
        }
        btnSend.setEnabled(enabled);
        chatSplitPane.getTextInput().setEnabled(enabled);
        if (enabled) {
            chatSplitPane.setDividerLocation(
                Preferences.getInteger("groupchat","chatDivider",
                                       CHAT_DIVIDER_DEFAULT));
        } else {
			Preferences.putInteger("groupchat","chatDivider",
                                   chatSplitPane.getDividerLocation());
            chatSplitPane.setDividerLocation(1.0);
        }
	}
	
	public void addTextToSend(String text) 
	{
		Document d = chatSplitPane.getTextInput().getDocument();
		try
		{
			d.insertString(d.getLength(),text,null);
		} catch (BadLocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class MyListModel extends AbstractListModel 
	{//add update to listmodel
		private List list = new ArrayList();
		
		public int getSize()
		{
			return list.size();
		}
				
		public Object getElementAt(int index)
		{
			return list.get(index);
		}
		
		public int indexOf(Actor element)
		{
			return list.indexOf(element);
		}		
		
		public void removeElement(Actor element)
		{
			int index = list.indexOf(element);
			list.remove(element);
			fireIntervalRemoved(this, index, index);
		}
		
		public void addElement(Actor element)
		{
			int end = list.size();
            list.add(element);
			fireIntervalAdded(this,end ,end);
		}
		
		public void updateElement(int index)
		{
			fireContentsChanged(this, index, index);
		}
	}
    
    /*
     * MyPopupListener
     */
    class MyPopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
            if (SwingUtilities.isLeftMouseButton(e))
			{
            	int index = lstActors.locationToIndex(e.getPoint());
            	lstActors.setSelectedIndex(index);
            	if(e.getClickCount()==2)
            	{
            		Actor a = (Actor)lstActors.getSelectedValue();
            		backend.getMain().startChat(a.getJID());
            	}
            	//Actor a = (Actor)list.getSelectedValue();
            	//chatSplitPane.addTextToSend("@" + a.getJID().getResource() + " ");
			}
            if(SwingUtilities.isMiddleMouseButton(e))
            {
            	int index = lstActors.locationToIndex(e.getPoint());
            	lstActors.setSelectedIndex(index);
            	Actor a = (Actor)lstActors.getSelectedValue();
            	addTextToSend("@" + a.getJID().getResource() + ": ");
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int index = lstActors.locationToIndex(e.getPoint());
                lstActors.setSelectedIndex(index);
                configureActionMenu();
                actionMenu.show(e.getComponent(),
                                e.getX(), e.getY());
            }
        }
    }

    /*
     * ActionMenuListener
     */
    private class ActionMenuListener implements ActionListener {
        private JID jid;
        private boolean aff;
        private int value;
        private String nick;

        public ActionMenuListener(JID jid, boolean aff, int value) {
            this.jid = jid;
            this.aff = aff;
            this.value = value;
        }

        public ActionMenuListener(String nick, boolean aff, int value) {
            this.nick = nick;
            this.aff = aff;
            this.value = value;
        }

        public void actionPerformed(ActionEvent e) {
            String reason = JOptionPane.showInputDialog(
                lstActors,
                I18N.gettext("groupchat.Reason_(optional)"),
                I18N.gettext("groupchat.Reason"),
                JOptionPane.QUESTION_MESSAGE);
            if (reason == null) {
                return;
            }

            if (jid != null) {
                new IQMUCSetList(backend, roomJID, aff, value, jid, reason);
            } else {
                new IQMUCSetList(backend, roomJID, aff, value, nick, reason);
            }
        }
    }

	class MyListRenderer implements ListCellRenderer 
	{
		private JLabel renderer;
				
		public MyListRenderer() 
		{
			renderer = new JLabel(); 
			renderer.setOpaque(Preferences.getBoolean("jeti","bmw",true));
			renderer.setBackground(UIManager.getColor("Tree.selectionBackground"));
			renderer.setForeground(UIManager.getColor("Tree.textForeground"));
		}
		  
		public Component getListCellRendererComponent(JList tree,Object value,int row,boolean sel,boolean hasFocus)
		{
			if(sel)
			{
				renderer.setOpaque(true);
			}
			else
			{ 
				renderer.setOpaque(false);  
			}
			renderer.setText(value.toString());
			return(makeComponent((Actor)value));
		}

		private Component makeComponent(Actor actor)
		{
			int show = actor.getShow();
			Color color;
			switch(actor.getRole())
			{
				case XMUCUser.VISITOR: color = Color.DARK_GRAY; break;
				case XMUCUser.MODERATOR: color = Color.BLUE; break;
				default: color = Color.black; 
			}
			ImageIcon icon = StatusIcons.getStatusIcon(show,actor.getType());
			switch(actor.getAffiliation())
			{
				case XMUCUser.OWNER: icon =getImage("owner.gif",icon) ; break;
				case XMUCUser.ADMIN: icon =getImage("admin.gif",icon) ; break;
				case XMUCUser.MEMBER: icon =getImage("admin.gif",icon) ; break;
			}
			renderer.setIcon(icon);			
			renderer.setForeground(color);
			renderer.setToolTipText(getToolTipText(actor));
			return renderer; 
		}

		private ImageIcon getImage(String affiliation,ImageIcon icon)
		{
			
			Image image = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
			Image image2= new ImageIcon(getClass().getResource(affiliation)).getImage();
			Graphics g = image.getGraphics();
			g.drawImage(icon.getImage(), 0, 0, null);
			g.drawImage(image2, 6, 6, null);
			return new ImageIcon(image);
		}
		
		private String getToolTipText(Actor actor)
		{
        // XXX
        String tip = "<HTML><P> Status: "+Presence.toLongShow(actor.getShow());
        if (actor.getStatus() != null) {
            tip += "</p><p> Status Message: " + actor.getStatus();
        }
        if (actor.getFullJID() != null) {
            tip += "</p><p> JID: " +  actor.getFullJID();
		}
        return tip + "</p></HTML>";
        }
    }

		
    public class Actor extends NormalJIDStatus
	{
		private int role;
		private int affiliation;
		private JID fullJID;
		
		Actor(JID jid)
		{
			super(jid,jid.getResource());
		}
		
		public void update(Presence presence,XMUCUser muc)
		{
			updatePresence(presence);
			if(muc!=null)
			{	
				role=muc.getRole();
				affiliation=muc.getAffiliation();
				fullJID = muc.getJID();
                if (fullJID != null) {
                    JIDStatus jidStatus = Roster.getJIDStatus(fullJID);
                    if (jidStatus != null && jidStatus.getNick() != null) {
                    	if(Preferences.getBoolean("groupchat","showNick",true))
                    	{
                    		setNick(getNick() + " (" + jidStatus.getNick() + ")");
                    	}
                    }
                }
			}
		}
		
		public int getAffiliation()
		{
			return affiliation;
		}
		
		public int getRole()
		{
			return role;
		}
		
		public JID getFullJID()
		{
			return fullJID;
		}
		
		public JID getCompleteJID()
		{
			if(fullJID!=null)return fullJID;
			return getJID();
		}
		
				
		public boolean equals(Object object)
		{//equals resource ipv jid
			if (object instanceof Actor)
			{
				Actor temp = (Actor)object;
				return temp.getJID().getResource().equals(getJID().getResource());
			}
			else return false;
		} 
		
		//implement hashcode?
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
