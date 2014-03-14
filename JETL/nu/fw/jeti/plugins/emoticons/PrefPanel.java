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

package nu.fw.jeti.plugins.emoticons;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import nu.fw.jeti.images.IconPrefPanel;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class PrefPanel extends IconPrefPanel
{
	private BorderLayout borderLayout1 = new BorderLayout();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTable jTable1;
	private JButton jButton1 = new JButton();
    //private ListTableModel dataModel;

	public PrefPanel(Backend backend)
	{
		super("emoticons");
	}
/*
	void jbInit() throws Exception
	{
		dataModel = new ListTableModel(
            new String[] {
                I18N.gettext("emoticons.Name"),
                I18N.gettext("emoticons.Enabled"),
                I18N.gettext("emoticons.Description"),
                I18N.gettext("emoticons.Version") },
            Preferences.getPlugableCopy("emoticons"));

        TableSorter sorter = new TableSorter(dataModel);
        jTable1 = new JTable(sorter);
        sorter.setTableHeader(jTable1.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);

		jTable1.setRowSelectionAllowed(false);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(15);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(40);

		this.setLayout(borderLayout1);
		if (new File(Start.path + "plugins" + File.separator + "emoticons").exists())
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
		new Plugin().scanEmoticons();
		dataModel.reload(Preferences.getPlugableCopy("emoticons"));
	}
*/
	public void savePreferences()
	{
		List oldStatus = Preferences.getPlugable("emoticons");
		List newStatus = dataModel.getPlugins();
		Plugin plugin = null;
		boolean reloadNeeded = false;
		for (int i = 0; i < newStatus.size(); i++)
		{
			Object[] newS = (Object[]) newStatus.get(i);
			Object[] oldS = (Object[]) oldStatus.get(i);
			if (!newS[3].equals(oldS[3]))
			{
				reloadNeeded = true;
				if (plugin == null) plugin = new Plugin();
				plugin.unloadEmoticon((String) oldS[3]);					
				plugin.unloadEmoticon((String) newS[3]);
				oldS[3]=newS[3];
			}
			if (!newS[1].equals(oldS[1]))
			{
				oldS[1] = newS[1];
				if (((Boolean) oldS[1]).booleanValue())
				{
					if(!reloadNeeded)
					{//load
						if (plugin == null)	plugin = new Plugin();
						plugin.loadEmoticon((String) oldS[4],(String)oldS[3]);
					}
				}
				else
				{
					//unload
					if (plugin == null)
						plugin = new Plugin();
					plugin.unloadEmoticon((String) oldS[0]);
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
					if (plugin == null)	plugin = new  Plugin();
					plugin.loadEmoticon((String) oldS[4],(String)oldS[3]);
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
