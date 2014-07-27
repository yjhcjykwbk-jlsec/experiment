package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.JID;

/**
 * Info/Query, or IQ, is a simple request-response mechanism. Just as HTTP is a request-response medium,
 * the iq element enables an entity to make a request of, and receive a response from, another entity.
 * The data content of the request and response is defined by the namespace declaration of a
 * direct child element of the iq element.
 * @see <a href=http://www.jabber.org/ietf/draft-miller-xmpp-core-00.html#iq>xmpp-core</a>
 * @author E.S. de Boer
 * @version 1.0
 */

public class InfoQuery extends Packet
{
	private String type;

	public InfoQuery(String type,IQExtension ex)
    {
		this(null,type,null,ex);
    }

    public InfoQuery(JID to,String type,IQExtension ex)
    {
		this(to,type,null,ex);
    }

	public InfoQuery(JID to,String type,String id,IQExtension ex)
    {
		super(to,id,(Extension)ex);
		this.type = type;
    }
    
	public InfoQuery(JID to,String type,String id,IQExtension ex,String error,int errorCode)
	{//TODO remove type and ex, remove completly??
		super(to,id,error,errorCode);
		type = "error";
	}
	
	public InfoQuery(JID to,String id,XMPPError error)
	{
		super(to,id,error);
		type = "error";
	}


	protected InfoQuery(InfoQueryBuilder iqb)
	{
	    super(iqb);
		type = iqb.getType();
	}

	public String getType(){return type;}

    public void appendToXML(StringBuffer xml)
    {
        xml.append("<iq");
		appendBaseAttributes(xml);
		appendAttribute(xml,"type",type);
		if(getExtensions() == null && !"error".equals(getType()))
		{//short cut
		    xml.append("/>");
			return;
		}
		xml.append('>');
		if("error".equals(type)) appendError(xml);
		appendExtensions(xml);
		xml.append("</iq>");
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
