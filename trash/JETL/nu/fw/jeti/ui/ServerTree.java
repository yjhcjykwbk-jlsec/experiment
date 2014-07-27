package nu.fw.jeti.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import nu.fw.jeti.backend.roster.JIDStatusTree;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.events.JavaErrorListener;
import nu.fw.jeti.events.RegisterListener;
import nu.fw.jeti.events.ServerListener;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.ui.models.RosterTreeModel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


/**
 * @author E.S. de Boer
 */

public class ServerTree extends JTree implements JavaErrorListener, RegisterListener
{
	private Backend backend;
	private JPopupMenu popupMenu;
	private JIDStatus currentJIDStatus;
	private JID registerJID;
	private Jeti main;

	public ServerTree(Backend backend,Jeti main)
	{
		super(new RosterTreeModel());
		this.backend = backend;
		backend.addListener(ServerListener.class,(RosterTreeModel)getModel());
		backend.addListener(JavaErrorListener.class,this);
		this.main = main;
		ToolTipManager.sharedInstance().registerComponent(this);
		setRootVisible(false);
		putClientProperty("JTree.lineStyle", "None");
		setToggleClickCount(1);
		javax.swing.plaf.basic.BasicTreeUI basicTreeUI = (javax.swing.plaf.basic.BasicTreeUI) getUI();
		basicTreeUI.setRightChildIndent(1);
		basicTreeUI.setLeftChildIndent(1);
		basicTreeUI.setExpandedIcon(null);
		basicTreeUI.setCollapsedIcon(null);
		createPopupMenu();
		setCellRenderer(new MyRenderer());

		addMouseListener(new MouseAdapter()
		 {
				public void mousePressed(MouseEvent e)
				{
		//			if (popupPanel !=null) popupPanel.dispose();
					//timer.stop();
					 TreePath selPath = getPathForLocation(e.getX(), e.getY());
					 if(selPath != null)
					 {
						setSelectionPath(selPath);
						Object o = selPath.getLastPathComponent();
						if(o instanceof PrimaryJIDStatus)
						{
							//String group = ((JIDStatusGroup)selPath.getPathComponent(selPath.getPathCount() -2)).getName();
							maybeShowPopup(e,((PrimaryJIDStatus)o).getJIDPrimaryStatus());//cde
							if (SwingUtilities.isLeftMouseButton(e))
							{
								if(e.getClickCount() >1) sendChat(((PrimaryJIDStatus)o).getJIDPrimaryStatus());
							}
						}
						/*
						if(o instanceof JIDStatus2)
						{
							String group = ((JIDGroup2)selPath.getPathComponent(selPath.getPathCount() -2)).getName();
							maybeShowPopup(e,(JIDStatus2)o,group);//cde
							if (SwingUtilities.isLeftMouseButton(e))
							{
		//						sendChat((JIDStatus2)o);
							}
						}
						*/
					 }
				}
				public void mouseReleased(MouseEvent e)
				{
					TreePath selPath = getPathForLocation(e.getX(), e.getY());
					if(selPath != null)
					{
						Object o = selPath.getLastPathComponent();
						if(o instanceof PrimaryJIDStatus)
						{
							//String group = ((JIDStatusGroup)selPath.getPathComponent(selPath.getPathCount() -2)).getName();
							maybeShowPopup(e,((PrimaryJIDStatus)o).getJIDPrimaryStatus());
						}
						/*
						if(o instanceof JIDStatus2)
						{
							String group = ((JIDGroup2)selPath.getPathComponent(selPath.getPathCount() -2)).getName();
							maybeShowPopup(e,(JIDStatus2)o,group);
						}
						*/
					}
				}
				public void mouseExited(MouseEvent e)
				{
					//if (popupPanel !=null) popupPanel.dispose();
					//timer.stop();
					clearSelection();//weg als multi select?
				}
			}
		);

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				//if (popupPanel !=null) popupPanel.dispose();
				// timer.stop();
				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if(selPath != null)
				{
					Object o = selPath.getLastPathComponent();
					if(o instanceof PrimaryJIDStatus)	setSelectionPath(selPath);
					/*
					if(o instanceof JIDStatus2)
					{
//						timer.init(e.getPoint(),(JIDStatus)o);
						setSelectionPath(selPath);
					}
					*/
				}
			}
		});

	}


	private void createPopupMenu()
	{
		popupMenu = new JPopupMenu();
		JMenuItem menuItem =null;
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Message",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendMessage(currentJIDStatus);
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Chat",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				sendChat(currentJIDStatus);
			}
		});
		popupMenu.add(menuItem);
		
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Log_On",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.send(new Presence(currentJIDStatus.getJID(),"available"));
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Log_Off",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.send(new Presence(currentJIDStatus.getJID(),"unavailable"));
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.rostermenu.Remove") +"...");
		I18N.setMnemonic("main.main.rostermenu.Remove",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JID to = currentJIDStatus.getJID();
				if (JOptionPane.showConfirmDialog(main,MessageFormat.format(I18N.gettext("main.popup.Really_remove_{0}_from_all_groups_?"),new Object[] { to.toString() }),I18N.gettext("Remove"),JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					//backend.remove(currentJIDStatus.getJID());
					IQXRoster roster = new IQXRoster(new RosterItem(to,null,"remove",null,null));
					backend.send(new InfoQuery("set",roster));
				}
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.rostermenu.Rename")+"...");
		I18N.setMnemonic("main.main.rostermenu.Rename",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String nick = JOptionPane.showInputDialog(main,MessageFormat.format(I18N.gettext("main.popup.Rename_{0}_to:"),new Object[] { currentJIDStatus.getNick() }));
				if (nick == null) return;
				IQXRoster roster = new IQXRoster(new RosterItem(currentJIDStatus.getJID(),nick,null,null,currentJIDStatus.getGroupsCopy()));
				backend.send(new InfoQuery("set",roster));
			}
		});
		popupMenu.add(menuItem);
		/*
		//submenu group
		subMenu = new JMenu("Group");
		menuItem = new JMenuItem("Change group...");
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//String group = JOptionPane.showInputDialog(main,"Change group "+ currentJIDStatus.getNick() + " to:");
				//if (group == null) return;
				//backend.changeGroup(currentJIDStatus,currentGroup,group);
				new GroupDialog(main,currentGroup,currentJIDStatus2,backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem("Add to group...");
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//String group = JOptionPane.showInputDialog(main,"Add "+ currentJIDStatus.getNick() + " to:");
				//if (group == null) return;
				//backend.addGroup(currentJIDStatus,group);
				new GroupDialog(main,currentJIDStatus2,backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem("Remove from group");
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JID to = currentJIDStatus2.getJID();
				if(currentJIDStatus2.groupCount() == 1)
				{
					if (JOptionPane.showConfirmDialog(main, to + "is in the last group, remove completely?","Remove",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						IQXRoster roster = new IQXRoster(new RosterItem(to,null,"remove",null,null));
						backend.send(new InfoQuery("set",roster));
					}
				}
				//else backend.removeGroup(currentJIDStatus,currentGroup);
				else
				{
					util.StringArray groups = currentJIDStatus2.getGroupsCopy();
					groups.remove(currentGroup);
					IQXRoster roster = new IQXRoster(new RosterItem(to,currentJIDStatus2.getNick(),null,null,groups));
					backend.send(new InfoQuery("set",roster));
				}
			}
		});
		subMenu.add(menuItem);
		popupMenu.add(subMenu);
		//end group
		*/
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Local_Time",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				//backend.getTime(currentJIDStatus.getCompleteJID());
				backend.send(new InfoQuery(currentJIDStatus.getCompleteJID(), "get", new IQTime()));

			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Last_Seen",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				//backend.getTime(currentJIDStatus.getCompleteJID());
				backend.send(new InfoQuery(currentJIDStatus.getCompleteJID(), "get", new IQLast()));

			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Local_Version",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//backend.getVersion(currentJIDStatus.getCompleteJID());
				backend.send(new InfoQuery(currentJIDStatus.getCompleteJID(), "get", new IQVersion()));
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Invisible",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//backend.setInvisible(currentJIDStatus.getJID());
				backend.send(new Presence(currentJIDStatus.getJID(), "invisible"));
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.rostermenu.Edit_Registration"));
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.addListener(RegisterListener.class,ServerTree.this);
				registerJID = currentJIDStatus.getJID();
				backend.send(new InfoQuery(registerJID,"get",backend.getIdentifier(),new IQRegister()));
			}
		});
		popupMenu.add(menuItem);
	}
	
	public void updateLF()
	{
		SwingUtilities.updateComponentTreeUI(popupMenu);
	}
	
	public void clearError()
	{
		((RosterTreeModel)getModel()).remove();
	}
	
	public void error()
	{
		((RosterTreeModel)getModel()).add();
	}
	
	public void openGroups()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JIDStatusTree tree = ((JIDStatusTree)getModel().getRoot());
				TreePath path = new TreePath(new Object[] {tree,tree.getGroup(I18N.gettext("main.main.roster.Servers"))});
					//System.out.println("path " + path);
				expandPath(path);
			}
		});
		 					
	}
	
	private void sendMessage(JIDStatus jidStatus)
	{
		new SendMessage(backend,jidStatus.getJID(),jidStatus.getNick()).show();
	}

	private void sendChat(JIDStatus jidStatus)
	{
		main.chat(jidStatus);
	}


