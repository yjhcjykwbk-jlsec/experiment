package nu.fw.jeti.events;

/**
 * The listener interface for receiving messages.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface MessageListener extends JETIListener
{
	/**
	 * Called when a new message has arrived.
	 * @param message The new message.
	 */
	void message(nu.fw.jeti.jabber.elements.Message message);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
