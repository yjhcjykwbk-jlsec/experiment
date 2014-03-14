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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import nu.fw.jeti.events.DiscoveryListener;
import nu.fw.jeti.events.LoginListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.plugins.OpenPGP;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.ui.LoginStatusWindow;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Log;

/**
 * @author E.S. de Boer
 */

//TODO stop packet start spul en de andere dingen in aparte class??
//TODO improve error reporting, make it translatable
 //class voor connectie
 public class Connect implements ConnectionPacketReceiver
{
	private Output output;
	private String authenticationId="yytr";
	private JabberHandler jabberHandler;
	private LoginInfo loginInfo;
	private Backend backend;
	private Handlers handlers;
	private static JID myJID = new JID("test","test","test");
	private boolean authenticated = false;
	private boolean reconnecting = false;
	private int show;
	private String status;
	private String connectionID;
	private Discovery browse;
	private Socket socket;//socket needed to close if abort
	private Thread connectThread;//login thread
	private volatile boolean abort = false;//abort login
	private OpenPGP openPGP;
	private IQTimerQueue iqTimerQueue;
    private LoginStatusWindow loginStatusWindow;
    private boolean gotStreamError = false;

	public Connect(Backend backend,IQTimerQueue timerQueue)
	{
		this.backend = backend;
		iqTimerQueue = timerQueue;
	    handlers = new Handlers();
		browse = new Discovery(backend) ;
	}

	public Handlers getHandlers()
	{
		return handlers;
	}

	public void setJabberHandler(JabberHandler jH)
	{
		jabberHandler = jH;
	}

//	public void browse(JID jid, nu.fw.jeti.events.BrowseListener listener)
//	{
//		browse.browse(jid,listener);
//	}
	
	public void getItems(JID jid, DiscoveryListener listener, boolean useCache)
	{
		browse.getItems(jid,listener,useCache);
	}
	
	public void getItems(JID jid, DiscoveryListener listener)
	{
		browse.getItems(jid,listener);
	}
	
	public void getInfo(JID jid, DiscoveryListener listener)
	{
		browse.getInfo(jid,listener);
	}
	
	public void getItems(JID jid,String node, DiscoveryListener listener)
	{
		browse.getItems(jid,node,listener);
	}
	
	public void getInfo(JID jid,String node, DiscoveryListener listener)
	{
		browse.getInfo(jid,node,listener);
	}
	
	public boolean isLoggedIn()
	{
		return authenticated;
	}

//	//muc hack
//	public void browseNotCached(JID jid, nu.fw.jeti.events.BrowseListener listener)
//	{
//		browse.browseNotCached(jid,listener);
//	}
	
	private void reconnect()
	{
        if (loginStatusWindow != null) {
            loginStatusWindow.abort();
        }
		loginStatusWindow = new LoginStatusWindow(loginInfo ,backend,2);	
	}

    public void login(LoginInfo info)
    {
    	if(PluginsInfo.isPluginLoaded("openpgp"))
    	{
    		openPGP =  openPGP =  (OpenPGP)PluginsInfo.getPluginInstance("openpgp");
    	}
    	abort = false;
		loginInfo = info;
		connectThread = new Thread()
		{
			public void run()
			{
				connect();
				//if(isInterrupted()) disconnect();  
			}
		};
		connectThread.start(); 
    }
    
	public void autoLogin(LoginInfo info,final int tries)
	{
		abort = false;
		loginInfo = info;
		connectThread = new Thread()
		{
			int tel = 0;
			public void run()
			{
				boolean connected=false;
				while(tel < tries)
				{
					connected = connect();
					if(connected) break;
					//synchronized (this)
					{
						try
						{
							//this.wait(60000);
							Thread.sleep(60000);
						}
						catch (InterruptedException e){}
					}
					tel++;
				}
				if(!connected) sendLoginMessage("login failed, tried " + tries + " times, stopping");
			}
		};
		connectThread.start(); 
	}
    
