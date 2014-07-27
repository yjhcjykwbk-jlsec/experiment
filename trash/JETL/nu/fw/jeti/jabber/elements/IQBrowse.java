package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.util.StringArray;

import java.util.Iterator;
import java.util.List;
/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQBrowse extends Extension implements IQExtension, DiscoveryItem, DiscoveryInfo
{
	private StringArray namespaces;
	private String name;
	private String type;
	private String category;
	private String version;
	private JID jid;
	private List childItems;

	public IQBrowse()
	{
	}

	public IQBrowse(JID jid)
	{
		this.jid = jid;
	}


	public IQBrowse(IQBrowseBuilder ib)
	{
		name = ib.getName();
		jid = ib.getJID();
		category = ib.getCategory();
		type = ib.getType();
		version = ib.getVersion();
		namespaces = ib.getNamespaces();
		childItems = ib.getItems();
	}


	public String getName(){return name;}

	public String getType(){return type;}

	public String getCategory(){return category;}

	public JID getJID(){return jid;}

	public Iterator getItems()
	{
		if(childItems == null) return null;
		return childItems.iterator();
	}

	public boolean hasItems(){return childItems != null;}

	public Iterator getFeatures()
	{
		if (namespaces == null)return null;
		return namespaces.iterator();
	}
	
//	public StringArray getNamespaces()
//	{
//		return namespaces;
//	}

	public boolean hasFeatures(){return namespaces != null;}

	public void appendToXMLNoIQ(StringBuffer xml)
	{
		xml.append("<item");
		toXML(xml);
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<item xmlns='jabber:iq:browse'");
		toXML(xml);
	}

	private void toXML(StringBuffer xml)
	{
		appendAttribute(xml,"jid",jid);
		appendAttribute(xml,"category",category);
		appendAttribute(xml,"type",type);
		appendAttribute(xml,"name",name);
		appendAttribute(xml,"version",version);
		if(namespaces == null && childItems == null)
		{ //short cut
			xml.append("/>");
			return;
		}
		xml.append('>');
		if(namespaces !=null)
		{
			for(int i=0;i < namespaces.getSize();i++)
			{
				appendElement(xml,"ns",namespaces.get(i));
			}
		}
		if(childItems != null)
		{
			  for(Iterator i = childItems.iterator();i.hasNext();)
			  {
				  ((IQBrowse)i.next()).appendToXMLNoIQ(xml);
			  }
		}
		xml.append("</item>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
