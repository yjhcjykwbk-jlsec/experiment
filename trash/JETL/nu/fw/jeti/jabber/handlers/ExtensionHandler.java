package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public abstract class ExtensionHandler extends BaseHandler
{
	private ExtensionHandler extensionHandler;
	private String name;//start tag name
	private int depth;

	public ExtensionHandler getParent()
	{
	    return extensionHandler;
	}

	public void setParent(ExtensionHandler handler)
	{
	    extensionHandler = handler;
	}


	public void setName(String name)
	{
	    this.name =name;
	}

	public String getName()
	{
	    return name;
	}

	public void up(){depth++;}

	public void down(){depth--;}

	public boolean isTop(){return depth == 0;}

	public void addExtension(Extension extension)
	{//overide for extension embedded in extension
	    Log.notParsedXML("this extension does not support embedded extensions");
	}

	abstract public Extension build() throws InstantiationException;

	public void startElement(String name,Attributes attr){}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
