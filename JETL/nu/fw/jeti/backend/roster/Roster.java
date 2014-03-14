package nu.fw.jeti.backend.roster;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.fw.jeti.events.CompleteRosterListener;
import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.events.RosterListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQXRoster;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.elements.RosterItem;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.StringArray;

/**
 * Class that controls the Roster. JIDS whithout a username
 * are assumed to be a transport/server and are transfered to Server.
 * The items in the roster are sorted stored.
 * Changes in the roster are broadcasted using the RosterListener
 * @see Server
 * @see RosterListener
 * @author E.S. de Boer
 * @version 1.0
 */

public class Roster implements CompleteRosterListener, PresenceListener
{
	private JIDStatusTree jidTree;
	private static Map jidStatussen = new HashMap(32); //default 20 different?;//fast access to tree
	private Backend backend;
	private boolean first; //server hack
	private Server server;
	
	public Roster(Backend backend, Server server)
	{
		this.server = server;
		this.backend = backend;
		backend.addListener(CompleteRosterListener.class, this);
		backend.addListener(PresenceListener.class, this);
	}

	//-------------------roster events--------------------------------------\\
	public void rosterReceived(InfoQuery infoquery, IQXRoster roster)
	{
		if (infoquery.getType().equals("result"))
		{
			//complete new roster
			completeRoster(roster);
		}
		else if (infoquery.getType().equals("set"))
		{
			//add result
			for (Iterator i = roster.getItems(); i.hasNext();)
			{
				RosterItem item = (RosterItem) i.next();
				JID jid = item.getJID();
				String nick = item.getName();
				if (nick == null) nick = jid.toString();
				NormalJIDStatus oldJIDStatus = (NormalJIDStatus) jidStatussen.get(jid);
				if ("remove".equals(item.getSubscription()))
				{
					removeFromRoster(oldJIDStatus, item, jid);
				}
				else if (oldJIDStatus == null)
				{
					//group must be present (no unfiled) except when server
					if (item.getGroups() != null || jid.getUser() == null) addToRoster(nick, item, jid);
				}
				else
				{
					changeItem(oldJIDStatus, nick, item, jid);
				}
			}
		}
	}

	private void completeRoster(IQXRoster roster)
	{
		//add complete roster
		//System.out.println("result");
		jidStatussen = new HashMap(32); //default 20 different?
		server.addJIDStatussen(jidStatussen);
		jidTree = new JIDStatusTree();
		server.clear();
		for (Iterator i = roster.getItems(); i.hasNext();)
		{
			RosterItem item = (RosterItem) i.next();
			JIDStatus jidStatus = new NormalJIDStatus(item);
			jidStatussen.put(item.getJID(), jidStatus);
			if (item.getJID().getUser() == null)
			{
				//server /transport
				server.addServerNoFire(jidStatus);
			}
			else
			{
				if (item.getGroups() == null)
					addJIDStatus(I18N.gettext("main.main.roster.Unfiled"), jidStatus);
				else
				{
					for (Iterator j = item.getGroups().iterator(); j.hasNext();)
					{
						//new jidstatus per group?
						addJIDStatus((String) j.next(), jidStatus);
					}
				}
			}
		}
		backend.rosterLoaded();
		server.fire();
		for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
		{
			((RosterListener) j.next()).rosterReplaced(jidTree);
		}
	}

	private void removeFromRoster(NormalJIDStatus oldJIDStatus, RosterItem item, JID jid)
	{
		if (oldJIDStatus == null)
			return; //jidstatus unknown
		String oldNick = oldJIDStatus.getNick();
		if (item.getJID().getUser() == null)
		{
			//server /transport
			server.removeServer(oldNick, oldJIDStatus);
		}
		else
		{
			if (oldJIDStatus.groupCount() < 1)
			{
				//unknown group
				removeJIDStatus(I18N.gettext("main.main.roster.Unfiled"), oldNick, oldJIDStatus);
			}
			else
			{
				//known groups
				for (Iterator j = oldJIDStatus.getGroups(); j.hasNext();)
				{
					//remove
					removeJIDStatus((String) j.next(), oldNick, oldJIDStatus);
				}
			}
		}
		jidStatussen.remove(jid); //remove from cache
	}

