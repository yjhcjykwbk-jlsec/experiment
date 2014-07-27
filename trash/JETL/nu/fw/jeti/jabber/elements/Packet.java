package nu.fw.jeti.jabber.elements;

import java.util.LinkedList;
import java.util.Iterator;

import nu.fw.jeti.jabber.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.backend.*;

/**
 * @author E.S. de Boer
 */

public abstract class Packet extends XMLData
{
	private JID to;
	private JID from;
	private String id;
	private int errorCode;
	private String errorDescription;
	private XMPPError xmppError;
	private LinkedList extensions;

	public Packet(){}

	public Packet(JID to)
	{
		this.to = to;
	}
	
	public Packet(Extension ex)
	{
		if(ex!=null)
		{
		    extensions = new LinkedList();
		    extensions.add(ex);
		}
	}

    public Packet(JID to,Extension ex)
	{
		this(ex);
		this.to = to;
	}

	public Packet(JID to,String id,Extension ex)
	{
		this(to);
		this.id = id;
		if(ex!=null)
		{
		    extensions = new LinkedList();
		    extensions.add(ex);
		}
	}
	
	public Packet(JID to,String id,String error,int errorCode)
	{
		this(to);
		this.id = id;
		this.errorDescription = error;
		this.errorCode = errorCode; 
	}
	
	public Packet(JID to,String id,XMPPError error)
	{
		this(to);
		this.id = id;
		xmppError = error; 
	}

	protected Packet(PacketBuilder pb)
	{
		to = pb.to;
		from = pb.from;
		id = pb.id;
		errorCode = pb.errorCode;
		errorDescription = pb.errorDescription;
		extensions = pb.getExtensions();
		xmppError = pb.xmppError;
	}

	public IQExtension getIQExtension()
	{
		if(extensions == null)return null;
	    return (IQExtension)extensions.getFirst();
	}

	public XMPPError getXMPPError()
	{
		return xmppError;
	}
	
	public Iterator getExtensions()
	{
		if(extensions == null) return null;
	    return extensions.iterator();
	}

	public boolean hasExtensions(){return extensions != null;}

	public String getID(){return id;}

	public JID getTo(){return to;}

	public JID getFrom(){return from;}

	public int getErrorCode(){return errorCode;}

	public String getErrorDescription() {
       if (xmppError!=null && errorDescription.length() == 0) {
           StringBuffer desc = new StringBuffer();
            for(Iterator i = xmppError.getXMPPErrors(); i.hasNext();) {
                if (desc.length() > 0) {
                    desc.append(", ");
                }
                XMPPErrorTag et = (XMPPErrorTag)i.next();
                desc.append(I18N.gettext("main.error.xmpp." + et.getError()));
            }
            if (desc.length() > 0) {
                return desc.toString();
            }
            else return I18N.gettext("main.error.Unknown_error");
        } else {
            return errorDescription;
        }
    }

	protected void appendBaseAttributes(StringBuffer xml)
	{
		appendAttribute(xml,"to",to);
		appendAttribute(xml,"from",from);
		appendAttribute(xml,"id", id);

	}

	protected void appendError(StringBuffer xml)
	{
		if(xmppError==null)
		{
			xml.append("<error");
			appendAttribute(xml,"code",String.valueOf(errorCode));
			if(errorDescription == null)
			{
				xml.append( "/>");
				return;
			}
			xml.append(">");
			escapeString(xml,errorDescription);
			xml.append("</error>");
		}
		else xmppError.appendToXML(xml);
	}

	protected void appendExtensions(StringBuffer xml)
	{
		if(extensions !=null)
		{
			for(Iterator i = extensions.iterator();i.hasNext();)
			{
				((Extension)i.next()).appendToXML(xml);
			}
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
