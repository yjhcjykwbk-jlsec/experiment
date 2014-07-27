// Created on 7-jul-2003
package nu.fw.jeti.events;

/**
 * Listener for information about the login
 * @author E.S. de Boer
 *
 */
public interface LoginListener extends JETIListener
{

	/**
	 *	Messages send when logging in
	 *  @param message
	 */
	void loginMessage(String message);
		
	/**
	 * Counter for where the login proces is
	 * @param count the counter values are:
	 * 0 = not logged in
	 * 1 = socket connection
	 * 2 = in & outputstream connections
	 * 3 = connected
	 * 4 = authenticated
	 * 5 = logged in 
	 */
	void loginStatus(int count);

	/**
	 * Error messages when logging in 
	 * @param errorMessage
	 */
	void loginError(String errorMessage);
	
	/**
	 * Unauthorized to this server
	 */
	void unauthorized();
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