	private void addToRoster(String nick, RosterItem item, JID jid)
	{
		if (!first && (item.getGroups() == null || item.getName() == null))
		{
			//server sends first a rosterset without nickname and groups
			//we don't need that because it generates a new group or new nicknames
			first = true; //first time sended
			return;
		}
		first = false;
		//System.out.println("addnew");
		NormalJIDStatus jidStatus = new NormalJIDStatus(item);
		jidStatussen.put(jid, jidStatus);
		if (jid.getUser() == null)
		{
			//server /transport
			server.addServer(nick, jidStatus);
		}
		else if (item.getGroups() == null)
		{
			//no groups only when complete roster
			addJIDStatusFire(I18N.gettext("main.main.roster.Unfiled"), nick, jidStatus);
		}
		else
		{
			for (Iterator j = item.getGroups().iterator(); j.hasNext();)
			{
				//new jidstatus per group?
				addJIDStatusFire((String) j.next(), nick, jidStatus);
			}
		}
	}

	private void changeItem(NormalJIDStatus oldJIDStatus, String nick, RosterItem item, JID jid)
	{
		//group nickname or other change to rosteritem
		if (!nick.equals(oldJIDStatus.getNick()))
		{
			//nick changes
			nickChanges(oldJIDStatus, nick, item, jid);
		}
		if (jid.getUser() == null) //no else because change nick & change groups could be at same time
		{
			//server doesn't care about groups
			oldJIDStatus.update(item);
		}
		else if (item.getGroups() == null)
		{
			//heeft 0 groups
			if (oldJIDStatus.groupCount() < 1)
				oldJIDStatus.update(item); //had 0 heeft 0 overbodig?
			else
			{
				if (!oldJIDStatus.isGroupPresent(I18N.gettext("main.main.roster.Unfiled")))
				{
					//heeft 0 had meer
					for (Iterator j = oldJIDStatus.getGroups(); j.hasNext();)
					{
						//remove
						removeJIDStatus((String) j.next(), nick, oldJIDStatus);
					}
					addJIDStatusFire(I18N.gettext("main.main.roster.Unfiled"), nick, oldJIDStatus); //add to unfiled
					oldJIDStatus.update(item);
				}
			}
		}
		else
		{
			changeGroups(oldJIDStatus, nick, item);
		}
	}

	private void nickChanges(NormalJIDStatus oldJIDStatus, String nick, RosterItem item, JID jid)
	{
		//nickname changed
		String oldNick = oldJIDStatus.getNick();
		if (jid.getUser() == null)
		{
			//server /transport
			server.removeServer(oldNick, oldJIDStatus);
			oldJIDStatus.update(item);
			server.addServer(nick, oldJIDStatus);
		}
		else
		{
			//nickname is different so new primarys
			//System.out.println("remove old nicknames");
			//remove old
			if (oldJIDStatus.groupCount() < 1)
			{
				//unknown group
				removeJIDStatus(I18N.gettext("main.main.roster.Unfiled"), oldNick, oldJIDStatus);
			}
			else
			{
				//known groups
				for (Iterator j = oldJIDStatus.getGroups(); j.hasNext();)
				{
					//remove
					removeJIDStatus((String) j.next(), oldNick, oldJIDStatus);
				}
			}
			oldJIDStatus.update(item);
			//addnew
			if (item.getGroups() == null)
			{
				addJIDStatusFire(I18N.gettext("main.main.roster.Unfiled"), nick, oldJIDStatus);
			}
			else
			{
				for (Iterator j = item.getGroups().iterator(); j.hasNext();)
				{
					//new jidstatus per group?
					addJIDStatusFire((String) j.next(), nick, oldJIDStatus);
				}
			}
		}
		
	}

