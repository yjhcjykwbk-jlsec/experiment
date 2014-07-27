package nu.fw.jeti.events;

/**
 * The listener interface for receiving browse results.
 * The results are cached for later use.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface BrowseListener extends JETIListener
{
	/**
	 * The result of a Backend.browse(JID jid).
	 * @param browseItem the result of the browse if there was a error
	 * browse will only contain the requested JID.
	 */
	void browseResult(nu.fw.jeti.jabber.elements.IQBrowse browseItem);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
