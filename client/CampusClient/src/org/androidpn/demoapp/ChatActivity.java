package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.androidpn.data.ChatInfo;
import org.androidpn.data.ChatInfoAdapter;
import org.androidpn.data.SessionManager;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.UIUtil;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
	private Handler mHandler = new Handler();
	private EditText mTextMessage;
	private ListView mListView;
	private List<User> friendList;
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
		
		friendList=Constants.friendList;
		if(friendList==null) getFriend();		
 	
		//init views
		initHeaderView();
		
		TextView tv=(TextView)this.findViewById(R.id.ChatTitleLabel);
		tv.setText("与"+recipient+"会话中");
		
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
				Message msg = new Message("abc", Message.Type.chat);
				msg.setBody(text);
				if (xmppManager == null) {
					Log.i(LOGTAG,
							"XMPPChatActivity#send.onclicklistener xmppmanager is null");
					return;
				} else {
					//send msg
					ChatActivity.this.addMsgView(new ChatInfo(USERNAME, msg.getBody(),
						new Date(System.currentTimeMillis()),msg.getPacketID(),true));
					addMsg(msg);
				}
				mTextMessage.setText("");
			}
		});
		
		
		smThread = new SendMsgThread(xmppManager);
		smThread.start();
	}
	
	/*
	 * init the page's chat-message-list
	 */
	@SuppressWarnings("unchecked")
	private void initMsgListView(){
		// messages stores the talk contents with friends
//		messages = (List<ChatInfo>) SessionManager.getMsgList(recipient);
		// ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,messages);
		mAdapter = new ChatInfoAdapter(this,SessionManager.getMsgList(recipient));
		mListView = (ListView) this.findViewById(R.id.MessageListView);
		mListView.setAdapter(mAdapter);
		SessionManager.addListener(recipient, mAdapter);
		
		addMsgView(new ChatInfo("test", "helloworld", new Date(), "1", true));
	}
	/*
	 * add a message to the list view
	 */
	private void addMsgView(ChatInfo ci){
		Log.i(LOGTAG,"add Msg view "+ci.getContent());
//		messages.add(ci);
//		mAdapter.notifyDataSetChanged(); 
		try {
			SessionManager.addMsg(recipient, ci);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChatActivity.this,
						DemoAppActivity.class);
				startActivity(intent);
			}
		});
		Button contactsBtn=(Button) this.findViewById(R.id.FriendListBtn);
		contactsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ChatActivity.this,ContactActivity.class);
				intent.putExtras(ChatActivity.this.getIntent().getExtras());
				startActivity(intent);
			}
		});
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
				UIUtil.alert(ChatActivity.this,"未找到相应用户");
				Log.i(LOGTAG, "USER NOT FOUND");
				return;
			} else {
				String str = resp.substring(i, j + 7);
				Log.i(LOGTAG, "user :" + str);
				Xmler.getInstance().alias("user", User.class);
				User u = (User) Xmler.getInstance().fromXML(str);

				if (u == null) {
					Log.i(LOGTAG, "user not valid");
					UIUtil.alert(ChatActivity.this,"用户无效");
					return;
				}
				Log.i(LOGTAG, "USER FOUND:" + u.getName());
				displayUser(u);
			}
		}
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
				Intent intent=new Intent(ChatActivity.this,ChatActivity.class);
				Bundle bundle=ChatActivity.this.getIntent().getExtras();
				bundle.putString("recipient", u.getName());
				intent.putExtras(bundle);
				startActivity(intent);
				Log.i(LOGTAG, "FRIEND ADDED");
			}
		});
		new AlertDialog.Builder(ChatActivity.this).setView(layout)
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
	
	
	/*
	 * alert a find user form window
	 */
	private void alertFindUserForm() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.alert_user_find, null);
		AlertDialog dlg = new AlertDialog.Builder(ChatActivity.this).setView(layout)
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
					UIUtil.alert(ChatActivity.this,"添加失败:"+(reason==null?"":reason));
				}else if(status=="1"){
					UIUtil.alert(ChatActivity.this,"添加关注成功");
				}else{
					UIUtil.alert(ChatActivity.this,"你们现在已经是好友了");
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
					UIUtil.alert(ChatActivity.this,resp);
					int i = resp.indexOf("<list>"), j;
					if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
						UIUtil.alert(ChatActivity.this,"未找到相应用户");
						Log.i(LOGTAG, "USER NOT FOUND");
						return;
					} else {
						String str = resp.substring(i, j + 7);
						Log.i(LOGTAG, "list :" + str);
						Xmler.getInstance().alias("user", User.class);
						List<User> list = (List) Xmler.getInstance().fromXML(str);

						if (list == null) {
							Log.i(LOGTAG, "friendlist invalid");
							UIUtil.alert(ChatActivity.this,"没有找到好友");
							return;
						}
						friendList=Constants.friendList=list;
						UIUtil.alert(ChatActivity.this,"通讯录已经同步");
					}
				}else{
					String reason=getXmlElement(resp,"reason");
					UIUtil.alert(ChatActivity.this,"拉取好友列表失败:"+(reason==null?"":reason));
				}
			}
		}.run();
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
					packet = (Packet) packetList.get(0).first;
				}
				// packetList.remove(0);　//we may not need to remove it now, we
				// can remove when it's sent
				Log.i(LOGTAG, "XmppManager#sendMsgTask packet in sending:"
						+ packet.toXML());
				xmppManager.sendMsg(packet);

				Log.i(LOGTAG,
						"XmppManager#sendMsgTask waiting for packet to be sent");
				Log.i(LOGTAG, "XmppManager#sendMsgTask !!!!packet sent");
				synchronized (packetList) {
					packetList.remove(0);
				}
			}
		}

	}

	/*
	 * related to get roster(friend list) thread
	 */
	public class InitTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(LOGTAG, "init task running");
			Roster rost = xmppManager.getConnection().getRoster();
			if (rost == null) {
				Log.d(LOGTAG, "roster null");
				return;
			}
			Log.i(LOGTAG, "roster:" + rost.toString());
			Collection<RosterEntry> entries = rost.getEntries();
			for (RosterEntry entry : entries) {
				User u = new User();
				u.setUsername(entry.getUser());
				Log.i(LOGTAG, "roster item:" + entry.getUser());
				friendList.add(new User());
			}
			Log.i(LOGTAG, "init task finished");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	public void onDestroy(){
		super.onDestroy();
		SessionManager.removeListener(recipient);
	}
}
