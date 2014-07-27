// Created on 7-okt-2003
package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class XMUCUser extends Extension implements XExtension
{
	public final static int NONE = 0;
	public final static int OUTCAST = 1;
	public final static int MEMBER = 2;
	public final static int ADMIN = 3;
	public final static int OWNER = 4;
	public final static int VISITOR = 5;
	public final static int PARTICIPANT = 6;
	public final static int MODERATOR = 7;
	private int affiliation;
	private int role;
	private String nick;
	private JID jid;
	private int statusCode;
    private String reason;

    public XMUCUser(String affiliation, String role, String nick,
                    JID jid, int statusCode)
    {
        this(affiliation, role, nick, jid, statusCode, null);
    }

    public XMUCUser(String affiliation, String role, String nick,
                    JID jid, int statusCode, String reason)
	{
		if(affiliation == null)this.affiliation = NONE;
		else if(affiliation.equals("outcast"))this.affiliation = OUTCAST;
		else if(affiliation.equals("member"))this.affiliation = MEMBER;
		else if(affiliation.equals("admin"))this.affiliation = ADMIN;
		else if(affiliation.equals("owner"))this.affiliation = OWNER;
		if(role == null)this.role = NONE;
		else if(role.equals("visitor"))this.role = VISITOR;
		else if(role.equals("participant"))this.role = PARTICIPANT;
		else if(role.equals("moderator"))this.role = MODERATOR;
		this.jid = jid;
		this.statusCode = statusCode;
		this.nick = nick;
        this.reason = reason;
    }

    public XMUCUser(JID jid, int affiliation, int role) {
        this.jid = jid;
        this.affiliation = affiliation;
        this.role = role;
	}
	
	/**
	 * @return affiliation
	 */
	public int getAffiliation()
	{
		return affiliation;
	}

	/**
     * @return affiliation
     */
    public String getStringAffiliation()
    {
        return getStringAffiliation(affiliation);
    }

    /**
     * @return affiliation
     */
    static public String getStringAffiliation(int affiliation)
    {
        switch (affiliation) {
        case OUTCAST:
            return I18N.gettext("groupchat.affiliation.outcast");
        case MEMBER:
            return I18N.gettext("groupchat.affiliation.member");
        case ADMIN:
            return I18N.gettext("groupchat.affiliation.admin");
        case OWNER:
            return I18N.gettext("groupchat.affiliation.owner");
        default:
            return I18N.gettext("groupchat.affiliation.none");
        }
    }

    /**
     * Get the protocol name for an affiliation
     *
     * @return affiliation
     */
    static public String getProtocolStringAffiliation(int affiliation)
    {
        switch (affiliation) {
        case OUTCAST:
            return "outcast";
        case MEMBER:
            return "member";
        case ADMIN:
            return "admin";
        case OWNER:
            return "owner";
        default:
            return "none";
        }
    }

    /**
	 * @return role
	 */
	public int getRole()
	{
		return role;
	}
	
    /**
     * @return role
     */
    public String getStringRole()
    {
        return getStringRole(role);
    }

    /**
     * @return role
     */
    static public String getStringRole(int role)
    {
        switch (role) {
        case VISITOR:
            return I18N.gettext("groupchat.role.visitor");
        case PARTICIPANT:
            return I18N.gettext("groupchat.role.participant");
        case MODERATOR:
            return I18N.gettext("groupchat.role.moderator");
        default:
            return I18N.gettext("groupchat.role.none");
        }
    }

    /**
     * Get the protocol name for a role
     *
     * @return role
     */
    static public String getProtocolStringRole(int role)
    {
        switch (role) {
        case VISITOR:
            return "visitor";
        case PARTICIPANT:
            return "participant";
        case MODERATOR:
            return "moderator";
        default:
            return "none";
        }
    }
	
	public String getNick()
	{
		return nick;
	}
	
	public JID getJID()
	{
		return jid;
	}
	
	public int getStatusCode()
	{
		return statusCode;
	}

    public String getReason()
    {
        return reason;
    }
	
	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns= 'http://jabber.org/protocol/muc#user'");
		//xml.append(body);
		xml.append("/>");
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
