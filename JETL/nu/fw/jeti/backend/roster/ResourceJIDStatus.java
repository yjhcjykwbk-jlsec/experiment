package nu.fw.jeti.backend.roster;


import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.util.StringArray;

import nu.fw.jeti.jabber.*;


/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class ResourceJIDStatus implements SecondaryJIDStatus,Cloneable
{
	private JID jid;
	private boolean online = false;
	private int show;
	private String status;
	private NormalJIDStatus normalJIDStatus;

	public ResourceJIDStatus(JID jid,NormalJIDStatus normalJIDStatus)
	{
		this.jid = jid;
		this.normalJIDStatus = normalJIDStatus;
	}

	public void updatePresence(Presence presence)
	{//resources
		online = presence.getType().equals("available");
		show = presence.getShow();
		status = presence.getStatus();
		jid = presence.getFrom();
	}

	public void update(JID jid,int show, String status,boolean online)
	{
		this.online = online;
		this.show = show;
		this.status = status;
		this.jid = jid;
	}

	public String toString()
	{//jid
		return jid.toString();
	}
	
	public JIDStatus normalJIDStatus()
	{
		return normalJIDStatus; 
	}

	public JID getJID(){return normalJIDStatus.getJID();}

	public JID getCompleteJID(){return jid;}

//probeley only online so online can go
	public boolean isOnline()
	{
		return online;
	}

	public int getShow()
	{
		return show;
	}

	public String getStatus()
	{
		return status;
	}

	public String getNick()
	{
		return normalJIDStatus.getNick();
	}

	public String getType()
	{
		return normalJIDStatus.getType();
	}

	public String getSubscription(){return normalJIDStatus.getSubscription();}

	public String getWaiting()
	{
		return normalJIDStatus.getWaiting();
	}

	public StringArray getGroupsCopy(){return normalJIDStatus.getGroupsCopy();}

	public int groupCount(){return normalJIDStatus.groupCount();}

	public boolean isGroupPresent(String group){return normalJIDStatus.isGroupPresent(group);}

	public int compareTo(Object o)
	{//compare jid
		//if(nickname==null) return -1;
		//return nickname.compareTo(((JIDStatus2)o).nickname);
		if(!(o instanceof ResourceJIDStatus)) return normalJIDStatus.compareTo(this,(JIDStatus)o);
		ResourceJIDStatus resourceJIDStatus = (ResourceJIDStatus)o;
		if(this.equals(resourceJIDStatus)) return 0;
		int result = (normalJIDStatus.compareTo(this,resourceJIDStatus));
		if(result == 0)
		{
			return jid.getResource().compareTo(resourceJIDStatus.getCompleteJID().getResource());
		}
		return result;
	}



	public Object clone()
	{//shallow copy
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
