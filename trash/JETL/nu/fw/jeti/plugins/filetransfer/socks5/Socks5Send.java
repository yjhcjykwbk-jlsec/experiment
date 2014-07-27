/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
package nu.fw.jeti.plugins.filetransfer.socks5;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.events.IQResultListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.filetransfer.Plugin;
import nu.fw.jeti.plugins.filetransfer.SendFileProgress;
import nu.fw.jeti.plugins.filetransfer.StreamSend;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.ProxyServer;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.ServerAuthenticatorNone;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.Socks5Proxy;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.SocksSocket;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;



//31-okt-2004
public class Socks5Send implements StreamSend
{
	private Backend backend;
	private String sid;
	private List streamHosts;
	private File file;
	private long bytes;
	private SendFileProgress window;
	private ProxyServer proxyServer;
	private Thread proxyServerThread;
	private Thread sendThread;
	private int port = 7777;
		
	public Socks5Send(File file,final Backend backend,SendFileProgress f,JID jid)
	{
		window = f;
		sid = backend.getIdentifier();
    	streamHosts = new LinkedList();
    	this.backend = backend;
    	this.file = file;
    	if(Preferences.getBoolean("filetransfer", "useLocalIP", true))
    	{
    		String ip =Preferences.getString("filetransfer","ip",null);
    		if (ip!=null)
    		{
    			if(ip.equals("automatic"))ip = Plugin.getIP();
    		   	streamHosts.add(new StreamHost(backend.getMyJID(),ip,
    		   			Preferences.getInteger("filetransfer","port",7777),null));
				startSocksServer(file, backend, jid);
    		}
    	}
		
    	streamHosts.add(new StreamHost(new JID("proxy.jabber.org"),
    									"208.245.212.67",7777,null));
    	streamHosts.add(new StreamHost(new JID("proxy65.jabber.ccc.de"),
    										"217.10.0.254",7777,null));	
    	backend.send(new InfoQuery(jid,"set",backend.getIdentifier(),new Socks5Extension(sid,streamHosts)),
    			new IQResultListener()
				{
					public void iqResult(InfoQuery iq)
					{
						if(iq.getType().equals("result"))
						{
							IQExtension e = iq.getIQExtension();
							if(e instanceof Socks5Extension)
							{
								JID streamHostJID = ((Socks5Extension)e).getStreamHostUsed();
								if(streamHostJID!=null)
								{
									if(!streamHostJID.equals(backend.getMyJID()))
									{
										if(proxyServerThread!=null)proxyServerThread.interrupt();
										doProxy(streamHostJID,iq.getFrom());
									}
								}
							}
						}
						else
						{//TODO add refuse error
							Popups.messagePopup(I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted"),I18N.gettext("filetransfer.File_Transfer"));
							
						}
					}
				},0);
	}
	
	private void startSocksServer(final File file,final Backend backend, final JID jid)
	{
		proxyServerThread = new Thread(new Runnable()
		  {
		  	public void run()
		  	{
//		  	set socks5 server
		  		String digest =Plugin.sha(sid,backend.getMyJID(),jid);
		  		ServerSocket ss=null;			
				try{
					 ss = new ServerSocket(port);
			        // while(true){
			          Socket s = ss.accept();
			          System.out.println("Accepted from:"+s.getInetAddress().getHostName()+":"
			                              +s.getPort());
			          proxyServer = new ProxyServer(new ServerAuthenticatorNone(),s,file,digest,Socks5Send.this);
			        //}
			      }catch(IOException ioe){
			        ioe.printStackTrace();
			      }
			      finally
				  {
			      	try {
						if(ss!=null)ss.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  }
		  	}
		  });
		proxyServerThread.start();
	}

	private void doProxy(final JID streamHostID,final JID targetJID)
	{
		new Thread(new Runnable()
		{
		  	public void run()
		  	{
		  		StreamHost streamHost=null;
		  		for(Iterator i= streamHosts.iterator();i.hasNext();)
		  		{
		  			StreamHost s = (StreamHost)i.next();
		  			if(streamHostID.equals(s.getJID()))
		  			{
		  				streamHost =s;
		  				break;
		  			}
		  		}
		  		//if(streamHost==null) sendError();
		  		
				 
				try{
					//TODO stringprep
					
					String digest =Plugin.sha(sid,backend.getMyJID(),targetJID);
									
					
					final SocksSocket s = new SocksSocket(new Socks5Proxy(streamHost.getHost(),streamHost.getPort()),digest,0);
		//			TODO get sid?
					backend.send(new InfoQuery(streamHostID,"set",backend.getIdentifier(),
								new Socks5Extension(sid,targetJID)),new IQResultListener()
								{
									public void iqResult(InfoQuery iq)
									{
										if(iq.getType().equals("result"))sendFile(s);
									}
								},0);
					
			
		
				}
				catch (IOException e2)
				{
					e2.printStackTrace();
					//sendError();
					Popups.errorPopup("file could not be downloaded","File transfer");
					
					return;
				}
		  	}
		}).start();
	}
	
	private void sendFile(final SocksSocket s)
	{
		new Thread(new Runnable()
		{
		  	public void run()
		  	{
		  		try
				{
					InputStream is = new FileInputStream(file.getAbsolutePath());
					OutputStream out = s.getOutputStream();
					//OutputStream out = (OutputStream) ps;
					byte[] buf = new byte[4096];
					try
					{
						int n;
						while ((n = is.read(buf)) > 0)
						{
							out.write(buf, 0, n);
							out.flush();
							bytes+=n;
							if (Thread.interrupted())
							{
								return;
							}
							//if(ps.checkError()) throw new IOException("I/O Error");
						}
					} finally
					{
						is.close();
						s.close();
					}
					window.done();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
		  	}
		}).start();
	}
		
	public void finished()
	{
		window.stop();
	}

	public void addBytes(int bytes)
	{
		this.bytes+=bytes;
	}
	
	public long getBytes()
	{
		return bytes;
	}
	
	public void cancel()
	{
		if(proxyServer!=null)proxyServer.stop();
		if(sendThread!=null)sendThread.interrupt();
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
