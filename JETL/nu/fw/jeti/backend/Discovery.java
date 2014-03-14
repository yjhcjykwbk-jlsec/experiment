/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 */

package nu.fw.jeti.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nu.fw.jeti.events.DiscoveryListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;

/**
 * @author E.S. de Boer
 */
//created on 6-8-2004
public class Discovery implements DiscoveryListener, StatusChangeListener
{
	private Backend backend;
	private Map infoCache = new HashMap();
	private Map itemCache = new HashMap();
	private Map itemRequests = new HashMap(5);
	private Map infoRequests = new HashMap(10);
	private Map timeoutTimers = new HashMap(20);
	private Timer timer = new Timer(true);
	
	private int idCount;

    public Discovery(Backend backend)
    {
		this.backend = backend;
    }
	
//	//muc hack
//	public void browseNotCached(JID jid,BrowseListener listener)
//	{
//		//if (listener == null) listener = this;
//		String id = "J²M_Browse_" + idCount++;
//		backend.send(new InfoQuery(jid,"get",id,new IQBrowse()));
//		//requests.put(id,listener);
//	}
	
	public void getItems(JID jid,DiscoveryListener listener) {
        getItems(jid, listener, true);
    }

	public void getItems(JID jid,DiscoveryListener listener, boolean useCache)
    {
		if (listener == null) listener = this;
		DiscoveryItem item = null;
        if (!useCache || null == (item = (DiscoveryItem)itemCache.get(jid)))
		{//first try disco
			String id = "Jeti_Disco_" + idCount++;
			InfoQuery iq = new InfoQuery(jid,"get",id,new IQDiscoItems());
			backend.send(iq);
			timeout(iq);
			itemRequests.put(id,listener);
		} else {
            listener.discoveryItemResult(jid,item);
        }
	}
	
	public void getInfo(JID jid,DiscoveryListener listener)
	{
		if (listener == null) listener = this;
		DiscoveryInfo info = (DiscoveryInfo)infoCache.get(jid);
		if(info == null)
		{//first try disco
			String id = "Jeti_Disco_" + idCount++;
			InfoQuery iq =new InfoQuery(jid,"get",id,new IQDiscoInfo());
			backend.send(iq);
			timeout(iq);
			infoRequests.put(id,listener);
		}
		else listener.discoveryInfoResult(jid,info);
	}
	
	//no cache TODO add no browse if node query error
	public void getItems(JID jid,String node,DiscoveryListener listener)
	{
		if (listener == null) return;//no cache so empty listener is useless
		//DiscoveryItem item = (DiscoveryItem)itemCache.get(jid);
		//if(item == null)
		{
			String id = "Jeti_Disco_" + idCount++;
			InfoQuery iq = new InfoQuery(jid,"get",id,new IQDiscoItems(node));
			backend.send(iq);
			timeout(iq);
			itemRequests.put(id,listener);
		}
		//else listener.discoveryItemResult(jid,item);
	}
	
	public void getInfo(JID jid,String node,DiscoveryListener listener)
	{
		if (listener == null) listener = this;
		DiscoveryInfo info = (DiscoveryInfo)infoCache.get(jid);
		if(info == null)
		{//first try disco
			String id = "Jeti_Disco_" + idCount++;
			InfoQuery iq =new InfoQuery(jid,"get",id,new IQDiscoInfo(node));
			backend.send(iq);
			timeout(iq);
			infoRequests.put(id,listener);
		}
		else listener.discoveryInfoResult(jid,info);
	}
	
	public void discoveryInfoResult(JID jid,String id,IQDiscoInfo info)
	{
		TimerTask t =(TimerTask) timeoutTimers.remove(id);
		if(t!=null)t.cancel();
		infoCache.put(jid,info);
		DiscoveryListener d = (DiscoveryListener)infoRequests.remove(id);
		if(d!=null) d.discoveryInfoResult(jid,info);
	}
	
	public void discoveryItemResult(JID jid,String id,IQDiscoItems item)
	{
		TimerTask t =(TimerTask) timeoutTimers.remove(id);
		if(t!=null)t.cancel();
		itemCache.put(jid,item);
		DiscoveryListener d =(DiscoveryListener)itemRequests.remove(id);
		if(d!=null)d.discoveryItemResult(jid,item);
	}
	

