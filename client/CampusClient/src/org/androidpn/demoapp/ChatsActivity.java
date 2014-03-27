package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidpn.client.ChatPacketListener;
import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.androidpn.data.ChatInfoAdapter;
import org.androidpn.data.ChatsAdapter;
import org.androidpn.data.ChatInfo;
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
import android.os.AsyncTask;
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
    private ChatPacketListener plManager;
	private ChatViewController chatAct=null;
	private Integer viewState=1;//indicate which view activity currently is in
	ChatsAdapter chatsAdapter;
	boolean inited=false;
	
	View chatsView=null;
	Map <String,View> chatViews=new HashMap();
	Map <String,List> messageLists=new HashMap();
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
		
		chatAct=new ChatViewController();//handle chat view page
		
		if(getIntent().getStringExtra("recipient")!=null){
			String recipient=getIntent().getStringExtra("recipient");
			setChatView(recipient);
		}else{
			//chats list view ui
			setChatsView();
		}
		
		//this add listener on XMPPManager for once
		//no more need: it is done by xmpp-manager on its create
		//plManager.listen();
		
		Toast.makeText(this, "chatsActivity start",Toast.LENGTH_SHORT).show();
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
	protected void onResume(){
		Toast.makeText(this, "chatsActivity resume",Toast.LENGTH_SHORT).show();
		super.onResume();
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
		
		//点击会话列表进入会话
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				ChatsAdapter adapter=(ChatsAdapter) arg0.getAdapter();
				ChatInfo ci=(ChatInfo) adapter.getItem(arg2);
				
				String recipient=ci.getRecipient();
				if(recipient==null){
					Log.e(LOGTAG,"assert lv.setOnclickLIstener failed");
					return;
				}
				setChatView(recipient);
			}
		});
		
		Log.i(LOGTAG,"setChatsView set chats listener");
		SessionManager.setChatsUiListener(chatsHandler);
		 
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
				if(friendList==null) {
					UIUtil.alert(ChatsActivity.this, "通讯录拉取失败，请检查网络状况");
					return;
				}
				Intent intent=new Intent(ChatsActivity.this,ContactActivity.class);
				intent.putExtras(ChatsActivity.this.getIntent().getExtras());
				startActivityForResult(intent,0);
			}
		});
	}
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        // TODO Auto-generated method stub  
	        super.onActivityResult(requestCode, resultCode, data);  
	        //requestCode标示请求的标示   resultCode表示有数据  
	        if (resultCode == RESULT_OK) {  
	            String recipient=data.getStringExtra("recipient") ;
	            if(recipient==null){
	            	Log.e(LOGTAG,"onActivityResult: contactActivity return null");
	            }
	            setChatView(recipient);
	        }  
	    }  
	@Override
	protected void onStop(){
		 Toast.makeText(this, "chatsActivity has stopped",Toast.LENGTH_SHORT).show();
		super.onStop();
	}
	@Override
	protected void onDestroy(){
		SessionManager.removeChatsUiListener();
		SessionManager.removeChatUiListener();
		Toast.makeText(this, "chatsActivity has destroyed",Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	private ChatsHandler chatsHandler=new ChatsHandler();
	class ChatsHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			//更新
			Log.i(LOGTAG,"chatsUIHandler.handleMessage");
			Bundle b=msg.getData();
			String recipient=b.getString("recipient");
			ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),b.getString("id"),b.getBoolean("isSelf"));
			if(!(latestChats!=null&&recipient!=null&&ci!=null&&ci.isComplete())){
				if(latestChats==null){
					Log.e(LOGTAG,"latestchats null");
				}else if(recipient==null){
					Log.e(LOGTAG,"recipient null");
				}else if(!ci.isComplete()){
					Log.e(LOGTAG,"ci not complete");
				}
				Log.e(LOGTAG,"assert ChatsHandler.handleMessage failed");
				return;
			}
			latestChats.put(recipient, ci);
			if(viewState==1)
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
	@SuppressWarnings("unchecked")
	private void findUser(String s) {
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=getUser"); //
		parameter.append("&username=" + s);
		new AsyncTask<StringBuilder,Integer,String>(){
			@Override
			protected String doInBackground(StringBuilder...  parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST", 
						getString(R.string.androidpnserver) + "user.xml",
							parameter[0]);
				return resp;
			}
			@Override
			protected void onPostExecute(String resp){
				if (resp != null) {
					resp = resp.substring(resp.indexOf("\n") + 1);
					resp = resp.replaceAll("\n", "");
					int i = resp.indexOf("<user>"), j;
					if (i < 0 || (j = resp.indexOf("</user>")) < 0) {
						UIUtil.alert(ChatsActivity.this,"未找到相应用户");
						Log.i(LOGTAG, "USER NOT FOUND");
					} else {
						String str = resp.substring(i, j + 7);
						Log.i(LOGTAG, "user :" + str);
						Xmler.getInstance().alias("user", User.class);
						User u = (User) Xmler.getInstance().fromXML(str);
		
						if (u == null) {
							Log.i(LOGTAG, "user not valid");
							UIUtil.alert(ChatsActivity.this,"用户无效");
						}
						Log.i(LOGTAG, "USER FOUND:" + u.getName());
						displayUser(u);
					}
				}
			}
		}.execute(parameter);
	}
	
	/*
	 * add friend
	 * send a add-friend request to server in a asynchronous way
	 */
	@SuppressWarnings("unchecked")
	private void addFriend(String userId){
		new AsyncTask<String,Integer,String>(){
			@Override
			protected String doInBackground(String... userId) {
				String androidpnURL = getString(R.string.androidpnserver);
				String params="action=addFriend&id2="+userId[0]+"&username1="+USERNAME;
				String resp=GetPostUtil.sendPost(androidpnURL+
						"user.xml",params);
				return resp;
			}
			@Override
			protected void onPostExecute(String resp){
				Log.i(LOGTAG,"addfriend.onclick:"+resp);
				String status=getXmlElement(resp,"status");
				String reason=getXmlElement(resp,"reason");
				if(status==null){
					UIUtil.alert(ChatsActivity.this,"添加失败:"+(reason==null?"":reason));
				}else if(status.equals("1")){
					UIUtil.alert(ChatsActivity.this,"添加关注成功");
				}else{
					UIUtil.alert(ChatsActivity.this,"你们现在已经是好友了");
					getFriend();
				}
			}
		}.execute(userId);
	}
	/*
	 * do pull friend List asynchronously
	 */
	@SuppressWarnings("unchecked")
	private void getFriend(){
		new AsyncTask<Object,Integer,String>(){
			@Override
			protected String doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				String androidpnURL = getString(R.string.androidpnserver);
				String params="action=listFriend&username="+USERNAME;
				String resp=GetPostUtil.sendPost(androidpnURL+
						"user.xml",params);
				return resp;
			}
			@Override
			protected void onPostExecute(String resp){
				if(!"succeed".equals(getXmlElement(resp,"result"))){
					String reason=getXmlElement(resp,"reason");
					UIUtil.alert(ChatsActivity.this,"拉取通讯录失败:"+(reason==null?"":reason));
					return;
				}
				
				int i = resp.indexOf("<list>"), j;
				if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
					//UIUtil.alert(ChatsActivity.this,"您还没有好友");//"</list>"
					friendList=Constants.friendList=new ArrayList();
				} else {
					String str = resp.substring(i, j + 7);
					Xmler.getInstance().alias("user", User.class);
					List<User> list = (List) Xmler.getInstance().fromXML(str);

					if (list == null) {
						UIUtil.alert(ChatsActivity.this,"没有找到好友");
					}
					friendList=Constants.friendList=list;
					//UIUtil.alert(ChatsActivity.this,"通讯录已经同步");
				}
			}
		}.execute();
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
			SessionManager.setChatUiListener(chatHandler);
		}
		private void initSendThread(){
			packetList=Constants.packetList;
			if(packetList==null)
				packetList = Constants.packetList=new ArrayList<Pair>();
			if(smThread==null||!smThread.isAlive()){
				Log.i(LOGTAG,"smThread (re)start");
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
						
			//发送消息
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
						//聊天消息添加到发送队列
						addMsg(msg);
						//同步到SessionManager
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
		private ChatHandler chatHandler=new ChatHandler();
		class ChatHandler extends Handler {
	         //收到消息或者消息发送成功消息或者发送消息
			 //同步当前保存的各个聊天view中的数据，必要时更新当前view的ui
	         @Override
	         public void handleMessage(android.os.Message msg) {
	             Log.d("chatHandler", "handleMessage......");
	             if(!messageLists.containsKey(recipient)||!chatViews.containsKey(recipient)){
            		Log.e(LOGTAG,"handleMessage:messageList or chatView not found");
            		return;
	             }
          	
            	 ChatInfoAdapter chatAdapter=(ChatInfoAdapter) ((ListView)(chatViews.get(recipient).findViewById(R.id.MessageListView))).getAdapter();
            	 Bundle b = msg.getData();
            	 String theRecipient=b.getString("recipient");
            	 List<ChatInfo> messageList=messageLists.get(recipient);
            	 
            	 if(theRecipient==null){
            		 UIUtil.alert(ChatsActivity.this,"处理到无效消息，会话方为空");
            		 return;
            	 }
	             switch(msg.what){
	             case 1://msg recv or send
	            	ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),
	        	             b.getString("id"),b.getBoolean("isSelf"));
	            	if(!ci.isComplete()){
	            		 UIUtil.alert(ChatsActivity.this,"处理到不完整消息");
	            		 return;
	            	}
            		if(!ci.isSelf()&&!theRecipient.equals(recipient)){
            			//以广播方式通知用户有新的聊天消息到来
            			Intent intent = new Intent(Constants.ACTION_SHOW_CHAT);
            			intent.putExtra("recipient", b.getString("username"));
            			sendBroadcast(intent);
            		}
            		
        			//该会话已经有聊天消息队列，将消息添加到聊天队列
        			if(messageList!=null){
        				messageList.add(ci);
        			}

            		//该会话在当前ui上
	            	if(theRecipient.equals(recipient)){
		            	chatAdapter.notifyDataSetChanged();
		            	UIUtil.alert(ChatsActivity.this,"new msg recved or send");
	            	}
	            	return;
	            	
	             case 2://sent
					String packetID=b.getString("id");
        			//该会话已经有聊天消息队列，更新该消息状态为已发送
        			if(messageList!=null){
        				for(ChatInfo ci_1 : messageList){
        					if(ci_1.getPacketID().equals(packetID)){
        						ci_1.setSent();
        						if(theRecipient.equals(recipient)){
        							chatAdapter.notifyDataSetChanged();
    		    	    			UIUtil.alert(ChatsActivity.this,"new msg sent");
        						}
        						return;
        					}
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