/*
	public MyTreeModel getModel()
	{
		return treeModel;
	}
*/
	private void maybeShowPopup(MouseEvent e,JIDStatus jidStatus)
	{
		if(e.isPopupTrigger())
		{
			currentJIDStatus = jidStatus;
			//currentGroup = group;
			popupMenu.show(e.getComponent(),e.getX(),e.getY());
		}
	}


	public String getToolTipText(MouseEvent ev)
	{
		if(ev == null) return null;
		TreePath path = getPathForLocation(ev.getX(),ev.getY());
		if (path != null)
		{
			Object o = path.getLastPathComponent();
			if(o instanceof PrimaryJIDStatus)
			{
				JIDStatus jidStatus = ((PrimaryJIDStatus)o).getJIDPrimaryStatus();
				return "<HTML><P>" + I18N.gettext("main.main.roster.Status:")
						+ " " + Presence.toLongShow(jidStatus.getShow())+"</p><p>" +
						I18N.gettext("main.main.statusmenu.Status_message:")
						+ " " + jidStatus.getStatus()
						+ "</p><p> JID: " + jidStatus.getJID() + "</p><p>"
						+ I18N.gettext("main.main.roster.Subscription:")
						+ " " + jidStatus.getSubscription()	+ "</p><p>" 
						+ I18N.gettext("main.main.roster.Waiting_Status:")
						+ " " + jidStatus.getWaiting() + "</p></HTML>";
				
			}
		 }
		return null;
	}

	 public void register(IQRegister register,String id)
	 {
	 		backend.removeListener(RegisterListener.class,this);
	 		new RegisterWindow(backend,register,registerJID,id);
	 }

	static class MyRenderer implements TreeCellRenderer 
	{
		private JLabel renderer;
					
		public MyRenderer() {
			renderer = new JLabel(); 
			renderer.setOpaque(Preferences.getBoolean("jeti","bmw",true));
			renderer.setBackground(UIManager.getColor("Tree.selectionBackground"));
			renderer.setForeground(UIManager.getColor("Tree.textForeground"));
			renderer.setFont(UIManager.getFont("Tree.font"));
			
		  }

		
		
		

		public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,	boolean leaf,int row,boolean hasFocus)
		{
			//super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
			if(sel)
			{
				renderer.setOpaque(true);
			}
			else
			{ 
				renderer.setOpaque(false);  
			}
			renderer.setText(value.toString());
			//super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row,hasFocus);
			if (leaf)
			{
				//JIDStatus2 jidStatus = ((JIDPrimaryStatus)value).getJIDPrimaryStatus();
				return(makeComponent((JIDStatus)value));
				//System.out.println(jidStatus.getType());

			}
			if (value instanceof PrimaryJIDStatus) return makeComponent(((PrimaryJIDStatus)value).getJIDPrimaryStatus());
			else if (!leaf && expanded) renderer.setIcon(StatusIcons.getImageIcon("arrowDown"));
			else if (!leaf && !expanded) renderer.setIcon(StatusIcons.getImageIcon("arrowUp"));
			return renderer;
		}

		private Component makeComponent(JIDStatus jidStatus)
		{
			if (jidStatus.isOnline())
			{
				int show = jidStatus.getShow();
				renderer.setIcon(StatusIcons.getStatusIcon(show,jidStatus.getType()));
			}
			else
			{
				renderer.setIcon(StatusIcons.getStatusIcon(Presence.UNAVAILABLE,jidStatus.getType()));
			}
			return renderer;
		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
