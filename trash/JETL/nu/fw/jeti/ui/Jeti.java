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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.roster.JIDStatusGroup;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.events.*;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.ui.models.RosterTreeModel;
import nu.fw.jeti.util.*;


/**
 * @author E.S. de Boer
 */

public class Jeti extends JPanel implements MessageListener, PresenceListener, StatusChangeListener, PreferenceListener, MessageEventListener
{
	private RosterTree onlinePanel;
	private RosterTree offlinePanel;
	private List chatWindows = new ArrayList();
	private JPopupMenu popupMenu;
    private JMenu popdownMenu;
	private JToggleButton btnJeti;
    private StatusButton btnStatus;
	private JPanel pnlMenuButtons;
	private JPanel empty =new JPanel();
	private JScrollPane jScrollPaneTop;
	private JPanel pnlRoster;
	private JSplitPane jSplitPane1;
	private JScrollPane scrollPaneBottom;
	private ServerTree serverPanel;
	private int status;
	private String message = "";
	private List menuItems;
	private JetiFrame jetiFrame;
	private JetiDialog jetiDialog;
	private boolean heightInvalid=true;
	private Map rosterMenuItems= new HashMap(10);

	private Backend backend;

    private static final String TITLE = "JETI";

	public Jeti(Backend backend,Container container)
	{
		if(container!=null)backend.setMain(this,null,null);
		else
		{
			jetiFrame = new JetiFrame();
			backend.setMain(this,jetiFrame,jetiFrame);
			if(Preferences.getBoolean("jeti","showNotInTaskbar",false))
			{
				jetiFrame.setIconImage(StatusIcons.getOfflineIcon().getImage());
				jetiDialog = new JetiDialog(jetiFrame);
				backend.setMain(this,jetiDialog,null);
			}
		}
				
		this.backend = backend;

		backend.addListener(MessageListener.class, this);
		backend.addListener(PresenceListener.class, this);
		backend.addListener(StatusChangeListener.class, this);
		backend.addListener(PreferenceListener.class, this);
		backend.addListener(MessageEventListener.class, this);

		RosterTreeModel model = new RosterTreeModel();
		backend.addListener(RosterListener.class, model);
		TreeModel offlineModel=model;
		TreeModel onlineModel=model;
		
		if ( System.getProperty("os.name").startsWith("Mac")) {
		    onlineModel = new TreeModelFilter(model, new OnlineSelector());
		    offlineModel = new TreeModelFilter(model, new OfflineSelector());
		}
	    onlinePanel = new RosterTree(backend, this, true,onlineModel);
		offlinePanel = new RosterTree(backend, this, false,offlineModel);
		            
		serverPanel = new ServerTree(backend, this);
	}

