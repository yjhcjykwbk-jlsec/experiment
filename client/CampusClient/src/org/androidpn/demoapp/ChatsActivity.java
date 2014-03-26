package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.androidpn.data.ChatInfoAdapter;
import org.androidpn.data.ChatsAdapter;
import org.androidpn.data.ChatInfo;
import org.androidpn.data.MessagePacketListener;
import org.androidpn.data.SessionManager;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.UIUtil;
import org.androidpn.util.Xmler;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ChatsActivity extends Activity{
	private static String LOGTAG="ChatsActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<User> friendList;
	private Map<String,ChatInfo> latestChats;
	private MyHandler handler=new MyHandler();
    private MessagePacketListener plManager;
	private ChatViewController chatAct=null;
	private Integer viewState=1;//indicate which view activity currently is in
	ChatsAdapter chatsAdapter;
	boolean inited=false;
	
	View chatsView=null;
	Map <String,View> chatViews=null;
	Map <String,List> messageLists=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(LOGTAG, "hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");// used for 8080 connection
		
		friendList=Constants.friendList;
		if(friendList==null) getFriend();		
		
		latestChats=SessionManager.cloneLatestChats();
		assert(latestChats!=null);
		
		chatViews=new HashMap();
		messageLists=new HashMap();
		
		chatAct=new ChatViewController();//handle chat view page
		
		if(getIntent().getStringExtra("recipient")!=null){
			String recipient=getIntent().getStringExtra("recipient");
			setChatView(recipient);
		}else{
			//chats list view ui
			setChatsView();
		}
		
		//this add listener on XMPPManager for once
		plManager.listen();
	}
	/*
	 * if activity is called by intent
	 * then judge the intent and route to different views
	 */
	@Override
	protected void onNewIntent(Intent intent){
		Log.i(LOGTAG,"onNewIntent");
		if(intent.getStringExtra("recipient")!=null){
			String recipient=intent.getStringExtra("recipient");
			setChatView(recipient);
		}else{
			//chats list view ui
			setChatsView();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK&&viewState==2){
			setChatsView();
			return false;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void setChatsView(){
		
		Log.i(LOGTAG,"setChatsView setChatsView");
		if(chatsView!=null) {
			setContentView(chatsView);
			viewState=1;
			chatsAdapter.notifyDataSetChanged();
			return;
		}
		chatsView=getLayoutInflater().inflate(R.layout.activity_chats, null);
		setContentView(chatsView);
		viewState=1;
		
		
		//init views
		initHeaderView();
		
		Log.i(LOGTAG,"setChatsView setListView");
		ListView lv=(ListView) this.findViewById(R.id.ChatListView);
		
		if(chatsAdapter==null)  chatsAdapter=new ChatsAdapter(this,latestChats);
		lv.setAdapter(chatsAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				ChatsAdapter adapter=(ChatsAdapter) arg0.getAdapter();
				ChatInfo ci=(ChatInfo) adapter.getItem(arg2);
				
				String recipient=ci.getRecipient();
				assert(recipient!=null);
				setChatView(recipient);
			}
		});
		
		Log.i(LOGTAG,"setChatsView set chats listener");
		SessionManager.setChatsUiListener(handler);
		
		Log.i(LOGTAG,"setChatsView finished");
		//UIUtil.alert(this,"异常:SessionManager already have an chats-ui listener");
	}
	
	//go to chat view page
	private void setChatView(String recipient){
		if(recipient==null) return;
		View chatView=null;List<ChatInfo> messageList=null;
		if(chatViews.containsKey(recipient)){
			chatView=chatViews.get(recipient);
		}
		if(messageLists.containsKey(recipient)){
			messageList=messageLists.get(recipient);
		}
		try {
			chatAct.initView(recipient,messageList,chatView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	/*
	 * init the page's header with three buttons
	 */
	private void initHeaderView(){
		Button findBtn = (Button) this.findViewById(R.id.FindUserBtn);
		findBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertFindUserForm();
			}
		});
		// go back home button
		Button homeBtn = (Button) this.findViewById(R.id.HomeBtn);
		homeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ChatsActivity.this.finish();
			}
		});
		Button contactsBtn=(Button) this.findViewById(R.id.FriendListBtn);
		contactsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ChatsActivity.this,ContactActivity.class);
				intent.putExtras(ChatsActivity.this.getIntent().getExtras());
				startActivity(intent);
			}
		});
	}

	protected void onDestory(){
		SessionManager.removeChatsUiListener();
		super.onDestroy();
	}
	
	class MyHandler extends Handler{
		public void dispatchMessage(Message msg){
			//更新
			Bundle b=msg.getData();
			String recipient=b.getString("recipient");
			ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),b.getString("id"),b.getBoolean("isSelf"));
			assert(latestChats!=null);
			assert(recipient!=null);
			assert(ci!=null);
			latestChats.put(recipient, ci);
			chatsAdapter.notifyDataSetChanged();
		}
	}
	
	/*
	 * alert a find user form window
	 */
	private void alertFindUserForm() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.alert_user_find, null);
		AlertDialog dlg = new AlertDialog.Builder(ChatsActivity.this).setView(layout)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				EditText et=(EditText)layout.findViewById(R.id.FindUserEdit);
				findUser(et.getText().toString());
			}
		}).setNegativeButton("取消",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1){
			}
		}).show();
	}
	/*
	 * related to find and add friend
	 */
	private void findUser(String s) {
		String androidpnURL = getString(R.string.androidpnserver);
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=getUser"); //
		parameter.append("&username=" + s);
		/*--End--*/
		String resp = GetPostUtil.send("POST", androidpnURL + "user.xml",
				parameter);
		if (resp != null) {
			resp = resp.substring(resp.indexOf("\n") + 1);
			resp = resp.replaceAll("\n", "");
			int i = resp.indexOf("<user>"), j;
			if (i < 0 || (j = resp.indexOf("</user>")) < 0) {
				UIUtil.alert(ChatsActivity.this,"未找到相应用户");
				Log.i(LOGTAG, "USER NOT FOUND");
				return;
			} else {
				String str = resp.substring(i, j + 7);
				Log.i(LOGTAG, "user :" + str);
				Xmler.getInstance().alias("user", User.class);
				User u = (User) Xmler.getInstance().fromXML(str);

				if (u == null) {
					Log.i(LOGTAG, "user not valid");
					UIUtil.alert(ChatsActivity.this,"用户无效");
					return;
				}
				Log.i(LOGTAG, "USER FOUND:" + u.getName());
				displayUser(u);
			}
		}
	}
	
	/*
	 * add friend
	 * send a add-friend request to server in a asynchronous way
	 */
	private void addFriend(final String userId){
		new Runnable(){
			public void run(){
				String androidpnURL = getString(R.string.androidpnserver);
				String params="action=addFriend&id2="+userId+"&username1="+USERNAME;
				String res=GetPostUtil.sendPost(androidpnURL+
						"user.xml",params);
				Log.i(LOGTAG,"addfriend.onclick:"+res);
				String status=getXmlElement(res,"status");
				String reason=getXmlElement(res,"reason");
				if(status==null){
					UIUtil.alert(ChatsActivity.this,"添加失败:"+(reason==null?"":reason));
				}else if(status.equals("1")){
					UIUtil.alert(ChatsActivity.this,"添加关注成功");
				}else{
					UIUtil.alert(ChatsActivity.this,"你们现在已经是好友了");
				}
			}
		}.run();
	}
	/*
	 * 
	 */
	private void getFriend(){
		new Runnable(){
			public void run(){
				String androidpnURL = getString(R.string.androidpnserver);
				String params="action=listFriend&username="+USERNAME;
				String resp=GetPostUtil.sendPost(androidpnURL+
						"user.xml",params);
				if("succeed".equals(getXmlElement(resp,"result"))){
					int i = resp.indexOf("<list>"), j;
					if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
						UIUtil.alert(ChatsActivity.this,"未找到相应用户");
						Log.i(LOGTAG, "USER NOT FOUND");
						return;
					} else {
						String str = resp.substring(i, j + 7);
						Log.i(LOGTAG, "list :" + str);
						Xmler.getInstance().alias("user", User.class);
						List<User> list = (List) Xmler.getInstance().fromXML(str);

						if (list == null) {
							Log.i(LOGTAG, "friendlist invalid");
							UIUtil.alert(ChatsActivity.this,"没有找到好友");
							return;
						}
						friendList=Constants.friendList=list;
						UIUtil.alert(ChatsActivity.this,"通讯录已经同步");
					}
				}else{
					String reason=getXmlElement(resp,"reason");
					UIUtil.alert(ChatsActivity.this,"拉取好友列表失败:"+(reason==null?"":reason));
				}
			}
		}.run();
	}
	/*
	 * display a user in a alert window
	 */
	private void displayUser(final User u) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.alert_user_info, null);
		Log.i(LOGTAG,"display user:"+Xmler.getInstance().toXML(u));
		((TextView) layout.findViewById(R.id.UsernameLabel)).setText(u.getName());
		((TextView) layout.findViewById(R.id.UserIDLabel)).setText(u.getId()+"");
		((TextView) layout.findViewById(R.id.UserEmailLabel)).setText(u.getEmail());
		((ImageView) layout.findViewById(R.id.UserPhotoLabel)).setImageDrawable(
				getResources().getDrawable(UIUtil.getPhoto(u.getName())));
		TextView foLink = (TextView) layout.findViewById(R.id.ChatWithBtn);
		foLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ChatsActivity.this,ChatsActivity.class);
				Bundle bundle=ChatsActivity.this.getIntent().getExtras();
				bundle.putString("recipient", u.getName());
				intent.putExtras(bundle);
				startActivity(intent);
				Log.i(LOGTAG, "FRIEND ADDED");
			}
		});
		new AlertDialog.Builder(ChatsActivity.this).setView(layout)
			.setPositiveButton("添加关注", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					final String s=u.getId()+"";
					addFriend(s);
				}
			}).setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			}).show();
	
	}
	private String getXmlElement(String resp,String tag){
		if(resp==null||tag==null) return null;
		int i = resp.indexOf("<"+tag+">"), j;
		if (i < 0 || (j = resp.indexOf("</"+tag+">")) < 0) {
			return null;
		}
		return resp.substring(i+tag.length()+2,j);
	}
	

	/*
	 * manage with chat ui view
	 */
	class ChatViewController {
		private String LOGTAG = "chatActivity";
		private String recipient = null;// who you are talking with
//		private List<ChatInfo> messageList;
		private XmppManager xmppManager= Constants.xmppManager;
		/*
		 * should start smThread only for once;
		 */
		private ChatViewController(){
			recipient=USERNAME;
			initSendThread();
			SessionManager.setChatUiListener(uiHandler);
		}
		private void initSendThread(){
			packetList=Constants.packetList;
			if(packetList==null)
				packetList = Constants.packetList=new ArrayList<Pair>();
			if(smThread==null||!smThread.isAlive()){
				smThread = new SendMsgThread(xmppManager);
				smThread.start();
			}
		}
		
		/*
		 * page jumped 
		 */
		public void initView(String recipient, List messageList , View chatView) throws Exception{
			Log.i(LOGTAG,"chatviewController.initview");
			
			this.recipient=recipient;
			if(chatView!=null) {
				setContentView(chatView);
				viewState=2;
				return;
			}
			
			chatView=getLayoutInflater().inflate(R.layout.activity_chat, null);
			setContentView(chatView);
			chatViews.put(recipient, chatView);
			viewState=2;
			
			Button sendBtn = (Button) (ChatsActivity.this).findViewById(R.id.SendBtn);
			TextView tv=(TextView)(ChatsActivity.this).findViewById(R.id.ChatTitleLabel);
			tv.setText(USERNAME+"与"+recipient+"会话中");

			// messages stores the talk contents with friends
			if(messageList==null){
				messageList=SessionManager.cloneMsgList(recipient);
				if(messageList==null)
					throw new Exception("initView:messageList null and creation failed");
				messageLists.put(recipient, messageList);
			}
			
			BaseAdapter chatAdapter=new ChatInfoAdapter((ChatsActivity.this),messageList);
			ListView mListView = (ListView) (ChatsActivity.this).findViewById(R.id.MessageListView);
			mListView.setAdapter(chatAdapter);
			chatAdapter.notifyDataSetInvalidated();
						
			// send message button and edit
			sendBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					EditText  mTextMessage = (EditText) (ChatsActivity.this).findViewById(R.id.MessageEdit);
					String to = ChatViewController.this.recipient;
					String text = mTextMessage.getText().toString();
					Log.i(LOGTAG,
							"XMPPChatActivity#send.onclicklistener Sending text "
									+ text + " to " + to);
					org.jivesoftware.smack.packet.Message msg = 
							new org.jivesoftware.smack.packet.Message(to, org.jivesoftware.smack.packet.Message.Type.chat);
					msg.setBody(text);
					if (xmppManager == null) {
						Log.i(LOGTAG,
								"XMPPChatActivity#send.onclicklistener xmppmanager is null");
						return;
					} else {
						//send msg
						ChatInfo ci=new ChatInfo(ChatViewController.this.recipient, msg.getBody(),
								new Date(System.currentTimeMillis()),msg.getPacketID(),true);
						addMsg(msg);
						SessionManager.addMsg(ChatViewController.this.recipient, ci);
					}
					mTextMessage.setText("");
				}
			});
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
	             if(!messageLists.containsKey(recipient)||!chatViews.containsKey(recipient)){
            		Log.e(LOGTAG,"handleMessage:messageList or chatView not found");
            		return;
	             }
          		 List<ChatInfo> messageList=messageLists.get(recipient);
            	 ChatInfoAdapter chatAdapter=(ChatInfoAdapter) ((ListView)(chatViews.get(recipient).findViewById(R.id.MessageListView))).getAdapter();
            	 Bundle b = msg.getData();
            	 
	             switch(msg.what){
	             case 1://recv or send
	            	if(!b.getString("username").equals(recipient)) return;
	            	ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),
	            			b.getString("id"),b.getBoolean("isSelf"));
	            	
	           
	            	messageList.add(ci);
	            	chatAdapter.notifyDataSetChanged();
	            	UIUtil.alert(ChatsActivity.this,"new msg recved or send");
	            	return;
	             case 2://sent
					String r=b.getString("recipient");
					if(r==null||!r.equals(recipient)) return;
	            	String packetID=b.getString("id");
	            	for(ChatInfo ci_1 : messageList){
	    	    		if(ci_1.getPacketID().equals(packetID)){
	    	    			Log.i(LOGTAG,"setMsgView "+" update view");
	    	    			ci_1.setSent();
	    	    			chatAdapter.notifyDataSetChanged();
	    	    			UIUtil.alert(ChatsActivity.this,"new msg sent");
	    	    			return;
	    	    		}
	    	    	}
	             }
	         }
	     }
		
		/**
		 * related to send message thread always try to send packets to server if
		 * packet in packetList
		 */
		private List<Pair> packetList;
		@SuppressWarnings("unchecked")
		public void addMsg(Packet msg) {
			synchronized (packetList) {
				packetList.add(new Pair(msg,false));
			}
		}
		private SendMsgThread smThread;
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


//	
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.chat, menu);
//		return true;
//	}
//	
//	@Override
//    public void onDestroy(){
//    	super.onDestroy();
//    	SessionManager.removeListener(recipient);
//	}
}

}
