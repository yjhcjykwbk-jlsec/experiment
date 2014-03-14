package nu.fw.jeti.jabber.elements;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class XMessageEventBuilder implements ExtensionBuilder
{
	//remove this
	
	/** id is the id from the message that this event is a reply to */
	private String id;

	/** type holds a string describing the type of the event (composing/offline/deliverd/displayed) */
	private String type;

	/** construct a new XMessageEventBuilder object */
	public XMessageEventBuilder()
	{
		reset();
	}

	/** reset the builder to a default state, for reuse */
	public void reset()
	{
		id=null;
		type=null;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type=type;
	}


	public String getID()
	{
		return id;
	}

	public void setID(String txt)
	{
		id=txt;
	}

	public Extension build()
	{
		//if(id == null) throw new InstantiationException("ID may not be null");
		return new XMessageEvent(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
