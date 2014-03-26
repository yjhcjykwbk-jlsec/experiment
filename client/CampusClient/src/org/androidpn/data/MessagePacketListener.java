package org.androidpn.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.BaseAdapter;

public class MessagePacketListener{
	private static String LOGTAG = "PacketListenerManager";
//	private SessionServiceBinder myBinder = new SessionServiceBinder();
	private XmppManager manager = null; 
	private static MessagePacketListener instance=null;
	private static Object lock=new Object();
	public static void listen(){
		getInstance();
	}
	private static MessagePacketListener getInstance(){
		synchronized(lock){
			if(instance==null){
				try {
					instance=new MessagePacketListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		}
		return instance;
	}
	public MessagePacketListener() throws Exception {
		Log.i(LOGTAG,"onconstruct");
		manager=Constants.xmppManager;
		if(manager==null) {
			throw new Exception("packetListenerManager:manager not initilized");
		}
		manager.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				if (packet instanceof Message) {
					// 显示接收到的消息
					String jid = packet.getFrom();
					String username = jid.substring(0, jid.indexOf('@'));
					Log.i(LOGTAG, "recv packet from:" + username+" content:"+((Message)packet).getBody());
					
					SessionManager.addMsg(
						username,new ChatInfo(username, 
							((Message) packet).getBody(), new Date(System
							.currentTimeMillis()), packet
							.getPacketID(), false));

//					Intent intent = new Intent(Constants.ACTION_SHOW_CHAT);
//					intent.putExtra("recipient", username);
//					intent.putExtra("message",((Message)packet).getBody());
					
				} else if (packet instanceof IQ) {
					if (((IQ) packet).getType() == IQ.Type.RESULT) {
						Log.i(LOGTAG, "recv a result IQ whose id is :"
								+ packet.getPacketID());
						if (((IQ) packet).getError() == null) {
							// 发送的消息成功被服务器接收
							SessionManager.msgSent(packet.getPacketID());
							Intent intent = new Intent(
									Constants.ACTION_CHAT_SENT);
							intent.putExtra("packetid", packet.getPacketID());
						}else{
							Log.i(LOGTAG,"iq error is:"+((IQ)packet).getError());
						}
					}
				}
			}
		}, new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				// TODO Auto-generated method stub
				return packet.getPacketID() != null;
			}
		});
	}

//	@Override
//	public IBinder onBind(Intent arg0) {
//		// TODO Auto-generated method stub
//		Log.i(LOGTAG, "onbind");
//		return myBinder;
//	}
//
//	public class SessionServiceBinder extends Binder {
//		// this getService function should be changed to public
//		public PacketListenerManager getService() {
//			// Return this instance of LocalService so clients can call public
//			// methods
//			return PacketListenerManager.this;
//		}
//	}


}