	public void init()
	{
		setLayout(new BorderLayout());
		btnStatus = new StatusButton(backend, this);
		pnlMenuButtons = new JPanel();
		jScrollPaneTop = new JScrollPane();
		pnlRoster = new JPanel();
		jSplitPane1 = new JSplitPane();
		scrollPaneBottom = new JScrollPane();
		boolean opaque = Preferences.getBoolean("jeti","bmw",true);
		pnlRoster.setOpaque(opaque);
		onlinePanel.setOpaque(opaque);
		offlinePanel.setOpaque(opaque); 
		serverPanel.setOpaque(opaque);
		empty.setOpaque(opaque);
		empty.setBackground(Color.WHITE);
				
		Font bold =(UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD));
		onlinePanel.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), I18N.gettext("main.main.Online"),TitledBorder.LEFT,TitledBorder.TOP,bold));
		offlinePanel.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), I18N.gettext("main.main.Offline"),TitledBorder.LEFT,TitledBorder.TOP,bold));
		jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jSplitPane1.setDividerSize(5);
		scrollPaneBottom.setMinimumSize(new Dimension(22, 0));
		jScrollPaneTop.getViewport().add(pnlRoster, null);
		pnlMenuButtons.setLayout(new BoxLayout(pnlMenuButtons, BoxLayout.X_AXIS));

		if (Preferences.getBoolean("jeti","menutop",false)) {
            createJetiMenu();
        } else {
            createBtnJeti();
            pnlMenuButtons.add(btnJeti, null);
        }
		pnlMenuButtons.add(btnStatus, null);
        add(jSplitPane1, BorderLayout.CENTER);

        jSplitPane1.add(scrollPaneBottom, JSplitPane.BOTTOM);
        scrollPaneBottom.getViewport().add(serverPanel, null);
        jSplitPane1.add(jScrollPaneTop, JSplitPane.TOP);
		add(pnlMenuButtons, java.awt.BorderLayout.SOUTH);
		pnlRoster.setLayout(new BorderLayout());
		pnlRoster.add(onlinePanel, BorderLayout.NORTH);
		
		if(Preferences.getBoolean("jeti","showoffline",true)) pnlRoster.add(offlinePanel, BorderLayout.CENTER);
		else pnlRoster.add(empty, BorderLayout.CENTER);

		if(jetiDialog!=null)jetiDialog.init(this);
		else if(jetiFrame!=null)jetiFrame.init(this);
		
		int divLoc = Preferences.getInteger("jeti","dividerLocation",-10);
		if(divLoc == -10) jSplitPane1.setDividerLocation(0.75);
		else jSplitPane1.setDividerLocation(divLoc);
		
		initMenu();
		
		updateLF();
	}

    private void createJetiMenu() {
        JMenuBar menuBar = new JMenuBar();
        popdownMenu = new JMenu("Jeti");
        popdownMenu.setMnemonic('J');
        menuBar.add(popdownMenu);
        jetiFrame.setJMenuBar(menuBar);
    }

    private void createBtnJeti()
    {
		btnJeti = new JToggleButton("Jeti");
		btnJeti.setMnemonic('J');
        btnJeti.setMaximumSize(new Dimension(500, 23));
		btnJeti.setMargin(new Insets(0, 0, 0, 0));
		btnJeti.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//popupMenu.show(Jeti.this, btnJeti.getX(), btnJeti.getY() + pnlMenuButtons.getY() - popupMenu.getHeight() + btnJeti.getHeight());
				  if (heightInvalid) {
				  	popupMenu.show(Jeti.this, 0, 0);
				  	popupMenu.setVisible(false);
		            heightInvalid = false;
		        }
				popupMenu.show(Jeti.this, btnJeti.getX(), btnJeti.getY() + pnlMenuButtons.getY() - popupMenu.getHeight() );
			}
		});
        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            //toggle toggle button
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                btnJeti.setSelected(false);
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {}
            public void popupMenuCanceled(PopupMenuEvent e)
            {}
        });
    }

	public void close()
	{
		Container w = backend.getMainWindow();
		if(w!=null && w instanceof Window)((Window)w).dispose();
		System.out.println("Exiting, closing connection please wait....");
		
		saveOpenGroups();
		backend.exit();
		backend=null;
		//if(!Start.applet) System.exit(1);
	}

	public JID askJID()
	{
		String server = JOptionPane.showInputDialog(this, I18N.gettext("main.popup.Chat_with"), I18N.gettext("main.popup.Chat_with_who?"), JOptionPane.QUESTION_MESSAGE);
		if (server == null || server.equals("")) return null;
		JID jid = null;
		try
		{
			jid = JID.checkedJIDFromString(server);
		}
		catch (InstantiationException ex)
		{
			Popups.errorPopup(ex.getMessage(), I18N.gettext("main.error.Wrong_Jabber_Identifier"));
			return null;
		}
		if (jid == null) return null;
		if (jid.getUser() == null) return null;
		return jid;
	}
	
	public void startChat(JID jid)
	{
		if (jid==null) return;
		ChatWindow chatWindow = new ChatWindow(backend, jid, backend.createThread());
		chatWindow.show();
		setChatWindowPosition(chatWindow);
		chatWindows.add(chatWindow);
	}
	
	public void addToMenu(JMenuItem menuItem)
	{
		if(menuItems==null) menuItems = new LinkedList();
		menuItems.add(menuItem);
        initMenu();
	}
	
	public Map getRosterMenuItems()
	{
		return rosterMenuItems; 
	}
	
	public void removeFromMenu(JMenuItem menuItem)
	{
		menuItems.remove(menuItem);
		if(menuItems.isEmpty()) menuItems = null;
		initMenu();
	}

	public void addToRosterMenu(String name, RosterMenuListener listener)
	{
		onlinePanel.addToMenu(name, listener);
		offlinePanel.addToMenu(name, listener);
		rosterMenuItems.put(name,listener);
	}
	
	public void removeFromRosterMenu(String name)
	{
		onlinePanel.removeFromMenu(name);
		offlinePanel.removeFromMenu(name);
		rosterMenuItems.remove(name);
	}
	
	public void addToOnlineRosterMenu(String name, RosterMenuListener listener)
	{
		onlinePanel.addToMenu(name, listener);
		rosterMenuItems.put(name,listener);
	}
	
	public void removeFromOnlineRosterMenu(String name)
	{
		onlinePanel.removeFromMenu(name);
		rosterMenuItems.remove(name);
	}
	
	public void addToOfflineRosterMenu(String name, RosterMenuListener listener)
	{
		offlinePanel.addToMenu(name, listener);
	}
	
	public void removeFromOfflineRosterMenu(String name)
	{
		offlinePanel.removeFromMenu(name);
	}

    private void addMenuItem(JMenuItem item) {
        if (popupMenu != null) {
            popupMenu.add(item);
        } else {
            popdownMenu.add(item);
        }
    }

	private void initMenu()
	{
		//JETI menu
        if (popdownMenu == null && popupMenu == null) {
            return;
        }
        if (popdownMenu != null) {
            popdownMenu.removeAll();
        } else {
            popupMenu.removeAll();
        }
		JMenuItem menuItem = null;
		JMenu subMenu = null;
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Message")+"...");
		I18N.setMnemonic("main.main.jetimenu.Message",menuItem);  
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JID jid = askJID();
				if (jid != null) new SendMessage(backend,jid,jid.getUser()).show(); 
			}
		});
        addMenuItem(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Chat")+"...");
		I18N.setMnemonic("main.main.jetimenu.Chat",menuItem); 
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startChat(askJID());
			}
		});
        addMenuItem(menuItem);

		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Add_Contact")+"...");
		I18N.setMnemonic("main.main.jetimenu.Add_Contact",menuItem); 
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new AddContact(backend.getMainFrame(), backend).show();
			}
		});
        addMenuItem(menuItem);
		
		//add plugin menus
		if(menuItems!=null)
		{
			for(Iterator i = menuItems.iterator();i.hasNext();)
			{
				JMenuItem item = (JMenuItem)i.next();
                addMenuItem(item);
			}
		}
		menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.jetimenu.Show_Log",menuItem,true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				serverPanel.clearError();
				new LogWindow(backend).show();
			}
		});
        addMenuItem(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Options")+"...");
		I18N.setMnemonic("main.main.jetimenu.Options",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new OptionsWindow(backend).show();
			}
		});
        addMenuItem(menuItem);

		subMenu = new JMenu(I18N.gettext("main.main.jetimenu.Account"));
		I18N.setMnemonic("main.main.jetimenu.Account",subMenu);
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Login")+"...");
		I18N.setMnemonic("main.main.jetimenu.Login",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!Start.applet) LoginWindow.createLoginWindow(backend);
				else new nu.fw.jeti.applet.LoginWindow(backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Manage_Services")+"...");
		I18N.setMnemonic("main.main.jetimenu.Manage_Services",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new RegisterServices(backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.jetimenu.About_this_Account",
                                menuItem, true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(Jeti.this, backend.getAccountInfo(), I18N.gettext("main.main.jetimenu.About_this_Account"), JOptionPane.QUESTION_MESSAGE);

			}
		});
		subMenu.add(menuItem);

        addMenuItem(subMenu);
		menuItem = new JMenuItem(I18N.gettext("main.main.jetimenu.Comment/Bug"));
		I18N.setMnemonic("main.main.jetimenu.Comment/Bug",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new CommentWindow(backend).show();
			}
		});
        addMenuItem(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.jetimenu.About",menuItem,true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new AboutWindow().show();
			}
		});
        addMenuItem(menuItem);
		final JMenuItem menuItem2 = new JMenuItem(I18N.gettext("main.main.jetimenu.Exit"));
		I18N.setMnemonic("main.main.jetimenu.Exit",menuItem2);
		menuItem2.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				menuItem2.removeActionListener(this);
				close();
			}
		});
        addMenuItem(menuItem2);
		heightInvalid=true;
	}

	protected void saveOpenGroups()
	{
		List groups = onlinePanel.getOpenGroups();
		if(groups != null)	backend.send(new InfoQuery("set",new IQPrivate(new JetiPrivateRosterExtension((String[]) groups.toArray(new String[] {})))));
	}
	
	public void openGroups(JetiPrivateRosterExtension extension)
	{
		onlinePanel.openGroups(extension);
		serverPanel.openGroups();
	}
	

	private void beep()
	{
		if (Preferences.getBoolean("jeti","beep",true))
			java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	public void updateLF()
	{
        if (popupMenu != null) {
            SwingUtilities.updateComponentTreeUI(popupMenu);
        }
        if (popdownMenu != null) {
            SwingUtilities.updateComponentTreeUI(popdownMenu);
        }
        btnStatus.updateLF();
		onlinePanel.updateLF();
		offlinePanel.updateLF();
		serverPanel.updateLF();
	}
	
	public void changeOFFlinePanel(boolean show)
	{
		if(show)
		{ 
			pnlRoster.remove(empty);
			pnlRoster.add(offlinePanel, BorderLayout.CENTER);
		}
		else 
		{
			pnlRoster.remove(offlinePanel);
			pnlRoster.add(empty, BorderLayout.CENTER);
		}
		pnlRoster.validate();
		pnlRoster.repaint();
	}
	
	
	/**
	 *  translate the main display if another locale is choosen
	 */
	public void translate()
	{
		preferencesChanged();
		onlinePanel.createPopupMenu();
		offlinePanel.createPopupMenu();
		onlinePanel.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), I18N.gettext("main.main.Online")));
		offlinePanel.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), I18N.gettext("main.main.Offline")));
	}

	public void preferencesChanged()
	{
		initMenu(); //remake options
		StatusButton.reloadMessages();
	}
	
	//---------------------message event---------------------------
	public void message(Message message)
	{
		if (message.getType().equals("chat") || (message.getType().equals("error") && message.getThread() != null))
		{
			beep();
			ChatWindow chatWindow = null;
			if (message.getThread() != null)
			{
				for (int tel = 0; tel < chatWindows.size(); tel++)
				{
					if (message.getThread().equals(((ChatWindow) chatWindows.get(tel)).getThread()))
					{
						chatWindow = (ChatWindow) chatWindows.get(tel);
						//if (!chatWindow.isVisible())
							//chatWindow.setState(JFrame.ICONIFIED); //prevent focus stealing
						//	chatWindow.setVisible(true);
						break;
					}
				}
			}
			else
			{
				for (int tel = 0; tel < chatWindows.size(); tel++)
				{
					if (((ChatWindow) chatWindows.get(tel)).compareJID(message.getFrom()))
					{
						chatWindow = (ChatWindow) chatWindows.get(tel);
						//if (!chatWindow.isVisible())
						//{
							//chatWindow.setState(JFrame.ICONIFIED); //prevent focus stealing
						//	chatWindow.setVisible(true);
						//}
						//chatWindow.show();
						break;
					}
				}
			}
			if (chatWindow == null)
			{
				JIDStatus jidStatus = backend.getJIDStatus(message.getFrom());
				if (jidStatus == null)	chatWindow = new ChatWindow(backend, message); // ,"images",true,"unknown",e.getThread());
				else chatWindow = new ChatWindow(backend, jidStatus, message.getThread());
//				chatWindow.setExtendedState(JFrame.ICONIFIED); //prevent focus stealing, does not work anymore for some stupid reason  
				//Thread.yield();
				//chatWindow.show();
				chatWindow.setVisible(true);
				//chatWindow.toBack();
				setChatWindowPosition(chatWindow);
				
				chatWindows.add(chatWindow);
			}
			chatWindow.appendMessage(message);
		}
		else if(message.getType().equals("groupchat"))
		{
			//no sound on groupchat server messages
        	if(message.getFrom().getResource()!=null) beep();
		}
		else
		{
			beep();
			SendMessage sm = new SendMessage(backend, message);
//			sm.setState(JFrame.ICONIFIED); //prevent focus stealing
			sm.setVisible(true);
		}
	}
	
	private void setChatWindowPosition(ChatWindow chatWindow)
	{
		if(chatWindows.isEmpty())
		{
			int posX = Preferences.getInteger("jeti","chatPosX",100);
			int posY = Preferences.getInteger("jeti","chatPosY",100);
			int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			if(posX>screenX) posX=100;
			if(posY>screenY) posY=100;
			chatWindow.setLocation(posX,posY);
		}
		else
		{
			ChatWindow oldWindow = (ChatWindow)chatWindows.get(chatWindows.size()-1);
			chatWindow.setLocation(oldWindow.getX()+50,oldWindow.getY()+50);
		}
	}

	public void removeChatWindow(ChatWindow chatWindow)
	{
		chatWindows.remove(chatWindow);
		if(chatWindows.isEmpty())
		{// save window pos if it is the last window
			Preferences.putInteger("jeti","chatPosX",chatWindow.getX());
			Preferences.putInteger("jeti","chatPosY",chatWindow.getY());
		}
	}

	public void onComposing(JID from, String thread, XMessageEvent messageEvent)
	{
		ChatWindow chatWindow = null;
		if (thread != null)
		{
			for (int tel = 0; tel < chatWindows.size(); tel++)
			{
				if (thread.equals(((ChatWindow) chatWindows.get(tel)).getThread()))
				{
					chatWindow = (ChatWindow) chatWindows.get(tel);
					chatWindow.composing(from, messageEvent.getType());
					return;
				}
			}
		}

		else
		{
			//add composing to all windows
			for (int tel = 0; tel < chatWindows.size(); tel++)
			{
				if (((ChatWindow) chatWindows.get(tel)).compareJID(from))
				{
					chatWindow = (ChatWindow) chatWindows.get(tel);
					chatWindow.composing(from, messageEvent.getType());
				}
			}
		}
	}
	
	public void requestComposing(JID from, String id, String thread)
	{
		ChatWindow chatWindow = null;
		if (thread != null)
		{
			for (int tel = 0; tel < chatWindows.size(); tel++)
			{
				if (thread.equals(((ChatWindow) chatWindows.get(tel)).getThread()))
				{
					chatWindow = (ChatWindow) chatWindows.get(tel);
					chatWindow.composingID(id);
					return;
				}
			}
		}

		else
		{
			//check all open windows
			for (int tel = 0; tel < chatWindows.size(); tel++)
			{
				if (((ChatWindow) chatWindows.get(tel)).compareJID(from))
				{
					chatWindow = (ChatWindow) chatWindows.get(tel);
					chatWindow.composingID(id);
				}
			}
		}
	}

	public void chat(JIDStatus jidStatus)
	{
		ChatWindow chatWindow = new ChatWindow(backend, jidStatus, backend.createThread());
		chatWindow.show();
		setChatWindowPosition(chatWindow);
		chatWindows.add(chatWindow);
	}

	public void presenceChanged(Presence presence)
	{
		JIDStatus jidStatus = backend.getJIDStatus(presence.getFrom());
		String nick = null;
		if (jidStatus != null) nick = jidStatus.getNick();
		if (nick == null) nick = presence.getFrom().getUser();
		if(jetiFrame!=null) jetiFrame.initTimer(nick,Presence.toLongShow(presence.getShow())); 
		//beep();
		for (int tel = 0; tel < chatWindows.size(); tel++)
		{
			if (((ChatWindow) chatWindows.get(tel)).compareJID(presence.getFrom()))
			{
				((ChatWindow) chatWindows.get(tel)).appendPresenceChange(presence);
				//return;
			}
		}
	}

	/*--------------------Status Change events----------------------------------*/
	public void ownPresenceChanged(int astatus, String amessage)
	{
		this.status = astatus;
		this.message = amessage;
        btnStatus.ownPresenceChanged(status, message);
        Runnable updateAComponent = new Runnable() {
            public void run() {
                if(jetiFrame!=null) {
                    ImageIcon icon = StatusIcons.getStatusIcon(status);
                    jetiFrame.setIconImage(icon.getImage());
                }
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
	}
	
	public void connectionChanged(boolean online)
	{
		if (online) {
			if(jetiFrame!=null) jetiFrame.setTitle(TITLE);
		} else {
            btnStatus.connectionOffline();
            Runnable updateAComponent = new Runnable() {
                public void run() {
                    if(jetiFrame!=null) {
						jetiFrame.setTitle(I18N.gettext("main.main.Offline"));
						jetiFrame.setIconImage(
                            StatusIcons.getOfflineIcon().getImage());
                    }
                }
            };
            SwingUtilities.invokeLater(updateAComponent);
		}
	}

	public void exit()
	{//if size of main window changed save
		Window w = jetiDialog ==null ? (Window)jetiFrame : (Window)jetiDialog;
		if(w!=null)
		{
			Preferences.putInteger("jeti","height",w.getHeight());
			Preferences.putInteger("jeti","width",w.getWidth());
	 		Preferences.putInteger("jeti","posX",w.getX());
			Preferences.putInteger("jeti","posY",w.getY());
			Preferences.putInteger("jeti","dividerLocation",jSplitPane1.getDividerLocation());
			Preferences.save();
		}
		for(Iterator i = chatWindows.iterator();i.hasNext();)
		{
			((ChatWindow)i.next()).exit();
		}
	}

	static {
		JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP).addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 1),new DefaultEditorKit.PasteAction());
		JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP).addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 2),new DefaultEditorKit.CopyAction());
		JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP).addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 1),new DefaultEditorKit.CutAction());
		Action findAction = new FindAction();
		JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP).addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F, 2),findAction);
		JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP).addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),findAction);
	}
	
	public RosterTree getOnlinePanel() {
        return onlinePanel;
    }

