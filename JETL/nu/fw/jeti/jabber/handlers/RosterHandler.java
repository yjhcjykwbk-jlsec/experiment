package nu.fw.jeti.jabber.handlers;
import nu.fw.jeti.jabber.elements.RosterBuilder;
import nu.fw.jeti.jabber.elements.RosterItemBuilder;
import org.xml.sax.Attributes;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.JID;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class RosterHandler extends ExtensionHandler
{
	private RosterBuilder builder;
	private RosterItemBuilder itemBuilder;

    public RosterHandler()
    {
		builder = new RosterBuilder();
		itemBuilder = new RosterItemBuilder();
    }

	public void startHandling(Attributes attr)
	{
	    builder.reset();
	}

	public void startElement(String name,Attributes attr)
	{
		if(name.equals("item"))
		{
			itemBuilder.reset();
			//jid not checked because you can't delete it then?
			itemBuilder.jid = JID.jidFromString(attr.getValue("jid"));
			itemBuilder.subscription =attr.getValue("subscription");
			itemBuilder.name = attr.getValue("name");
			itemBuilder.ask = attr.getValue("ask");
		}
		else if (!name.equals("group")) nu.fw.jeti.util.Log.notParsedXML("roster " + name);
	}

	public void endElement(String name)
	{
		if(name.equals("item"))
		{
			try{
		        builder.addItem(itemBuilder.build());
			}catch (InstantiationException e) {nu.fw.jeti.util.Log.xmlParseException(e);}
		}
		else if (name.equals("group")) itemBuilder.addGroup(getText());
		else nu.fw.jeti.util.Log.notParsedXML("Roster " + name + " " + getText());
		clearCurrentChars();
	}
	
	public Extension build()
	{
		Extension e = builder.build();
		builder.reset();
	    return e;
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
