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
 *  
 *  30-12-2004
 */

package nu.fw.jeti.images;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.ui.models.ListTableModel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.TableSorter;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IconPrefPanel extends PreferencesPanel
{
	private BorderLayout borderLayout1 = new BorderLayout();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTable jTable1;
	private JButton jButton1 = new JButton();
    protected ListTableModel dataModel;
    private String iconType;

	public IconPrefPanel(String type)
	{
		iconType = type;
		dataModel = new ListTableModel(
            new String[] {
                I18N.gettext("emoticons.Name"),
                I18N.gettext("emoticons.Enabled"),
                I18N.gettext("emoticons.Description"),
                I18N.gettext("emoticons.Type") },
            Preferences.getPlugableCopy(iconType));

        TableSorter sorter = new TableSorter(dataModel);
        jTable1 = new JTable(sorter);
        sorter.setTableHeader(jTable1.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);
        //jTable1.setDefaultRenderer(String.class, new IconRenderer());
		jTable1.setRowSelectionAllowed(false);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(15);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(40);
        
        String c[];
        if(iconType.equals("rostericons"))
        {
        	c =new String[]{"jabber","msn","icq",
        		"aim","yahoo","gadu-gadu","sms","smtp","unknown"};
        }
        else
        {
        	c =new String[]{"jabber","msn","icq",
            		"aim","yahoo","gadu-gadu","groupchat","default","unknown"};
        }
        JComboBox comboBox =  new JComboBox(c);
        comboBox.setEditable(true);
        jTable1.getColumnModel().getColumn(3).
				setCellEditor(new DefaultCellEditor(comboBox));
                
		this.setLayout(borderLayout1);
		if (new File(Start.path + "plugins" + File.separator + iconType).exists())
		{	
			I18N.setTextAndMnemonic("emoticons.Scan_Emoticons",jButton1);
			jButton1.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					jButton1_actionPerformed(e);
				}
			});
			this.add(jButton1, BorderLayout.SOUTH);
		}
		this.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(jTable1, null);
	}

	void jButton1_actionPerformed(ActionEvent e)
	{
		new Icons(iconType).scanRosterIcons();
		dataModel.reload(Preferences.getPlugableCopy(iconType));
	}

	public void savePreferences()
	{
		List oldStatus = Preferences.getPlugable("rostericons");
		List newStatus = dataModel.getPlugins();
		
		StatusIcons plugin = null;
		boolean reloadNeeded=false;
		for (int i = 0; i < newStatus.size(); i++)
		{
			Object[] newS = (Object[]) newStatus.get(i);
			Object[] oldS = (Object[]) oldStatus.get(i);
			if (!newS[3].equals(oldS[3]))
			{
				reloadNeeded = true;
				if (plugin == null) plugin = new StatusIcons();
				plugin.unloadRosterIcon((String) oldS[3]);
				plugin.unloadRosterIcon((String) newS[3]);
				oldS[3]=newS[3];
			}
			if (!newS[1].equals(oldS[1]))
			{
				oldS[1] = newS[1];
				if (((Boolean) oldS[1]).booleanValue())
				{
					if(!reloadNeeded)
					{
						//load
						if (plugin == null)	plugin = new StatusIcons();
						plugin.loadRosterIcon((String) oldS[4],(String)oldS[3]);
					}
				}
				else
				{
					//unload
					if (plugin == null) plugin = new StatusIcons();
					plugin.unloadRosterIcon((String) oldS[3]);
				}
			}
		}
		if(reloadNeeded)
		{
			for (int i = 0; i < newStatus.size(); i++)
			{
				Object[] oldS = (Object[]) oldStatus.get(i);
				if (((Boolean) oldS[1]).booleanValue())
				{
					if (plugin == null)	plugin = new StatusIcons();
					plugin.reloadRosterIcon((String) oldS[4],(String)oldS[3]);
				}
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
