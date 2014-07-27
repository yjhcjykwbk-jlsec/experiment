package nu.fw.jeti.events;

/**
 * @author E.S. de Boer
 *
 *	Signals that a error has happend
 */
public interface JavaErrorListener extends JETIListener
{

	
	/**
	 * signals occurence of an error so it can be shown in the ui
	 */
	void error();

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
