package nu.fw.jeti.backend;

import nu.fw.jeti.events.BrowseListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.IQBrowse;
import nu.fw.jeti.jabber.elements.InfoQuery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author E.S. de Boer
 * @deprecated use Discovery, will be removed
 */
public class Browse implements BrowseListener, StatusChangeListener
{//verbeter door naar server te kijken?? + request naar server ipv naar transports how??
	private Backend backend;
	private Map cache = new HashMap();
	private Map requests = new HashMap();
	private int idCount;

    public Browse(Backend backend)
    {
		this.backend = backend;
    }

	public void browse(JID jid,BrowseListener listener)
	{
		if (listener == null) listener = this;
		IQBrowse item = (IQBrowse)cache.get(jid);
		if(item == null)
		{
			String id = "Jeti_Browse_" + idCount++;
			backend.send(new InfoQuery(jid,"get",id,new IQBrowse()));
			requests.put(id,listener);
		}
		else listener.browseResult(item);
	}
	
	//muc hack
	public void browseNotCached(JID jid,BrowseListener listener)
	{
		if (listener == null) listener = this;
		String id = "Jeti_Browse_" + idCount++;
		backend.send(new InfoQuery(jid,"get",id,new IQBrowse()));
		requests.put(id,listener);
	}
	

	public void browseResult(String id,IQBrowse item)
	{
		if(requests.containsKey(id))
		{
			cache.put(item.getJID(),item);
//			if(item.hasChildItems())
//			{//wrong server xml hack
//				for(Iterator i=item.getItems();i.hasNext();)
//				{
//					IQBrowse tempItem = (IQBrowse) i.next();
//					//jid are equal if they are the same without there resources
//					//if there is a same jid in the chace, do not replace it
//					if(!cache.containsKey(tempItem.getJID())) cache.put(tempItem.getJID(),tempItem);
//				}
//			}
			((BrowseListener)requests.remove(id)).browseResult(item);
		}
	}

	public void error(String id,JID jid)
	{
		if (cache.get(jid) == null)	cache.put(jid,new IQBrowse(jid));//if nothing in cache cache = empty browse (maby server browse did succeed)
		((BrowseListener)requests.remove(id)).browseResult((IQBrowse)cache.get(jid));
	}

	public void browseResult(IQBrowse browse){}

	//-------------------status events-------------\
	public void connectionChanged(boolean online)
	{//clear cache when offline
		if (!online)
		{
			requests = new HashMap();
			cache = new HashMap();
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
