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

public class MessageHandler extends PacketHandler
{
    public MessageHandler()
    {
		super(new MessageBuilder());
    }

	public void startHandling(Attributes attr)
	{
	    builder.reset();
		super.startHandling(attr);
		((MessageBuilder)builder).type = attr.getValue("type");
	}

	public void endElement(String name)
	{
		super.endElement(name);
		if(name.equals("error")){}
		else if(name.equals("body")){((MessageBuilder)builder).body = getText();}
		else if(name.equals("thread")){((MessageBuilder)builder).thread = getText();}
		else if(name.equals("subject")){((MessageBuilder)builder).subject = getText();}
		else nu.fw.jeti.util.Log.notParsedXML("message " + name + getText());
		clearCurrentChars();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
