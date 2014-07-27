/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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
 *	Created on 14-aug-2003
 */

package nu.fw.jeti.applet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.util.Preferences;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class PreferencesHandler extends ExtensionHandler
{
	
	private Map preferences;
	//private List plugins;
	private String xmlVersion;

	public void startHandling(Attributes attributes)
	{
		preferences = new HashMap();
		xmlVersion = attributes.getValue("xmlVersion");
	}

	public void startElement(String qName,Attributes attrs)
	{
		if(qName.equals("plugin"))
		{
			String type = attrs.getValue("type");
			String name = attrs.getValue("name");
			Boolean enabled = Boolean.valueOf(attrs.getValue("enabled"));
			List plugables=Preferences.getPlugable(type);
			boolean found = false;
			for(Iterator i=plugables.iterator();i.hasNext();)
			{
				Object[] o =(Object[])i.next();
				if(o[0].equals(name))
				{
					o[1] =enabled;
					found = true;
				}
			}
			if(!found)
			{
				Object[] temp = new Object[6];
				temp[0] = name;
				temp[1] = enabled;
				plugables.add(temp);
			}
		}
		else if(qName.equals("preference"))
		{
			String key = attrs.getValue("key");
			String value = attrs.getValue("value");
			preferences.put(key,value);
		}
	}

	public Extension build()
	{
		return new JetiPrivatePreferencesExtension(new Preferences(preferences));
	}
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
