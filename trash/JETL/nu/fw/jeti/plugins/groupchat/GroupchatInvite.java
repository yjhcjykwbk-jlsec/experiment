/*
 * Inivite an user to a group
 */
package nu.fw.jeti.plugins.groupchat;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUserInvite;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author Martin Forssen
 *
 */
public class GroupchatInvite extends JFrame {
    private JPanel userPanel = new JPanel();
    private JLabel userLabel = new JLabel();
    private JTextField txtUser = new JTextField();
    private JPanel roomPanel = new JPanel();
    private JLabel roomLabel = new JLabel();
    private JComboBox cmbRoom = new JComboBox();
    private JPanel reasonPanel = new JPanel();
    private JLabel reasonLabel = new JLabel();
    private JTextField txtReason = new JTextField();
    private JPanel butPanel = new JPanel();
    private JButton btnCancel = new JButton();
    private JButton btnOK = new JButton();

    private Backend backend;
    private JID user;
    private JID room;

    private GroupchatInvite(Backend backend, JID user, JID room, Map rooms) {
        this.backend = backend;
        this.user = user;
        if (room != null) {
            this.room = room;
        } else {
            initRooms(rooms);
        }
        try {
            init();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void inviteUser(Backend backend, JID user, Map rooms) {
        if (rooms.isEmpty()) {
            Popups.errorPopup(
                I18N.gettext("groupchat.You_are_not_in_any_rooms"),
                I18N.gettext("main.error.Error")); 
            return;
        }
        new GroupchatInvite(backend, user, null, rooms).show();
    }

    public static void inviteToRoom(Backend backend, JID room) {
        new GroupchatInvite(backend, null, room, null).show();
    }

    private void initRooms(Map rooms) {
        TreeSet sorted = new TreeSet();
        sorted.addAll(rooms.keySet());
        for (Iterator i = sorted.iterator(); i.hasNext();) {
            JID jid = (JID)i.next();
            cmbRoom.addItem(new JID(jid.getUser(), jid.getDomain()));
        }
    }

    private void init() throws Exception
    {
    	setIconImage(StatusIcons.getImageIcon("jeti").getImage());
        JComponent comp;

    	I18N.setTextAndMnemonic("groupchat.User",userLabel);
        if (user != null) {
            comp = new JLabel(user.toStringNoResource());
        } else {
            comp = txtUser;
        }
        userLabel.setLabelFor(comp);
        comp.setPreferredSize(new Dimension(200, 21));
        userPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        userPanel.add(userLabel, null);
        userPanel.add(comp, null);

    	I18N.setTextAndMnemonic("groupchat.Room",roomLabel);
        if (room != null) {
            comp = new JLabel(room.toString());
        } else {
            comp = cmbRoom;
        }
        roomLabel.setLabelFor(comp);
        comp.setPreferredSize(new Dimension(200, 21));
        roomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        roomPanel.add(roomLabel, null);
        roomPanel.add(comp, null);

    	I18N.setTextAndMnemonic("groupchat.Reason",reasonLabel);
        reasonLabel.setLabelFor(txtReason);
        txtReason.setPreferredSize(new Dimension(200, 21));
        reasonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        reasonPanel.add(reasonLabel, null);
        reasonPanel.add(txtReason, null);

        Action cancelAction =
            new AbstractAction(I18N.gettext("Cancel")) {
                public void actionPerformed(ActionEvent e)
                {
                    btnCancel_actionPerformed(e);
                }
            };
        btnCancel.setAction(cancelAction);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.getActionMap().put("cancel", cancelAction);
        layeredPane.getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
       
        btnOK.setText(I18N.gettext("OK"));
        getRootPane().setDefaultButton(btnOK);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnOK_actionPerformed(e);
            }
        });
        butPanel.add(btnOK, null);
        butPanel.add(btnCancel, null);

        getContentPane().setLayout(
            new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(userPanel);
        getContentPane().add(roomPanel);
        getContentPane().add(reasonPanel);
        getContentPane().add(butPanel);

        pack();
        setLocationRelativeTo(null);
    }

    void btnCancel_actionPerformed(ActionEvent e) {
        dispose();
    }

    void btnOK_actionPerformed(ActionEvent e) {
        if (user == null) {
            try {
                user = JID.checkedJIDFromString(txtUser.getText());
            } catch (InstantiationException e1) {
                Popups.errorPopup(
                    MessageFormat.format(
                        I18N.gettext("groupchat.{0}_is_an_invalid_user_name")
						, new Object[] {user}),I18N.gettext("groupchat.invalid_user_name")); 
                return;
            }
        }

        if (room == null) {
            room = (JID)cmbRoom.getSelectedItem();
        }

        backend.send(
            new Message(null, room,
                        new XMUCUserInvite(user, txtReason.getText())));
        dispose();
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
