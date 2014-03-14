package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.util.StringArray;

import nu.fw.jeti.jabber.*;
import nu.fw.jeti.backend.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class RosterItem extends XMLData
{
	private StringArray groups;
	private String name;
	private String subscription;
	private String ask;
	private JID jid;


	public RosterItem(JID jid, String name,String subscription, String ask, StringArray groups)
	{
		this.name = name;
		this.subscription = subscription;
		this.ask = ask;
		this.jid = jid;
		this.groups = groups;
	}

	public RosterItem(RosterItemBuilder ib)
	{
		name = ib.name;
		subscription = ib.subscription;
		ask = ib.ask;
		jid = ib.jid;
		groups = ib.getGroups();
	}

	public String getName(){return name;}

	public String getSubscription(){return subscription;}

	public String getAsk(){return ask;}

	public JID getJID(){return jid;}

	public StringArray getGroups()
	{//clone? nullpointers
		//return (StringArray)groups.clone();
		return groups;
	}

	public void appendToXML(StringBuffer xml)
    {
        xml.append("<item");
		appendAttribute(xml,"jid",jid);
		appendAttribute(xml,"name",name);
		appendAttribute(xml,"subscription",subscription);
		appendAttribute(xml,"ask",ask);
		if(groups ==null)
		{ //short cut
		    xml.append("/>");
			return;
		}
		xml.append('>');
		for(int i=0;i < groups.getSize();i++)
		{
		    appendElement(xml,"group",groups.get(i));
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
