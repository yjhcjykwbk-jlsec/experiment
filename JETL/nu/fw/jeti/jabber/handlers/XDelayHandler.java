package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XDelay;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class XDelayHandler extends ExtensionHandler
{
	private String stamp;
	private JID from;
	
	public void startHandling(Attributes attr)
	{
		reset();
		from = JID.jidFromString(attr.getValue("from"));
		stamp = attr.getValue("stamp");
	}



	private void reset()
	{
		stamp=null;
		from=null;

	}

//	public void endElement(String name)
//	{
//		if("from".equals(name)) from = JID.jidFromString(getText());
//		else if("stamp".equals(name)) stamp = getText();
//		else util.Log.notParsedXML("x:delay " + name + getText());
//		clearCurrentChars();
//	}

	public Extension build()
	{
		Extension e = new XDelay(stamp,from);
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
