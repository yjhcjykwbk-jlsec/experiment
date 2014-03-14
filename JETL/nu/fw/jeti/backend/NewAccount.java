package nu.fw.jeti.backend;

import java.io.*;
import java.net.*;

import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.ui.RegisterWindow;

import nu.fw.jeti.jabber.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class NewAccount implements ConnectionPacketReceiver
{
	private String server;
	private String username;
	private String password;
	private Output output;
	private String registerId="";
	private Connect backend;
	private RegisterWindow registerWindow;

//	public NewAccount(String server,Connect backend)
//	{
//		this.backend = backend;
//		try{
//		    connect(server,5222);
//		}catch (IOException e){e.printStackTrace();}
//	}
	
	public NewAccount(String server,Connect backend,String username,String password)
	{
		this.backend = backend;
		this.password = password;
		this.username = username;
		try{
		    connect(server,5222);
		}catch (IOException e){e.printStackTrace();}
	}

	public Handlers getHandlers()
	{
		return backend.getHandlers();
	}

	public void setJabberHandler(JabberHandler jH){}

	public void connect(String host, int port) throws IOException
	{
		Socket socket = new Socket(host,port);
		new Input(socket.getInputStream(),this);
		output = new Output(socket,host,this);
		server = host;
	}

	public void connected(String connectionID)
	{
		send(new InfoQuery(new JID(server),"get",new IQRegister()));
	}

	public void receivePackets(Packet packet)
	{
		if(registerId.equals(packet.getID()))
		{
			InfoQuery query = (InfoQuery) packet;
			if(query.getType().equals("result"))
			{
			    registerWindow.login(backend,server);
				output.disconnect();
			}
			//maak mooi
			else registerWindow.error(packet.getErrorDescription());
		}
		else if(packet instanceof InfoQuery)
		{
		    IQExtension extension = packet.getIQExtension();
			if(extension instanceof IQRegister)
			{
				//System.out.println(extension);
				new RegisterWindow(this,(IQRegister)extension,username,password).show();
			}

		}
		//System.out.println(packet.toString());
	}

	public void sendRegister(IQRegister register,RegisterWindow window)
	{
		registerWindow = window;
		registerId = "JetiRegister_"+ new java.util.Date().getTime();
		send(new InfoQuery(null,"set",registerId,register));
	}


	public void inputDeath()
	{//verbeter
	//	if(authenticated) disconnect();
		//else
		//io error while logging in
		//do something usefull
		System.out.println("input death");
	}

	public void outputDeath()
	{//verbeter
	//	if(authenticated) disconnect();
		//else
		//io error while logging in
		//do something usefull
		System.out.println("input death");
	}

	public void streamError()
	{
		System.out.println("stream error");
	}

	public void send(Packet packet)
	{
		output.send(packet);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
