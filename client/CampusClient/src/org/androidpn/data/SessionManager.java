package org.androidpn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidpn.client.Constants;

import android.util.Log;
import android.widget.BaseAdapter;

public class SessionManager {
	private final static Map<String,List> sessions= new HashMap<String,List>();//recipient to list of msgs
	private final static Map<String,String> packetMap=new HashMap<String,String>();//packetID to recipient name
	private final static Map<String,BaseAdapter> listeners=new HashMap<String,BaseAdapter>();
	private static String LOGTAG="SessionManager";
	public static void addMsg(String recipient,ChatInfo ci){
		if(recipient==null||ci==null) return;
		Log.i(LOGTAG,"sessionManager.addMsg:"+recipient+"#"+ci.getPacketID());
		getMsgList(recipient).add(ci);
		packetMap.put(recipient,ci.getPacketID());
		
		updateView(recipient);
	}
	private static void updateView(String recipient){
		if(listeners.get(recipient)!=null){
			BaseAdapter adapter=listeners.get(recipient);
			adapter.notifyDataSetChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<ChatInfo> getMsgList(String recipient){
		Log.i(LOGTAG,"sessionManager.getMsgList:"+recipient);
		if(recipient==null) return null;
		if(sessions.get(recipient)==null) sessions.put(recipient, new ArrayList<ChatInfo>());
		return sessions.get(recipient);
	}
	
	public static void addListener(String recipient,BaseAdapter adapter){
		Log.i(LOGTAG,"sessionManager.addListener"+recipient);
		listeners.put(recipient, adapter);
	}
	
	public static void removeListener(String recipient){
		if(recipient!=null)
			listeners.remove(recipient);
	}
	
	public static void msgSent(String packetID){
//		 runOnUiThread(new Runnable() {
//			public void run() {
				Log.i(LOGTAG,"sessionManager.msgSent:"+packetID);
				if(packetID==null) return;
				String recipient=packetMap.get(packetID);
				if(recipient==null||sessions.get(recipient)==null) return ;
				List list=sessions.get(recipient);
				Iterator it=list.iterator();
				while(it.hasNext()){
					ChatInfo ci=(ChatInfo) it.next();
					if(ci.getPacketID().equals(packetID)){
						ci.setSent();
						updateView(recipient);
						return;
					}
				}
			}
//			});
//	}
}