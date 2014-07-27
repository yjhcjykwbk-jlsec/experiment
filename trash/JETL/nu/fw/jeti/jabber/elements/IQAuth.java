package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.Backend;



/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQAuth extends Extension implements IQExtension
{
	/** @todo add zeroK */
	private String username;
	private String password;
	private String digest;
	private String resource;
//	private String zeroKHash;
//	private String zeroKToken;
//	private String zeroKSequence;

	public IQAuth(String username, String password,String resource)
    {
		this.username = username;
		this.password = password;
		this.resource = resource;
    }

	protected IQAuth(IQAuthBuilder iqab)
	{
	    username = iqab.username;
		password = iqab.password;
		digest = iqab.digest;
		resource = iqab.resource;
	}

	public boolean hasDigest()
	{
		return digest != null;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		
	}

	public void appendToXML(StringBuffer xml)
    {
        xml.append("<query xmlns=\"jabber:iq:auth\">");
		appendElement(xml,"username",username);
		appendElement(xml,"password",password);
		appendElement(xml,"digest",digest);
		appendElement(xml,"resource",resource);
		//appendChild(retval,"hash",ZeroKHash);
		//appendChild(retval,"token",ZeroKToken);
		//appendChild(retval,"sequence",   ((sequence==-1)?null:new Integer(sequence).toString()));
		xml.append("</query>");
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
