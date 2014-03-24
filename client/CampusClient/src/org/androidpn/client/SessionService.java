package org.androidpn.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidpn.data.ChatInfo;
import org.androidpn.data.SessionManager;
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

public class SessionService extends Service {
	private static String LOGTAG = "SessionService";
	private SessionServiceBinder myBinder = new SessionServiceBinder();
	private XmppManager manager = Constants.xmppManager;
	public SessionService() {
		Log.i(LOGTAG,"onconstruct");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(LOGTAG,"oncreate");
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
							.getPacketID(), false),false);//false means is not ui thread

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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG, "onbind");
		return myBinder;
	}

	public class SessionServiceBinder extends Binder {
		// this getService function should be changed to public
		public SessionService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return SessionService.this;
		}
	}
	
//	static class SessionManager {
//		private final static Map<String,List> sessions= new HashMap<String,List>();//recipient to list of msgs
//		private final static Map<String,String> packetMap=new HashMap<String,String>();//packetID to recipient name
//		private final static Map<String,BaseAdapter> listeners=new HashMap<String,BaseAdapter>();
//		private final static Map<String,ChatInfo> latestChats=new HashMap<String,ChatInfo>();
//		private  static final String LOGTAG="SessionManager";
//		public static void addMsg(String recipient,ChatInfo ci){
//			if(recipient==null||ci==null) return;
//			Activity.
//			Log.i(LOGTAG,"sessionManager.addMsg:"+ci.getPacketID()+"#"+ci.getContent());
//			latestChats.put(recipient, ci);
//			getMsgList(recipient).add(ci);
//			packetMap.put(ci.getPacketID(),recipient);
//			
//			updateView(recipient);
//		}
//		private static void updateView(String recipient){
//			if(listeners.get(recipient)!=null){
//				BaseAdapter adapter=listeners.get(recipient);
//				adapter.notifyDataSetChanged();
//			}
//		}
//		
//		@SuppressWarnings("unchecked")
//		public static List<ChatInfo> getMsgList(String recipient){
//			if(recipient==null) return null;
//			if(sessions.get(recipient)==null) sessions.put(recipient, new ArrayList<ChatInfo>());
//			return sessions.get(recipient);
//		}
//		
//		public static void addListener(String recipient,BaseAdapter adapter){
//			Log.i(LOGTAG,"sessionManager.addListener"+recipient);
//			listeners.put(recipient, adapter);
//		}
//		
//		public static void removeListener(String recipient){
//			if(recipient!=null)
//				listeners.remove(recipient);
//		}
//		
//		public static void msgSent(String packetID){
////			 runOnUiThread(new Runnable() {
////				public void run() {
//					Log.i(LOGTAG,"sessionManager.msgSent:"+packetID);
//					if(packetID==null) return;
//					String recipient=packetMap.get(packetID);
//					if(recipient==null||sessions.get(recipient)==null) return ;
//					List list=sessions.get(recipient);
//					Iterator it=list.iterator();
//					while(it.hasNext()){
//						ChatInfo ci=(ChatInfo) it.next();
//						if(ci.getPacketID().equals(packetID)){
//							ci.setSent();
//							Log.i(LOGTAG,"update view:"+ci.getContent());
//							updateView(recipient);
//						}
//					}
//				}
////				});
////		}
//		public static Map<String, ChatInfo> getLatestChats() {
//			// TODO Auto-generated method stub
//			return latestChats;
//		}
//	}


}
