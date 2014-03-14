package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQBrowseBuilder;

import org.xml.sax.Attributes;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQBrowseHandler extends ExtensionHandler
{
	private IQBrowseBuilder iqBuilder;
	private IQBrowseBuilder itemBuilder;
	private IQBrowseBuilder currentBuilder;

	public IQBrowseHandler()
	{
		iqBuilder = new IQBrowseBuilder();
		itemBuilder = new IQBrowseBuilder();
		currentBuilder = iqBuilder;
	}

	public void startHandling(Attributes attr)
	{
		iqBuilder.reset();
		if(getName().equals("item")) parseStartElement(attr.getValue("category"),attr);
		//backwards compatible:
		//else if(getName().equals("service")) parseStartElement(attr.getValue("service"),attr);
		//else if(getName().equals("conference")) parseStartElement(attr.getValue("conference"),attr);
		//else if(getName().equals("user")) parseStartElement(attr.getValue("user"),attr);
		else parseStartElement(getName(),attr);
	}

	private void parseStartElement(String category, Attributes attr)
	{
		String jid = attr.getValue("jid");
		if(jid != null)	currentBuilder.setJID(JID.jidFromString(jid));
		currentBuilder.setCategory(category);
		currentBuilder.setType(attr.getValue("type"));
		currentBuilder.setName(attr.getValue("name"));
		currentBuilder.setVersion(attr.getValue("version"));
	}

	public void startElement(String name,Attributes attr)
	{
		if (name.equals("ns")) return;
		currentBuilder = itemBuilder;
		itemBuilder.reset();
		if(name.equals("item"))	parseStartElement(attr.getValue("category"),attr);
		else parseStartElement(name,attr);
	}

	public void endElement(String name)
	{
		if(name.equals("ns")) currentBuilder.addNamespace(getText());
		else
		{
			iqBuilder.addItem(itemBuilder.build());
			currentBuilder = iqBuilder;
		}
		//else util.Log.notParsedXML("IQBrowse " + name + getText());
		clearCurrentChars();
	}

	public Extension build()
	{
        Extension e = null;
        e = iqBuilder.build();
        iqBuilder.reset();
		return e;
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
