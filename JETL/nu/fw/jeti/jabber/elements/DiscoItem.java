// Created on 26-apr-2003
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;

import nu.fw.jeti.backend.XMLData;
import nu.fw.jeti.jabber.JID;

/**
 * @author E.S. de Boer
 * The items used in the disco spec
 * @see nu.fw.jeti.jabber.elements.IQDiscoItems 
 */
public class DiscoItem extends XMLData implements DiscoveryItem
{
	private JID jid;
	private String name;
	private String node;
	private String action;

	public DiscoItem()
	{}

	public DiscoItem(JID jid,String name,String node,String action)
	{
		this.jid = jid;
		this.name = name;
		this.node = node;
		this.action = action;
	}

	
	public JID getJID()
	{
		return jid;
	}

	public String getName()
	{
		return name;
	}

	public String getNode()
	{
		return node;
	}

	public String getAction()
	{
		return action;
	}
	
	//---------------DiscoveryItem------------------------
	public Iterator getItems(){return null;}
	
	public boolean hasItems(){return false;}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<item");
		appendAttribute(xml, "jid", jid);
		appendAttribute(xml, "name", name);
		appendAttribute(xml, "node", node);
		appendAttribute(xml, "action", action);
		xml.append("/>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
