package org.androidpn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * this class main manage static back-end data for chating
 *   @function:
 *   this back-end data is isolate from UI-thread's data
 *   first update this data, then look for handlers, 
 *   if any , dispatch message to ui-thread's handlers to update UI
 *   
 *   @attention:
 *   this class is visited by a packet-listener of Notification-Service's xmpp-connection 
 *   it is also visited by ChatsActivity when send chat msg
 *   so it need synchronizing, and this may block xmpp-connection a little
 *   
 *   @author:xuzhigang
 */
public class SessionManager {
	private final static Map<String,List> sessions= new HashMap<String,List>();//recipient to list of msgs
	private final static Map<String,String> packetMap=new HashMap<String,String>();//packetID to recipient name
	private final static Map<String,ChatInfo> latestChats=new HashMap<String,ChatInfo>();
//	private final static Map<String,Handler> listeners=new HashMap<String,Handler>();
	private static Handler chatUiHandler=null;
	private static Handler chatsUiHandler=null;
	private static String LOGTAG="SessionManager";
	
	
	public static void addMsg(String recipient,ChatInfo ci){
		if(recipient==null||ci==null||!ci.isComplete()) return;
		Log.i(LOGTAG,"sessionManager.addMsg:"+ci.getPacketID()+"#"+ci.getContent());
		
		synchronized(packetMap){
			packetMap.put(ci.getPacketID(),recipient);
		}
		//1
		synchronized(latestChats){
			latestChats.put(recipient, ci);
		}
		
		Bundle b = new Bundle();// 存放数据
		b.putString("recipient",ci.getName());
		b.putString("username",ci.getName());
		b.putString("chatXml",ci.getContent());
		b.putString("id",ci.getPacketID());
		b.putBoolean("isSelf", ci.isSelf());
		
		//synchronize the data change with the ui thread
		if(chatsUiHandler!=null){
			Message msg=new Message();
			msg.setData(b);
			Log.i(LOGTAG,"dispatchMessager: latestchats changed "+ ci.getName());
			chatsUiHandler.dispatchMessage(msg);
		}
		
		List lst=getMsgList(recipient);
		synchronized(lst){
			lst.add(ci);
		}
		//synchronize data change with..
		Handler handler=chatUiHandler;//listeners.get(recipient);
		if(handler!=null){
			Message msg=new Message();
			msg.setData(b);
			msg.what=1;
			if(ci.isSelf()) Log.i(LOGTAG,"dispatchMessager: packet send to "+ ci.getName());
			else Log.i(LOGTAG,"dispatchMessager: packet recved from "+ci.getName());
			handler.dispatchMessage(msg);
		}
	}
	
	public static void msgSent(String packetID){
		Log.i(LOGTAG,"sessionManager.msgSent:"+packetID);
		if(packetID==null) return;
		String recipient=packetMap.get(packetID);
		if(recipient==null||sessions.get(recipient)==null) {
			Log.i(LOGTAG,"msgSent:recipient null or session not valid");
			return ;
		}
		
		List list=sessions.get(recipient);
		synchronized(list){
			Iterator it=list.iterator();
			while(it.hasNext()){
				ChatInfo ci=(ChatInfo) it.next();
				if(ci.getPacketID().equals(packetID)){
					ci.setSent();
				}
			}
		}
		
		Log.i(LOGTAG,"try to syn msg-sent event with chat-ui");
		//synchronize data change with ui-thread
		Handler handler=chatUiHandler;//listeners.get(recipient);
		if(handler!=null){
			Bundle b = new Bundle();// 存放数据
			b.putString("id",packetID);
			b.putString("recipient",recipient);
			Message msg=new Message();
			msg.setData(b);
			msg.what=2;
			Log.i(LOGTAG,"dispatchMessager:recved"+packetID);
			handler.dispatchMessage(msg);
		}
	}
	@SuppressWarnings("unchecked")
	private static List<ChatInfo> getMsgList(String recipient){
		if(recipient==null) return null;
		synchronized(sessions){
			if(sessions.get(recipient)==null) {
				sessions.put(recipient, new ArrayList<ChatInfo>());
				Log.i(LOGTAG,"add a message queue for "+recipient);
			}
		}
		return sessions.get(recipient);
	}
	
	public static List<ChatInfo> cloneMsgList(String recipient){
		assert(recipient!=null);
		List lst=getMsgList(recipient),lst_1;
		if(lst==null) return null;
		synchronized(lst){
			lst_1=new ArrayList<ChatInfo>(lst);
		}
		return lst_1;
	}
	
	private static Map<String, ChatInfo> getLatestChats() {
		// TODO Auto-generated method stub
//		Map m;
//		synchronized(latestChats){
//			m=new HashMap<String,ChatInfo>(latestChats);
//		}
//		return m;
		return latestChats;
	}
	public static Map<String, ChatInfo> cloneLatestChats() {
		// TODO Auto-generated method stub
		Map m;
		synchronized(latestChats){
			m=new HashMap<String,ChatInfo>(latestChats);
		}
		return m;
	}
	/*
	 * 
	 */
	public static void setChatUiListener(Handler handler) {
//		Log.i(LOGTAG,"sessionManager.setListener:"+recipient);
//		synchronized(listeners){
////			if(listeners.get(recipient)!=null) 
////				throw new Exception("already have a chat-ui watching "+recipient);
//			listeners.put(recipient, handler);
//		}
		if(chatUiHandler!=null){
			synchronized(chatUiHandler){
				chatUiHandler=handler;
			}
		}
		else chatUiHandler=handler;
	}
	
	public static void removeChatUiListener(){
//		Log.i(LOGTAG,"sessionManager.removeListener:"+recipient);
//		synchronized(listeners){
//			if(recipient!=null)
//				listeners.remove(recipient);
//		}
		if(chatUiHandler!=null){
			synchronized(chatUiHandler){
				chatUiHandler=null;
			}
		}
	}
	
	public static void setChatsUiListener(Handler handler) {
//		if(chatsUiHandler!=null){
//			throw new Exception("already have a chats-ui watching");
//		}
		chatsUiHandler=handler;
	}
	public static void removeChatsUiListener(){
		chatsUiHandler=null;
	}
	
}
