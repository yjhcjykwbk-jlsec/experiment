package nu.fw.jeti.jabber.elements;
import java.util.LinkedList;


/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class RosterBuilder implements ExtensionBuilder
{
	private LinkedList items;

    public RosterBuilder()
    {
		reset();
    }

	public void reset()
	{
		items = null;
	}

	public void addItem(RosterItem item)
	{
	    if(items ==null) items = new LinkedList();
		items.add(item);
	}

	public LinkedList getItems()
	{
	    return items;
	}

	public Extension build()
	{
	    return new IQXRoster(this);
	}


}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
