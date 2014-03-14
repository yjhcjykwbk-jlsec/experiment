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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nu.fw.jeti.backend.roster.JIDStatusGroup;
import nu.fw.jeti.backend.roster.JIDStatusTree;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.StringArray;
import nu.fw.jeti.util.TreeExpander;

/**
 * @author E.S. de Boer
 */

public class RosterTree extends JTree
{
	private Backend backend;
	private JPopupMenu popupMenu;
	private JPopupMenu groupPopupMenu;
	private JIDStatus currentJIDStatus;
	private JIDStatusGroup currentJIDStatusGroup2;
	private JIDStatusGroup currentJIDStatusGroup;
	private boolean onlineTree;
	private Jeti main;
	private String currentGroup;
	private Map menuItems; //added menus
    private TreeExpander treeExpander;

    /**
     * @param type True for an online tree
     */
	public RosterTree(Backend backend, Jeti main, boolean onlineTree,
                      TreeModel model)
	{
		super(model);
		this.onlineTree = onlineTree;
		this.backend = backend;
		this.main = main;

		ToolTipManager.sharedInstance().registerComponent(this);

		setRootVisible(false);
		setToggleClickCount(0);//set expanding on mouseclicks of because
							   // detection needed for single or double click

		javax.swing.plaf.basic.BasicTreeUI basicTreeUI = (javax.swing.plaf.basic.BasicTreeUI) getUI();
		basicTreeUI.setRightChildIndent(1);
		basicTreeUI.setLeftChildIndent(1);
		basicTreeUI.setExpandedIcon(null);
		basicTreeUI.setCollapsedIcon(null);
		putClientProperty("JTree.lineStyle", "None");

		createPopupMenu();
		createGroupPopupMenu();
		if ( System.getProperty("os.name").startsWith("Mac")) {
			setCellRenderer(new MacRenderer());
            if (onlineTree) {
                treeExpander = new TreeExpander(this, model);
            }
 		}
 		else setCellRenderer(new MyRenderer());

		addMouseListener(new MouseAdapter()
		{
			TreePath lastTreePath;//save tree path for single click event
			//timer needed to check if double or single mouseclick
			Timer timer = new Timer(300, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (isExpanded(lastTreePath)) collapsePath(lastTreePath);
					else expandPath(lastTreePath);
					timer.stop();
				}
			});

			public void mousePressed(MouseEvent e)
			{
				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if (selPath != null)
				{
					setSelectionPath(selPath);
					Object o = selPath.getLastPathComponent();
					if (o instanceof JIDStatusGroup)
					{//group
						maybeShowGroupPopup(e, (JIDStatusGroup) o);
						if (SwingUtilities.isLeftMouseButton(e))
						{
							if (isExpanded(selPath)) collapsePath(selPath);
							else expandPath(selPath);
						}
					}
					else
					{
						JIDStatusGroup group = null;
						if (o instanceof PrimaryJIDStatus) group = ((JIDStatusGroup) selPath.getPathComponent(selPath.getPathCount() - 2));
						else if (o instanceof JIDStatus) group = ((JIDStatusGroup) selPath.getPathComponent(selPath.getPathCount() - 3));
						else return;
						maybeShowPopup(e, (JIDStatus) o, group);//cde
						if (SwingUtilities.isLeftMouseButton(e))
						{
							lastTreePath = selPath;
							//check if double or single mouseclick, needed
							// because double click= 2 single clicks
							if (timer.isRunning())
							{
								timer.stop();
								sendChat((JIDStatus) o);
							}
							else
							{
								timer.restart();
							}
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if (selPath != null)
				{
					Object o = selPath.getLastPathComponent();
					if (o instanceof JIDStatusGroup)
					{//group
						maybeShowGroupPopup(e, (JIDStatusGroup) o);
					}
					if (o instanceof PrimaryJIDStatus)
					{
						JIDStatusGroup group = ((JIDStatusGroup) selPath.getPathComponent(selPath.getPathCount() - 2));
						maybeShowPopup(e, (PrimaryJIDStatus) o, group);
					}
					else if (o instanceof JIDStatus)
					{
						JIDStatusGroup group = ((JIDStatusGroup) selPath.getPathComponent(selPath.getPathCount() - 3));
						maybeShowPopup(e, (JIDStatus) o, group);
					}
				}
			}

			public void mouseExited(MouseEvent e)
			{
				//if (popupPanel !=null) popupPanel.dispose();
				//timer.stop();
				clearSelection();//weg als multi select?
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				//if (popupPanel !=null) popupPanel.dispose();
				// timer.stop();
				TreePath selPath = getPathForLocation(e.getX(), e.getY());
				if (selPath != null)
				{
					Object o = selPath.getLastPathComponent();
					if (o instanceof PrimaryJIDStatus || o instanceof JIDStatus) setSelectionPath(selPath);
					/*
					 * if(o instanceof JIDStatus2) { //
					 * timer.init(e.getPoint(),(JIDStatus)o);
					 * setSelectionPath(selPath); }
					 */
				}
			}
		});
		//setOpaque(false);
	}

	public void updateLF()
	{
		SwingUtilities.updateComponentTreeUI(popupMenu);
		SwingUtilities.updateComponentTreeUI(groupPopupMenu);
	}
		
	private void createGroupPopupMenu()
	{
		groupPopupMenu = new JPopupMenu();
		JMenuItem menuItem = null;
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Remove",menuItem,true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (JOptionPane.showConfirmDialog(main, MessageFormat.format(I18N.gettext("main.popup.Really_remove_{0}?_All_JIDs_in_this_group_will_be_removed!"), new Object[] { new String(currentGroup) }),
						I18N.gettext("main.popup.Remove_Group"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					for (Iterator i = currentJIDStatusGroup.iterator(); i.hasNext();)
					{
						PrimaryJIDStatus primary = (PrimaryJIDStatus) i.next();
						if (primary.hasMultiple())
						{
							for (Iterator j = primary.getOtherJidStatussen(); j.hasNext();)
							{
								groupRemove((JIDStatus) j.next());
							}
						}
						groupRemove(primary);
					}
				}
			}

			private void groupRemove(JIDStatus jidStatus)
			{
				JID to = jidStatus.getJID();
				if (jidStatus.groupCount() == 1)
				{
					if (JOptionPane.showConfirmDialog(main,
							MessageFormat.format(I18N.gettext(
							"main.popup.{0}_is_in_the_last_group,_remove_completely?")
							, new Object[] {new String(to.toString())})
							, I18N.gettext("main.main.rostermenu.Remove")
							, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)	{
						IQXRoster roster = new IQXRoster(new RosterItem(to, null, "remove", null, null));
						backend.send(new InfoQuery("set", roster));
					}
				}
				else
				{
					StringArray groups = jidStatus.getGroupsCopy();
					groups.remove(currentGroup);
					IQXRoster roster = new IQXRoster(new RosterItem(to, jidStatus.getNick(), null, null, groups));
					backend.send(new InfoQuery("set", roster));
				}
			}
		});
		groupPopupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Rename",menuItem,true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String groupName = JOptionPane.showInputDialog(main, MessageFormat.format(I18N.gettext("main.popup.Rename_{0}_to:"),new Object[] { new String(currentGroup) }));
				if (groupName == null) return;
				for (Iterator i = currentJIDStatusGroup.iterator(); i.hasNext();)
				{
					PrimaryJIDStatus primary = (PrimaryJIDStatus) i.next();
					if (primary.hasMultiple())
					{
						for (Iterator j = primary.getOtherJidStatussen(); j.hasNext();)
						{
							renameGroup(groupName, (JIDStatus) j.next());
						}
					}
					renameGroup(groupName, primary);
				}
			}

			private void renameGroup(String groupName, JIDStatus jidStatus)
			{
				StringArray groups = jidStatus.getGroupsCopy();
				groups.remove(currentGroup);
				groups.add(groupName);
				IQXRoster roster = new IQXRoster(new RosterItem(jidStatus.getJID(), jidStatus.getNick(), null, null, groups));
				backend.send(new InfoQuery("set", roster));
			}
		});
		groupPopupMenu.add(menuItem);

	}

	public void createPopupMenu()
	{
		popupMenu = new JPopupMenu();
		JMenuItem menuItem = null;
		JMenu subMenu = null;
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Message",menuItem,true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendMessage(currentJIDStatus);
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Chat",menuItem,true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				sendChat(currentJIDStatus);
			}
		});
		popupMenu.add(menuItem);
		subMenu = new JMenu(I18N.gettext("main.main.rostermenu.Subscriptions"));
		menuItem = new JMenuItem(I18N.gettext("main.main.rostermenu.Subscribe_from"));
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Subscribe_from",menuItem);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.send(new Presence(currentJIDStatus.getJID(), "subscribe"));
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem(I18N.gettext("main.main.rostermenu.Unsubscribe_from"));
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Unsubscribe_from",menuItem);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.send(new Presence(currentJIDStatus.getJID(), "unsubscribe"));
			}
		});
		subMenu.add(menuItem);
		popupMenu.add(subMenu);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Remove",menuItem,true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JID to = currentJIDStatus.getJID();
				if (JOptionPane.showConfirmDialog(main, MessageFormat.format(I18N.gettext("main.popup.Really_remove_{0}_from_all_groups_?"),
												new Object[] { new String(to.toString()) }),I18N.gettext("main.main.rostermenu.Remove"),
														JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					//backend.remove(currentJIDStatus.getJID());
					IQXRoster roster = new IQXRoster(new RosterItem(to, null, "remove", null, null));
					backend.send(new InfoQuery("set", roster));
				}
			}
		});
		popupMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Rename",menuItem,true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String nick = JOptionPane.showInputDialog(main, MessageFormat.format(I18N.gettext("main.popup.Rename_{0}_to:"),new Object[] { new String(currentJIDStatus.getNick()) }));
				if (nick == null) return;
				IQXRoster roster = new IQXRoster(new RosterItem(currentJIDStatus.getJID(), nick, null, null, currentJIDStatus.getGroupsCopy()));
				backend.send(new InfoQuery("set", roster));
			}
		});
		popupMenu.add(menuItem);
		subMenu = new JMenu();
		I18N.setTextAndMnemonic("main.main.rostermenu.Group",subMenu);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Change_Group",
                                menuItem,true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new GroupDialog(currentGroup, currentJIDStatus, backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Add_to_Group",
                                menuItem, true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new GroupDialog(currentJIDStatus, backend).show();
			}
		});
		subMenu.add(menuItem);
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Remove_from_group",
                                menuItem,true);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JID to = currentJIDStatus.getJID();
				if (currentJIDStatus.groupCount() == 1)
				{
					if (JOptionPane.showConfirmDialog(main
						, MessageFormat.format(
						I18N.gettext("main.popup.{0}_is_in_the_last_group,_remove_completely?")
						,new Object[]{ new String(to.toString()) })
						, I18N.gettext("main.main.rostermenu.Remove")
						, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						IQXRoster roster = new IQXRoster(new RosterItem(to, null, "remove", null, null));
						backend.send(new InfoQuery("set", roster));
					}
				}
				else
				{
					nu.fw.jeti.util.StringArray groups = currentJIDStatus.getGroupsCopy();
					groups.remove(currentGroup);
					IQXRoster roster = new IQXRoster(new RosterItem(to, currentJIDStatus.getNick(), null, null, groups));
					backend.send(new InfoQuery("set", roster));
				}
			}
		});
		subMenu.add(menuItem);
		popupMenu.add(subMenu);
        if (onlineTree) {
            menuItem = new JMenuItem();
            I18N.setTextAndMnemonic("main.main.rostermenu.Local_Time",
                                    menuItem, true);
            menuItem.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    backend.send(new InfoQuery(currentJIDStatus.getCompleteJID(), "get", new IQTime()));
                    
                }
            });
            popupMenu.add(menuItem);
        }
        if (!onlineTree) {
            menuItem = new JMenuItem();
            I18N.setTextAndMnemonic("main.main.rostermenu.Last_Seen",menuItem,true);
            menuItem.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    backend.send(new InfoQuery(currentJIDStatus.getJID(), "get", new IQLast()));
                }
            });
            popupMenu.add(menuItem);
        }
        if (onlineTree) {
            menuItem = new JMenuItem();
            I18N.setTextAndMnemonic("main.main.rostermenu.Local_Version",
                                    menuItem, true);
            menuItem.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    backend.send(new InfoQuery(currentJIDStatus.getCompleteJID(), "get", new IQVersion()));
                }
            });
            popupMenu.add(menuItem);
        }
		menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("main.main.rostermenu.Invisible",menuItem);
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				backend.send(new Presence(currentJIDStatus.getJID(), "invisible"));
			}
		});
		popupMenu.add(menuItem);
		if (menuItems != null)
		{
			for (Iterator i = menuItems.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				JMenuItem item = new JMenuItem((String) entry.getKey());
				final RosterMenuListener listener = (RosterMenuListener) entry.getValue();
				item.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						listener.actionPerformed(currentJIDStatus, currentJIDStatusGroup2);
					}
				});
				popupMenu.add(item);
			}
		}
	}

	public void addToMenu(String name, RosterMenuListener listener)
	{
		if (menuItems == null) menuItems = new HashMap(10);
		menuItems.put(name, listener);
		if (popupMenu != null) createPopupMenu();
	}

	public void removeFromMenu(String name)
	{
		if (menuItems == null) return;
		menuItems.remove(name);
		if (menuItems.isEmpty()) menuItems = null;
		if (popupMenu != null) createPopupMenu();
	}

	public List getOpenGroups()
	{
		List tempList = new LinkedList();
		JIDStatusTree tree = ((JIDStatusTree) getModel().getRoot());
		if (tree == null) return null;
		for (int i = 0; i < tree.getSize(); i++)
		{
			TreePath path = new TreePath(new Object[] { tree, tree.get(i)});
			if (isExpanded(path)) {
                tempList.add(tree.get(i).toString());
            }
		}
		return tempList;

	}

	public void openGroups(JetiPrivateRosterExtension extension)
	{
		String[] groups = extension.getOpenGroups();
		JIDStatusTree tree = ((JIDStatusTree) getModel().getRoot());
		if (groups == null) return;
		for (int i = 0; i < groups.length; i++)
		{
			if(tree.existGroup(groups[i]))
			{
				TreePath path = new TreePath(new Object[] { tree, tree.getGroup(groups[i])});
                if (treeExpander != null) {
                    treeExpander.expand(path);
                } else {
                    expandPath(path);
                }
			}
		}

	}

	private void sendMessage(JIDStatus jidStatus)
	{
		new SendMessage(backend, jidStatus.getJID(), jidStatus.getNick()).show();
	}

	private void sendChat(JIDStatus jidStatus)
	{
		main.chat(jidStatus);
	}

	private void maybeShowPopup(MouseEvent e, JIDStatus jidStatus, JIDStatusGroup group)
	{
		if (e.isPopupTrigger())
		{
			currentJIDStatus = jidStatus;
			currentGroup = group.getName();
			currentJIDStatusGroup2 = group;
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void maybeShowGroupPopup(MouseEvent e, JIDStatusGroup jidStatusGroup)
	{
		if (e.isPopupTrigger())
		{
			currentJIDStatusGroup = jidStatusGroup;
			currentGroup = jidStatusGroup.getName();
			groupPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public String getToolTipText(MouseEvent ev)
	{
		if (ev == null) return null;
		TreePath path = getPathForLocation(ev.getX(), ev.getY());
		if (path != null)
		{
			JIDStatus jidStatus = null;
			Object o = path.getLastPathComponent();
			if (o instanceof PrimaryJIDStatus)
			{
				jidStatus = ((PrimaryJIDStatus) o).getJIDPrimaryStatus();

			}
			if (o instanceof JIDStatus) {
				jidStatus = ((JIDStatus) o);
			} else {
                return null;
            }
            String statusMsg;
            if (jidStatus.getStatus() != null) {
                statusMsg = 
                    I18N.gettext("main.main.statusmenu.Status_message:")
                    + " " + jidStatus.getStatus() + "</p><p>";
            } else {
                statusMsg = "";
            }
                        
            String waitingStatus = jidStatus.getWaiting();
            if (jidStatus.getWaiting() != null) {
                waitingStatus =
                    "<p>" + I18N.gettext("main.main.roster.Waiting_Status:")
                    + " " + jidStatus.getWaiting() + "</p>";
            } else {
                waitingStatus = "";
            }
			return "<HTML><P>"
                + I18N.gettext("main.main.roster.Status:") 
                + " " + Presence.toLongShow(jidStatus.getShow()) + "</p><p>"
                + statusMsg
                + "JID: " + jidStatus.getCompleteJID() + "</p><p>"
                + I18N.gettext("main.main.roster.Subscription:")
                + " " + jidStatus.getSubscription() + "</p>"
                + waitingStatus + "</p></HTML>";
		}
		return null;
	}
	

	class MyRenderer implements TreeCellRenderer
	{
		private JLabel renderer;
		private Component fill = Box.createRigidArea(new Dimension(0, 0));

		public MyRenderer()
		{
			renderer = new JLabel();
			renderer.setOpaque(Preferences.getBoolean("jeti", "bmw", true));
			renderer.setBackground(UIManager
					.getColor("Tree.selectionBackground"));
			renderer.setForeground(UIManager.getColor("Tree.textForeground"));
			renderer.setFont(UIManager.getFont("Tree.font"));

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			renderer.setForeground(Color.BLACK);
			renderer.setText(value.toString());
			if (sel)
			{
				renderer.setOpaque(true);
			} else
			{
				renderer.setOpaque(false);
			}
			if (leaf) { return (makeComponent((JIDStatus) value)); }
			if (value instanceof PrimaryJIDStatus)
			{
				PrimaryJIDStatus ps = (PrimaryJIDStatus) value;
				JIDStatus jidsStatus = ps.getJIDPrimaryStatus();
				if (ps.hasMultiple())
				{
					if (onlineTree)
					{
						if (ps.multipleJIDstatusOnline())
							renderer.setForeground(new Color(0, 0, 190));
						return makeComponent(jidsStatus);
					}
					if (ps.isAJIDstatusOffline())
					{// if offline tree and a jidStatus in primary is offline
					// show
						renderer.setIcon(StatusIcons.getImageIcon("multiple"));
						return renderer;
					}
					return fill;
				}
				return makeComponent(jidsStatus);
			} else if (value instanceof JIDStatusGroup)
			{
				JIDStatusGroup group = (JIDStatusGroup) value;
				if (onlineTree)
				{
					if (group.getOnlines() == 0) return fill;
					else renderer.setText(group.toString() + " ("
							+ group.getOnlines() + ")");
				}
				if (expanded) renderer.setIcon(StatusIcons
						.getImageIcon("arrowDown"));
				else renderer.setIcon(StatusIcons.getImageIcon("arrowUp"));
			}
			return renderer;
		}

		private Component makeComponent(JIDStatus jidStatus)
		{
			if (jidStatus.isOnline())
			{
				if (!onlineTree) return fill; // leeg want offline tree
				int show = jidStatus.getShow();
				renderer.setIcon(StatusIcons.getStatusIcon(show, jidStatus
						.getType()));
			} else
			{
				if (onlineTree) return fill; //leeg want online tree
				renderer.setIcon(StatusIcons.getStatusIcon(
						Presence.UNAVAILABLE, jidStatus.getType()));
			}
			return renderer;
		}
	}


	class MacRenderer implements TreeCellRenderer 
	{//special render for Mac because it doesn't support 0 sized components in the tree
		private JLabel renderer;

		public MacRenderer()
		{
			renderer = new JLabel();
			renderer.setOpaque(Preferences.getBoolean("jeti", "bmw", true));
			renderer.setBackground(UIManager.getColor("Tree.selectionBackground"));
			renderer.setForeground(UIManager.getColor("Tree.textForeground"));
			renderer.setFont(UIManager.getFont("Tree.font"));
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			renderer.setForeground(Color.BLACK);
			renderer.setText(value.toString());
			if (sel) {
				renderer.setOpaque(true);
			} else {
				renderer.setOpaque(false);
			}
			if (leaf) { return (makeComponent((JIDStatus) value)); }
			if (value instanceof PrimaryJIDStatus)
			{
				PrimaryJIDStatus ps = (PrimaryJIDStatus) value;
				JIDStatus jidsStatus = ps.getJIDPrimaryStatus();
				if (ps.hasMultiple())
				{
					if (onlineTree) {
						if (ps.multipleJIDstatusOnline()) {
                            renderer.setForeground(new Color(0, 0, 190));
                        }
						return makeComponent(jidsStatus);
					}
					if (ps.isAJIDstatusOffline())
					{//if offline tree and a jidStatus in primary is offline
					 // show
						renderer.setIcon(StatusIcons.getImageIcon("multiple"));
					}
                    return renderer;
				}
				return makeComponent(jidsStatus);
			}
			else if (value instanceof JIDStatusGroup)
			{
				JIDStatusGroup group = (JIDStatusGroup) value;
				if (onlineTree) {
					renderer.setText(group.toString() + " ("
                                     + group.getOnlines() + ")");
				}
				if (expanded) {
                    renderer.setIcon(StatusIcons.getImageIcon("arrowDown"));
				} else {
                    renderer.setIcon(StatusIcons.getImageIcon("arrowUp"));
                }
			}
			return renderer;
		}

		private Component makeComponent(JIDStatus jidStatus)
		{
            int show;
			if (jidStatus.isOnline()) {
				show = jidStatus.getShow();
			} else {
                show = Presence.UNAVAILABLE;
			}
            renderer.setIcon(StatusIcons.getStatusIcon(show,
                                                       jidStatus.getType()));
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
