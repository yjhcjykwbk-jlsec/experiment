/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 10-jul-2004
 */
 
package nu.fw.jeti.ui.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class LocaleModel extends AbstractListModel implements ComboBoxModel, ActionListener
{
	private Map countries;
	private List currentCountries;
	private Object selectedItem;
	
	public LocaleModel(I18N i18n)
	{
		countries = i18n.getCountries();
		//currentCountries = (List)countries.get("en");
	}
		
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem()
	{
		return selectedItem;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object arg0)
	{
		selectedItem = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return currentCountries.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int pos)
	{
		return currentCountries.get(pos);
	}

	/**
	 * react to language combobox events
	 */
	public void actionPerformed(ActionEvent e)
	{
		  JComboBox cb = (JComboBox)e.getSource();
		  String languageCode =((I18N.Language)cb.getSelectedItem()).getLanguageCode();
		 // if(currentCountries!=null) fireIntervalRemoved(this,0,currentCountries.size());
		  currentCountries = (List) countries.get(languageCode);
	      //fireIntervalAdded(this,0,currentCountries.size());
	      
	      fireContentsChanged(this,0,currentCountries.size());
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
