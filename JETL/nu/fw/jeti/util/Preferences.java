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

package nu.fw.jeti.util;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.SAXParser;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.XMLDataFile;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.JetiPrivateExtension;
import nu.fw.jeti.jabber.elements.Presence;

import org.xml.sax.SAXException;

/**
 * @author E.S. de Boer
 * 2001
 */

public class Preferences extends XMLDataFile
{
	//private static Map map;
	private static Map mapMessages = new HashMap();
	private static Backend backend;
	private static boolean save = false;
    private static Map plugable = new HashMap();
    private static Map preferences = new HashMap();

	public Preferences()
	{}

	public Preferences(Map p)
	{//copy instead of put all?
		preferences.putAll(p);
	}
	
	public Preferences(Backend backend, SAXParser parser)
	{
		Preferences.backend = backend;

		InputStream data = null;

        // First load the built in default preferences
        data = getClass().getResourceAsStream("/default_preferences.xml");
        if (data != null) {
        	System.out.println("loading default prefs");
            addPreferences(parser, data);
        }
        else
        {
        	try
        	{
        		data = new FileInputStream(Start.path + "default_preferences.xml");
                addPreferences(parser, data);
                System.out.println("loading default prefs");
	        } catch (IOException ex) {
				// Do Nothing
	        }
        }
        // Try to load any saved preferences
		try {
			if(Start.applet) {
                URL url = new URL(Start.programURL + "preferences.xml");
                data = url.openStream();
			} else {
                data = new FileInputStream(Start.path + "preferences.xml");
            }
            addPreferences(parser, data);
		} catch (IOException ex) {
			// Do Nothing
        }

        // Initialize presence messages if none have been specified
        if (mapMessages.isEmpty()) {
            initMessages(mapMessages);
        }		
	}

