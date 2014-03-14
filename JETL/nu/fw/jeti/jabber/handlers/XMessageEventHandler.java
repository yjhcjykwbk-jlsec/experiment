package nu.fw.jeti.jabber.handlers;
/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XMessageEventBuilder;
import org.xml.sax.Attributes;


public class XMessageEventHandler extends ExtensionHandler
{
    private XMessageEventBuilder builder;

    public XMessageEventHandler()
    {
		builder=new XMessageEventBuilder();
    }

    public void startHandling(Attributes attributes)
	{
		builder.reset();
    }

	public void endElement(String name)
	{
		if (name.equals("id")) builder.setID(getText());
		//else if (name.equals("type"))
		else builder.setType(name);
		//else nu.fw.jeti.util.Log.notParsedXML("messageEvent " + name + getText());
		clearCurrentChars();
	}

	public Extension build()
	{
		return builder.build();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
