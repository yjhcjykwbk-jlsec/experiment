package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.*;
import org.xml.sax.Attributes;
import nu.fw.jeti.jabber.*;
import nu.fw.jeti.util.I18N;
/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public abstract class PacketHandler extends BaseHandler
{
	protected PacketBuilder builder;

	public PacketHandler(PacketBuilder builder)
    {
		this.builder = builder;
    }

	public void startHandling(Attributes attr)
	{
		builder.reset();
		builder.setTo(JID.jidFromString(attr.getValue("to")));
		builder.setFrom(JID.jidFromString(attr.getValue("from")));
		builder.setId(attr.getValue("id"));
	}

	public void startElement(String name,Attributes attr)
	{
		if(name.equals("error"))
		{
			builder.setErrorType(attr.getValue("type"));
			String code = attr.getValue("code");
			try
			{
				builder.errorCode = Integer.parseInt(code);
			}catch(NumberFormatException e){nu.fw.jeti.util.Log.notParsedXML(I18N.gettext("main.error.Illegal_errorcode") + " " +  code);}
		}
	}

	public void endElement(String name)
	{
		if(name.equals("error"))
		{
			builder.errorDescription = getText();
			clearCurrentChars();
		}
	}

	public void addExtension(Extension extension)
	{
		builder.addExtension(extension);
	}

	public Packet build() throws InstantiationException
	{
		Packet p = builder.build();
		builder.reset();
	    return p;
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
