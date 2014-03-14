package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.*;
import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 */

public class PresenceHandler extends PacketHandler
{
    public PresenceHandler()
    {
		super(new PresenceBuilder());
    }

	public void startHandling(Attributes attr)
	{
	    builder.reset();
		super.startHandling(attr);
		((PresenceBuilder)builder).type = attr.getValue("type");
	}

	public void endElement(String name)
	{
		super.endElement(name);
		if("show".equals(name)) {
            ((PresenceBuilder)builder).showAsString = getText();
        } else if("status".equals(name)) {
            ((PresenceBuilder)builder).status = getText();
		} else if("priority".equals(name)) {
            ((PresenceBuilder)builder).priorityAsString = getText();
        } else if("error".equals(name)) {
            // Do Nothing
        } else {
            nu.fw.jeti.util.Log.notParsedXML("presence " + name + getText());
        }
		clearCurrentChars();
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
