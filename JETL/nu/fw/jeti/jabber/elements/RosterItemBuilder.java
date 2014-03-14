package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.StringArray;
import nu.fw.jeti.jabber.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class RosterItemBuilder
{
	private StringArray groups;
	public String name;
	public String subscription;
	public String ask;
	public JID jid;

	public void reset()
	{
		name=null;
		subscription =null;
		ask=null;
		jid =null;
		groups =null;
	}

	public void addGroup(String group)
	{
		if(groups == null) groups = new StringArray();
		groups.add(group);
	}

	public StringArray getGroups()
	{
		return groups;
	}

	public RosterItem build() throws InstantiationException
	{
		if(jid == null) throw new InstantiationException(I18N.gettext("main.error.No_JID_found"));
		if(subscription !=null && !(subscription.equals("none") ||
									subscription.equals("from") ||
									subscription.equals("to") ||
									subscription.equals("both") ||
									subscription.equals("remove")))
									subscription = null;
		if(ask != null && !(ask.equals("subscribe") || ask.equals("unsubscribe"))) ask =null;
		return new RosterItem(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
