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

package nu.fw.jeti.plugins.spell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.images.IconPrefPanel;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.TableSorter;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class PrefPanel extends PreferencesPanel
{
	private BorderLayout borderLayout1 = new BorderLayout();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTable jTable1;
	private JButton jButton1 = new JButton();
	private TableSorter sorter;    

	public PrefPanel(Backend backend)
	{
		sorter = new TableSorter(initTableModel());
        jTable1 = new JTable(sorter);
        sorter.setTableHeader(jTable1.getTableHeader());
        sorter.setSortingStatus(0, TableSorter.ASCENDING);

		jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(15);
        jTable1.setDefaultRenderer(String.class, new MyTableRenderer());
        jTable1.setDefaultEditor(String.class,new MyTableEditor());
		jTable1.setRowSelectionAllowed(false);
        
		this.setLayout(borderLayout1);
		if (new File(Start.path + "dictionaries").exists())
		{	
			I18N.setTextAndMnemonic("spell.Scan_Dictionaries",jButton1);
			jButton1.addActionListener(new ActionListener()
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

	private TableModel initTableModel()
	{
		String[] languages = Plugin.getLanguages();
		Object[][] dictionaries = new Object[languages.length][3];
		String defaultLanguage =Preferences.getString("spell","defaultLanguage", null);
		for(int i=0;i<languages.length;i++)
		{
			String language = languages[i].substring(0,languages[i].indexOf('.'));
			Boolean enabled = new Boolean(
					Preferences.getString("spell",language, "false"));
			String def =String.valueOf(language.equals(defaultLanguage));
			dictionaries[i] = new Object[]{language,enabled,def};
		}
		if(defaultLanguage==null && languages.length>0)
		{
			dictionaries[0][2]="true";
			dictionaries[0][1]=Boolean.TRUE;
		}
			
		TableModel model = new MyTableModel(dictionaries ,new Object[]{
				I18N.gettext("spell.Language"),I18N.gettext("spell.Enabled")
				,I18N.gettext("spell.Default")});
		return model;
	}

	void jButton1_actionPerformed(ActionEvent e)
	{
		Plugin.scanDictionaries();
		sorter.setTableModel(initTableModel());
		//dataModel.reload(Preferences.getPlugableCopy("emoticons"));
	}

	public void savePreferences()
	{
		String[] languages = Plugin.getLanguages();
		for(int i=0;i<languages.length;i++)
		{
			String language = languages[i].substring(0,languages[i].indexOf('.'));
			boolean old = Preferences.getBoolean("spell",language, false);
			for(int j=0;j<sorter.getRowCount();j++)
			{
				if(language.equals(sorter.getValueAt(j, 0)))
				{
					boolean newValue = ((Boolean)sorter.getValueAt(j, 1))
														.booleanValue();
					Preferences.putBoolean("spell",language,newValue);
					if(!old && newValue)
					{
						Plugin.loadDictionary(languages[i]);
					}
					else if(old && !newValue)
					{
						Plugin.unloadDictionary(language);
					}
				}
			}
			for(int j=0;j<sorter.getRowCount();j++)
			{
				if(sorter.getValueAt(j, 2).equals("true"))
				{
					Preferences.putString("spell","defaultLanguage"
								,(String)sorter.getValueAt(j, 0));
					break;
				}
			}
		}
		
	}
	
	class MyTableModel extends DefaultTableModel
	{
		public MyTableModel(Object[][] o, Object[] names)
		{
			super(o,names);
		}
		
		public Class getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();
		}
		
		public boolean isCellEditable(int row,int col)
		{
			return col>0;
		}
		
		public void setValueAt(Object value,int row,int col)
		{
			if(col==2)
			{
				if(value.equals("true"))
				{
					for(int i=0;i<getRowCount();i++)
					{
						if(row==i)continue;
						super.setValueAt("false", i, 2);
					}
					super.setValueAt("true", row, 2);
				}
				else super.setValueAt("false", row,2);
			}
			else super.setValueAt(value, row, col);
		}
	}
	
	class MyTableRenderer extends DefaultTableCellRenderer
	{
		private JRadioButton radio = new JRadioButton();
		
		public MyTableRenderer()
		{
			super();
			radio.setBackground(Color.white);
			radio.setAlignmentX(JRadioButton.CENTER_ALIGNMENT);
		}
				
		public Component getTableCellRendererComponent(JTable table, 
				Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			if(column==2)
			{
		    	radio.setSelected(Boolean.valueOf((String)value).booleanValue());
				radio.setBackground(table.getBackground());
				return radio;
			}
			return super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
		}
	}
	
	class MyTableEditor extends AbstractCellEditor implements TableCellEditor
	{
		private JRadioButton radio = new JRadioButton();
				
		public MyTableEditor()
		{
			radio.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JRadioButton b= (JRadioButton)e.getSource();
					if(!b.isSelected())b.setSelected(true);
					stopCellEditing();
				}
			});
		}
		
		public boolean isCellEditable(int col)
		{
			return col==2;
		}
		       
	    public Object getCellEditorValue()
	    {
	    	return String.valueOf(radio.isSelected());
	    }

	    public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected,int row, int column)
	    {
	    	radio.setBackground(table.getBackground());
	    	radio.setSelected(Boolean.valueOf((String)value).booleanValue());
	    	return radio;
	    }
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
