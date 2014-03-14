package nu.fw.jeti.backend.roster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nu.fw.jeti.jabber.JIDStatus;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class JIDStatusGroup implements Comparable
{
//	synchronized because used in event thread & jabberinput thread
	private String name;
	private List members;
	private int onlines;

	public JIDStatusGroup(String name)
	{
		this.name = name;
		members = new ArrayList(16);
	}

	public synchronized int size()
	{
		return members.size();
	}
	
	public boolean isOffline()
	{
		for(Iterator i = iterator();i.hasNext();)
		{
			if(((JIDStatus)i.next()).isOnline()) return false;		
		}
		return true;
	}
	
	public synchronized void addOnline() {onlines++;}
	
	public synchronized void removeOnline() {onlines--;}
	
	public synchronized int getOnlines() {return onlines;}
	
	public Iterator iterator()
	{
		return new ArrayList(members).iterator();
	}

	/*
	public void addJIDStatus(JIDStatus2 jid)
	{
		members.add(jid);
		Collections.sort(members);
	}
	*/

	public synchronized void addPrimaryJIDStatus(PrimaryJIDStatus jid)
	{
		members.add(jid);
		Collections.sort(members);
	}
/*
	public void sort()
	{
	    Collections.sort(members);
	}
*/
	public synchronized Object getPrimaryJIDStatus(int index)
	{
		return members.get(index);
	}

	/*
	public  JIDStatus searchJID(JID jid)
	{
		for(int tel =0; tel<members.size();tel++)
		{
			if (((JIDStatus)members.get(tel)).getJID().equals(jid)) return (JIDStatus)members.get(tel);
		}
		return null;
	}
	*/

	public synchronized PrimaryJIDStatus searchPrimaryJIDStatus(String nickname)
	{
		for(int tel =0; tel<members.size();tel++)
		{
			PrimaryJIDStatus temp = (PrimaryJIDStatus)members.get(tel);
			if (temp.getNick().equals(nickname)) return temp;
		}
		return null;
	}

	/*
	public boolean remove(JID jid)
	{
		for(int tel =0; tel<members.size();tel++)
		{
			if (((JIDStatus)members.get(tel)).getJID().equals(jid))
			{
			     members.remove(tel);
				 return true;
			}
		}
		return false;
	}
	*/

	public synchronized void removePrimaryJIDStatus(PrimaryJIDStatus primary)
	{
		members.remove(primary);
	}

	public synchronized int indexOfPrimaryJIDStatus(Object jidPrimaryStatus)
	{
	    return members.indexOf(jidPrimaryStatus);
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public boolean equals(Object object)
	{//als name is gelijk object is gelijk
		if(!(object instanceof JIDStatusGroup)) return false;
		return name.equals(((JIDStatusGroup)object).name);
	}
	
	public int hashCode()
	{
		return name.hashCode();
	}

	public int compareTo(Object o)
	{
		return name.compareTo(((JIDStatusGroup)o).name);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
