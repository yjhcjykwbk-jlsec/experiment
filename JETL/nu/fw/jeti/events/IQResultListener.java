package nu.fw.jeti.events;

import nu.fw.jeti.jabber.elements.InfoQuery;

/**
 * Created on 18-okt-2004
 * @author E.S. de Boer
 * returns IQ results 
 *
 */
public interface IQResultListener extends JETIListener
{
	
	/**
	 * The errorcode and the errorMessage from a error infoquery 
	 * @param iq the infoquery result
	 */
	void iqResult(InfoQuery iq);

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
