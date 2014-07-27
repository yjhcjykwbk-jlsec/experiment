package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQTime;
import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQTimeHandler extends ExtensionHandler
{
	private String tz;
	private String utc;
	private String display;

	public void startHandling(Attributes attr)
	{
	    reset();
	}

	private void reset()
	{
	    tz=utc=display=null;

	}

	public void endElement(String name)
	{
		if("tz".equals(name)) tz = getText();
		else if("utc".equals(name)) utc = getText();
		else if("display".equals(name)) display = getText();
		else nu.fw.jeti.util.Log.notParsedXML("iq:time " + name + getText());
		clearCurrentChars();
	}

	public Extension build()
	{
		Extension e = new IQTime(utc,tz,display);
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
