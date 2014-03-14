package nu.fw.jeti.jabber.elements;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

import java.util.*;


public class JetiExtensionBuilder implements ExtensionBuilder
{

	private Map entries;
	private Map messages;
	private String xmlVersion;


	public JetiExtensionBuilder()
	{
		reset();
	}

	public void reset()
	{
		entries=new HashMap();
		messages=new HashMap();
	}

	public void put(String name, String value)
	{
		//if (value==null)            entries.remove(name);
		//System.out.println(name + " : " + value);
		entries.put(name,value);
	}

	public void putMessages(String name, List value)
	{
		//if (value==null)            entries.remove(name);
		//System.out.println(name + " : " + value);
		messages.put(name,value);
	}

	public void addMap (Map map)
	{
		//if (value==null)            entries.remove(name);
		entries =map;
	}


	public void addMapMessages(Map mapMessages)
	{
		messages = mapMessages;
	}

	public Map getMap()
			{ return entries; }


	public Map getMapMessages()
	{
		return messages;
	}

	public String getXmlVersion()
	{
		return xmlVersion;
	}

	public void setXmlVersion(String xmlVersion)
	{
		this.xmlVersion = xmlVersion;
	}

	public Extension build()
	{
		return new JetiPrivateExtension(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
