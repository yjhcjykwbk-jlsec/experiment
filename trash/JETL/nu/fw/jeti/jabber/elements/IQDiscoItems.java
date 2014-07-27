//Created on 26-apr-2003
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.List;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;

/**
 * @author E.S. de Boer
 * implements the http://jabber.org/protocol/disco#items'/ spec
 */
public class IQDiscoItems extends Extension implements IQExtension, DiscoveryItem
{
	private String node;
	private List items;

	public IQDiscoItems()
	{}

	public IQDiscoItems(String node)
	{
		this.node = node;
	}
	
	public IQDiscoItems(String node,List items)
	{
		this.node = node;
		this.items = items;
	}

	public String getNode()
	{
		return node;
	}
		
	public Iterator getItems()
	{
		if (items == null)return null;
		return items.iterator();
	}

	public boolean hasItems()
	{
		return items != null;
	}
	
	//---------------------------discoveryInfo implementation, returns null--------------
	public JID getJID()
	{
		return null;
	}

	public String getName()
	{
		return null;
	}
	
	//----------------------------------------------------------------------------
	
	
	
	public void execute(InfoQuery iq,Backend backend){}

	

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns= \"http://jabber.org/protocol/disco#items\"");
		appendAttribute(xml, "node", node);
		if (items == null)
		{ //short cut
			xml.append("/>");
			return;
		}
		xml.append('>');
		if (items != null)
		{
			for (Iterator i = items.iterator(); i.hasNext();)
			{
				((DiscoItem) i.next()).appendToXML(xml);
			}
		}
		xml.append("</query>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
