/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 3-sep-2004
 */
 
package nu.fw.jeti.plugins.groupchat;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.groupchat.elements.Conference;
import nu.fw.jeti.plugins.groupchat.elements.PrivateBookmarkExtension;
import nu.fw.jeti.plugins.groupchat.elements.XMUC;
import nu.fw.jeti.plugins.groupchat.handlers.PrivateBookmarkHandler;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.TableSorter;

/**
 * @author E.S. de Boer
 *
 */
public class Bookmarks implements StatusChangeListener
{
	private Backend backend;
	private JMenu groupchatMenu;
	private List urls;
	private List conferences;
    private ConferencesTableModel conferencesTableModel
        = new ConferencesTableModel();
    private JButton delButton;

    static private String[] columnNames = {
        I18N.gettext("groupchat.Room"),
        I18N.gettext("groupchat.Auto_open")
    };
	
	public Bookmarks(Backend backend, JMenu menu)
	{
		this.backend = backend;
		groupchatMenu = menu;
		backend.addExtensionHandler("storage:bookmarks",new PrivateBookmarkHandler(this));
		backend.addListener(StatusChangeListener.class,this);
	}
	
	private void manageBookmarks()
	{
        if (conferences==null || conferences.isEmpty()) {
            return;
        }
        final JDialog dialog = new JDialog(
            backend.getMainFrame(),
            I18N.gettext("groupchat.Manage_Bookmarks"));
		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		JLabel lbl = new JLabel(I18N.gettext("groupchat.bookmarks"));
		lbl.setAlignmentX(0.5f);
		contentPane.add(lbl);
        TableSorter sorter = new TableSorter(conferencesTableModel);
        final JTable table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);
        table.setAlignmentX(0.5f);
        table.setPreferredScrollableViewportSize(new Dimension(200, 100));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel sm = table.getSelectionModel();
        sm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
                delButton.setEnabled(!lsm.isSelectionEmpty());
            }
        });
		final JList list = new JList(conferences.toArray());
		list.setAlignmentX(0.5f);

        JScrollPane listScroller = new JScrollPane(table);
        contentPane.add(listScroller);
        delButton =
            new JButton(I18N.gettext("groupchat.delete_bookmark"));
        delButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                int i = table.getSelectedRow();
                if(i != -1) {
                    conferences.remove(i);
                    conferencesTableModel.update();
                    storeConferences();
					dialog.dispose();
				}
			}
		});
        delButton.setEnabled(false);
        contentPane.add(delButton);
        delButton.setAlignmentX(0.5f);
        JButton button = new JButton();
        Action closeAction = new AbstractAction(I18N.gettext("Close"))
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
			}
		};
        button.setAction(closeAction);
		button.setAlignmentX(0.5f);
		contentPane.add(button);
		dialog.pack();
		dialog.setLocationRelativeTo(backend.getMainFrame());
		dialog.show();
		
	}
	
	public void newBookmarks(PrivateBookmarkExtension bookmarks)
	{
		urls = bookmarks.getURLs();
		conferences = bookmarks.getConferences();
        conferencesTableModel.update();
        if(!conferences.isEmpty()) addBookmarks(true);
	}
	
	public void addBookmark(final JID jid, final String nick)
	{
		//String name = JOptionPane.showInputDialog(null,I18N.gettext("groupchat.The_name_of_the_bookmark"), I18N.gettext("groupchat.Bookmark_name", "groupchat"), JOptionPane.QUESTION_MESSAGE);
		final JDialog dialog = new JDialog(); 
		Container contantPane =dialog.getContentPane();
		contantPane.setLayout(new BoxLayout(contantPane,BoxLayout.Y_AXIS));
		contantPane.add(new JLabel(I18N.gettext("groupchat.The_name_of_the_bookmark")));
        final JTextField txtName = new JTextField(jid.getUser());
		contantPane.add(txtName);
		final JCheckBox chkAutoJoin = new JCheckBox(I18N.gettext("groupchat.Automatically_start_groupchat_on_startup?"));
		contantPane.add(chkAutoJoin);
		JPanel panel = new JPanel();
		JButton button = new JButton(I18N.gettext("OK"));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String name = txtName.getText();
				if(name.equals(""))
				{
					dialog.dispose();
					return;
				}
                if(conferences==null) {
                    conferences = new ArrayList(10);
                } else {
                    for(Iterator i = conferences.iterator();i.hasNext();)
                    {
                        Conference conference = (Conference)i.next();
                        if (conference.getJid().equals(jid)) {
                            conferences.remove(conference);
                            break;
                        }
                    }
                }
                conferences.add(
                    new Conference(name,jid, chkAutoJoin.isSelected(),
                                   nick, null));
                conferencesTableModel.update();
                storeConferences();
				dialog.dispose();
			}
		});
		panel.add(button);
		dialog.getRootPane().setDefaultButton(button);
		button = new JButton();
		Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
			}
		};
		button.setAction(cancelAction);
		panel.add(button);
		contantPane.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.show();
		
	}
		
    private void addBookmarks(boolean open)
	{
		groupchatMenu.removeAll();
		JMenuItem menuItem = new JMenuItem();
		I18N.setTextAndMnemonic("groupchat.Join/Create", menuItem, true);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new GroupchatSignin(backend).show();
			}
		});
		groupchatMenu.add(menuItem);
		if(!conferences.isEmpty())
		{
			menuItem = new JMenuItem();
            I18N.setTextAndMnemonic("groupchat.Manage_Bookmarks",
                                    menuItem, true);
			menuItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					manageBookmarks();
				}
			});
			groupchatMenu.add(menuItem);
		}
		groupchatMenu.addSeparator();
		
		for(Iterator i = conferences.iterator();i.hasNext();)
		{
			Conference conference = (Conference)i.next();
			JID room = conference.getJid();
			String nick = conference.getNick();
			if(nick==null) nick = backend.getMyJID().getUser();
			final JID jid = new JID(room.getUser(),room.getDomain(),nick);
            if(conference.autoJoins() && open)startGroupchat(jid);
			menuItem = new JMenuItem(conference.getName());
			menuItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
			   		startGroupchat(jid); 
				}
			});
			groupchatMenu.add(menuItem);
		}
	}
		
	private void startGroupchat(JID jid)
	{
		GroupchatWindow gcw = Plugin.getGroupchat(jid,backend);
		backend.send(new Presence(jid,"available",new XMUC()));
		gcw.show();
	}
	
    private void storeConferences() {
        backend.send(
            new InfoQuery("set",
                          new IQPrivate(
                              new PrivateBookmarkExtension(urls,
                                                           conferences))));
        addBookmarks(false);
    }
	
//	status change listener
	public void exit(){}
	
	public void connectionChanged(boolean online)
	{
		if(online) backend.send(new InfoQuery("get",new IQPrivate(new PrivateBookmarkExtension())));
	}
	
	public void ownPresenceChanged(int show,String status){}

    private class ConferencesTableModel extends AbstractTableModel {
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return conferences.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            Conference conference = (Conference)conferences.get(row);
            switch(col) {
            case 0:
                return conference.getName();
            case 1:
                return new Boolean(conference.autoJoins());
            }
            return null;
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public void setValueAt(Object value, int row, int col) {
            Conference conference = (Conference)conferences.get(row);
            switch(col) {
            case 0:
                conference.setName((String)value);
                break;
            case 1:
                conference.setAutoJoins(((Boolean)value).booleanValue());
                break;
            }
            storeConferences();
        }

        void update() {
            fireTableDataChanged();
        }
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
