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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * A status button which shows the users status (available, away etc).
 * There may be any number of buttons but they all share the same instance
 * of a popup menu which modifies the status.
 *
 * @author M Forssen
 */

public class StatusButton extends JToggleButton
{
    // The shared status menu
    static private boolean heightInvalid = true;
    static private JPopupMenu statusMenu = null;
    static private JTextArea txtStatus = null;
    static private StatusButton currentParent = null;

    // List of instances
    static private StatusButton master = null;
    static private LinkedList subInstances = new LinkedList();

    // Per instance data
    private JID jid;
    private int status;
    private String message;
    private Backend backend;
    private Jeti jeti;

    /**
     * Creates the master instance of the status buttons
     */
    public StatusButton(Backend backend, Jeti jeti) {
        this.backend = backend;
        this.jeti = jeti;
        master = this;
        init();
    }

    /**
     * Creates a sub-instance of of the status buttons
     */
    public StatusButton(Backend backend, JID jid) {
        this.backend = backend;
        this.jid = jid;
        subInstances.add(this);
        init();
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {}
            public void ancestorRemoved(AncestorEvent event) {
                subInstances.remove(StatusButton.this);
            }
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
    
    public void setJID(JID jid)
    {
    	this.jid = jid;
    }

    /**
     * Update the Look'n'Feel
     */
    public void updateLF () {
        SwingUtilities.updateComponentTreeUI(statusMenu);
    }

    /**
     * Reloads all messages and icons in the status menu
     */
    static public void reloadMessages() {
        populateStatusMenu();
    }

    /**
     * Callback which is called when my presence status has changed.
     */
    public void ownPresenceChanged(int astatus, String amessage) {
        this.status = astatus;
        if (amessage != null) {
            this.message = amessage;
        } else {
            this.message = Presence.toLongShow(astatus);
        }
        Runnable updateAComponent = new Runnable() {
            public void run() {
                ImageIcon icon = StatusIcons.getStatusIcon(status);
                StatusButton.this.setIcon(icon);
                String sid = null;
                switch (status) {
                case Presence.AVAILABLE:     sid = "Available"; break;
                case Presence.FREE_FOR_CHAT: sid = "FreeforChat"; break;
                case Presence.AWAY:          sid = "Away"; break;
                case Presence.XA:            sid = "XA"; break;
                case Presence.DND:           sid = "DND"; break;
                case Presence.UNAVAILABLE:   sid = "Offline"; break;
                case Presence.INVISIBLE:     sid = "Invisible"; break;
                }			
                I18N.setTextAndMnemonic("main.main.presencebutton." + sid,
                                        StatusButton.this);
                txtStatus.setText(message);
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
    }

    /**
     * Callback which is called when we have gone offline
     */
    public void connectionOffline() {
        Runnable updateComponents = new Runnable() {
            public void run() {
                master.setText(I18N.gettext("main.main.Offline"));
                master.setIcon(StatusIcons.getOfflineIcon());
                for (Iterator i = subInstances.iterator(); i.hasNext(); ) {
                    StatusButton sub = (StatusButton)i.next();
                    sub.setText(I18N.gettext("main.main.Offline"));
                    sub.setIcon(StatusIcons.getOfflineIcon());
                }
            }
        };
        SwingUtilities.invokeLater(updateComponents);
    }

    /**
     * Initialize the status button. Also creates the popup menu if needed.
     */
    synchronized private void init() {
        I18N.setTextAndMnemonic("main.main.presencebutton.Offline", this);
        setMaximumSize(new Dimension(1043, 23));
        setIcon(StatusIcons.getOfflineIcon());
        setMargin(new Insets(0, 0, 0, 0));
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStatusMenu();
            }
        });
        addMouseListener(new PopupListener());
        if (statusMenu == null) {
            statusMenu = new JPopupMenu();
            txtStatus = new JTextArea();
            populateStatusMenu();
            statusMenu.addPopupMenuListener(new PopupMenuListener() {
                //toggle popup button
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    currentParent.setSelected(false);
                }
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
                public void popupMenuCanceled(PopupMenuEvent e) {}
            });
        }
    }

    /**
     * Show the status popup menu over this status button
     */
    private void showStatusMenu() {
        if (statusMenu.isVisible()) {
            return;
        }
        currentParent = StatusButton.this;
        txtStatus.setText(message);

        if (heightInvalid) {
            statusMenu.show(currentParent, 0, 0);
            statusMenu.setVisible(false);
            heightInvalid = false;
        }
        statusMenu.show(currentParent, 0, 0 - statusMenu.getHeight());
    }

    /**
     * Populate the popup menu
     */
    static private void populateStatusMenu() {
        statusMenu.removeAll();
        JMenuItem menuItem = null;
        statusMenu.add(new JMenuItem(
                        I18N.gettext("main.main.statusmenu.Status_message:")));

        txtStatus.setEditable(false);
        txtStatus.setBorder(
            BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                                            Color.white, Color.white,
                                            new Color(134, 134, 134),
                                            new Color(93, 93, 93)));
        txtStatus.setOpaque(false);
        txtStatus.setLineWrap(true);
        txtStatus.setWrapStyleWord(true);
        statusMenu.add(txtStatus);

        menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.statusmenu.Change_message",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentParent.message = JOptionPane.showInputDialog(
                    currentParent,
                    I18N.gettext("main.main.statusmenu.Status_message:"),
                    currentParent.message);
                if (currentParent.message == null)
                    return;
                changeStatus(currentParent.status, currentParent.message);
            }
        });
        statusMenu.add(menuItem);

        menuItem = new JMenuItem();
        I18N.setTextAndMnemonic("main.main.statusmenu.Manage_messages",
                                menuItem, true);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StatusMessagesWindow().show();
            }
        });
        statusMenu.add(menuItem);

        statusMenu.add(createSubMenu(Presence.FREE_FOR_CHAT));
        statusMenu.add(createSubMenu(Presence.AVAILABLE));
        statusMenu.add(createSubMenu(Presence.DND));
        statusMenu.add(createSubMenu(Presence.AWAY));
        statusMenu.add(createSubMenu(Presence.XA));

        menuItem = new JMenuItem(
            StatusIcons.getStatusIcon(Presence.INVISIBLE));
        I18N.setTextAndMnemonic("main.main.presencebutton.Invisible", menuItem);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeStatus(Presence.INVISIBLE, null);
            }
        });
        statusMenu.add(menuItem);

        menuItem = new JMenuItem(
            StatusIcons.getOfflineIcon());
        I18N.setTextAndMnemonic("main.main.presencebutton.Offline",menuItem);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeStatus(Presence.UNAVAILABLE, null);
            }
        });
        statusMenu.add(menuItem);

        heightInvalid = true;
    }

    /**
     * Create a submenu with teh different messages for one status.
     */
    static private JMenu createSubMenu(final int status) {
        JMenu subMenu = new JMenu();
        I18N.setTextAndMnemonic(Presence.getI18NKey(status),subMenu);
        subMenu.setIcon(StatusIcons.getStatusIcon(status));
        List messages = Preferences.getStatusMessages(status);
        for (Iterator it = messages.iterator(); it.hasNext();) {
            final String messageText = (String) it.next();
            JMenuItem menuItem = new JMenuItem(messageText);
            menuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeStatus(status, messageText);
                }
            });
            subMenu.add(menuItem);
        }
        return subMenu;
    }

    /**
     * Change the status
     * This is the master function which may change the status of one or
     * more instances.
     */
    static public void changeStatus(int status, String messageText) {
        if (Preferences.getBoolean("jeti", "statusLinked", true)) {
            for (Iterator i = subInstances.iterator(); i.hasNext(); ) {
                StatusButton sub = (StatusButton)i.next();
                sub.changeInstanceStatus(status, messageText);
            }
            master.changeInstanceStatus(status, messageText);

        } else {
            if (currentParent == master || currentParent == null) {
                for (Iterator i = subInstances.iterator(); i.hasNext(); ) {
                    StatusButton sub = (StatusButton)i.next();
                    if (sub.status == master.status
                        && ((sub.message == null && master.message == null)
                            || (sub.message != null
                                && sub.message.equals(master.message)))) {
                        sub.changeInstanceStatus(status, messageText);
                    }
                }
            }
            if (currentParent != null) {
                currentParent.changeInstanceStatus(status, messageText);
            } else {
                master.changeInstanceStatus(status, messageText);
            }
        }
    }

    /**
     * Change the status
     * This changes the status of thsi instance.
     */
    private void changeInstanceStatus(int status, String messageText) {
        if (master == this) {
            if (status == Presence.UNAVAILABLE) {
                jeti.saveOpenGroups();
                backend.disconnect();
            } else {
                backend.changeStatus(status, messageText);
            }
        } else {
            String statusText;
            switch (status) {
            case Presence.INVISIBLE:   statusText = "invisible";   break;
            case Presence.UNAVAILABLE: statusText = "unavailable"; break;
            default:                   statusText = "available";   break;
            }
            backend.send(new Presence(jid, statusText, status, messageText));
        }
    }

    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                showStatusMenu();
            }
        }
    }
}