	private void changeGroups(NormalJIDStatus oldJIDStatus, String nick, RosterItem item)
	{
		/*
		if(oldJIDStatus.groupCount() < 1)//overbodig??
		{//had 0 heeft meer
			removeJIDStatus("Unfiled",nick,oldJIDStatus);
			for(Iterator j = item.getGroups().iterator();j.hasNext();)
			{//new jidstatus per group?
				addJIDStatusFire((String) j.next(),nick,oldJIDStatus);
			}
			oldJIDStatus.update(item);
		}
		else
		*/
		//{//heeft x had y
		StringArray oldGroups = oldJIDStatus.getGroupsCopy();
		for (Iterator j = item.getGroups().iterator(); j.hasNext();)
		{
			//new jidstatus per group?
			String newGroup = (String) j.next();
			if (oldGroups.contains(newGroup))
				oldGroups.remove(newGroup);
			else
				addJIDStatusFire(newGroup, nick, oldJIDStatus);
		}
		for (Iterator j = oldGroups.iterator(); j.hasNext();)
		{
			//remove old groups
			removeJIDStatus((String) j.next(), nick, oldJIDStatus);
		}
		oldJIDStatus.update(item);
		//}
	}

	//---------------------add //remove //change methods--------------------------------------
	private void addJIDStatus(String group, JIDStatus jidStatus)
	{
		//add jidstatus for complete roster the roster will be completely refreshed so no fires per jidstatus
		JIDStatusGroup jidGroup = jidTree.getGroup(group);
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(jidStatus.getNick());
		if (primary == null)
		{
			primary = new PrimaryJIDStatus(jidStatus.getNick(),jidStatus);
			jidGroup.addPrimaryJIDStatus(primary);
		}
		else primary.addJIDStatus(jidStatus);
		//jidGroup.sort();
	}

	private void addJIDStatusFire(String group, String nick, NormalJIDStatus normalJIDStatus)
	{
	//	System.out.println("add");
		JIDStatusGroup jidGroup;
		if (!jidTree.existGroup(group))
		{
			jidGroup = jidTree.getGroup(group);
			int index = jidTree.indexOfGroup(jidGroup);
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{
				//fire group make
				 ((RosterListener) j.next()).groupAdded(jidGroup, index);
			}
		}
		else jidGroup = jidTree.getGroup(group);
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(nick);
		for (Iterator i = normalJIDStatus.getSecondaryJIDStatussen(); i.hasNext();)
		{//add secondary jidstatussen
			SecondaryJIDStatus jidStatus = (SecondaryJIDStatus) i.next();
			// bug if primary was offline but now becomes online
			// then odd results so now do things slower but consistent 
			//(add is rare so no problem)
			primary = addJIDStatus(nick, jidGroup, primary, jidStatus);
		}
		primary = addJIDStatus(nick, jidGroup, primary, normalJIDStatus);
		primary.updatePresence(normalJIDStatus);
		boolean oldOnline = primary.isOnline(); 
		primary.updateOnline();
		if(!oldOnline && primary.isOnline())
		{//add online
			jidGroup.addOnline();
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{
				((RosterListener) j.next()).groupUpdated(jidGroup, jidTree.indexOfGroup(jidGroup));
			}	
		}
				
	//	{//fire update if changed (faster)	
	//		int index = jidGroup.indexOfPrimaryJIDStatus(primary);
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{
				((RosterListener) j.next()).jidStatussenUpdated(jidGroup,primary);
				//primaryUpdated(jidGroup, primary,index);
			}
	//	}
				
		
		/*
		else if(primary.addJIDStatus(jidStatus))
		{ //fire update
			int index = jidGroup.indexOfPrimaryJIDStatus(primary);
			for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
			{
				((RosterListener)j.next()).primaryUpdated(jidGroup,primary,index);
			}
		}
		else
		{//primary doesn't change
			int index = primary.indexOfJIDStatus(jidStatus);
			for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
			{
				((RosterListener)j.next()).jidStatusAdded(jidGroup,primary,jidStatus,index);
			}
		}
		*/
		//primary.updatePresence(secondary);

		/*
		for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
		{
			((RosterListener)j.next()).jidStatussenUpdated(jidGroup,primary);
		}
		*/
	}
	private PrimaryJIDStatus addJIDStatus(String nick, JIDStatusGroup jidGroup, PrimaryJIDStatus primary, SecondaryJIDStatus jidStatus)
	{
		if (primary == null)
		{
			primary = new PrimaryJIDStatus(nick,jidStatus);
			jidGroup.addPrimaryJIDStatus(primary);
			//primary.addJIDStatus(jidStatus);
			int index = jidGroup.indexOfPrimaryJIDStatus(primary);
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{//fire add primary
				 ((RosterListener) j.next()).primaryAdded(jidGroup, primary, index);
			}
		}
		else
		{
			primary.addJIDStatus(jidStatus);
//			int index = primary.indexOfJIDStatus(jidStatus);
//			for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
//			{//fire add jidstatus
//				((RosterListener)j.next()).jidStatusAdded(jidGroup,primary,jidStatus,index);
//				
//			}
		}
//		primary.updatePresence(jidStatus);
		return primary;
	}

