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

package nu.fw.jeti.plugins.filetransfer.ibb;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.plugins.filetransfer.GetFileWindow;
import nu.fw.jeti.plugins.filetransfer.StreamReceive;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.Popups;

//24-okt-2004
public class IBBReceive extends Thread implements StreamReceive
{
	private String sid;
	private JID jid;
	private Backend backend;
	private long bytes;
	private LinkedList queue = new LinkedList();
	private volatile boolean isDownloading=true;
	private GetFileWindow getFileWindow;


    public IBBReceive(JID jid,String sid,Backend backend,GetFileWindow window)
    {
		this.jid = jid;
		this.sid=sid;
		this.backend = backend;
		getFileWindow =window;
		start();
	}
		
    public void addData(String data)
    {
    	synchronized(queue)
		{
			queue.addLast(data);
			queue.notifyAll();
		}
    }
    
    public void stopDownloading()
    {
    	isDownloading = false;
		synchronized(queue){queue.notifyAll();}
    }
    
    public long getBytes()
    {
    	return bytes;
    }
    
    public void cancel()
    {
    	backend.send(new InfoQuery(jid,"set",new IBBExtension(sid)));
    	stopDownloading();
    }
      
	public void run()
	{
									
		BufferedOutputStream out= new BufferedOutputStream(getFileWindow.getOutputStream());
//		try{
//			out = new BufferedOutputStream (new FileOutputStream(file));
//		}catch(FileNotFoundException e2)
//		{
//			Popups.errorPopup(file.getAbsolutePath() + " could not be openend in write mode","File transfer");
//			getFileWindow.stopDownloading();
//		}
		if(out!=null)
		{	
			try{
				while(!queue.isEmpty() || isDownloading)
				{
					String base64Data;
					synchronized(queue)
					{
						if (queue.isEmpty())
						{
							try
							{
								System.out.println("waiting");
								queue.wait();
							}
							catch(InterruptedException e)
							{//bug when thrown? called when interrupted
								e.printStackTrace();
								return;
							}
							continue;
						}
						base64Data = (String)queue.removeFirst();
						System.out.println("data read");
					}	
					//System.out.println(base64Data);
					//System.out.println(Base64.decode2(base64Data));
					
					byte[] data = Base64.decode(base64Data);
												
					
					System.out.println("data converted");
					out.write(data, 0, data.length);
					System.out.println("data written");
					bytes+=data.length;
					//progressMonitor.setProgress(bytes);
					//bytes++;
					//if (Thread.interrupted()) throw new InterruptedException();
					//yield();
				}
			}catch (IOException e2)  //probably out of hd
			{
				Popups.errorPopup(e2.getMessage() + " while downloading " ,"File transfer");
				//download not ok
				backend.send(new InfoQuery(jid,"set",new IBBExtension(sid)));
				getFileWindow.stopDownloading();
			}
			finally
			{
				try
				{
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		getFileWindow.stopDownloading();
		System.out.println("downloaded");
	}
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
