package nu.fw.jeti.backend.roster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.elements.RosterItem;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.StringArray;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

 //stores jid + status

//inconsistent with equals to make consistant add jid + equals if comletejids are same
public class NormalJIDStatus implements SecondaryJIDStatus
{
	private JID jid;
	private JID completeJID;
	private boolean online = false;
	private int show;
	private String status;
	private String nickname;
	private String subscription;
	private String waiting;
	private StringArray groups;
	ArrayList resources;
	private String resource;
	private String type;
	//private StringArray namespaces;

	public NormalJIDStatus(RosterItem item)
	{//call update??
		jid = item.getJID();
		completeJID = jid;
		nickname = item.getName();
		if(nickname == null || nickname.equals(""))
		{
		   nickname = jid.toString();
		}
		subscription = item.getSubscription();
		waiting = item.getAsk();
		groups = item.getGroups();
		if (groups== null)
		{
			groups = new StringArray();
			groups.add(I18N.gettext("main.main.roster.Unfiled"));
		}
		show = Presence.UNAVAILABLE;
		resource = completeJID.getResource();
		//System.out.println("jidstatus made");
		JIDStatus server = Roster.getJIDStatus(new JID(jid.getDomain()));
		if(server == null) type =null;
		else type = server.getType();
	}
	
	
	/**
	 * Make jidStatus from jid and nickname (used by groupchat) 
	 * @param jid
	 * @param nickname
	 */
	public NormalJIDStatus(JID jid,String nickname)
	{//call update??
		this.jid = jid;
		completeJID = jid;
		this.nickname = nickname; 
		if(nickname == null || nickname.equals(""))
		{
		   nickname = jid.toString();
		}
		show = Presence.UNAVAILABLE;
		resource = completeJID.getResource();
		//System.out.println("jidstatus made");
		JIDStatus server = Roster.getJIDStatus(new JID(jid.getDomain()));
		if(server == null) type =null;
		else type = server.getType();
	}

	//public JIDStatus(JID jid,String nick, String subscription,String waiting,String group)
	//{
		/*
		this.jid = jid;
		completeJID = jid;
		nickname = nick;
		groups = new StringArray();
		groups.add(group);
		if(nick == null)
		{
			nickname = jid.getUsername();
			if(nickname == null) nickname ="";
		}
		if (subscription == null) subscription = "none";
		this.subscription =subscription;
		if (waiting == null) waiting = "nothing";
		this.waiting = waiting;
		JIDStatus server = Cache.getJidStatus(new JID(jid.getServer()));
		if(server == null) type =null;
		else type = server.getType();
		//System.out.println(jid+ " " + nick + " " + subscription);
		*/
	//}

	//public void updatePresence(JID from, boolean online, String show, String status)
	//{
		/*
		String resource = from.getResource();
		if(resource == null)
		{//if multiple resources resource can not be null
			this.online = online;
			this.show = show;
			this.status = status;
		}
		else
		{
			if(resources == null)
			{
				if((resource.equals(completeJID.getResource()) || completeJID.getResource() == null))
				{//only 1 resource  because recourse are the same or current resource is null (no presence received)
					this.online = online;
					this.show = show;
					this.status = status;
					completeJID = from;
				}
				else
				{//two resources
					if(online)
					{//if new resource = offline do nothing
						if(this.online)
						{// if currently online create recourcemap
							resources =new HashMap();
							resources.put(completeJID.getResource(),new String[]{this.show,this.status}); //add old resource
							resources.put(resource,new String[]{show,status});//add new resource
						}
						completeJID = from;
						this.online = online;
						this.show = show;
						this.status = status;
					}
				}
			}
			else
			{
				// add resource when online / remove when offline
				if(online)
				{
					resources.put(resource,new String[]{show,status});
					completeJID = from;
					this.online = online;
					this.show = show;
					this.status = status;
				}
				else
				{
					resources.remove(resource);
					if(resources.size() == 1)
					{
						Map.Entry entry = (Map.Entry) resources.entrySet().iterator().next();
						completeJID = new JID(from.getUsername(),from.getServer(),(String)entry.getKey());
						String[] sta = (String[]) entry.getValue();
						this.show = sta[0];
						this.status = sta[1];
						resources = null;
					}
					else
					{//resources >1
						System.out.println("more than 2 resources online");
					}
				}
			}
		}
		*/
	//}

