package nu.fw.jeti.events;

/**
 * The listener interface for listening to presence packets.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface PresenceListener extends JETIListener
{
	/**
	 * Called when someones presence has been changed.
	 * The subscription part of presence is handeld by Roster.
	 * @param presence The changed presence.
	 */
	void presenceChanged(nu.fw.jeti.jabber.elements.Presence presence);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
