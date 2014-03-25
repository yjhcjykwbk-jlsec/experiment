package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.NotificationService;
import org.androidpn.client.XmppManager;
import org.androidpn.client.NotificationService.LocalBinder;
import org.androidpn.data.ChatInfo;
import org.androidpn.data.ChatInfoAdapter;
import org.androidpn.data.PacketListenerManager;
import org.androidpn.data.SessionManager;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.UIUtil;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import android.widget.EditText;

public class ChatActivity extends Activity {
	private String LOGTAG = "chatActivity";
	private String USERNAME;
	private String PASSWORD;
	private String recipient = null;// who you are talking with
	private EditText mTextMessage;
	private ListView mListView;

	private List<ChatInfo> messageList;
	private XmppManager xmppManager;
	private ChatInfoAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Log.i(LOGTAG, "hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");// used for 8080 connection
		recipient= getIntent().getStringExtra("recipient");
		if(recipient==null) recipient=USERNAME;
		
		//init connection
		xmppManager = Constants.xmppManager;// if it is null, this will be a
											// trouble
		//init data
		packetList=Constants.packetList;
		if(packetList==null)
			packetList = Constants.packetList=new ArrayList<Pair>();
		
		
		
		TextView tv=(TextView)this.findViewById(R.id.ChatTitleLabel);
		tv.setText(USERNAME+"与"+recipient+"会话中");
		
		initMsgListView();
		
		// send message button and edit
		mTextMessage = (EditText) this.findViewById(R.id.MessageEdit);
		Button sendBtn = (Button) this.findViewById(R.id.SendBtn);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String to = recipient;
				String text = mTextMessage.getText().toString();
				Log.i(LOGTAG,
						"XMPPChatActivity#send.onclicklistener Sending text "
								+ text + " to " + to);
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				if (xmppManager == null) {
					Log.i(LOGTAG,
							"XMPPChatActivity#send.onclicklistener xmppmanager is null");
					return;
				} else {
					//send msg
					ChatInfo ci=new ChatInfo(USERNAME, msg.getBody(),
							new Date(System.currentTimeMillis()),msg.getPacketID(),true);
					addMsgView(ci);
					addMsg(msg);
					SessionManager.addMsg(recipient, ci, true);
				}
				mTextMessage.setText("");
			}
		});
		
		
		smThread = new SendMsgThread(xmppManager);
		smThread.start();
	}
	
	
	
	
    /*
	 * add a message to the list view
	 */
    private void addMsgView(ChatInfo ci){
    	Log.i(LOGTAG,"addMsgView "+ci.getContent());
    	messageList.add(ci);
    	mAdapter.notifyDataSetChanged();
    }
    private void setMsgView(String packetID){
    	Log.i(LOGTAG,"setMsgView "+packetID);
    	for(ChatInfo ci : messageList){
    		if(ci.getPacketID().equals(packetID)){
    			Log.i(LOGTAG,"setMsgView "+" update view");
    			ci.setSent();
    		}
    	}
    	mAdapter.notifyDataSetChanged();
    }
    
	/*
	 * handle with new message or sent
	 */
	@SuppressWarnings("unchecked")
	private MyHandler uiHandler=new MyHandler();
	class MyHandler extends Handler {
         public MyHandler() {
         }
         // 子类必须重写此方法,接受数据
         @Override
         public void handleMessage(android.os.Message msg) {
             Log.d("MyHandler", "handleMessage......");
             Bundle b = msg.getData();
             switch(msg.what){
             case 1://recv
              /*String username;//the man you're talking with
        		String chatXml;
        		Date time;
        		String packetID;*/
            	ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),b.getString("id"),false);
            	addMsgView(ci);
            	return;
             case 2://sent
            	setMsgView(b.getString("id"));
             }
         }
     }
	private void initMsgListView(){
		// messages stores the talk contents with friends
		// messages = (List<ChatInfo>) SessionManager.getMsgList(recipient);
		messageList=SessionManager.cloneMsgList(recipient);
		mAdapter = new ChatInfoAdapter(this,messageList);
		mListView = (ListView) this.findViewById(R.id.MessageListView);
		mListView.setAdapter(mAdapter);
		try {
			SessionManager.addListener(recipient, uiHandler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * related to send message thread always try to send packets to server if
	 * packet in packetList
	 */
	private List<Pair> packetList;
	private SendMsgThread smThread;

	@SuppressWarnings("unchecked")
	public void addMsg(Packet msg) {
		synchronized (packetList) {
			packetList.add(new Pair(msg,false));
		}
	}

	@SuppressWarnings("unused")
	private class SendMsgThread extends Thread {
		final XmppManager xmppManager;

		private SendMsgThread(XmppManager manager) {
			xmppManager = manager;
		}

		public void run() {
			Log.i(LOGTAG, "chatactivity#SendMsgTask#run()... ");
			XMPPConnection conn = null;
			Packet packet = null;
			while (true) {
				// since this thread is only reading the packetlist, no need to
				// synchronized
				// synchronized(packetList){
				if (packetList.isEmpty()) {
					try {
						Thread.currentThread();
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				synchronized (packetList) {
					if(packetList.isEmpty()) continue;
					packet = (Packet) packetList.get(0).first;
					// packetList.remove(0);　//we may not need to remove it now, we
					// can remove when it's sent
					Log.i(LOGTAG, "XmppManager#sendMsgTask packet in sending:"
							+ packet.toXML());
					xmppManager.sendMsg(packet);
					
					Log.i(LOGTAG,
							"XmppManager#sendMsgTask waiting for packet to be sent");
					Log.i(LOGTAG, "XmppManager#sendMsgTask !!!!packet sent");
					packetList.remove(0);
				}
			}
		}

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
    public void onDestroy(){
    	super.onDestroy();
    	SessionManager.removeListener(recipient);
	}
}
