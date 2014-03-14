package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class Message extends Packet
{
	private String body;
	private String thread;
	private String subject;
	private String type;

	public Message(MessageBuilder mb)
	{
		super(mb);
		body = mb.body;
		thread = mb.thread;
		subject = mb.subject;
		type = mb.type;
	}

	/**
	 * normal message
	 * @param body
	 * @param to
	 */
	public Message(String body,String subject, JID to)
	{
		super(to);
		type = "normal";
		this.body = body;
		this.subject = subject;
	}
	
	/**
	 * normal message
	 * @param body
	 * @param to
	 */
	public Message(String body, JID to,XExtension extension)
	{
		super(to, (Extension) extension);
		type = "normal";
		this.body = body;
	}
	
	
	/**
	 * groupchat message
	 * @param to
	 * @param body
	 */
	public Message(JID to,String body,XExtension extension)
	{
		super(to, (Extension) extension);
		type = "groupchat";
		this.body = body;
	}
	
	/**
	 * groupchat message
	 * @param to
	 * @param body
	 */
	public Message(JID to,String body)
	{
		super(to);
		type = "groupchat";
		this.body = body;
	}

	public Message(String body, JID to, String thread)
	{
		super(to);
		type = "chat";
		this.body = body;
		this.thread = thread;
	}

	public Message(String body, JID to, String id, String thread, XExtension extension)
	{
		super(to, id, (Extension) extension);
		type = "chat";
		this.body = body;
		this.thread = thread;
	}

	public Message(String body, JID to, String id,XExtension extension)
	{
		super(to, id, (Extension) extension);
		type = "chat";
		this.body = body;
	}
		
	public String getSubject()
	{
		return subject;
	}

	public String getThread()
	{
		return thread;
	}

	public String getBody()
	{
		return body;
	}

	public String getType()
	{
		return type;
	}

	public void appendToXML(StringBuffer xml)
	{
		//make short cut?
		xml.append("<message");
		appendBaseAttributes(xml);
		if (!type.equals("normal"))
			appendAttribute(xml, "type", type);
		xml.append(">");
		appendElement(xml, "thread", thread);
		appendElement(xml, "subject", subject);
		appendElement(xml, "body", body);
		if ("error".equals(type))
			appendError(xml);
		appendExtensions(xml);
		xml.append("</message>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
