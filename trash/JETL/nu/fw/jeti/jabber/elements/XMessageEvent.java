package nu.fw.jeti.jabber.elements;


/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class XMessageEvent extends Extension implements XExtension
{
    /** id is the id from the message that this event is a reply to */
    private String id;

    /** type holds a string describing the type of the event (composing/offline/deliverd/displayed) */
    private String type;

	public XMessageEvent(String type,String id)
	{
		this.id=id;
		this.type=type;
	}

    public XMessageEvent(XMessageEventBuilder builder)
    {
		id=builder.getID();
		type=builder.getType();
	}

	public String getType()
    {
		return type;
    }

	public String getID()
	{
		return id;
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns=\"jabber:x:event\">");
		if(type!=null) xml.append("<"+type + "/>");
		if(id!=null) xml.append("<id>"+ id + "</id>");
		//retval.append('>');
		xml.append("</x>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
