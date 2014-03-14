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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.fw.jeti.events.IQResultListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.filetransfer.StreamSend;
import nu.fw.jeti.plugins.filetransfer.SendFileProgress;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

public class IBBSend extends Thread implements StreamSend
{
	final static int BUF_SIZE = 4096;
    private File file;
	
    private SendFileProgress window;
	private long bytes;
	private int sequence;
	private Backend backend;
	private JID to;
	private String sid;
    /* buffer to use for requests */
    byte[] buf;
 
    public IBBSend(File file,Backend backend,SendFileProgress ws,JID to) {
        buf = new byte[BUF_SIZE];
		this.file = file;
		this.backend = backend;
		window =ws;
		this.to = to;
		sid = backend.getIdentifier();
		backend.send(new InfoQuery(to,"set","ibb" + backend.getIdentifier(), new IBBExtension(sid,BUF_SIZE)),new IQResultListener()
		{
			public void iqResult(InfoQuery iq)
			{
				if(iq.getType().equals("result"))
				{
					start();
				}
				else
				{//TODO add refuse error
					Popups.messagePopup(I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted"),I18N.gettext("filetransfer.File_Transfer"));
					window.stop();
				}
			}
		},0);
	}

    public void run() 
    {
       	InputStream is = null;
    	try {
    		is = new FileInputStream(file.getAbsolutePath());
            int len;
            while ((len = is.read(buf)) > 0) {
                String encoded = Base64.encode(buf, len);
                backend.send(new Message(null,to,new IBBExtension(sid,sequence,encoded)));
                //TODO add message resultlistener instead of iqresultlistener
				sequence++;
				bytes+=BUF_SIZE;
				if (Thread.interrupted())
				{
					sendStreamClose();
					is.close();
					window.stop();
					return;
				}
				Thread.yield();
            }
            backend.send(new InfoQuery(to,"set",new IBBExtension(sid)));
    	}catch(IOException e)
		{
    		sendStreamClose();
    		Popups.messagePopup(I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted"),I18N.gettext("filetransfer.File_Transfer"));
			window.stop();
		}
        finally {
        	try{
            is.close();
        	}catch(IOException e){e.printStackTrace();}
		}
        window.done();
    }

    private void sendStreamClose()
    {
    	backend.send(new InfoQuery(to,"set","ibb" + backend.getIdentifier(), new IBBExtension(sid)));
    }
    			
    public long getBytes()
	{
		return bytes;
	}
	
	public void cancel()
	{
		interrupt();
	}
}





/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
