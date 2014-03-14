package nu.fw.jeti.jabber;

import nu.fw.jeti.util.I18N;

/**
 *
 * @author E.S. de Boer
 * @version 1.0
 */

public final class JID
{
	private String domain;
	private String user;
	private String resource;

    public JID(String server)
    {
		if(server == null || server.equals("")) throw new NullPointerException(I18N.gettext("main.error.Server_has_no_value"));
		this.domain = server;
	}

	public JID(String user,String server)
	{
	    this(server);
		this.user = user;
	}

	public JID(String user,String server,String resource)
	{
		this(user,server);
		this.resource = resource;
	}

	public static JID jidFromString(String jid)
	{
	    if(jid == null || jid.equals("")) return null;
		String node=null, domain=null, resource=null;
		int loc = jid.indexOf("@");
		if (loc != -1)
		{
		  node = jid.substring(0, loc);
		  jid = jid.substring(loc + 1);
		}
		loc = jid.indexOf("/");
		if (loc == -1) domain = jid;
		else
		{
		  domain = jid.substring(0, loc);
		  resource = jid.substring(loc + 1);
		}
		if(domain ==null) return null;
		return new JID(node,domain,resource);
	}

	public static JID checkedJIDFromString(String jid) throws InstantiationException
	{
	    if(jid == null || jid.equals("")) return null;
		String node=null, domain=null, resource=null;
		int loc = jid.indexOf("@");
		if (loc != -1)
		{
		  node = jid.substring(0, loc);
		  jid = jid.substring(loc + 1);
		}
		loc = jid.indexOf("/");
		if (loc == -1) domain = jid;
		else
		{
		  domain = jid.substring(0, loc);
		  resource = jid.substring(loc + 1);
		}
		if (node != null && node.length() > 255) throw new InstantiationException (I18N.gettext("main.error.Username_>_255_Characters"));
		if(domain.indexOf("@") != -1) throw new InstantiationException (I18N.gettext("main.error.Server_or_Username_contains_a_'@'"));
		if(!isValidUser(node)) throw new InstantiationException(I18N.getTextWithAmp("main.error.Username_contains_illegal_chars_(see_english_translation)"));
		if(!isValidServer(domain)) throw new InstantiationException(I18N.gettext("main.error.Server_must_start_with_a_letter_(see english translation)"));
		return new JID(node,domain,resource);
	}

	public static boolean isValidUser(String user)
	{
		if(user == null) return true;
		int len = user.length();
		if (len > 255) return false;
		char c;
		for(int i=0; i<len; i++)
		{
			c = user.charAt(i);
			if (c <= ' ') return false;
			if (c == ':') return false;
			if (c == '@') return false;
			if (c == '"') return false;
			if (c == '>') return false;
			if (c == '<') return false;
			if (c == '/') return false;
			if (c == '\'') return false;
			if (c == '&') return false;
			if (c == '\u077F') return false;
			if (c == '\u0FFE') return false;
			if (c == '\u0FFF') return false;
		}
		return true;
	}

	//check dns name
	public static boolean isValidServer(String server)
	{
		if(server == null || server.equals("")) return false;
		int len = server.length();
		//if (len > 255) return false;
		//first leter must be alphanumeric
		if(!Character.isLetterOrDigit(server.charAt(0)))return false;
		char c;
		for(int i=0; i<len; i++)
		{
			c = server.charAt(i);
			if(!(Character.isLetterOrDigit(c) || c=='.' || c=='-')) return false;
		}
		return true;
	}

	public String getUser(){return user;}

	public String getDomain(){return domain;}

	public String getResource(){return resource;}

	public String toString()
	{
		StringBuffer jid = new StringBuffer();
		if (user != null){
			jid.append(user);
			jid.append("@");
		}
		jid.append(domain);
		if (resource != null){
			jid.append("/");
			jid.append(resource);
		}
		return jid.toString();
	}

	public String toStringNoResource()
	{
		StringBuffer jid = new StringBuffer();
		if (user != null){
			jid.append(user);
			jid.append("@");
		}
		jid.append(domain);
		return jid.toString();
	}


	
	/**
	 * equals doesn't look at resources
	 * @param jid
	 * @return boolean
	 */
	public boolean equals(JID jid){
        return equalsNode(jid) && domain.equalsIgnoreCase(jid.domain);
    }

	private boolean equalsNode(JID jid){
		if (user == null ^ jid.user == null ) return false;
		if(user == null) return true;
		return user.equalsIgnoreCase(jid.user);
	}

	/**
	 * equals doesn't look at resources
	 * @param jid
	 * @return boolean
	 */
	public boolean equals(Object o){
		if(!(o instanceof JID)) return false;
		return equals((JID)o);
	}

	public int hashCode()
	{return toStringNoResource().toLowerCase().hashCode();}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
