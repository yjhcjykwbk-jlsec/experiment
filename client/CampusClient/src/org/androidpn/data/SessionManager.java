package org.androidpn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//in fact most time, this object is run by only one thread : Session-service
//but other times it is visited by other threads like chatactivity(like send msg)
//so data change need synchronizing
public class SessionManager {
	private final static Map<String,List> sessions= new HashMap<String,List>();//recipient to list of msgs
	private final static Map<String,String> packetMap=new HashMap<String,String>();//packetID to recipient name
	private final static Map<String,Handler> listeners=new HashMap<String,Handler>();
	private static Handler chatsUiHandler=null;
	private final static Map<String,ChatInfo> latestChats=new HashMap<String,ChatInfo>();
	private static String LOGTAG="SessionManager";
	//now this data is isolate from ui-thread's data
	public static void addMsg(String recipient,ChatInfo ci, boolean UIUpdated){
		if(recipient==null||ci==null) return;
		Log.i(LOGTAG,"sessionManager.addMsg:"+ci.getPacketID()+"#"+ci.getContent());
		
		packetMap.put(ci.getPacketID(),recipient);
		
		//1
		synchronized(latestChats){
			latestChats.put(recipient, ci);//
		}
		//synchronize the data change with the ui thread
		if(chatsUiHandler!=null){
			chatsUiHandler.dispatchMessage(new Message());
		}
		
		//2
		List lst=getMsgList(recipient);
		synchronized(lst){
			lst.add(ci);
		}
		//synchronize data change with..
		if(!UIUpdated){
			Handler handler=listeners.get(recipient);
			if(handler!=null){
				Bundle b = new Bundle();// 存放数据
				b.putString("username",ci.getName());
				b.putString("chatXml",ci.getContent());
				b.putString("id",ci.getPacketID());
				Message msg=new Message();
				msg.setData(b);
				msg.what=1;
				handler.dispatchMessage(msg);
			}
		}
	}
	public static void msgSent(String packetID){
		Log.i(LOGTAG,"sessionManager.msgSent:"+packetID);
		if(packetID==null) return;
		String recipient=packetMap.get(packetID);
		if(recipient==null||sessions.get(recipient)==null) {
			Log.i(LOGTAG,"msgSent:recipient or session not usable");
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
		
		Log.i(LOGTAG,"syn msg sent event");
		//synchronize data change with ui-thread
		Handler handler=listeners.get(recipient);
		if(handler!=null){
			Bundle b = new Bundle();// 存放数据
			b.putString("id",packetID);
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
			if(sessions.get(recipient)==null) 
				sessions.put(recipient, new ArrayList<ChatInfo>());
		}
		return sessions.get(recipient);
	}
	public static List<ChatInfo> cloneMsgList(String recipient){
		List lst=getMsgList(recipient),lst_1;
		synchronized(lst){
			lst_1=new ArrayList<ChatInfo>(lst);
		}
		return lst_1;
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
	public static void addListener(String recipient,Handler handler) throws Exception{
		Log.i(LOGTAG,"sessionManager.addListener"+recipient);
		synchronized(listeners){
			if(listeners.get(recipient)!=null) 
				throw new Exception("already have a chat-ui watching "+recipient);
			else listeners.put(recipient, handler);
		}
	}
	
	public static void removeListener(String recipient){
		synchronized(listeners){
			if(recipient!=null)
				listeners.remove(recipient);
		}
	}
	
	
	
	public static void addChatsListener(Handler handler) throws Exception{
		if(chatsUiHandler!=null){
			throw new Exception("already have a chats-ui watching");
		}
		else chatsUiHandler=handler;
	}
	public static void removeChatsListener(){
		chatsUiHandler=null;
	}
	
}
