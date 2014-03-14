package nu.fw.jeti.events;

/**
 * Created on 1-mrt-2003
 * @author E.S. de Boer
 * Signals protocol errors 
 *
 */
public interface ErrorListener extends JETIListener
{
	
	/**
	 * The errorcode and the errorMessage from a error infoquery 
	 * @param errorCode
	 * @param error
	 */
	void error(int errorCode,String error);

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
