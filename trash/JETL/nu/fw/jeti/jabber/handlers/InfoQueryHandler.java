package nu.fw.jeti.jabber.handlers;

import org.xml.sax.Attributes;
import nu.fw.jeti.jabber.elements.InfoQueryBuilder;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class InfoQueryHandler extends PacketHandler
{

    public InfoQueryHandler()
    {
		super(new InfoQueryBuilder());
    }

	public void startHandling(Attributes attr)
	{
	    builder.reset();
		super.startHandling(attr);
		((InfoQueryBuilder)builder).setType(attr.getValue("type"));
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
