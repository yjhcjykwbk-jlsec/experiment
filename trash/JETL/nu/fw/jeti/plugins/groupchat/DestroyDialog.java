package nu.fw.jeti.plugins.groupchat;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.groupchat.elements.IQMUCOwnerDestroy;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.util.I18N;


/**
 * @author Martin Forssen
 */

public class DestroyDialog extends JFrame
{
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JTextField txtReason = new JTextField();
    private JPanel jPanel4 = new JPanel();
    private JButton btnCancel = new JButton();
    private JButton btnOK = new JButton();
    private Backend backend;
    private JID roomJID;

    public DestroyDialog(Backend backend, JID roomJID)
    {
        this.backend = backend;
        this.roomJID = roomJID;
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        setTitle(MessageFormat.format(
                     I18N.gettext("groupchat.Destroy_{0}"),
                     new Object[]{roomJID.toString()}));
    	setIconImage(StatusIcons.getImageIcon("jeti").getImage());
    	I18N.setTextAndMnemonic("groupchat.Reason",jLabel1);
    	jLabel1.setLabelFor(txtReason);
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                                                 BoxLayout.Y_AXIS));
        
        Action cancelAction =
            new AbstractAction(I18N.gettext("Cancel")) {
                public void actionPerformed(ActionEvent e) {
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
        txtReason.setPreferredSize(new Dimension(200, 21));

        this.getContentPane().add(jPanel1);
        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jPanel1.add(jLabel1, null);
        jPanel1.add(txtReason, null);
        jPanel4.add(btnOK, null);
        jPanel4.add(btnCancel, null);
        this.getContentPane().add(jPanel4, null);
        pack();
    }    

    void btnOK_actionPerformed(ActionEvent e)
    {
        new IQMUCOwnerDestroy(backend, roomJID, txtReason.getText());
        dispose();
    }

    void btnCancel_actionPerformed(ActionEvent e)
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