	public void update(RosterItem item)
	{
		nickname = item.getName();
		if(nickname == null || nickname.equals(""))
		{
			nickname = jid.toString();
		}
		subscription = item.getSubscription();
		waiting = item.getAsk();
		groups = item.getGroups();
		if (groups== null)
		{
			groups = new StringArray();
			groups.add(I18N.gettext("main.main.roster.Unfiled"));
		}
	}

	public void updatePresence(Presence presence)
	{//resources
		online = presence.getType().equals("available");
		show = presence.getShow();
		status = presence.getStatus();
		completeJID = presence.getFrom();
		resource = completeJID.getResource();
	}

	public void sortSecondary()
	{//swap resources ,highest in normal
		if(resources == null) return;
		Collections.sort(resources);
		if(this.compareTo(resources.get(0)) > 0)
		{
			//System.out.println("swap");
			ResourceJIDStatus resourceJIDStatus = (ResourceJIDStatus) resources.get(0);
			ResourceJIDStatus tempJIDStatus =(ResourceJIDStatus) resourceJIDStatus.clone();
			resourceJIDStatus.update(completeJID,show,status,online);
			completeJID = tempJIDStatus.getCompleteJID();
			resource = completeJID.getResource();
			show = tempJIDStatus.getShow();
			status = tempJIDStatus.getStatus();
			online = tempJIDStatus.isOnline();
			Collections.sort(resources);
		}
	}

	public String toString()
	{//jid
		if(status!=null && jid.getUser()!=null  && Preferences.getBoolean("jeti","showRealNick", false))
		{
			if("msn".equals(type)) return status;
		}
		return nickname;
	}

	public JID getJID(){return jid;}

	public JID getCompleteJID(){return completeJID;}

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

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getNick()
	{
		return nickname;
	}

	public void setNick(String nick)
	{
		nickname = nick;
	}

	public String getType()
	{
		if(type == null) return "jabber";
		return type;
	}

	public void setType(String type)
	{
		//System.out.println(type);
		this.type = type;
	}


//	public StringArray getNamespaces()
//	{
//		return namespaces;
//	}
//
//	public void setNamespaces(StringArray namespaces)
//	{
//		this.namespaces = namespaces;
//	}


	public String getSubscription(){return subscription;}

	public String getWaiting()
	{
		return waiting;
	}
	
	public JIDStatus normalJIDStatus()
	{
		return this;
	}

	public SecondaryJIDStatus getSecondaryJIDStatus(String resource)
	{
		if(this.resource == null)
		{
			this.resource = resource;
			return this;
		}
		if(resource.equals(this.resource)) return this;
		if(resources !=null)
		{
			for(Iterator i = resources.iterator();i.hasNext();)
			{
				SecondaryJIDStatus secondary = (SecondaryJIDStatus)i.next();
				if(resource.equals(secondary.getCompleteJID().getResource())) return secondary;
			}
		}
		return null;
	}

	public ResourceJIDStatus addSecondaryJIDStatus(JID jid)
	{
		ResourceJIDStatus resource = new ResourceJIDStatus(jid,this);
		if(resources == null) resources = new ArrayList(4);
		resources.add(resource);
		return resource;
	}
	
	
	public Iterator getSecondaryJIDStatussen()
	{
		if(resources ==null)
		{
			return new Iterator()
			{
				public boolean hasNext()
				{
					return false;
				}
				
				public Object next(){throw new NoSuchElementException();}
				
				public void remove(){}
			};
		}
		else
		{
			return new Iterator()
			{
				private int count = resources.size();
							
				public boolean hasNext()
				{
					return count > 0;
				}
				
				public Object next()
				{
					count--;
					if(count<0)throw new NoSuchElementException();
					return resources.get(count); 
				}
				
				public void remove(){}
				
			};
		}
	}
	