	private void removeJIDStatus(String group, String nick, NormalJIDStatus jidStatus)
	{
		//System.out.println("remove");
		JIDStatusGroup jidGroup = jidTree.getGroup(group);
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(nick);
		boolean oldOnline = primary.isOnline();
		for (Iterator i = jidStatus.getSecondaryJIDStatussen(); i.hasNext();)
		{
			SecondaryJIDStatus secondaryJIDStatus = (SecondaryJIDStatus) i.next();
			if (removeJIDStatus2(secondaryJIDStatus, jidGroup, primary))
			{
				if(oldOnline)
				{//gone offline
					jidGroup.removeOnline();
					for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
					{
						((RosterListener) j.next()).groupUpdated(jidGroup, jidTree.indexOfGroup(jidGroup));
					}
				}
				return;
			}
		}
		boolean deleted = removeJIDStatus2(jidStatus, jidGroup, primary);
		if(!deleted) primary.updateOnline();
		if(((deleted && oldOnline) || (oldOnline && !primary.isOnline()))
            && -1 != jidTree.indexOfGroup(jidGroup))
		{//gone offline
			jidGroup.removeOnline();
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{
				((RosterListener) j.next()).groupUpdated(jidGroup, jidTree.indexOfGroup(jidGroup));
			}
		}
		//fire update
		for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
		{
			((RosterListener) j.next()).jidStatussenUpdated(jidGroup, primary);
		}
	}

	private boolean removeJIDStatus2(JIDStatus jidStatus, JIDStatusGroup jidGroup, PrimaryJIDStatus primary)
	{
		//return true if primary deleted
		//int jidStatusIndex =0;
		//if(primary.hasMultiple()) jidStatusIndex = primary.indexOfJIDStatus(jidStatus);
		if (primary.removeJIDStatus(jidStatus))
		{
			//if (primary.isEmpty())
			{
				int index = jidGroup.indexOfPrimaryJIDStatus(primary);
				jidGroup.removePrimaryJIDStatus(primary);
				for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
				{
					//fire delete
					 ((RosterListener) j.next()).primaryDeleted(jidGroup, primary, index);
				}
				//fire remove
				if (jidGroup.size() < 1)
				{
					//fire group remove
					int index2 = jidTree.indexOfGroup(jidGroup);
					jidTree.removeGroup(jidGroup);
					for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
					{
						//fire group delete
						 ((RosterListener) j.next()).groupDeleted(jidGroup, index2);
					}
				}
				return true;
			}
			//			else
			//			{//fire update
			//				for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
			//				{
			//					((RosterListener)j.next()).jidStatussenUpdated(jidGroup,primary);
			//				}
			//			}
		}
		//		else
		//		{//primary doesn't change
		//			for(Iterator j = backend.getListeners(RosterListener.class);j.hasNext();)
		//			{
		//				((RosterListener)j.next()).jidStatusDeleted(jidGroup,primary,jidStatus,jidStatusIndex);
		//			}
		//		}
		return false;
	}

