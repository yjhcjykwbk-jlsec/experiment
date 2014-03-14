package nu.fw.jeti.backend.roster;

import java.util.*;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.util.StringArray;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class PrimaryJIDStatus implements JIDStatus, Comparable
{//contains secondary jidstatussen (resource + same nick)
	private JIDStatus primaryJIDStatus;
	private List jidStatussen;
	private String nick;
	private boolean online=false;


    public PrimaryJIDStatus(String nick,JIDStatus primaryJIDStatus)
    {
		this.nick = nick;
		this.primaryJIDStatus = primaryJIDStatus;
    }

//	public boolean isEmpty()
//	{
//		return isEmpty;
//	}

	public boolean hasMultiple()
	{
		return jidStatussen != null;
	}

	public boolean isAJIDstatusOffline()
	{
		if(!primaryJIDStatus.isOnline()) return true;
		if(jidStatussen == null) return false;
		for(Iterator i = jidStatussen.iterator();i.hasNext();)
		{
			if(!((JIDStatus)i.next()).isOnline()) return true;
		}
		return false;
	}
	
	public boolean multipleJIDstatusOnline()
	{
		if(!primaryJIDStatus.isOnline()) return false;
		if(jidStatussen == null) return false;
		for(Iterator i = jidStatussen.iterator();i.hasNext();)
		{
			if(((JIDStatus)i.next()).isOnline()) return true;
		}
		return false;
	}

	public void addJIDStatus(JIDStatus jidStatus)
	{
//		if(primaryJIDStatus == null)
//		{
//			primaryJIDStatus = jidStatus;
//			//return true;
//		}
//		else
//		{
			if(jidStatussen == null) jidStatussen = new ArrayList(8);
			jidStatussen.add(jidStatus);
		    Collections.sort(jidStatussen);
			//return false;
		//}
	}

	public boolean removeJIDStatus(JIDStatus jidStatus)
	{//returns true if primaryJIDStatus is now empty
	    if(primaryJIDStatus == jidStatus)
		{
			if(jidStatussen == null)
			{
				//primaryJIDStatus = null;
				return true;
			}
			else if(jidStatussen.size() == 1)
			{
			    primaryJIDStatus = (JIDStatus)jidStatussen.get(0);
				jidStatussen =null;
			}
			else
			{//jidstatussen sorted so first will be new primary
				primaryJIDStatus = (JIDStatus)jidStatussen.remove(0);
			}
			return false;
		}
		jidStatussen.remove(jidStatus);
		if (jidStatussen.size() == 0) jidStatussen = null;
		return false;
	}

	public void updatePresence(SecondaryJIDStatus jidStatus)
	{
		if(jidStatussen != null) Collections.sort(jidStatussen);
		if(jidStatus == primaryJIDStatus)
		{//check of secondery jids better then primary
			if(jidStatussen == null) return;

			//jidstatussen sorted so only first checked
			checkVolgorde((JIDStatus) getJIDStatus(0));
			/*
			for(Iterator i = jidStatussen.iterator();i.hasNext();)
			{
				JIDStatus2 temp = (JIDStatus2)i.next();
				checkVolgorde(jidGroup, temp, backend);
			}
			*/
		}
	  	else checkVolgorde(jidStatus);
	}
	
	public void updateOnline()
	{
		online = primaryJIDStatus.isOnline();
	}

    private void checkVolgorde(JIDStatus jidStatus)
    {
        if(primaryJIDStatus.compareTo(jidStatus) > 0)
        {
            int index = jidStatussen.indexOf(jidStatus);
            jidStatussen.set(index,primaryJIDStatus);
		    primaryJIDStatus = jidStatus;
			Collections.sort(jidStatussen);//sort new list
        }
    }

	public JIDStatus getJIDPrimaryStatus()
	{
//		if(primaryJIDStatus == null)
//		{//jidstatus is removed from group before from gui so use a dummy until removed from gui
//			//System.out.println("Primary jids null");
//		 	return new NormalJIDStatus(new JID("test","test") ,nick); 
//		}
		return primaryJIDStatus;
	}

	public Object getJIDStatus(int index)
	{
		return jidStatussen.get(index);
	}

	public int indexOfJIDStatus(Object jidStatus)
	{
		return jidStatussen.indexOf(jidStatus);
	}

	public int size()
	{
		if(jidStatussen == null) return 0;
		return jidStatussen.size();
	}

	public Iterator getOtherJidStatussen()
	{
		return new ArrayList(jidStatussen).iterator();
	}

	public String toString()
	{
	    //return nick;//
	    return primaryJIDStatus.toString();
	}

	public int compareTo(Object o)
	{//compare jid
		//if(nickname==null) return -1;
	    return nick.compareTo(((PrimaryJIDStatus)o).nick);
	}

	//-------------------JIStatus implementation--------------------\\

	public JID getJID(){return primaryJIDStatus.getJID();}

	public JID getCompleteJID(){return primaryJIDStatus.getCompleteJID();}

	public boolean isOnline()
	{
		return online;
	}

	public int getShow()
	{
		return primaryJIDStatus.getShow();
	}

	public String getStatus()
	{
		return primaryJIDStatus.getStatus();
	}

	public String getNick()
	{
		return nick;
	}

	public String getType()
	{
		return primaryJIDStatus.getType();
	}

	public String getSubscription(){return primaryJIDStatus.getSubscription();}

	public String getWaiting()
	{
		return primaryJIDStatus.getWaiting();
	}

	public StringArray getGroupsCopy(){return primaryJIDStatus.getGroupsCopy();}

	public int groupCount(){return primaryJIDStatus.groupCount();}

	public boolean isGroupPresent(String group){return primaryJIDStatus.isGroupPresent(group);}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
