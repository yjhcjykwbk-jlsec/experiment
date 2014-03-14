package nu.fw.jeti.ui;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQXRoster;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.RosterItem;
import nu.fw.jeti.util.I18N;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Title: im Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author E.S. de Boer
 * @version 1.0
 */

public class GroupDialog extends JDialog
{
	private JPanel btnPanel = new JPanel();
	private JLabel lbltekst = new JLabel();
	private JComboBox cmbGroup;
	private JButton btnCancel = new JButton();
	private JButton btnOK = new JButton();
	private String[] groups;
	//private String operation;
	private Backend backend;
	private String currentGroup;
	private JIDStatus jidStatus;

	public GroupDialog(JIDStatus jidStatus, Backend backend)
	{
		super(backend.getMainFrame(), I18N.gettext("main.main.rostermenu.Add_to_Group"), false);
		groups = backend.getAllGroups();
		String[] temp = new String[groups.length - 1];
		int i = 0;
		for (int tel = 0; tel < groups.length; tel++)
		{//remove group wich contains jid
			if (!jidStatus.isGroupPresent(groups[tel]))
			{
				temp[i] = groups[tel];
				i++;
			}
		}
		groups = new String[i];
		System.arraycopy(temp, 0, groups, 0, i);
		init(jidStatus, backend);
		lbltekst.setText(MessageFormat.format(I18N.gettext("main.popup.Add_{0}_to:"), new Object[] { jidStatus.getNick()}));
		pack();
		setLocationRelativeTo(null);
	}

	private void init(JIDStatus jidStatus, Backend backend)
	{
		this.backend = backend;
		this.jidStatus = jidStatus;
		try
		{
			jbInit();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public GroupDialog(String group, JIDStatus jidStatus, Backend backend)
	{
		super(backend.getMainFrame(), I18N.gettext("main.main.rostermenu.Change_Group"), false);
		groups = backend.getAllGroups();
		String[] temp = new String[groups.length - 1];
		int i = 0;
		for (int tel = 0; tel < groups.length; tel++)
		{//remove group wich contains jid
			if (!jidStatus.isGroupPresent(groups[tel]))
			{
				temp[i] = groups[tel];
				i++;
			}
		}
		groups = new String[i];
		System.arraycopy(temp, 0, groups, 0, i);

		currentGroup = group;
		init(jidStatus, backend);
		lbltekst.setText(MessageFormat.format(I18N.gettext("main.popup.Change_{0}_from_{1}_to:"), new Object[] { jidStatus.getNick(),
				group}));
		pack();
		setLocationRelativeTo(null);
	}

	void jbInit() throws Exception
	{
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getRootPane().setDefaultButton(btnOK);

		cmbGroup = new JComboBox(groups);
		cmbGroup.setAlignmentX((float) 0.0);
		cmbGroup.setEditable(true);

		Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				btnCancel_actionPerformed(e);
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

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 3, 0, 3);
        c.weightx = 1.0;

        getContentPane().add(lbltekst, c);
        getContentPane().add(cmbGroup, c);
        btnPanel.add(btnOK, null);
        btnPanel.add(btnCancel, null);
        getContentPane().add(btnPanel, c);
	}

	void btnOK_actionPerformed(ActionEvent e)
	{
		String group = (String) cmbGroup.getSelectedItem();
		if (!group.equals(" "))
		{
			nu.fw.jeti.util.StringArray groups = jidStatus.getGroupsCopy();
			if (currentGroup != null)
			{
				//backend.changeGroup(jidStatus,currentGroup,group);
				groups.remove(currentGroup);
				groups.add(group);
				IQXRoster roster = new IQXRoster(new RosterItem(jidStatus.getJID(), jidStatus.getNick(), null, null, groups));
				backend.send(new InfoQuery("set", roster));
			} else
			{
				if (!jidStatus.isGroupPresent(group))
				{
					groups.add(group);
					IQXRoster roster = new IQXRoster(new RosterItem(jidStatus.getJID(), jidStatus.getNick(), null, null, groups));
					backend.send(new InfoQuery("set", roster));
					//backend.addGroup(jidStatus,group);
				}
			}
		}
		this.dispose();
	}

	void btnCancel_actionPerformed(ActionEvent e)
	{
		this.dispose();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
