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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.images.IconPrefPanel;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 * @version 1.0
 * window for the user and plugin preferences
 */

public class OptionsWindow extends JFrame
{
	//private JTabbedPane jTabbedPane1 = new JTabbedPane();
	private JPanel cards = new JPanel(new CardLayout());
	private JTree tree;
	private JPanel jPanel2 = new JPanel();
	private JButton btnApply = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnOK = new JButton();
	private Map panels = new HashMap();
	private Map nodes = new HashMap(); //needed to delete from tree
	private Backend backend;
	private PreferencesPanel pluginPanel;
	
	public OptionsWindow(Backend backend)
	{
		this.backend = backend;
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.options.Options"));
		I18N.setTextAndMnemonic("Apply",btnApply);
		btnApply.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnApply_actionPerformed(e);
			}
		});
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
		getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnOK_actionPerformed(e);
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		jPanel2.add(btnOK);
		jPanel2.add(btnCancel);
		jPanel2.add(btnApply);
		
		pluginPanel = new PluginsPanel(this);
		panels.put(I18N.gettext("main.options.Plugins"), pluginPanel);
		cards.add(pluginPanel, I18N.gettext("main.options.Plugins"));
		PreferencesPanel panel = null;
		node.add(new DefaultMutableTreeNode(I18N.gettext("main.options.Plugins")));

		panel = new StdPreferencesPanel(backend);
		panels.put(I18N.gettext("main.options.Standard"), panel);
		cards.add(panel, I18N.gettext("main.options.Standard"));
		node.add(new DefaultMutableTreeNode(I18N.gettext("main.options.Standard")));
		
		//if (new File(Start.path + "plugins" + File.separator + "rostericons").exists())
		{
			panel = new IconPrefPanel("rostericons");
			panels.put(I18N.gettext("main.options.RosterIcons"), panel);
			cards.add(panel, I18N.gettext("main.options.RosterIcons"));
			node.add(new DefaultMutableTreeNode(I18N.gettext("main.options.RosterIcons")));
		}

		
		for (Iterator i = PluginsInfo.loadedPreferencePanels.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry temp = (Map.Entry) i.next();
			try
			{
				Class c = (Class) temp.getValue();
				Constructor co = c.getConstructor(new Class[] { Backend.class });
				panel = (PreferencesPanel) co.newInstance(new Object[] { backend });
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				break;
			}
            if (!panel.inhibited()) {
                String name = (String) temp.getKey();
                panels.put(name, panel);
                cards.add(panel, name);
                DefaultMutableTreeNode mnode= new DefaultMutableTreeNode(name);
                nodes.put(name, mnode);
                node.add(mnode);
            }
		}
		panel = null;
		
		tree = new JTree(new DefaultTreeModel(node));
		tree.setPreferredSize(new Dimension(100,10));
		tree.setRootVisible(false);
		tree.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		DefaultTreeCellRenderer renderer =	new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		tree.setCellRenderer(renderer);
		
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e) 
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node == null) return;
				String nodeInfo = (String) node.getUserObject();
				if(panels.containsKey(nodeInfo))
				{
					((CardLayout)cards.getLayout()).show(cards, nodeInfo);
				}
			}
		});
		getContentPane().add(cards, BorderLayout.CENTER);
		getContentPane().add(tree, BorderLayout.WEST);
		getContentPane().add(jPanel2, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}

	public void removePanel(String name)
	{
		if(panels.containsKey(name))
		{	
			cards.remove((Component) panels.get(name));
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			model.removeNodeFromParent((DefaultMutableTreeNode)nodes.get(name));
			nodes.remove(name);
			panels.remove(name);
		}
	}

	public void addPanel(String name)
	{
		PreferencesPanel panel = null;
		try
		{
			Class c = (Class) PluginsInfo.loadedPreferencePanels.get(name);
			Constructor co = c.getConstructor(new Class[] { Backend.class });
			panel = (PreferencesPanel) co.newInstance(new Object[] { backend });
		}
		catch (Exception e2)
		{
			//System.err.println("no preferences panel");
			return;
		}
        if (!panel.inhibited()) {
            panels.put(name, panel);
            cards.add(panel, name);
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            DefaultMutableTreeNode mnode= new DefaultMutableTreeNode(name);
            nodes.put(name, mnode);
            model.insertNodeInto(mnode,(MutableTreeNode)model.getRoot(),((TreeNode)model.getRoot()).getChildCount());
        }
	}

	public void savePreferences()
	{
		for (Iterator i = panels.values().iterator(); i.hasNext();)
		{
			PreferencesPanel pp = (PreferencesPanel) i.next();
			if(pp!=pluginPanel) pp.savePreferences();
		}
		pluginPanel.savePreferences();
		if(Start.applet)backend.send(new InfoQuery("set",new IQPrivate(new nu.fw.jeti.applet.JetiPrivatePreferencesExtension(new Preferences()))));
		else Preferences.save();
	}

	void btnApply_actionPerformed(ActionEvent e)
	{
		savePreferences();
	}

	void btnCancel_actionPerformed(ActionEvent e)
	{
		panels = null;
		cards.removeAll();
		cards = null;
		this.dispose();
	}

	void btnOK_actionPerformed(ActionEvent e)
	{
		savePreferences();
		btnCancel_actionPerformed(null);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
