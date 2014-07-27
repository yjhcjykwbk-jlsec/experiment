package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQVersion;
import org.xml.sax.Attributes;

/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQVersionHandler extends ExtensionHandler
{
	private String os;
	private String name;
	private String version;

	public void startHandling(Attributes attr)
	{
	    reset();
	}

	private void reset()
	{
	    os=name=version=null;

	}

	public void endElement(String name)
	{
		if("os".equals(name)) os = getText();
		else if("name".equals(name)) this.name = getText();
		else if("version".equals(name)) version = getText();
		else nu.fw.jeti.util.Log.notParsedXML("iq:auth " + name + getText());
		clearCurrentChars();
	}

	public Extension build()
	{
		Extension e = new IQVersion(name,version ,os);
		reset();
	    return e;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
