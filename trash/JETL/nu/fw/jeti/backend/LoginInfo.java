/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 */

package nu.fw.jeti.backend;

/**
 * @author E.S. de Boer
 */

public class LoginInfo
{
	private String server;
    private String host;
	private int port;
	private String username;
	private String resource;
	private String password;
	private boolean ssl=false;
	private boolean useProxy=false;
	private boolean hideStatusWindow=false;
	private String proxyServer;
	private String proxyPort;
	private String proxyUsername;
	private String proxyPassword;  //encrypt?
	private int priority;

	
//    public LoginInfo(String server, String username, String password,
//                     String resource, int port, boolean ssl) {
//        this(server, null, username, password, resource, port, ssl);
//    }

	//applet
    public LoginInfo(String server, String host, String username,
                     String password, String resource,int port,boolean ssl)
    {
	 	this.ssl = ssl;
		this.server = server;
        this.host = host;
		this.username = username;
		this.password = password;
		if(password != null && password.equals("")) this.password =null;
		this.resource = resource;
		if(resource != null && resource.equals("")) this.resource ="Jeti";
		this.port = port;
    }
	
	
//    public LoginInfo(String server, String username,
//                     String password, String resource,int port, boolean ssl,
//                     boolean useProxy, boolean hideStatusWindow,
//                     String proxyServer, String proxyUsername,
//                     String proxyPassword, String proxyPort) {
//        this(server, null, username, password, resource, port, ssl,
//             useProxy, hideStatusWindow, proxyServer, proxyUsername,
//             proxyPassword, proxyPort);
//    }

    public LoginInfo(String server, String host, String username,
                     String password, String resource,int port, boolean ssl,int priority,
                     boolean useProxy, boolean hideStatusWindow,
                     String proxyServer, String proxyUsername,
                     String proxyPassword, String proxyPort)
    {
        this(server,host,username,password,resource,port,ssl);
		this.hideStatusWindow = hideStatusWindow;
		this.priority = priority;
		this.useProxy = useProxy;
		this.proxyServer = proxyServer;
		this.proxyPort = proxyPort;
		this.proxyUsername = proxyUsername ;
		this.proxyPassword = proxyPassword;
    }


	/**
	 * Returns the port.
	 * @return String
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the resource.
	 * @return String
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Returns the server.
	 * @return String
	 */
	public String getServer() {
		return server;
	}

	/**
     * Returns the host to connect to.
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
	 * Returns the ssl.
	 * @return boolean
	 */
	public boolean isSSl() {
		return ssl;
	}
	
	/**
	 * Hide status window?.
	 * @return boolean
	 */
	public boolean hideStatusWindow() {
		return hideStatusWindow;
	}
	
	public int getPriority()
	{
		return priority;
	}

	/**
	 * Returns the username.
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the proxyPassword.
	 * @return String
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * Returns the proxyPort.
	 * @return int
	 */
	public String getProxyPort() {
		return proxyPort;
	}

	/**
	 * Returns the proxyServer.
	 * @return String
	 */
	public String getProxyServer() {
		return proxyServer;
	}

	/**
	 * Returns the proxyUsername.
	 * @return String
	 */
	public String getProxyUsername() {
		return proxyUsername;
	}

	/**
	 * Returns the useProxy.
	 * @return boolean
	 */
	public boolean useProxy() {
		return useProxy;
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
