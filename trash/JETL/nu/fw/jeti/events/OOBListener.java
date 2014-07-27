package nu.fw.jeti.events;

import nu.fw.jeti.jabber.elements.IQXOOB;

/**
 * doesn't work yet
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public interface OOBListener extends JETIListener
{
	/**
	 * 
	 * 
	 * @param jid
	 * @param id
	 * @param oob
	 */
	void oob(nu.fw.jeti.jabber.JID jid,String id,IQXOOB oob);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