    private void addPreferences(SAXParser parser, InputStream data) {
        try {
            parser.parse(data,
                         new PreferencesHandler(preferences, mapMessages));
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	private static Map initMessages(Map mapMessages)
	{
		List tempList = new ArrayList(10);
		tempList.add("Available");
		tempList.add("Free for Chat");
		mapMessages.put("chat", tempList);
		mapMessages.put("available", tempList);
		tempList = new ArrayList(10);
		tempList.add("In a Meeting");
		tempList.add("Busy");
		tempList.add("Working");
		mapMessages.put("dnd", tempList);
		tempList = new ArrayList(10);
		tempList.add("On the Phone");
		tempList.add("Be Right Back");
		mapMessages.put("away", tempList);
		tempList = new ArrayList(10);
		tempList.add("Out to Dinner");
		tempList.add("Out to Lunch");
		tempList.add("On Vacation");
		tempList.add("Gone Home");
		tempList.add("Sleeping");
		tempList.add("Extend Away");
		mapMessages.put("xa", tempList);
		return mapMessages;
	}


	public static void load(JetiPrivateExtension jetiExtension)
	{
		mapMessages.putAll(jetiExtension.getMessages());

		try
		{
			//save if loaded xml version <= own xmlversion (backwards compatible)
			save = (Integer.parseInt(jetiExtension.getXmlVersion().substring(1)) <= Integer.parseInt(JetiPrivateExtension.XML_VERSION.substring(1)));
		}
		catch (Exception e)
		{
		} //exception not saven
	}

	/**
	 * saves status messages on a server.
	 * @param key
	 * @param value
	 */
	public static void saveStatusMessages(int key, List value)
	{
		mapMessages.put(convertPresenceKey(key), value);
		//if(save)backend.savePreferences(map,mapMessages);
	}
	
	/**
	 * saves preferences (saved with put) to server
	 */
	public static void saveToServer()
	{
		if (save)backend.send(new InfoQuery("set", new IQPrivate(new JetiPrivateExtension(null, mapMessages))));
	}
	
	/**
	 * loads status messages from server
	 * @param key
	 * @return
	 */
	public static List getStatusMessages(int key)
	{
		return (List) mapMessages.get(convertPresenceKey(key));
	}

	private static String convertPresenceKey(int key)
	{
		String stringKey;
		switch(key)
		{
			case Presence.FREE_FOR_CHAT: stringKey="chat"; break;
			case Presence.AWAY: stringKey="away"; break;
			case Presence.XA: stringKey="xa"; break;
			case Presence.DND: stringKey="dnd"; break;
			default: stringKey="available";
		}
		return stringKey;
	}

	/**
	 * gets a boolean preference
	 * @param prefix a prefix so there are no name clashes
	 * @param def the value to be returned in the event that this preference has no value associated with key or the associated value cannot be interpreted as an boolean 	
	 * @param key  key whose associated value is to be returned as an boolean.
	 * @return boolean the boolean value represented by the string associated with key, or def if the associated value does not exist. 
	 */
	public static boolean getBoolean(String prefix,String key,boolean def)
	{
		String value = get(prefix,key);
		if(value==null) return def; 
		return Boolean.valueOf((String) preferences.get(prefix + "." + key)).booleanValue();
	}
	
	/**
	* Method saves a boolean preference
	* @param prefix a prefix so there are no name clashes
	* @param key
	* @param value
	*/
	public static void putBoolean(String prefix, String key, boolean value)
	{
		preferences.put(prefix + "." + key,String.valueOf(value));
	}
	
	/**
	 * gets a integer preference
	 * @param prefix a prefix so there are no name clashes
	 * @param def the value to be returned in the event that this preference has no value associated with key or the associated value cannot be interpreted as an int 	
	 * @param key  key whose associated value is to be returned as an boolean.
	 * @return int the integer value represented by the string associated with key, or def if the associated value does not exist. 
	 */
	public static int getInteger(String prefix,String key,int def)
	{
		String value = get(prefix,key);
		if(value==null) return def; 
		try{
			return Integer.parseInt((String) preferences.get(prefix + "." + key));
		} catch (NumberFormatException e)
		{
			return def; 
		}
	}

	/**
	* Method saves a integer preference
	* @param prefix a prefix so there are no name clashes
	* @param key
	* @param value
	*/
	public static void putInteger(String prefix, String key,int value)
	{
		preferences.put(prefix + "." + key,String.valueOf(value));
	}
		
	/**
	* Method put saves a string preference.
	* @param prefix a prefix so there are no name clashes
	* @param key
	* @param value
	*/
	public static void putString(String prefix, String key, String value)
	{
		preferences.put(prefix + "." + key, value);
	}

	/**
	 * Method getPreference gets a string preference.
	 * @param prefix a prefix so there are no name clashes
	* @param def the value to be returned in the event that this preference has no value associated with key or the associated value cannot be interpreted as an boolean 	
	 * @param key  key whose associated value is to be returned as an boolean.
	 * @return boolean the boolean value represented by the string associated with key, or def if the associated value does not exist. 
	 */
	public static String getString(String prefix, String key,String def)
	{
		String value = get(prefix,key);
		if(value==null) return def; 
		return value;
	}

	private static String get(String prefix, String key)
	{
		return (String) preferences.get(prefix + "." + key);
	}

	/**
	* Method put saves a preference in a file.
	* use put boolean int or String instead
	* @deprecated
	* @param prefix a prefix so there are no name clashes
	* @param key
	* @param value
	*/
	public static void putPreference(String prefix, String key, String value)
	{
		preferences.put(prefix + "." + key, value);
	}

	/**
	 * Method getPreference gets a preference from file.
	 * @deprecated
	 * @param prefix a prefix so there are no name clashes
	 * @param key
	 * @param value
	 */
	public static String getPreference(String prefix, String key)
	{
		return get(prefix,key);
	}

	public static List getPlugins()
	{
		//put in new class?
		return getPlugable("plugins");
	}

	public static List getTranslatedPlugins()
	{
		//put in new class?
		List temp = new ArrayList();
		for (Iterator i = getPlugins().iterator(); i.hasNext();)
		{
			Object[] tempArray = new Object[6];
			//System.arraycopy(i.next(), 0, tempArray, 0, 6);
			Object[] plugins = (Object[])i.next();
			tempArray[0]= plugins[0];
			tempArray[1]= plugins[1];
			tempArray[2]= I18N.gettext((String)plugins[2]);
			tempArray[3]= plugins[3];
			tempArray[4]= plugins[4];
			tempArray[5]= plugins[5];
			temp.add(tempArray);
		}
		return temp;
	}

	public static List getPlugable(String name)
	{
		List list = (List) plugable.get(name);
		if (list == null)
		{
			list = new ArrayList();
			plugable.put(name, list);
		}
		return list;
	}

	public static List getPlugableCopy(String name)
	{
		List temp = new ArrayList();
		for (Iterator i = getPlugable(name).iterator(); i.hasNext();)
		{
			Object[] tempArray = new Object[6];
			System.arraycopy(i.next(), 0, tempArray, 0, 6);
			temp.add(tempArray);
		}
		return temp;
	}
	
	/**
	 * Saves preferences to disk (in preferences.xml)
	 */
	public static void save()
	{
		StringBuffer xml = new StringBuffer();
		new Preferences().appendToXML(xml);
		xml.insert(0,"<?xml version='1.0'?>");
		//System.out.println(xml.toString());
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(Start.path + "preferences.xml"), "UTF8"));
			writer.write(xml.toString());
			writer.close();
		}
		catch (IOException ex2)
		{
			ex2.printStackTrace();
		}
	}

	public void appendToXML(StringBuffer xml)
	{
		//appendHeader(xml);
		appendOpenTag(xml, "<preferences>");
		for (Iterator i = preferences.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if(value!=null)
			{//only preference with a value, to reset old ones
				appendOpenTag(xml, "<preference");
				appendAttribute(xml, "key", key);
				appendAttribute(xml, "value", value);
				appendCloseSymbol(xml);
			}
		}
		appendOpenTag(xml, "<plugins>");
		for (Iterator i = plugable.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			String type = (String) entry.getKey();
			for (Iterator j = ((List) entry.getValue()).iterator(); j.hasNext();)
			{
				Object[] temp = (Object[]) j.next();
				appendOpenTag(xml, "<plugin");
				appendAttribute(xml, "type", type);
				appendAttribute(xml, "name", temp[0]);
				appendAttribute(xml, "enabled", temp[1]);
				appendAttribute(xml, "transport", temp[3]);
				appendCloseSymbol(xml);
			}
		}
		appendCloseTag(xml, "</plugins>");
		appendCloseTag(xml, "</preferences>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