	//------------------------------Prescence event------------------------------------\\

	public void presenceChanged(Presence presence)
	{
		JID jid = presence.getFrom();
		JIDStatus jidStatus = (JIDStatus) jidStatussen.get(jid);
		if (jidStatus == null)
		{
			return;
		}
		if (jid.getUser() == null)
		{
			//server, server has no multiple resources
			 ((NormalJIDStatus) jidStatus).updatePresence(presence);
			server.showChange(jidStatus);
			return;
		}
		SecondaryJIDStatus secondary = ((NormalJIDStatus) jidStatus).getSecondaryJIDStatus(jid.getResource());
		boolean seconderyAdded = false;
		ResourceJIDStatus remove = null;
		if (presence.getShow() == Presence.UNAVAILABLE)
		{
			remove = ((NormalJIDStatus) jidStatus).removeResource(jid.getResource());
			if (remove == null)
			{
				//no remove so no resource so update presence to unavailable 
				secondary.updatePresence(presence);
			}
		}
		else
		{
			if (secondary == null)
			{
				//new resource
				secondary = ((NormalJIDStatus) jidStatus).addSecondaryJIDStatus(jid);
				seconderyAdded = true;
			}
			else if (secondary.getShow() == presence.getShow())
			{//show hasn't changed
				if(secondary.getType().equals("msn") && Preferences.getBoolean("jeti","showRealNick", false))
				{//should status be visible?
					if(!secondary.getStatus().equals(presence.getStatus()))
					{//status has changed
						secondary.updatePresence(presence);
						for (Iterator i = ((NormalJIDStatus) jidStatus).getGroups(); i.hasNext();)
						{//update tree part status is in, replace by update only the changed part?
							JIDStatusGroup jidGroup = jidTree.getGroup((String) i.next());
							PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(jidStatus.getNick());
							for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
							{
								((RosterListener) j.next()).jidStatussenUpdated(jidGroup, primary);
							}
						}
					}
				}
				secondary.updatePresence(presence);
				return;
			}
			secondary.updatePresence(presence);
			//show has changed so update
			 ((NormalJIDStatus) jidStatus).sortSecondary();
		}
		//change
		for (Iterator i = ((NormalJIDStatus) jidStatus).getGroups(); i.hasNext();)
		{
			//new jidstatus per group?
			JIDStatusGroup jidGroup = jidTree.getGroup((String) i.next());
			PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(jidStatus.getNick());
			if (seconderyAdded) primary.addJIDStatus(secondary);
			if (remove != null) primary.removeJIDStatus(remove);
			else if (secondary != null) primary.updatePresence(secondary);
			boolean oldOnline = primary.isOnline();
			primary.updateOnline();
			boolean newOnline = primary.isOnline();
			if(oldOnline && !newOnline)
			{//gone offline
				jidGroup.removeOnline();
				for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
				{
					((RosterListener) j.next()).groupUpdated(jidGroup, jidTree.indexOfGroup(jidGroup));
				}
			}
			else if(!oldOnline && newOnline)
			{//gone online
				jidGroup.addOnline();
				for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
				{
					((RosterListener) j.next()).groupUpdated(jidGroup, jidTree.indexOfGroup(jidGroup));
				}	
			}
			//int index = jidGroup.indexOfPrimaryJIDStatus(primary);
			for (Iterator j = backend.getListeners(RosterListener.class); j.hasNext();)
			{
				((RosterListener) j.next()).jidStatussenUpdated(jidGroup, primary);
			}
		}
	}

	public static JIDStatus getJIDStatus(JID jid)
	{
		return (JIDStatus) jidStatussen.get(jid);
	}

	public String[] getAllGroups()
	{
		if (jidTree==null) return new String[]{ I18N.gettext("main.main.roster.Friends") };
		return jidTree.getGroups();
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
