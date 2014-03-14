package nu.fw.jeti.events;

/**
 * receive complete roster (to wrtite your own roster instead of suplied one)
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public interface CompleteRosterListener extends JETIListener
{
	void rosterReceived(nu.fw.jeti.jabber.elements.InfoQuery infoQuery, nu.fw.jeti.jabber.elements.IQXRoster roster);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
