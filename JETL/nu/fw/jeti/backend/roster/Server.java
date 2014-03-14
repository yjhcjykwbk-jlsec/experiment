package nu.fw.jeti.backend.roster;

import java.util.*;

import nu.fw.jeti.events.BrowseListener;
import nu.fw.jeti.events.DiscoveryListener;
import nu.fw.jeti.events.RosterListener;
import nu.fw.jeti.events.ServerListener;
import nu.fw.jeti.jabber.elements.DiscoveryInfo;
import nu.fw.jeti.jabber.elements.DiscoveryItem;
import nu.fw.jeti.jabber.elements.IQBrowse;
import nu.fw.jeti.jabber.*;
import nu.fw.jeti.util.I18N;


/**
 * Class that controls the transports/sever from the roster.
 * when a new transport is added this class wil lookup information
 * about the transport (with Browse) and will give every item in the roster
 * registered with that transport the right type.
 * Changes in the server roster are broadcasted using the ServerListener
 * @see ServerListener
 * @see Discovery
 * @see Roster
 * @author E.S. de Boer
 * @version 1.0
 */

//primarys wel/niet?? what about resources?
public class Server implements DiscoveryListener
{
	private JIDStatusTree jidTree = new JIDStatusTree();//serverrostertree
	private Backend backend;
	private boolean fireReady = false;//is the server roster displayed?
	private Map jidStatussen;//reference to rosters jidstatus cache
	private Map availableTransports;

	public Server(Backend backend)
	{
		this.backend = backend;
		backend.addListener(BrowseListener.class,this);
		availableTransports = new HashMap(5);
	}

	public void addJIDStatussen(Map jidStatussen)
	{//adds jidstatussen from roster
		this.jidStatussen = jidStatussen;
		//System.out.println("jidstatussen added");
	}

	public void clear()
	{//reset server
		fireReady = false;
		availableTransports.clear();
		if(jidTree.existGroup(I18N.gettext("main.main.roster.Servers")))
		{
			jidTree.removeGroup(jidTree.getGroup(I18N.gettext("main.main.roster.Servers")));
		}
	}

	public void fire()
	{//fire after complete server reset
		for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
		{
			((RosterListener)j.next()).rosterReplaced(jidTree);
		}
		fireReady = true;
	}


	public void addServerNoFire(JIDStatus jidStatus)
	{
		JIDStatusGroup jidGroup = jidTree.getGroup(I18N.gettext("main.main.roster.Servers"));
		PrimaryJIDStatus primary = new PrimaryJIDStatus(jidStatus.getNick(),jidStatus);
		jidGroup.addPrimaryJIDStatus(primary);
		//primary.addJIDStatus(jidStatus);
		//backend.browse(JID.jidFromString(jidStatus.getJID().toStringNoResource()) ,this);
		backend.getInfo(JID.jidFromString(jidStatus.getJID().toStringNoResource()) ,this);
	}

	public void addServer(String nick, JIDStatus jidStatus)
	{
		addServerNoBrowse(nick,jidStatus);
		//backend.browse(JID.jidFromString(jidStatus.getJID().toStringNoResource()),this);
		backend.getInfo(JID.jidFromString(jidStatus.getJID().toStringNoResource()),this);
	}

	private void addServerNoBrowse(String nick, JIDStatus jidStatus)
	{//when nick changes after browse
		String group = I18N.gettext("main.main.roster.Servers");
		//System.out.println("add");
		JIDStatusGroup jidGroup;
		if(!jidTree.existGroup(group))
		{
			jidGroup = jidTree.getGroup(group);
			int index = jidTree.indexOfGroup(jidGroup);
			for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
			{//fire group make
				((RosterListener)j.next()).groupAdded(jidGroup,index);
			}
		}
		else jidGroup = jidTree.getGroup(group);
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(nick);
		//if(primary == null)
		{//server no primarys
			primary = new PrimaryJIDStatus(nick,jidStatus);
			jidGroup.addPrimaryJIDStatus(primary);
			//primary.addJIDStatus(jidStatus);
			//jidGroup.sort();
			int index = jidGroup.indexOfPrimaryJIDStatus(primary);
			for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
			{//fire add
				((RosterListener)j.next()).primaryAdded(jidGroup,primary,index);
			}
		}
		/*
		else if(primary.addJIDStatus(jidStatus))
		{ //fire update
			int index = jidGroup.indexOfJIDPrimaryStatus(primary);
			for(Iterator j = backend.getListeners(RosterModelListener.class);j.hasNext();)
			{
				((RosterModelListener)j.next()).primaryUpdated(jidGroup,primary,index);
			}
		}
		*/
	}

