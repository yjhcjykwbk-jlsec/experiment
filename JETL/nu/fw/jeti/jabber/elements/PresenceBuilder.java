package nu.fw.jeti.jabber.elements;

/**
 * @author E.S. de Boer
 */

public class PresenceBuilder extends PacketBuilder
{
	public String showAsString;
	public int show;
	public String status;
	public String priorityAsString;
	public int priority;
	public String type;

    public PresenceBuilder()
    {
		reset();
    }

	public void reset()
	{
		super.reset();
	    show = Integer.MAX_VALUE;
	    showAsString = null;
		status=null;
		priorityAsString=null;
		priority = 0;
	}

	public Packet build() throws InstantiationException
	{
		//if(priority < -128 || priority >127) throw new InstantiationException("priority out of bounds (-128, 127");
		if(priority==0)
		{
			try{
				priority =Integer.parseInt(priorityAsString);
			}catch (NumberFormatException e){priority=0;}
		}
		if(type==null) type = "available";
		else
		{
			if(!(type.equals("unavailable") ||
				      type.equals("subscribe")||
					  type.equals("subscribed")||
					  type.equals("unsubscribe")||
					  type.equals("unsubscribed")||
					  type.equals("probe")||
					  type.equals("error")))
					  type ="available";
		}
		if(show == Integer.MAX_VALUE)
		{
			if("away".equals(showAsString)) show = Presence.AWAY; 
			else if("dnd".equals(showAsString)) show = Presence.DND;
			else if("xa".equals(showAsString)) show = Presence.XA;
			else if("chat".equals(showAsString)) show = Presence.FREE_FOR_CHAT;
			else
			{
				if (type.equals("available")) show = Presence.AVAILABLE;
				else show = Presence.UNAVAILABLE; 
			}
		}
		return new Presence(this);
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
