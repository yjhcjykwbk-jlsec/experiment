package nu.fw.jeti.events;

/**
 * The listener interface for listening to register queries.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface RegisterListener extends JETIListener
{
	/**
	 * Called when a register iq has been received
	 * @param register The register element
	 */
	void register(nu.fw.jeti.jabber.elements.IQRegister register,String id);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
