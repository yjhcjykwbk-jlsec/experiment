package nu.fw.jeti.jabber.elements;

import java.util.LinkedList;
import nu.fw.jeti.jabber.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public abstract class PacketBuilder
{
	public JID to;
	public JID from;
	public String id;
	public int errorCode;
	public String errorDescription;
	private String type;
	public XMPPError xmppError;
	private LinkedList extensions;

	public void reset()
	{
	    to = null;
		from = null;
		id = null;
		extensions = null;
		errorCode = 0;
		xmppError=null;
		type=null;
		errorDescription = null;
	}

	public void setTo(JID to){this.to =to;}
	public void setFrom(JID from){this.from =from;}
	public void setId(String id){this.id =id;}
	public void setErrorType(String type){this.type =type;}
	public void setErrorCode(int errorCode){this.errorCode =errorCode;}
	public void setErrorDescription(String errorDescription){this.errorDescription = errorDescription;}

	public void addExtension(Extension extension)
	{
		if(extension == null) return;
		if(extension instanceof XMPPErrorTag)
		{
			if(xmppError==null)xmppError=new XMPPError(type,errorCode);
			xmppError.addError((XMPPErrorTag)extension);
		}
		else
		{
		    if(extensions == null) extensions = new LinkedList();
			extensions.add(extension);
		}
	}

	public LinkedList getExtensions()
	{
	    return extensions;
	}

	abstract public Packet build() throws InstantiationException;
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
