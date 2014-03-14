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
import java.util.Iterator;

import javax.swing.ProgressMonitorInputStream;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XMPPError;
import nu.fw.jeti.jabber.elements.XMPPErrorTag;
import nu.fw.jeti.plugins.filetransfer.GetFileWindow;
import nu.fw.jeti.plugins.filetransfer.Plugin;
import nu.fw.jeti.plugins.filetransfer.StreamReceive;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.Socks5Proxy;
import nu.fw.jeti.plugins.filetransfer.socks5.jsocks.SocksSocket;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

//31-okt-2004

public class ReceiveSocks5 extends Thread implements StreamReceive
{
	static final int BUF_SIZE = 8192;
 	private Backend backend;
    private InfoQuery iq;
    private GetFileWindow getFileWindow;
    private long bytes;

  
    public ReceiveSocks5(Backend backend,InfoQuery iq,GetFileWindow window)
    {
		this.iq =iq;
		this.getFileWindow = window;
		this.backend = backend;
		start();
    }
    
	public void run()
	{
		Socks5Extension s5 =(Socks5Extension)iq.getIQExtension();
		if(!s5.hasStreamHosts())sendError();
		//s5.getSid();
				

	    //check if enough space on hd

		
		
//			TODO stringprep
		String digest =Plugin.sha(s5.getSid(),iq.getFrom(),backend.getMyJID());
		
	
		SocksSocket s=null;	
		for(Iterator i=s5.getStreamHosts();i.hasNext();)
		{
		
			StreamHost streamHost = (StreamHost)i.next();
							
			
			try{
				//Proxy p = new Socks5Proxy("192.168.10.2",5080);
				//TODO add timeout	
				s = new SocksSocket(new Socks5Proxy(streamHost.getHost(),streamHost.getPort()),digest,0);
//				TODO get sid?
//				check if connection is open
				backend.send(new InfoQuery(iq.getFrom(),"result",iq.getID(),
							new Socks5Extension(streamHost.getJID(),null)));
				break;
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
				continue;
			}
		}
		
		if(s==null)
		{
			sendError();
			Popups.messagePopup(I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted"),I18N.gettext("filetransfer.File_Transfer"));
			getFileWindow.stopDownloading();
			return;
		}
		
		
		InputStream in=null;
		OutputStream out=getFileWindow.getOutputStream();
		//try{
		//File file = getFileWindow.getFile();
//			in = new ProgressMonitorInputStream(backend.getMainWindow(),
//					"downloading " + file.getName(),
//					new BufferedInputStream(s.getInputStream()));
		in = s.getInputStream();	
		
		
			//in = new BufferedInputStream(s.getInputStream());
				System.out.println("begin dowloading");	
			if(in!=null)
			{	
//				try{
//					out = new FileOutputStream(file);
//				}catch(FileNotFoundException e2)
//				{
//			//		timer.stop();
//					Popups.errorPopup(file.getAbsolutePath() + " could not be openend in write mode","File transfer");
//					getFileWindow.stopDownloading();
//				}
				if(out!=null)
				{	
					try{
						byte[] buf = new byte[BUF_SIZE];
						int n;
						while ((n = in.read(buf)) > 0)
						{
							out.write(buf, 0, n);
							out.flush();
							bytes+=n;
							yield();
					    }
						//download ok
//						backend.send(new InfoQuery(jid,"result",id,null)); 
					}catch (IOException e2)  //probaly out of disk space
					{
						Popups.errorPopup(e2.getMessage() + " while downloading " ,"File transfer");
						getFileWindow.stopDownloading();
						//download not ok
	//					backend.send(new InfoQuery(jid,"error",id,null,"Not Acceptable",406)); 
					}//interuppeted io exception for cancel
					finally
					{
						try
						{
							out.close();
							in.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
						getFileWindow.stopDownloading();
					}
			//		timer.stop();
				}
				
			}
			
	}
	
	public long getBytes()
	{
		return bytes;
	}
	
	public void cancel()
	{
		interrupt();
	}
	
	private void sendError()
	{
		XMPPError error = new XMPPError("cancel",404);
		error.addError(new XMPPErrorTag("item-not-found"));
		backend.send(new InfoQuery(iq.getFrom(),iq.getID(),error));
	}
		
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