	public ResourceJIDStatus removeResource(String resource)
	{
		//System.out.println(resource + " mee");
		//System.out.println(this.resource + " eigen");
		
		if(resource == null) return null; //no resource so remove nothing
		if(resource.equals(this.resource))
		{
			//System.out.println(resource + " equal"    );
			if(resources != null)
			{
				ResourceJIDStatus resourceJIDStatus =(ResourceJIDStatus) resources.remove(0);
				completeJID = resourceJIDStatus.getCompleteJID();
				this.resource =    completeJID.getResource();
				show = resourceJIDStatus.getShow();
				status = resourceJIDStatus.getStatus();
				online = resourceJIDStatus.isOnline();

				if(resources.isEmpty()) resources = null;
				return resourceJIDStatus ;
			}
			else this.resource = null;
			completeJID = jid;
			return null;//resource does not change
		}
	  	for(Iterator i = resources.iterator();i.hasNext();)
		{
			ResourceJIDStatus resourceJIDStatus = (ResourceJIDStatus)i.next();
			if(resourceJIDStatus.getCompleteJID().getResource().equals(resource))
			{
				resources.remove(resourceJIDStatus);
				if(resources.isEmpty()) resources = null;
				return resourceJIDStatus;
			}
		}
		System.out.println("error"); //hier hoort ie niet te komen
		return null;
	}

	public int groupCount()
	{
		if(groups == null) return 0;
		return groups.getSize();
	}

	public void addGroup(String group)
	{
		groups.add(group);
	}

	public void removeGroup(String group)
	{
		groups.remove(group);
	}

	public String getGroup(int group)
	{
		return groups.get(group);
	}

	public StringArray getGroupsCopy()
	{
		return (StringArray)groups.clone();
	}

	public Iterator getGroups()
	{
		return groups.iterator();
	}

	public boolean isGroupPresent(String group)
	{
		return groups.contains(group);
	}
	
/*
	public boolean equals(Object object)
	{
		if (object instanceof JIDStatus)
		{
			JIDStatus temp = (JIDStatus)object;
			return temp.getCompleteJID().equals(completeJID);
		}
		else return false;
	}
	
	public int hashCode()
	{
		System.out.println("hash");
		return completeJID.hashCode();  
	}
*/	

	public int compareTo(Object o)
	{//compare jid
		return compareTo(this,(JIDStatus)o);
	}

	public int compareTo(JIDStatus jidStatus1,JIDStatus jidStatus)
	{
		if(jidStatus1.equals(jidStatus)) return 0;
		if(jidStatus1.getShow() == jidStatus.getShow())
		{
			if(jidStatus1.getJID().getDomain().equals(nu.fw.jeti.backend.Connect.getMyJID().getDomain()))
			{//is this jid own server?
				if(jidStatus.getJID().getDomain().equals(nu.fw.jeti.backend.Connect.getMyJID().getDomain()))
				{//other jid has the same server
					return (jidStatus1.getJID().toStringNoResource().compareTo(jidStatus.getJID().toString()));
				}
				return -1;
			}
			//check if other jid has the server you are logged in to
			if(jidStatus.getJID().getDomain().equals(nu.fw.jeti.backend.Connect.getMyJID().getDomain())) return 1;
			//check servertypes
			if(jidStatus1.getType().equals(jidStatus.getType()))
			{
				return (jidStatus1.getJID().toStringNoResource().compareTo(jidStatus.getJID().toString()));
			}
			if (getTypeRank(jidStatus1.getType()) < getTypeRank(jidStatus.getType())) return -1;
			return 1;
		}
		if(jidStatus1.getShow() < jidStatus.getShow()) return -1;
		return 1;
	}

//	private int getShowRank(String show)
//	{
//		if(show.equals("chat")) return 0;
//		if(show.equals("available")) return 1;
//		if(show.equals("away")) return 2;
//		if(show.equals("dnd")) return 3;
//		if(show.equals("xa")) return 4;
//		return 5;
//	}

	private int getTypeRank(String type)
	{
		if(type.equals("jabber")) return 0;
		if(type.equals("msn")) return 1;
		if(type.equals("icq")) return 2;
		return 3;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
