package nu.fw.jeti.events;

/**
 * The listener interface for receiving status events.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface StatusChangeListener extends JETIListener
{

	/**
	 * Called when the connection status has changed.
	 * @param online true when the connection is online
	 */
	void connectionChanged(boolean online);

	/**
	 * Called when you have changed your own presence status
	 * @param show The new show status, one of Presence FREE_FOR_CHAT, AVAILABLE, AWAY, DND, XA or UNAVAILABLE.
	 * @param status The new status message (can be null)
	 */
	void ownPresenceChanged(int show, String status);

	/**
	 * Called when Jeti will be closed
	 */
	void exit();
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