	public void browseResult(JID jid,String id,IQBrowse item)
	{
		TimerTask t =(TimerTask) timeoutTimers.remove(id);
		if(t!=null)t.cancel();
		if(itemRequests.containsKey(id))
		{
			itemCache.put(item.getJID(),item);
//			if(item.hasItems())
//			{//wrong server xml hack
//				for(Iterator i=item.getItems();i.hasNext();)
//				{
//					IQBrowse tempItem = (IQBrowse) i.next();
//					//jid are equal if they are the same without there resources
//					//if there is a same jid in the chace, do not replace it
//					if(!itemCache.containsKey(tempItem.getJID())) itemCache.put(tempItem.getJID(),tempItem);
//				}
//			}
			DiscoveryListener d =(DiscoveryListener)itemRequests.remove(id);
			if(d!=null)d.discoveryItemResult(jid,item);
		}
		else if(infoRequests.containsKey(id))
		{
			infoCache.put(item.getJID(),item);
//			if(item.hasItems())
//			{//wrong server xml hack
//				for(Iterator i=item.getItems();i.hasNext();)
//				{
//					IQBrowse tempItem = (IQBrowse) i.next();
//					//jid are equal if they are the same without there resources
//					//if there is a same jid in the chace, do not replace it
//					if(!infoCache.containsKey(tempItem.getJID())) infoCache.put(tempItem.getJID(),tempItem);
//				}
//			}
			DiscoveryListener d = (DiscoveryListener)infoRequests.remove(id);
			if(d!=null)d.discoveryInfoResult(jid,item);
		}
	}

	public void discoError(String id,JID jid)
	{//no disco so try browse
		TimerTask t =(TimerTask) timeoutTimers.remove(id);
		if(t!=null)t.cancel();
		String newID = "Jeti_Browse_" + idCount++;
		InfoQuery iq =new InfoQuery(jid,"get",newID,new IQBrowse());
		backend.send(iq);
		timeout(iq);
		Object o = infoRequests.remove(id);
		if(o!=null)infoRequests.put(newID,o);
		else
		{
			o=itemRequests.remove(id);
			if(o!=null)itemRequests.put(newID,o);
		}
	}
	
	public void browseError(String id,JID jid)
	{
		TimerTask t =(TimerTask) timeoutTimers.remove(id);
		if(t!=null)t.cancel();
		Object o = infoRequests.remove(id);
		if(o!=null)
		{
			DiscoveryInfo info = new IQBrowse(jid);
			if (infoCache.get(jid) == null)	infoCache.put(jid,info);//if nothing in cache cache = empty browse (maby server browse did succeed)
			((DiscoveryListener)o).discoveryInfoResult(jid,info);
		}
		else
		{
			o = itemRequests.remove(id);
			if(o!=null)
			{
				DiscoveryItem item = new IQBrowse(jid);
				if (itemCache.get(jid) == null)	itemCache.put(jid,item);//if nothing in cache cache = empty browse (maby server browse did succeed)
				((DiscoveryListener)o).discoveryItemResult(jid,item);
			}
		}
	}
	
	public void discoveryInfoResult(JID jid,DiscoveryInfo info){}
	
	public void discoveryItemResult(JID jid,DiscoveryItem item){}
	
	//times out a disco or browse request
	private void timeout(final InfoQuery query)
	{
		TimerTask t = new TimerTask()
		{
			public void run()
			{
				System.out.println("timeout " + query);
				if(query.getIQExtension() instanceof IQBrowse)
				{//browse
					browseError(query.getID(),query.getTo());
				}
				else
				{//disco
					discoError(query.getID(),query.getTo());
				}
			}
		};
		timer.schedule(t,20000);
		timeoutTimers.put(query.getID(),t);
	}

	//-------------------status events-------------\
	public void connectionChanged(boolean online)
	{//clear cache when offline
		if (!online)
		{
			infoRequests = new HashMap(10);
			itemRequests = new HashMap(5);
			infoCache = new HashMap();
			itemCache = new HashMap();
		}
	}

	public void ownPresenceChanged(int show, String status){}

	public void exit(){}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
