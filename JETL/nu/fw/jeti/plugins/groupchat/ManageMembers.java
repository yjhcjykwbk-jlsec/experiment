package nu.fw.jeti.plugins.groupchat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.groupchat.GroupchatWindow.Actor;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCGetList;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCSetList;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCSetListListener;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUser;
import nu.fw.jeti.plugins.groupchat.events.UserListener;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.TableSorter;


/**
 * @author Martin Forssen
 */

public class ManageMembers extends JFrame
    implements UserListener, ListSelectionListener, IQMUCSetListListener
{
    public final static int AFFILIATION = 0;
    public final static int ROLE = 1;

    private JPanel jPanel1 = new JPanel();
    private JPanel jPanel2 = new JPanel();
    private JPanel jPanel3 = new JPanel();
    private JPanel jPanel4 = new JPanel();
    private JPanel jPanel5 = new JPanel();

    private JTable tabMembers;
    private JLabel jLabel1 = new JLabel();
    private JComboBox cmbRole = new JComboBox();
    private JComboBox cmbAffiliation = new JComboBox();
    private JLabel jLabel2 = new JLabel();
    private JButton btnAdd = new JButton();
    private JButton btnClose = new JButton();
    private Backend backend;
    private JID roomJID;
    private MyTableModel tableModel;
    private TableSorter sorter;
    private ListSelectionModel lsModel;
    private XMUCUser selectedUser;

    static private String[] columnNames = {
        I18N.gettext("groupchat.JID"),
        I18N.gettext("groupchat.Affiliation"),
        I18N.gettext("groupchat.Role")
    };
    private int[] affiliations = {
        XMUCUser.OWNER,
        XMUCUser.ADMIN,
        XMUCUser.MEMBER,
        XMUCUser.OUTCAST,
        XMUCUser.NONE
    };
    private int[] roles = {
        XMUCUser.MODERATOR,
        XMUCUser.PARTICIPANT,
        XMUCUser.VISITOR,
        XMUCUser.NONE
    };

    public ManageMembers(Backend backend, JID roomJID,
                         AbstractListModel roosterList)
    {
        this.backend = backend;
        this.roomJID = roomJID;
        tableModel = new MyTableModel(roosterList);
        try {
            init();
        } catch(Exception e) {
            e.printStackTrace();
        }
        tableModel.intervalAdded(
            new ListDataEvent(roosterList, ListDataEvent.INTERVAL_ADDED,
                              0, roosterList.getSize()-1));
        new IQMUCGetList(backend, roomJID, true, XMUCUser.OWNER, this);
        new IQMUCGetList(backend, roomJID, true, XMUCUser.ADMIN, this);
        new IQMUCGetList(backend, roomJID, false, XMUCUser.MODERATOR, this);
        new IQMUCGetList(backend, roomJID, true, XMUCUser.MEMBER, this);
        new IQMUCGetList(backend, roomJID, true, XMUCUser.OUTCAST, this);
        new IQMUCGetList(backend, roomJID, false, XMUCUser.PARTICIPANT, this);
    }

    private void init() throws Exception
    {
        setTitle(I18N.gettext("groupchat.Manage_Members"));
    	setIconImage(StatusIcons.getImageIcon("jeti").getImage());

        sorter = new TableSorter(tableModel);
        tabMembers = new JTable(sorter);
        sorter.setTableHeader(tabMembers.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);
        JScrollPane scrollPane = new JScrollPane(tabMembers);
        tabMembers.setPreferredScrollableViewportSize(new Dimension(300, 200));
        lsModel = tabMembers.getSelectionModel();
        lsModel.addListSelectionListener(this);

        TableColumnModel tcm = tabMembers.getColumnModel();
        for (int i=0; i<tabMembers.getColumnCount(); i++) {
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(SwingConstants.CENTER);
            tcm.getColumn(i).setCellRenderer(r);
            int width = 80;
            if (i == 0) {
                width = 150;
            }
            tcm.getColumn(i).setPreferredWidth(width);
        }

    	I18N.setTextAndMnemonic("groupchat.Role",jLabel1);
    	jLabel1.setLabelFor(cmbRole);
        cmbRole.setPreferredSize(new Dimension(200, 21));
        cmbRole.setEnabled(false);
        for (int i=0; i<roles.length; i++) {
            cmbRole.addItem(XMUCUser.getStringRole(roles[i]));
        }
        cmbRole.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateMembersRole(roles[cmbRole.getSelectedIndex()]);
                }
            }
        });

        I18N.setTextAndMnemonic("groupchat.Affiliation",
                                jLabel2);
        jLabel2.setLabelFor(cmbAffiliation);
        cmbAffiliation.setPreferredSize(new Dimension(200, 21));
        cmbAffiliation.setEnabled(false);
        for (int i=0; i<affiliations.length; i++) {
            cmbAffiliation.addItem(
                XMUCUser.getStringAffiliation(affiliations[i]));
        }
        cmbAffiliation.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateMembersAff(
                        affiliations[cmbAffiliation.getSelectedIndex()]);
                }
            }
        });

        btnAdd.setPreferredSize(new Dimension(200, 21));
        btnAdd.setText(I18N.gettext("groupchat.Add_JID") + "...");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnAdd_actionPerformed(e);
            }
        });
        
        Action closeAction =
            new AbstractAction(I18N.gettext("Close")) {
                public void actionPerformed(ActionEvent e) {
                    btnClose_actionPerformed(e);
                }
            };
        btnClose.setAction(closeAction);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.getActionMap().put("close", closeAction);
        layeredPane.getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "close");
       
        getRootPane().setDefaultButton(btnClose);

        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel1.add(jLabel1, null);
        jPanel1.add(cmbRole, null);
        jPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel2.add(jLabel2, null);
        jPanel2.add(cmbAffiliation, null);
        jPanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel3.add(btnAdd, null);

        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));
        jPanel4.add(jPanel2);
        jPanel4.add(jPanel1);
        jPanel4.add(jPanel3);

        jPanel5.setLayout(new BorderLayout());
        jPanel5.add(jPanel4, BorderLayout.PAGE_START);
        jPanel5.add(btnClose, BorderLayout.PAGE_END);

        this.getContentPane().add(jPanel5, BorderLayout.LINE_END);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }    

    void btnAdd_actionPerformed(ActionEvent e)
    {
        String user = JOptionPane.showInputDialog(
            this,
            I18N.gettext("groupchat.Add_JID"),
            I18N.gettext("main.main.popup.JID_of_user_to_add"),
            JOptionPane.QUESTION_MESSAGE);
        if (user == null || user.equals("")) {
            return;
        }
        JID jid = null;
        try {
            jid = JID.checkedJIDFromString(user);
        } catch (InstantiationException ex) {
            Popups.errorPopup(ex.getMessage(),
                              I18N.gettext("main.error.Wrong_Jabber_Identifier"));
            return;
        }
        if (jid == null || jid.getUser() == null) {
            JOptionPane.showMessageDialog(null,
                                          "There is no user named '"
                                          + user + "'", "Unknown user",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.addUser(new XMUCUser(jid, XMUCUser.NONE, XMUCUser.NONE));
    }

    void btnClose_actionPerformed(ActionEvent e)
    {
        dispose();
    }

    void updateMembersAff(int affiliation) {
        if (selectedUser != null
            && selectedUser.getAffiliation() == affiliation) {
            return;
        }
        int[] rows = tabMembers.getSelectedRows();
        Vector toChange = new Vector(rows.length);
        for (int i=0; i<rows.length; i++) {
            XMUCUser u = tableModel.getUserAt(sorter.modelIndex(rows[i]));
            if (u.getAffiliation() != affiliation) {
                toChange.add(u.getJID());
            }
        }
        new IQMUCSetList(backend, roomJID, true, affiliation, toChange, this);
    }

    void updateMembersRole(int role) {
        if (selectedUser != null
            && selectedUser.getRole() == role) {
            return;
        }
        int[] rows = tabMembers.getSelectedRows();
        Vector toChange = new Vector(rows.length);
        for (int i=0; i<rows.length; i++) {
            XMUCUser u = tableModel.getUserAt(sorter.modelIndex(rows[i]));
             if (u.getRole() != role) {
                 toChange.add(u.getJID());
             }
        }
        new IQMUCSetList(backend, roomJID, false, role, toChange, this);
    }

    /*
     * IQMUCSetListListener interface
     */
    public void listSetOk(Vector jids, boolean aff, int value) {
        for (int i=0; i<jids.size(); i++) {
            JID jid = (JID)jids.get(i);
            XMUCUser oldUser = tableModel.getUser(jid);
            if (oldUser != null) {
                XMUCUser user;
                if (aff) {
                    user = new XMUCUser(jid, value, oldUser.getRole());
                } else {
                    user = new XMUCUser(jid, oldUser.getAffiliation(), value);
                }
                tableModel.addUser(user);
            }
        }
    }

    /*
     * UserListener interface
     */
    public void userResult(List users) {
        for (Iterator i = users.iterator(); i.hasNext();) {
            tableModel.addUser((XMUCUser)i.next());
        }
    }

    /*
     * ListSelectionListener interface
     */
    public void valueChanged(ListSelectionEvent e) {
        boolean enabled;
        int roleIndex = -1;
        int affiliationIndex = -1;

        selectedUser = null;
        if (lsModel.isSelectionEmpty()) {
            enabled = false;
        } else {
            if (lsModel.getMinSelectionIndex()
                == lsModel.getMaxSelectionIndex()) {
                int i;
                selectedUser =
                    tableModel.getUserAt(sorter.modelIndex(lsModel.getMinSelectionIndex()));
                for (i = roles.length-1;
                     i >= 0 && roles[i] != selectedUser.getRole();
                     i--);
                roleIndex = i;
                for (i=affiliations.length-1;
                     i>=0 && affiliations[i] != selectedUser.getAffiliation();
                     i--);
                affiliationIndex = i;
            }
            enabled = true;
        }
        cmbRole.setSelectedIndex(roleIndex);
        cmbAffiliation.setSelectedIndex(affiliationIndex);
        cmbRole.setEnabled(enabled);
        cmbAffiliation.setEnabled(enabled);
    }

    /*
     * MyTableModel class
     */
    private class MyTableModel extends AbstractTableModel 
        implements ListDataListener {
        Vector data = new Vector(30);
        AbstractListModel roosterList;

        public MyTableModel(AbstractListModel roosterList) {
            this.roosterList = roosterList;
            roosterList.addListDataListener(this);
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            XMUCUser user = (XMUCUser)data.get(row);
            switch(col) {
            case 0:
                return user.getJID().toStringNoResource();
            case 1:
                return user.getStringAffiliation();
            case 2:
                return user.getStringRole();
            }
            return null;
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void addUser(XMUCUser user) {
            for (int i=0; i<data.size(); i++) {
                XMUCUser oldUser = (XMUCUser)data.get(i);
                if (user.getJID().equals(oldUser.getJID())) {
                    data.setElementAt(user, i);
                    fireTableRowsUpdated(i, i);
                    if (oldUser == selectedUser) {
                        selectedUser = user;
                        valueChanged(null);
                    }
                    return;
                }
            }
            data.add(user);
            fireTableRowsInserted(data.size()-1, data.size()-1);
        }

        public XMUCUser getUser(JID jid) {
            for (int i=0; i<data.size(); i++) {
                XMUCUser user = (XMUCUser)data.get(i);
                if (jid.equals(user.getJID())) {
                    return user;
                }
            }
            return null;
        }

        public XMUCUser getUserAt(int row) {
            return (XMUCUser)data.get(row);
        }

        /*
         * ListDataListener interface
         */
        public void intervalAdded(ListDataEvent e) {
            contentsChanged(e);
        }
        public void intervalRemoved(ListDataEvent e) {
            // Do nothing
        }
        public void contentsChanged(ListDataEvent e) {
            for (int i=e.getIndex0(); i<=e.getIndex1(); i++) {
                Actor a = (Actor)roosterList.getElementAt(i);
                tableModel.addUser(new XMUCUser(a.getFullJID(),
                                                a.getAffiliation(),
                                                a.getRole()));
            }
        }
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
