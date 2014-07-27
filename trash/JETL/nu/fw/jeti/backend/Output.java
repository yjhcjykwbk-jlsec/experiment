package nu.fw.jeti.backend;

import java.io.*;
import java.util.*;
import java.net.*;
import nu.fw.jeti.jabber.elements.Packet;



/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class Output extends Thread
{
	private Writer out;
	private LinkedList queue = new LinkedList();
	private volatile boolean isRunning=true;
	//private Input input;
	private Socket socket;
	public static final int KEEP_ALIVE = 60000;
	private KeepAlivePacket keepAlivePacket =  new KeepAlivePacket();
	private ConnectionPacketReceiver backend;
	private volatile boolean authenticated = false;
	
    public Output(Socket socket,String host,ConnectionPacketReceiver backend) throws IOException
    {
		this.socket = socket;
		this.backend = backend;
		try{
		    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF8"));
		}catch (UnsupportedEncodingException e){e.printStackTrace();}
		//send stream init tag
	  //out.write("<?xml version='1.0' encoding='UTF-8'?><stream:stream xmlns:stream='http://etherx.jabber.org/streams' ");
		out.write("<?xml version='1.0' encoding='UTF-8'?><stream:stream xmlns:stream='http://etherx.jabber.org/streams' ");
		out.write("xmlns='jabber:client' ");
		out.write("to='");
		out.write(host);
		out.write("'>");
		out.flush();
		start();
	}

    public void send(Packet p)
    {
		if(isRunning)
		{
			synchronized(queue)
			{
				queue.addLast(p);
				queue.notifyAll();
			}
		}
	}

	public void disconnect()
	{
	    isRunning = false;
		synchronized(queue){queue.notifyAll();}
	}
	
	public void setAuthenticated()
	{
		authenticated = true;
	}

    public final void run()
    {
    	Packet packet;
		while ((isRunning) || (!queue.isEmpty()))
		{
			synchronized(queue)
			{
				if (queue.isEmpty())
				{
					if(isRunning)
					{
						try
						{
							queue.wait(KEEP_ALIVE);
						}
						catch(InterruptedException e)
						{//bug when thrown? called when interrupted
							e.printStackTrace();
							return;
						}
						if(authenticated && queue.isEmpty()) queue.add(keepAlivePacket);
					}
					continue;
				}
				else packet =(Packet) queue.removeFirst();
			}
			// we have a packet, we need to send it.
	//	    if (p!=null)
			String xml = packet.toString();
			try
			{
				nu.fw.jeti.util.Log.sendXML(xml);
				out.write(xml);
				out.flush();
			}
			catch(IOException e)
			{
				if(!socket.isClosed())
				{	
					nu.fw.jeti.util.Log.notSend(xml);
					if(isRunning) backend.outputDeath();//only reconnect if output thread in running status
	                try{
	                    socket.close();
	                } catch (IOException ex){ }
				}
				return;
			}
		}
		if(!socket.isClosed())
		{	
			try
			{//close stream
				//System.out.println("end stream now");
				out.write("</stream:stream>");
				out.flush();
				socket.close();
			}
			catch(Exception e)
			{e.printStackTrace();}
		}
    }

    class KeepAlivePacket extends Packet
    {
        public void appendToXML(StringBuffer xml)
        {
            xml.append(' ');
        }
    }
}



/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
