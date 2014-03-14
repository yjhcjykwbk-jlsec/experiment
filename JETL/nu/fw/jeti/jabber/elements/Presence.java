package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.*;
import nu.fw.jeti.util.I18N;


/**
 * @author E.S. de Boer
 */

public class Presence extends Packet
{
	public final static int NONE =0;
	public final static int FREE_FOR_CHAT =1;
	public final static int AVAILABLE =2;
	public final static int AWAY =3;
	public final static int DND =4;
	public final static int XA =5;
	public final static int INVISIBLE = 6;
	public final static int UNAVAILABLE = 7;
	 
	
	private int show;
	private String status;
	private int priority;
	private String type;
	
	public Presence(JID to,String type)
	{
	    super(to);
		this.type = type;
	}
	
	public Presence(JID to,String type,XExtension extension)
	{
		super(to,(Extension)extension);
		this.type = type;
	}

	public Presence(JID to,String type, int show, String status)
	{
	    super(to);
		this.show = show;
		this.status = status;
		this.type = type;
	}

	public Presence(int show, String status,int priority,XExtension extension)
	{
		super((Extension)extension);
		if(show == UNAVAILABLE) {
            type = "unavailable";
		} else if(show == INVISIBLE) {
			type = "invisible";
			return;
		} else {
            type = "available";
        }
	    this.show = show;
		this.status = status;
		this.priority = priority;
	}
	
//	public Presence(int show, String status,int priority,XExtension extension)
//	{
//		super(presence.getTo(),(Extension)extension);
//		this,type = presence.getType();
//		this.show = presence.getShow();
//		this.status = presence.getStatus();
//		this.priority = priority;
//	}

    protected Presence(PresenceBuilder pb)
	{
		super(pb);
		show = pb.show;
		status = pb.status;
		priority = pb.priority;
		type = pb.type;
	}

	public int getShow()
	{
		return show;
	}
	
	/**
	 * converts the constant status to a readable version
	 * @param show
	 * @return the long version of the presence
	 */
	public static String toLongShow(int show)
	{ 
		return I18N.gettext(getI18NKey(show));
//		switch(show)
//		{
//			case Presence.FREE_FOR_CHAT:return I18N.gettext("main.presence.Free_for_Chat");
//			case Presence.AWAY: return I18N.gettext("main.presence.Away");
//			case Presence.XA: return I18N.gettext("main.presence.Extended_Away");
//			case Presence.DND: return I18N.gettext("main.presence.Do_not_Disturb");
//			case Presence.NONE: return I18N.gettext("main.presence.Unknown");
//			case Presence.UNAVAILABLE: return I18N.gettext("main.presence.Unavailable");
//			case Presence.INVISIBLE: return I18N.gettext("main.presence.Invisible");
//			default: return I18N.gettext("main.presence.Available");
//		}
	}
	
	/**
	 * converts the constant status to a readable version
	 * @param show
	 * @return the long version of the presence
	 */
	public static String getI18NKey(int show)
	{ 
		switch(show)
		{
			case Presence.FREE_FOR_CHAT:return "main.presence.Free_for_Chat";
			case Presence.AWAY: return "main.presence.Away";
			case Presence.XA: return "main.presence.Extended_Away";
			case Presence.DND: return "main.presence.Do_not_Disturb";
			case Presence.NONE: return "main.presence.Unknown";
			case Presence.UNAVAILABLE: return "main.presence.Unavailable";
			case Presence.INVISIBLE: return "main.presence.Invisible";
			default: return "main.presence.Available";
		}
	}
	

	public String getStatus()
	{
		return status;
	}

	public int getPriorety()
	{
		return priority;
	}

	public String getType(){return type;}

	public void appendToXML(StringBuffer xml)
    {//make short cut?
        xml.append("<presence");
		appendBaseAttributes(xml);
		if(!type.equals("available")) appendAttribute(xml,"type",type);
		xml.append(">");
		if(! (show == AVAILABLE || show == UNAVAILABLE))
		{
			switch (show)
			{
				case Presence.FREE_FOR_CHAT: appendElement(xml,"show","chat"); break;
				case Presence.AWAY: appendElement(xml,"show","away"); break;
				case Presence.XA: appendElement(xml,"show","xa"); break;
				case Presence.DND: appendElement(xml,"show","dnd"); break;
			}	
		}
		appendElement(xml,"status",status);
		if(priority!=0)	appendElement(xml,"priority",Integer.toString(priority));
		if("error".equals(type)) appendError(xml);
		appendExtensions(xml);
		xml.append("</presence>");
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
