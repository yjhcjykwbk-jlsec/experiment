package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQLast;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class IQLastHandler extends ExtensionHandler
{
	private String seconds;
		
	public void startHandling(Attributes attr)
	{
		reset();
		seconds = attr.getValue("seconds");
	}

	private void reset()
	{
		seconds=null;
	}

	public Extension build()
	{
		Extension e = new IQLast(seconds); 
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