    public void abort()
    {
		abort = true;
		if(socket != null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
    	connectThread.interrupt();
		disconnect(); 
    }

	synchronized private boolean connect()
	{		
		if(loginInfo == null) return true;
		if(authenticated) disconnect();//clear old connection
		//System.getProperties().setProperty("socksProxySet", "true");
		if (loginInfo.useProxy())
		{
			System.getProperties().setProperty("socksProxyHost", loginInfo.getProxyServer());
			System.getProperties().setProperty("socksProxyPort", loginInfo.getProxyPort());
		  	System.getProperties().setProperty("socksProxyUserName", loginInfo.getProxyUsername());
		  	if(loginInfo.getProxyPassword()!=null)System.getProperties().setProperty("socksProxyPassword", loginInfo.getProxyPassword());
		}
        sendLoginStatus(0);
		sendLoginMessage(I18N.gettext("main.loginstatus.Opening_socket")+"...");
        try{   		
            String host = loginInfo.getHost();
            if (host == null || host.length() == 0) {
                host = loginInfo.getServer();
            }
              
           /* SRV handling code, this is unreliable if not on *nix
            String[] servers = null;
            servers = nu.fw.jeti.util.DNS.findSRVRecords(host);
            if (servers != null) {             	
            	host = servers[0];
            }
           */
            
           	if(loginInfo.isSSl()) {
           		socket = new DummySSLSocketFactory().createSocket(host,loginInfo.getPort());
           	} else {
           		socket = new Socket(host,loginInfo.getPort());
           	}
        }
		catch (UnknownHostException ex)
        {
            sendLoginError(MessageFormat.format(I18N.gettext("main.loginstatus.Server_{0}_could_not_be_found"), new Object[] { loginInfo.getHost() } ));
			return false;
        }
		catch (IOException ex)
		{
			sendLoginError(ex.getMessage());
			return false;
        }
		if(abort) return false;
        sendLoginStatus(1);
       	sendLoginMessage(I18N.gettext("main.loginstatus.Opening_Input")+"...");
		try{
            new Input(socket.getInputStream(),this);
        }catch (IOException ex)
        {
			sendLoginError(I18N.gettext("main.loginstatus.Could_not_open_input_because") + " " + ex.getMessage());
			return false;
        }
		if(abort) return false;
		sendLoginMessage(I18N.gettext("main.loginstatus.Opening_Output")+"...");
	    try{
			output = new Output(socket,loginInfo.getServer(),this);
        }catch (IOException ex)
        {
			sendLoginError(I18N.gettext("main.loginstatus.Could_not_open_output_because") + " " +ex.getMessage());
			return false;
        }
		sendLoginStatus(2);
		sendLoginMessage(I18N.gettext("main.loginstatus.Connected")+"...");
		socket = null; //clear socket reference
		return true;
	}
	
	private void sendLoginMessage(String message)
	{
		for(Iterator j = backend.getListeners(LoginListener.class);j.hasNext();)
		{//online
			((LoginListener)j.next()).loginMessage(message);
		}
	}
	
	private void sendLoginStatus(int count)
	{
		for(Iterator j = backend.getListeners(LoginListener.class);j.hasNext();)
		{//online
			((LoginListener)j.next()).loginStatus(count);
		}
	}
	
	private void sendLoginError(String message)
	{
		for(Iterator j = backend.getListeners(LoginListener.class);j.hasNext();)
		{//online
			((LoginListener)j.next()).loginError(message);
		}
	}
	
	private void sendUnauthorized()
	{
		for(Iterator j = backend.getListeners(LoginListener.class);j.hasNext();)
		{//online
			((LoginListener)j.next()).unauthorized();
		}
	}

/*
	public void reconnect()
	{
		connect(server,5222);
	}
*/

	synchronized public void connected(String connectionID)
	{
		if(!abort)
		{
			sendLoginStatus(3);
			this.connectionID = connectionID;
//			TODO remove lowercase if filetransfer bug fixed
			output.send(new InfoQuery(null,"get",new IQAuth(loginInfo.getUsername().toLowerCase(),null,null)));
			sendLoginMessage(I18N.gettext("main.loginstatus.Getting_available_login_methods")+"...");
		}
		
	}

	public void authenticate(IQAuth iqAuth)
	{
		if(!abort)
		{
			sendLoginStatus(4);
			sendLoginMessage(I18N.gettext("main.loginstatus.Authenticating")+"...");
			authenticationId = "Jeti_Auth_" + new java.util.Date().getTime();
			if(iqAuth.hasDigest())
			{
				MessageDigest sha = null;
				try {
					  sha = MessageDigest.getInstance("SHA");
					} catch (Exception ex){
					  Log.error(I18N.gettext("main.loginstatus.Could_not_login_with_SHA"));
					  //TODO remove lowercase if filetransfer bug fixed
					  output.send(new InfoQuery(null,"set",authenticationId,new IQAuth(loginInfo.getUsername().toLowerCase(),loginInfo.getPassword() ,loginInfo.getResource())));
					  return;
					}
	
					sha.update(connectionID.getBytes());
					String digest = toString(sha.digest(loginInfo.getPassword().getBytes()));
					IQAuthBuilder iqab =  new IQAuthBuilder();
					iqab.digest = digest;
//					TODO remove lowercase if filetransfer bug fixed
					iqab.username = loginInfo.getUsername().toLowerCase();
					iqab.resource = loginInfo.getResource();
					output.send(new InfoQuery(null,"set",authenticationId,(IQExtension)iqab.build()));
			}
			else
			{
//				TODO remove lowercase if filetransfer bug fixed
				output.send(new InfoQuery(null,"set",authenticationId,new IQAuth(loginInfo.getUsername().toLowerCase(),loginInfo.getPassword() ,loginInfo.getResource())));
			}
		}
	}

	private String toString(byte[] bytes)
	{
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for(int i = 0; i < bytes.length; i++)
		{
			int hex = bytes[i];
			if (hex < 0) hex = 256 + hex;
			if (hex >=16) buf.append(Integer.toHexString(hex));
			else
			{
				buf.append('0');
				buf.append(Integer.toHexString(hex));
			}
		}
		return buf.toString().toLowerCase();
	}

	public void authenticated(InfoQuery infoQuery)
	{
		if(!abort)
		{
			sendLoginMessage(I18N.gettext("main.loginstatus.Authenticated")+"...");
			if(infoQuery.getType().equals("error"))
			{
				if(infoQuery.getErrorCode() == 401)
				{
					sendUnauthorized();		
				}
				else
				{
					sendLoginError(I18N.gettext("main.loginstatus.Not_logged_in_because ") + " " + infoQuery.getErrorDescription());	
				}	
				return;
			}
			jabberHandler.changePacketReceiver(new Jabber(backend,browse,iqTimerQueue));
//			TODO remove lowercase if filetransfer bug fixed
			myJID = new JID(loginInfo.getUsername().toLowerCase(),loginInfo.getServer() ,loginInfo.getResource());
			authenticated = true;
            reconnecting = false;
			output.setAuthenticated(); 
			//output.send(new InfoQuery(new JID(server),"get",new IQBrowse()));
			//browse(new JID(loginInfo.getServer()),null);
			sendLoginMessage(I18N.gettext("main.loginstatus.Loading_roster")+"...");
			output.send(new InfoQuery("get",new IQPrivate(new JetiPrivateExtension())));
			output.send(new InfoQuery("get",new IQXRoster()));
			if(show == Presence.NONE)
			{
				show = Presence.AVAILABLE;
				status = Presence.toLongShow(show);
			}
			//moved to backend rosterloaded
//			for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
//			{//online
//				((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(true);
//			}
			//changeStatus(show,status);
		}
	}
	
	public void connected()
	{
		sendLoginMessage(I18N.gettext("main.loginstatus.Logged_in"));
		sendStatus();
		send(new InfoQuery("get",new IQPrivate(new JetiPrivateRosterExtension())));
		for(Iterator j =backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
		{//online
			((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(true);
		}
		sendLoginStatus(5);
	}

	public void receivePackets(Packet packet)
	{
		if(authenticationId.equals(packet.getID())) authenticated((InfoQuery)packet);
		else if(packet instanceof InfoQuery)
		{
		    IQExtension extension = packet.getIQExtension();
			if(extension instanceof IQAuth)
			{
				//System.out.println(extension.toString());
				authenticate((IQAuth) extension);
			}
		}
		//System.out.println(packet.toString());

	}

	public void inputDeath()
	{
		try
		{
			if(socket!=null) socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if((authenticated || reconnecting) && !gotStreamError)
		{
			authenticated = false;
            if (reconnecting) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {};
            }
            reconnecting = true;
			output.disconnect();
			for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
			{//offline
				((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(false);
			}
			reconnect();//only reconnect when logged in
		}
		sendLoginError(I18N.gettext("main.loginstatus.Lost_Input"));
		//else
		//io error while logging in
		//do something usefull
	}

	public void streamError()
	{//only when logging in
		if(authenticated)
		{
			output.disconnect();
			for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
			{//offline
				((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(false);
			}
		}
		sendLoginError(I18N.gettext("main.loginstatus.XML_stream_error,_lost_input"));
		//System.out.println("stream error");
		//output = null;
        gotStreamError = true;
	}

	public void outputDeath()
	{
		authenticated = false;
		for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
		{//offline
			((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(false);
		}
		reconnect(); 
	}

//	private void tryReconnect()
//	{
//		Thread t = new Thread()
//		{
//			int tel = 0;
//			public void run()
//			{
//				while(!autoReconnect() && tel < 3)
//				{
//					synchronized (this)
//					{
//						try
//						{
//							this.wait(60000);
//						}
//						catch (InterruptedException e){}
//					}
//					tel++;
//				} 
//			}
//		};
//		t.start();
//	}

//	public boolean autoReconnect()
//	{
//		Socket socket = null;
//		try{
//			socket = new Socket(loginInfo.getServer(),loginInfo.getPort());
//		}
//		catch (UnknownHostException ex)
//		{
//			Log.error(" Host " +  loginInfo.getServer()  + " could not be found.");
//			return false;
//		}
//		catch (IOException ex)
//		{
//			Log.error("Connection error:  " + ex.getMessage());
//			return false;
//		}
//		try{
//			new Input(socket.getInputStream(),this);
//		}catch (IOException ex)
//		{
//			Log.error("Could not get inputStream because " + ex.getMessage());
//			return false;
//		}
//		try{
//			output = new Output(socket,loginInfo.getServer(),this);
//		}catch (IOException ex)
//		{
//			Log.error("Could not open output because " + ex.getMessage());
//			return false;
//        }
//        return true;
//	}


	public static JID getMyJID()
	{
	    return myJID;
	}

	public boolean getOnline()
	{
	    return authenticated;
	}

	public void disconnect()
	{
		if(authenticated)
		{
			send(new Presence(myJID,"unavailable",Presence.NONE,"logged off"));
			authenticated = false;
			output.disconnect();
		}
		output = null;
		for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
		{//offline
			((nu.fw.jeti.events.StatusChangeListener)j.next()).connectionChanged(false);
		}
	}

	public void exit()
	{
		if(authenticated)
		{
			send(new nu.fw.jeti.jabber.elements.Presence(myJID,"unavailable"));
			authenticated = false;
			output.disconnect();
		}
		output = null;
		for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
		{//exit
			((nu.fw.jeti.events.StatusChangeListener)j.next()).exit();
		}
	}

//	public void newAccount(String server)
//	{
//		new NewAccount(server,this);
//	}
	

//	public void send(Presence presence)
//	{
//		System.out.println("sadfsd" + presence);
//		
////		if(PluginsInfo.isPluginLoaded("opengpg") && !presence.getType().equals("unavailable"))
////		{
////			PluginsInfo.newPluginInstance("openpg")
////		}
//		if(!presence.getType().equals("unavailable"))
//		{
//			nu.fw.jeti.plugins.openpgp.SignPresence(presence,loginInfo.getPriority());
//		}
//		else if (presence.getType().equals("available"))
//		{
//			presence = new Presence(presence,loginInfo.getPriority());
//		}
//		
//		send((Packet)presence);
//	}

	public void send(Packet packet)
	{
		if(authenticated) output.send(packet);
		else nu.fw.jeti.util.Log.notSend(packet.toString());
	}

	public String getAccountInfo()
	{
		return MessageFormat.format(I18N.gettext("main.popup.logged_in_as:_{0}_\n_on_server:_{1}_\n_with resource:_{2}"),new Object[]{loginInfo.getUsername(),loginInfo.getServer(),loginInfo.getResource()});
	}

	public void sendStatus()
	{
		if(!authenticated)
		{
			if(loginInfo == null) return;
			new LoginStatusWindow(loginInfo,backend,1); 
		}
		else
		{
			Presence presence=null;
			if(show!=Presence.UNAVAILABLE && openPGP!=null)
			{
				presence = openPGP.signPresence(show,status,loginInfo.getPriority());
			}
			if (presence==null)
			{
				presence = new Presence(show,status,loginInfo.getPriority(),null);
			}
			send(presence);
			for(Iterator j = backend.getListeners(nu.fw.jeti.events.StatusChangeListener.class);j.hasNext();)
			{
				((nu.fw.jeti.events.StatusChangeListener)j.next()).ownPresenceChanged(show,status);
			}
		}
		sendLoginMessage(I18N.gettext("main.loginstatus.Logged_in"));
		sendLoginStatus(5);
	}

	public void changeStatus(int show,String status)
	{
		this.show = show;
		this.status = status;
		sendStatus();
	}


	private static class DummySSLSocketFactory extends SSLSocketFactory
	{
		private SSLSocketFactory factory;

		public DummySSLSocketFactory() {
			try {
				SSLContext sslcontent = SSLContext.getInstance("TLS");
				sslcontent.init(null, // KeyManager not required
								new TrustManager[] {new DummyTrustManager()},
								new java.security.SecureRandom());
				factory = sslcontent.getSocketFactory();
			}
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			catch (KeyManagementException e) {
				e.printStackTrace();
			}
		}

		public static SocketFactory getDefault() {
			return new DummySSLSocketFactory();
		}

		public Socket createSocket(Socket socket, String s, int i,boolean flag)	throws IOException
		{
			return factory.createSocket(socket, s, i, flag);
		}

		public Socket createSocket(InetAddress inaddr, int i,InetAddress inaddr2, int j) throws IOException
		{
			return factory.createSocket(inaddr, i, inaddr2, j);
		}

		public Socket createSocket(InetAddress inaddr, int i) throws IOException
		{
			return factory.createSocket(inaddr, i);
		}

		public Socket createSocket(String s, int i, InetAddress inaddr,	int j)	throws IOException
		{
			return factory.createSocket(s, i, inaddr, j);
		}

		public Socket createSocket(String s, int i)	throws IOException
		{
			return factory.createSocket(s, i);
		}

		public String[] getDefaultCipherSuites() {
			return factory.getSupportedCipherSuites();
		}

		public String[] getSupportedCipherSuites() {
			return factory.getSupportedCipherSuites();
		}
	}

	/**
	 * Trust manager which accepts certificates without any validation
	 * except date validation.
	 */
	private static class DummyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) {	}

		public void checkServerTrusted(X509Certificate[] chain, String authType)  {
			try {
				chain[0].checkValidity();
			}
			catch (CertificateExpiredException e) {
			}
			catch (CertificateNotYetValidException e) {
			}
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
 }


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