	public void removeServer(String nick,JIDStatus jidStatus)
	{//als primary gebruik veranderen als in roster
		//System.out.println("remove");
		JIDStatusGroup jidGroup = jidTree.getGroup(I18N.gettext("main.main.roster.Servers"));
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(nick);
		if(primary.removeJIDStatus(jidStatus)) //primary onzin kan weg
		{
			//if(primary.isEmpty())
			{
				int index = jidGroup.indexOfPrimaryJIDStatus(primary);
				jidGroup.removePrimaryJIDStatus(primary);
				for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
				{//fire delete
					((RosterListener)j.next()).primaryDeleted(jidGroup,primary,index);
				}
				//fire remove
				if(jidGroup.size() < 1)
				{//fire group remove
					int index2 = jidTree.indexOfGroup(jidGroup);
					jidTree.removeGroup(jidGroup);
					for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
					{//fire group delete
						((RosterListener)j.next()).groupDeleted(jidGroup,index2);
					}
				}
			}
//			else
//			{//fire update
//				int index = jidGroup.indexOfPrimaryJIDStatus(primary);
//				for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
//				{
//					((RosterListener)j.next()).primaryUpdated(jidGroup,primary,index);
//				}
//			}
		}
	}

	private void changeNickServer(String nick, NormalJIDStatus jidStatus)
	{//changes nick from server  nick + oldjidstatus
		String group = I18N.gettext("main.main.roster.Servers");
		//System.out.println("update");
		JIDStatusGroup jidGroup = jidTree.getGroup(group);
		PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(jidStatus.getNick());
		if(primary.removeJIDStatus(jidStatus)) //primary onzin kan weg
		{
			//if(primary.isEmpty())
			{
				//int index = jidGroup.indexOfPrimaryJIDStatus(primary);
				jidGroup.removePrimaryJIDStatus(primary);
			}
		}
		jidStatus.setNick(nick);
		if(!jidTree.existGroup(group))
		{
			jidGroup = jidTree.getGroup(group);
			//int index = jidTree.indexOfGroup(jidGroup);
		}
		else jidGroup = jidTree.getGroup(group);
		//JIDPrimaryStatus primary = jidGroup.searchPrimaryJIDStatus(nick);
		//if(primary == null)
		{//server no primarys
			primary = new PrimaryJIDStatus(nick,jidStatus);
			jidGroup.addPrimaryJIDStatus(primary);
			//primary.addJIDStatus(jidStatus);
			//jidGroup.sort();
			//int index = jidGroup.indexOfJIDPrimaryStatus(primary);
		}
	}

	//------------------------------Prescence event------------------------------------\\

	public void showChange(JIDStatus jidstatus)
	{//als primary gebruik veranderen als in roster
		//System.out.println("server show change");
		//for(Iterator i = jidstatus.getGroups();i.hasNext();)
		{//new jidstatus per group?
			JIDStatusGroup jidGroup = jidTree.getGroup(I18N.gettext("main.main.roster.Servers"));
			PrimaryJIDStatus primary = jidGroup.searchPrimaryJIDStatus(jidstatus.getNick());
			if(primary!=null)
			{//no status change after remove
				int index = jidGroup.indexOfPrimaryJIDStatus(primary);
				for(Iterator j = backend.getListeners(ServerListener.class);j.hasNext();)
				{
					((RosterListener)j.next()).primaryUpdated(jidGroup,primary,index);
				}
			}
		}
	}

