package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.InfoQueryBuilder;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.elements.RosterBuilder;
import nu.fw.jeti.jabber.elements.RosterItemBuilder;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author E.S. de Boer
 */

public class AddContact extends JDialog
{
	//JPanel panel1 = new JPanel();
	private Box panel1 = Box.createVerticalBox();
	private JLabel jLabel1 = new JLabel();
	private JTextField txtContact = new JTextField();
	private JLabel jLabel2 = new JLabel();
	private JTextField txtNick = new JTextField();
	private JLabel jLabel3 = new JLabel();
	private JComboBox cmbGroup;
	private JComboBox cmbService;
	private JPanel jPanel1 = new JPanel();
	private JButton btnCancel = new JButton();
	private JButton btnOK = new JButton();
	//private String[] groups;
	private Backend backend;
	private Transport service;

	public AddContact(JFrame frame, Backend backend)
	{
		super(frame, I18N.gettext("main.AddContact.Add_Contact"), false);
		this.backend = backend;
		Object[] transports = makeTransportList(backend.getAvailableTransports());
		cmbService = new JComboBox(transports);
		cmbService.setAlignmentX(0.0f);
		cmbService.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent e)
			{
				service = (Transport) e.getItem();
				jLabel1.setText(service.getLabelName());
			}
		});
		service = (Transport) transports[0];
		//JLabel jLabel0 = new JLabel(I18N.gettext("main.AddContact.Select_Service"));
		JLabel jLabel0 = new JLabel();
		I18N.setTextAndMnemonic("main.AddContact.Select_Service",jLabel0);
		jLabel0.setLabelFor(cmbService);
		jLabel0.setHorizontalAlignment(SwingConstants.LEFT);
		panel1.add(jLabel0);
		panel1.add(cmbService);
		jbInit();
		pack();
		setLocationRelativeTo(frame);
	}

	public AddContact(JID jid, JFrame frame, Backend backend)
	{
		super(frame, I18N.gettext("main.AddContact.Add_Contact"), false);
		this.backend = backend;
		Object[] transports = makeTransportList(backend.getAvailableTransports());
		String server = jid.getDomain();
		for(int i=0;i<transports.length;i++)
		{
			if(server.equals(((Transport)transports[i]).getServer()))
			{
				service = (Transport)transports[i];
				if(service.equalsToType("msn"))
				{
					txtContact.setText(jid.getUser().replace('%', '@'));
				}
				else txtContact.setText(jid.getUser());
			}
		}
		if(service==null)
		{//if no recognized transport then jabber
			service = (Transport) transports[0];
			txtContact.setText(jid.toStringNoResource());
		}
		jLabel1.setText(service.getLabelName());
		txtContact.setEditable(false);
		txtContact.setHorizontalAlignment(SwingConstants.LEFT);
		jbInit();
		pack();
		setLocationRelativeTo(frame);
	}

	void jbInit()
	{
		String[] groups = backend.getAllGroups();
		if (groups.length == 0) groups = new String[] { I18N.gettext("main.main.roster.Friends") };
		getRootPane().setDefaultButton(btnOK);
		
		
		jLabel1.setText(service.getLabelName());

		txtContact.setHorizontalAlignment(SwingConstants.LEADING);
		txtNick.setHorizontalAlignment(SwingConstants.LEFT);
		cmbGroup = new JComboBox(groups);
		cmbGroup.setAlignmentX(0.0f);
		cmbGroup.setEditable(true);
		jPanel1.setAlignmentX(0.0f);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		panel1.add(Box.createHorizontalGlue());

		//jLabel2.setText(I18N.gettext("main.AddContact.Nickname"));
		I18N.setTextAndMnemonic("main.AddContact.Nickname",jLabel2);
		jLabel2.setLabelFor(txtNick);
		jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
		//jLabel3.setText(I18N.gettext("main.AddContact.Group"));
		I18N.setTextAndMnemonic("main.AddContact.Group",jLabel3);
		jLabel3.setLabelFor(cmbGroup);
		jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
			
		Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		};
		btnCancel.setAction(cancelAction);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
		
		btnOK.setText(I18N.gettext("OK"));
		btnOK.addActionListener(new java.awt.event.ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btnOK_actionPerformed(e);
			}
		});
		getContentPane().add(panel1, BorderLayout.CENTER);
		panel1.add(jLabel1, null);
		panel1.add(txtContact, null);
		panel1.add(jLabel2, null);
		panel1.add(txtNick, null);
		panel1.add(jLabel3, null);
		panel1.add(cmbGroup, null);
		panel1.add(jPanel1, null);
		jPanel1.add(btnOK, null);
		jPanel1.add(btnCancel, null);
	}

	void btnOK_actionPerformed(ActionEvent e)
	{
		JID contact = createJID();
		if (contact == null) return;
		backend.send(new Presence(contact, "subscribe", Presence.NONE, null));
		try
		{
			String nick = txtNick.getText();
			if (nick.equals("")) nick = contact.getUser();
			//still empty then use domain
			if (nick == null) nick = contact.getDomain();
			RosterBuilder rb = new RosterBuilder();
			RosterItemBuilder rib = new RosterItemBuilder();
			rib.addGroup((String) cmbGroup.getSelectedItem());
			rib.jid = contact;
			rib.name = nick;
			rib.ask = "subscribe";
			rb.addItem(rib.build());
			InfoQueryBuilder iqb = new InfoQueryBuilder();
			iqb.addExtension(rb.build());
			iqb.setType("set");
			backend.send(iqb.build());
		} catch (InstantiationException e2)
		{
			e2.printStackTrace();
		}

		//backend.subscribe(contact,txtNick.getText(),(String)cmbGroup.getSelectedItem());
		this.dispose();
	}

	private JID createJID()
	{
		JID contact = null;
		if (service.equalsToType("jabber") || service.equalsToType("other"))
		{
			try
			{
				contact = JID.checkedJIDFromString(txtContact.getText());
			} catch (InstantiationException e1)
			{
				Popups.errorPopup(e1.getMessage(), I18N.gettext("main.error.Wrong_Jabber_Identifier"));
			}
		} else
		{
			String contactname = txtContact.getText();
			if (service.equalsToType("aim"))
			{
				if (contactname.indexOf(' ') > -1)
				{//remove spaces
					StringBuffer temp = new StringBuffer();
					for (int i = 0; i < contactname.length(); i++)
					{
						char c = contactname.charAt(i);
						if (c != ' ') temp.append(c);
					}
					contactname = temp.toString();
				}
			} else if (service.equalsToType("msn")) contactname = contactname.replace('@', '%');
			if (JID.isValidUser(contactname)) contact = new JID(contactname, service.getServer());
			else Popups.errorPopup(MessageFormat.format(I18N.gettext("main.error.{0}_is_not_valid"), new Object[]{contactname}), I18N.gettext("main.error.Wrong_contact_name"));
		}
		return contact;
	}

	private Object[] makeTransportList(Map availableTransports)
	{
		List transports = new LinkedList();
		transports.add(new Transport("jabber", "Jabber", "JID", null));
		for (Iterator i = availableTransports.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			String type = (String) entry.getKey();
			if (type.equals("msn")) transports.add(new Transport(type, "MSN Messenger", I18N.gettext("main.AddContact.Address"), (JIDStatus) entry.getValue()));
			else if (type.equals("icq")) transports.add(new Transport(type, "ICQ", "AddContact.UIN", (JIDStatus) entry.getValue()));
			else if (type.equals("aim")) transports.add(new Transport(type, "AOL Instant Messenger", I18N.gettext("main.AddContact.Screen Name"), (JIDStatus) entry.getValue()));
			else if (type.equals("yahoo")) transports.add(new Transport(type, "Yahoo! Messenger", "ID", (JIDStatus) entry.getValue()));
		}
		transports.add(new Transport("other", I18N.gettext("main.AddContact.Other"), I18N.gettext("main.AddContact.Contactname@Transport:"), null));
		return transports.toArray();
	}

    class Transport
    {
        private String type;
        private String name;
        private String server;
        private String labelName;

        public Transport(String type, String name, String labelName, JIDStatus server)
        {
            this.type = type;
            this.name = name;
            this.labelName = labelName;
            if (server != null) this.server = server.getJID().getDomain();
        }

        public String getLabelName()
        {
            return labelName;
        }

        public String getServer()
        {
            return server;
        }

        public String toString()
        {
            return name;
        }

        public boolean equalsToType(String type)
        {
            return this.type.equals(type);
        }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */