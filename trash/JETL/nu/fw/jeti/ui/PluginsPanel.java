/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2002 E.S. de Boer  
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.plugins.PluginData;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.ui.models.ListTableModel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.TableSorter;

/**
 * @author E.S. de Boer
 * @version 1.0
 */
public class PluginsPanel extends PreferencesPanel
{//change to jtreetable http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTable jTable1;
    private JButton jButton1 = new JButton();
	private OptionsWindow prefWindow;
    private List plugins;
    private ListTableModel dataModel;

    public PluginsPanel(OptionsWindow pref)
    {
		prefWindow = pref;
		
        plugins = Preferences.getTranslatedPlugins();
        dataModel = new ListTableModel(
            new String[]{
                I18N.gettext("main.options.Name"),
                I18N.gettext("main.options.Enabled"),
                I18N.gettext("main.options.Description")},
            plugins);
        TableSorter sorter = new TableSorter(dataModel);
        jTable1 = new JTable(sorter);
        sorter.setTableHeader(jTable1.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);
        jTable1.setDefaultRenderer(String.class, new PluginRenderer());
		jTable1.setRowSelectionAllowed(false);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(15);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(180);
        
		setLayout(new BorderLayout());
		//if(Start.programURL == null)
		if (new File(Start.path + "plugins").exists())
		{	
	        jButton1.setText(I18N.gettext("main.options.Scan_plugins"));
	        jButton1.addActionListener(new java.awt.event.ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	                jButton1_actionPerformed(e);
	            }
	        });
	        add(jButton1, BorderLayout.SOUTH);
		}
        add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTable1);
    }

    void jButton1_actionPerformed(ActionEvent e)
    {
		new PluginData().scanPlugins();
		dataModel.reload(Preferences.getTranslatedPlugins());
			
    }

	public void savePreferences()
	{
		List oldStatus = nu.fw.jeti.util.Preferences.getPlugins();
		List newStatus =dataModel.getPlugins();
		for(int i=0;i<newStatus.size();i++)
		{
			Object[] newS =(Object[]) newStatus.get(i);
			Object[] oldS =(Object[]) oldStatus.get(i);
			if(!newS[1].equals(oldS[1]))
			{
				oldS[1] = newS[1];
				String name =(String)oldS[0];
				if(((Boolean)oldS[1]).booleanValue())
				{//load
					PluginsInfo.loadPlugin(name);
					prefWindow.addPanel(name);
				}
				else
				{//unload
					PluginsInfo.unloadPlugin(name);
					prefWindow.removePanel(name);
				}
			}
		}
	}
	
	  class PluginRenderer extends DefaultTableCellRenderer {
        String format;
	
        public PluginRenderer() {
            super();
            format = I18N.gettext("main.options.Version_{0}_(Jeti_{1})");
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
        	if(row<plugins.size())
        	{
	            String pluginVersion = (String)((Object[])plugins.get(row))[3];
	            String jetiVersion = (String)((Object[])plugins.get(row))[4];
        	
	            setToolTipText(
	            		MessageFormat.format(
	            			format, new Object[]{pluginVersion, jetiVersion}));
        	}
            return super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        }
    }

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
