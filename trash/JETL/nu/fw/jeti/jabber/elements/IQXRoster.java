package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import nu.fw.jeti.events.CompleteRosterListener;
import nu.fw.jeti.jabber.Backend;



/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQXRoster extends Extension implements IQExtension, XExtension
{//iq + x roster
	private LinkedList items;

	public IQXRoster(){}//extension tag for get

	public IQXRoster(RosterItem item)
	{
		items = new LinkedList();
		items.add(item);
	}

    public IQXRoster(RosterBuilder rb)
    {
		items=rb.getItems();
    }

	public Iterator getItems()
	{
		if (items==null)
		{
			return new Iterator()
			{//empty iterator
				public Object next()
				{
					throw new NoSuchElementException();
				}
				public boolean hasNext()
				{
					return false;
				}
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		return items.iterator();
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		for (Iterator j = backend.getListeners(CompleteRosterListener.class); j.hasNext();)
		{
			((CompleteRosterListener) j.next()).rosterReceived(iq,this);
		}
	}


	public void appendToXML(StringBuffer xml)
    {/** @todo x */
        xml.append("<query xmlns=\"jabber:iq:roster\"");
		if(items ==null)
		{ //short cut
		    xml.append("/>");
			return;
		}
		xml.append('>');
		for(Iterator i = items.iterator();i.hasNext();)
		{
		    ((RosterItem)i.next()).appendToXML(xml);
		}
		xml.append("</query>");
    }
}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