	public void discoveryItemResult(JID jid, DiscoveryItem browseItem){}
	
	public void discoveryInfoResult(JID jid, DiscoveryInfo browseItem)
	{
		//System.out.println("browse");
		NormalJIDStatus jidStatus = (NormalJIDStatus) backend.getJIDStatus(jid);
		if(jidStatus == null)
		{
			System.out.println(jid + " is unknown jidstatus");
			return;
		}
		try
		{//if transport has no nick/ jid for nick set nick to name of browseitem
			if(browseItem.getName() != null)
			{
				JID nick = JID.checkedJIDFromString(jidStatus.getNick());
				if(nick == null || nick.equals(jid))
				{
					if(fireReady)
					{
						removeServer(jidStatus.getNick(),jidStatus);
						jidStatus.setNick(browseItem.getName());
						addServerNoBrowse(browseItem.getName(), jidStatus);
					}
					else changeNickServer(browseItem.getName(), jidStatus);
				}
			}
		}
		catch (Exception e){}
		String type = browseItem.getType();
		if(type == null) type = "unknown";
		availableTransports.put(type, jidStatus);
		if(jidStatus.getType().equals("jabber") && !type.equals("jabber"))
		{//add type to jidstatusen in roster
			updateTypes(jidStatus, type);
		}
		//jidStatus.setNamespaces(browseItem.getNamespaces());
	}

	
	
	/*
	public void browseResult(IQBrowse browseItem)
	{
		//System.out.println("browse");
		NormalJIDStatus jidStatus = (NormalJIDStatus) Backend.getJIDStatus(browseItem.getJID());
		if(jidStatus == null)
		{
			System.out.println(browseItem.getJID() + " is unknown jidstatus");
			return;
		}
		try
		{//if transport has no nick/ jid for nick set nick to name of browseitem
			if(browseItem.getName() != null)
			{
				JID nick = JID.checkedJIDFromString(jidStatus.getNick());
				if(nick == null || nick.equals(browseItem.getJID()))
				{
					if(fireReady)
					{
						removeServer(jidStatus.getNick(),jidStatus);
						jidStatus.setNick(browseItem.getName());
						addServerNoBrowse(browseItem.getName(), jidStatus);
					}
					else changeNickServer(browseItem.getName(), jidStatus);
				}
			}
		}
		catch (Exception e){}
		String type = browseItem.getType();
		availableTransports.put(type, jidStatus);
		if(type == null) type = "unknown";
		if(jidStatus.getType().equals("jabber") && !type.equals("jabber"))
		{//add type to jidstatusen in roster
			updateTypes(jidStatus, type);
		}
		jidStatus.setNamespaces(browseItem.getNamespaces());
	}
	*/

	private void updateTypes(NormalJIDStatus jidStatus, String type)
	{
	   jidStatus.setType(type);
	   //System.out.println("type changed");
	   String server = jidStatus.getJID().getDomain();
	   //System.out.println(server);
	   for(Iterator i = jidStatussen.values().iterator();i.hasNext();)
	   {
		   NormalJIDStatus ji =(NormalJIDStatus) i.next();
		   //System.out.println(ji);
		   if(ji.getJID().getDomain().equals(server))
		   {//server van j == changed server
			   ji.setType(type);
			   /*
			   for(Iterator i2 = ji.getGroups();i2.hasNext();)
			  {//new jidstatus per group?
				  JIDGroup2 jidGroup = jidTree.getGroup((String)i2.next());
				  JIDPrimaryStatus primary = jidGroup.searchPrimaryJIDStatus(ji.getNick());
				  int index = jidGroup.indexOfJIDPrimaryStatus(primary);
				  for(Iterator j = backend.getListeners(RosterModelListener.class);j.hasNext();)
				  {
					  ((RosterModelListener)j.next()).primaryUpdated(jidGroup,primary,index);
				  }
			}
			   */
		   }
	   }
    }
	
	public Map getAvailableTransports()
	{
		return availableTransports;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
