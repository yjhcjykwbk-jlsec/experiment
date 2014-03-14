package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.*;
import org.xml.sax.Attributes;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQAuthHandler extends ExtensionHandler
{
	private IQAuthBuilder builder;

    public IQAuthHandler()
    {
		builder = new IQAuthBuilder();
    }

	public void startHandling(Attributes attr)
	{
	    builder.reset();
	}

	public void endElement(String name)
	{
		if("username".equals(name)) builder.username = getText();
		else if("password".equals(name)) builder.password = getText();
		else if("digest".equals(name)) builder.digest = getText();
		else if("resource".equals(name)) builder.resource = getText();
		else if("token".equals(name)) builder.zeroKToken = getText();
		else if("sequence".equals(name)) builder.zeroKSequence = getText();
		else if("hash".equals(name)) builder.zeroKHash = getText();
		else nu.fw.jeti.util.Log.notParsedXML("iq:auth " + name + getText());
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