class JetiDialog extends JDialog
{
	public JetiDialog(JFrame frame)
	{
		super(frame);
	}

	public void init(final Jeti jeti)
	{
		setContentPane(jeti);
		this.setTitle(TITLE);
		if (PluginsInfo.isPluginLoaded("systemtray")) setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		else
		{
			addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					jeti.close();
				}
			});
		}
		
		int heigth = Preferences.getInteger("jeti","height",0);
		int width = Preferences.getInteger("jeti","width",0);
		if( heigth ==0 || width == 0) pack();
		else setSize(width,heigth);
		int x = Preferences.getInteger("jeti","posX",50);
		int y = Preferences.getInteger("jeti","posY",50);
		int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if(x>screenX) x=50;
		if(y>screenY) y=50;
		setLocation(x,y);
	}
}


class JetiFrame extends JFrame
{
	private TitleTimer timer = new TitleTimer(this, TITLE);
	
	
	public void init(final Jeti jeti)
	{
		setContentPane(jeti);
		setIconImage(StatusIcons.getOfflineIcon().getImage());
		this.setTitle(TITLE);
		if (PluginsInfo.isPluginLoaded("systemtray")) setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		else
		{
			addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					jeti.close();
				}
			});
		}
		
		int heigth = Preferences.getInteger("jeti","height",0);
		int width = Preferences.getInteger("jeti","width",0);
		if( heigth ==0 || width == 0) pack();
		else setSize(width,heigth);
		int x = Preferences.getInteger("jeti","posX",50);
		int y = Preferences.getInteger("jeti","posY",50);
		int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if(x>screenX) x=50;
		if(y>screenY) y=50;
		setLocation(x,y);
	}
	
	public void initTimer(String nick, String status)
	{
		timer.init(nick,status);
	}
}

    class OnlineSelector implements TreeModelSelector {
        public boolean isVisible(Object o) {
			if (o instanceof JIDStatus) {
                return ((JIDStatus)o).isOnline();
            } else if (o instanceof PrimaryJIDStatus) {
                return ((PrimaryJIDStatus)o).getJIDPrimaryStatus().isOnline();
            } else if (o instanceof JIDStatusGroup) {
				return (((JIDStatusGroup)o).getOnlines() > 0);
}
            return false;
        }
    }

    class OfflineSelector implements TreeModelSelector {
        public boolean isVisible(Object o) {
			if (o instanceof JIDStatus) {
                return !((JIDStatus)o).isOnline();
            } else if (o instanceof PrimaryJIDStatus) {
                return ((PrimaryJIDStatus)o).isAJIDstatusOffline();
            } else if (o instanceof JIDStatusGroup) {
				return true;
            }
            return true;
        }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
