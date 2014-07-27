package nu.fw.jeti.jabber.elements;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class MessageBuilder extends PacketBuilder
{
	public String body;
	public String thread;
	public String subject;
	public String type;

   	public void reset()
	{
		super.reset();
	    body = null;
		thread=null;
		subject=null;
	}
	
	public void addXExtension(XExtension extension)
	{
		if(extension == null) return;
	    addExtension((Extension)extension);
	}

	public Packet build()
	{
		if(type==null) type = "normal";
		else
		{
		    if(!(type.equals("headline")||
			     type.equals("chat")||
			     type.equals("groupchat")||
			     type.equals("error")))
				 type = "normal";
		}
		return new Message(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
