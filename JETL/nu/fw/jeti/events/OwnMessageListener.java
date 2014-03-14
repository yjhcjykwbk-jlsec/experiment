package nu.fw.jeti.events;

/**
 * The listener interface for listening to your own send messages.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface OwnMessageListener extends JETIListener
{
	/**
	 * Called when you have sent a message
	 * @param message The sent message.
	 */
	void sendMessage(nu.fw.jeti.jabber.elements.Message  message);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
