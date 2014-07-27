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

/**
 * this class main manage static back-end data for chating
 *   @function:
 *   this back-end data is isolate from UI-thread's data
 *   first update this data, then look for handlers, 
 *   if any , dispatch message to ui-thread's handlers to update UI
 *   
 *   @attention:
 *   this class is visited by a packet-listener of Notification-Service's xmpp-connection 
 *   it is also visited by ChatsActivity when send chat msg
 *   so it need data synchronizing 
 *   
 *   @author xzg
 */
public class NoteManager {
	private final static Map<String,List> sessions= new HashMap<String,List>();//recipient to list of msgs
	//private final static Map<String,String> packetMap=new HashMap<String,String>();//map packetID to recipient name
	private final static Map<String,ChatInfo> latestNotes=new HashMap<String,ChatInfo>();
//	private final static Map<String,Handler> listeners=new HashMap<String,Handler>();
	private static Handler noteUiHandler=null;
	private static Handler notesUiHandler=null;
	private static String LOGTAG="NoteManager";
	
	/**
	 * 发送一个信息
	 * 更新会话列表和对应会话的数据和视图
	 * @param recipient
	 * @param ci
	 */
	public static void recvNote(String recipient,ChatInfo ci){
		if(recipient==null||ci==null||!ci.isComplete()) return;
		Log.i(LOGTAG,"sessionManager.addMsg:"+ci.getPacketID()+"#"+ci.getContent());
				
		//更新会话列表的数据
		synchronized(latestNotes){
			latestNotes.put(recipient, ci);
		}
		Bundle b = new Bundle();// 存放数据
		b.putString("recipient",ci.getName());
		b.putString("username",ci.getName());
		b.putString("chatXml",ci.getContent());
		b.putString("id",ci.getPacketID());
		b.putBoolean("isSelf", ci.isSelf());
		//更新会话列表的ui　
		if(notesUiHandler!=null){//通知更新会话列表的视图
			Message msg=new Message();
			msg.setData(b);
			Log.i(LOGTAG,"dispatchMessager: latestchats changed "+ ci.getName());
			notesUiHandler.dispatchMessage(msg);
		}
		
		//更新对应会话的数据
		List lst=getNoteList(recipient);
		synchronized(lst){
			lst.add(ci);
		}	
		//更新对应会话的视图
		Handler handler=noteUiHandler;//listeners.get(recipient);
		if(handler!=null){//通知更新对应会话的视图
			Message msg=new Message();
			msg.setData(b);
			msg.what=1;
			if(ci.isSelf()) Log.i(LOGTAG,"dispatchMessager: packet send to "+ ci.getName());
			else Log.i(LOGTAG,"dispatchMessager: packet recved from "+ci.getName());
			handler.dispatchMessage(msg);
		}
	}
	
	/**
	 * 成功发送一个消息(消息编号为packetID)
	 * 更新对应会话的数据和视图
	 * @param packetID
	 * @deprecated
	 */
	public static void msgSent(String packetID){
	}
	
	/**
	 * 获取recipient对应会话的数据(list)
	 * @param recipient
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<ChatInfo> getNoteList(String appName){
		if(appName==null) return null;
		synchronized(sessions){
			if(sessions.get(appName)==null) {
				sessions.put(appName, new ArrayList<ChatInfo>());
				Log.i(LOGTAG,"add a message queue for "+appName);
			}
		}
		return sessions.get(appName);
	}
	
	
	public static List<ChatInfo> cloneNoteList(String recipient){
		Log.i(LOGTAG,"cloneMsgList");
		assert(recipient!=null);
		List lst=getNoteList(recipient),lst_1;
		if(lst==null) return null;
		synchronized(lst){
			lst_1=new ArrayList<ChatInfo>(lst);
		}
		return lst_1;
	}
 
	public static Map<String, ChatInfo> cloneLatestNotes() {
		Log.i(LOGTAG,"cloneLatestChats");
		Map m;
		synchronized(latestNotes){
			m=new HashMap<String,ChatInfo>(latestNotes);
		}
		return m;
	}
	
	/**
	 * 设置用于通知更新＂对应会话的视图＂的handler
	 */
	public static void setNoteUiListener(Handler handler) {
		if(noteUiHandler!=null){
			synchronized(noteUiHandler){
				noteUiHandler=handler;
			}
		}
		else noteUiHandler=handler;
	}
	
	public static void removeNoteUiListener(){
		if(noteUiHandler!=null){
			synchronized(noteUiHandler){
				noteUiHandler=null;
			}
		}
	}
	
	/**
	 * 设置用于通知更新＂会话列表的视图＂的handler
	 */
	public static void setNotesUiListener(Handler handler) {
		notesUiHandler=handler;
	}
	
	public static void removeNotesUiListener(){
		notesUiHandler=null;
	}
	
}
