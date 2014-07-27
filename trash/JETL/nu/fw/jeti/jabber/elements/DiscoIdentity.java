// Created on 4-aug-2004
package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.backend.XMLData;
import nu.fw.jeti.jabber.JID;

/**
 * @author E.S. de Boer
 * The identities used in the disco spec
 * @see nu.fw.jeti.jabber.elements.IQDiscoInfo 
 */
public class DiscoIdentity extends XMLData 
{
	private String name;
	private String category;
	private String type;
	
	public DiscoIdentity(String category,String type,String name)
	{
		this.name = name;
		this.category = category;
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}

	public String getCategory()
	{
		return category;
	}

	public String getType()
	{
		return type;
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<identity");
		appendAttribute(xml, "category", category);
		appendAttribute(xml, "type", type);
		appendAttribute(xml, "name", name);
		xml.append("/>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
