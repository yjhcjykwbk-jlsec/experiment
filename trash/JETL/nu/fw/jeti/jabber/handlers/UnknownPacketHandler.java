package nu.fw.jeti.jabber.handlers;
import nu.fw.jeti.util.I18N;
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

public class UnknownPacketHandler extends PacketHandler
{

    public UnknownPacketHandler()
    {// against nullpoiner exceptions
		super(new nu.fw.jeti.jabber.elements.InfoQueryBuilder());
    }

	public void startHandling(Attributes attr)
	{}

	public void endElement(String name)
	{
		Log.notParsedXML(name);
		Log.notParsedXML(getText());
		clearCurrentChars();
	}

	public nu.fw.jeti.jabber.elements.Packet build() throws InstantiationException
	{throw new InstantiationException(I18N.gettext("main.error.Unknown_Packet"));}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
