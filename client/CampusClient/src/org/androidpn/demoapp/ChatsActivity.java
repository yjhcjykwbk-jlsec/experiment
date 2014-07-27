package org.androidpn.demoapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.androidpn.util.Util;
import org.androidpn.util.Xmler;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
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

public class ChatsActivity extends Activity {
	private static String LOGTAG = "ChatsActivity";
	private String USERNAME;
	private String PASSWORD;
	private String recipient = null;// who you are talking with
	private List<User> friendList;
	private Map<String, ChatInfo> latestChats;
	private ChatPacketListener plManager;
	private ChatViewController chatAct = null;
	private Integer viewState = 1;// indicate which view activity currently is
									// in
	ChatsAdapter chatsAdapter;
	boolean inited = false;

	View chatsView = null;
	Map<String, View> chatViews = new HashMap();
	Map<String, List> messageLists = new HashMap();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(LOGTAG, "hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");// used for 8080 connection

		uploadUri=getString(R.string.upload_uri);
		
		friendList = Constants.friendList;
		if (friendList == null)
			getFriend();

		latestChats = SessionManager.cloneLatestChats();
		assert (latestChats != null);
		
		chatAct = new ChatViewController();// handle chat view page

		if (getIntent().getStringExtra("recipient") != null) {
			String recipient = getIntent().getStringExtra("recipient");
			setChatView(recipient);
		} else {
			//SET chats list view
			setChatsView();
		}
		Toast.makeText(this, "chatsActivity start", Toast.LENGTH_SHORT).show();
	}

	/**
	 * called when new intent invoke this activity
	 * and choose which view to set
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(LOGTAG, "onNewIntent");
		if (intent.getStringExtra("recipient") != null) {
			String recipient = intent.getStringExtra("recipient");
			setChatView(recipient);
		} else {
			// SET chats list view  
			setChatsView();
		}
	}

	@Override
	protected void onResume() {
		Toast.makeText(this, "chatsActivity resume", Toast.LENGTH_SHORT).show();
		super.onResume();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && viewState == 2) {
			setChatsView();
			return false;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void setChatsView() {

		Log.i(LOGTAG, "setChatsView setChatsView");
		if (chatsView != null) {
			setContentView(chatsView);
			viewState = 1;
			chatsAdapter.notifyDataSetChanged();
			return;
		}
		chatsView = getLayoutInflater().inflate(R.layout.activity_chats, null);
		setContentView(chatsView);
		viewState = 1;

		// init views on the head
		initHeaderView();

		Log.i(LOGTAG, "setChatsView setListView");
		ListView lv = (ListView) this.findViewById(R.id.ChatListView);

		if (chatsAdapter == null)
			chatsAdapter = new ChatsAdapter(this, latestChats);
		lv.setAdapter(chatsAdapter);

		// ����Ự�б����Ự
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG, "item " + arg2 + " clicked");
				ChatsAdapter adapter = (ChatsAdapter) arg0.getAdapter();
				ChatInfo ci = (ChatInfo) adapter.getItem(arg2);

				String recipient = ci.getRecipient();
				if (recipient == null) {
					Log.e(LOGTAG, "assert lv.setOnclickLIstener failed");
					return;
				}
				setChatView(recipient);
			}
		});
		
		SessionManager.setChatsUiListener(chatsHandler);
	}

	/**
	 *  go to chat view page
	 * @param recipient
	 */
	private void setChatView(String recipient) {
		if (recipient == null)
			return;
		View chatView = null;
		List<ChatInfo> messageList = null;
		if (chatViews.containsKey(recipient)) {
			chatView = chatViews.get(recipient);
		}
		if (messageLists.containsKey(recipient)) {
			messageList = messageLists.get(recipient);
		}
		try {
			chatAct.initView(recipient, messageList, chatView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	/**
	 * init the page's header with three buttons
	 */
	private void initHeaderView() {
		//find user button
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
		//contacts button
		Button contactsBtn = (Button) this.findViewById(R.id.FriendListBtn);
		contactsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (friendList == null) {
					Util.alert(ChatsActivity.this, "ͨѶ¼��ȡʧ�ܣ���������״��");
					return;
				}
				Intent intent = new Intent(ChatsActivity.this,
						ContactActivity.class);
				intent.putExtras(ChatsActivity.this.getIntent().getExtras());
				startActivityForResult(intent, 0);
			}
		});
	}
	
	/**
	 * be called when contactActivity returned with a recipient
	 * and this activity will launch a chat with the recipient 
	 */
	private String uploadUri=null;
	private String imgPath="";
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// requestCode��ʾ����ı�ʾ resultCode��ʾ������
		if (requestCode==0&&resultCode == RESULT_OK) {//���� ѡ����ϵ�˽��жԻ�activity �ķ��ؽ��
			String recipient = data.getStringExtra("recipient");
			if (recipient == null) {
				Log.e(LOGTAG, "onActivityResult: contactActivity return null");
			}
			else setChatView(recipient);
		}
		else if(requestCode==1&&resultCode==RESULT_OK){//���� ѡ��ͼƬ�ϴ�activity �ķ��ؽ��
			Uri imageURI = data.getData();
			Cursor cursor = getContentResolver()
	                   .query(imageURI, null, null, null, null); 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaColumns.DATA); 
	        imgPath=cursor.getString(idx); 
	        showSendPic(imgPath);
		}
	}


	@Override
	protected void onStop() {
		Toast.makeText(this, "chatsActivity has stopped", Toast.LENGTH_SHORT)
				.show();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		SessionManager.removeChatsUiListener();
		SessionManager.removeChatUiListener();
		Toast.makeText(this, "chatsActivity has destroyed", Toast.LENGTH_SHORT)
				.show();
		super.onDestroy();
	}

	private ChatsHandler chatsHandler = new ChatsHandler();

	
	//////////////////////////////////related to chats-view update////////////////////////////////////////
	/**
	 * @author xu zhigang
	 * this handler handles with messages which indicates chats update
	 * for example, a new chat is launched,or some chat has get new messages
	 * and update the chats-view
	 */
	class ChatsHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// ����
			Log.i(LOGTAG, "chatsUIHandler.handleMessage");
			Bundle b = msg.getData();
			final String recipient = b.getString("recipient");
			final ChatInfo ci = new ChatInfo(b.getString("username"),
					b.getString("chatXml"), b.getString("id"),
					b.getBoolean("isSelf"));
			if (!(latestChats != null && recipient != null && ci != null && ci
					.isComplete())) {
				if (latestChats == null) {
					Log.e(LOGTAG, "latestchats null");
				} else if (recipient == null) {
					Log.e(LOGTAG, "recipient null");
				} else if (!ci.isComplete()) {
					Log.e(LOGTAG, "ci not complete");
				}
				Log.e(LOGTAG, "assert ChatsHandler.handleMessage failed");
				return;
			}
			runOnUiThread(new Runnable(){
				public void run(){
					latestChats.put(recipient, ci);
					//if (viewState == 1)
					chatsAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	
	//////////////////////////////related to friends and contacts///////////////////////////////////////////

	/**
	 * alert a find user form window
	 */
	private void alertFindUserForm() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.alert_user_find, null);
		AlertDialog dlg = new AlertDialog.Builder(ChatsActivity.this)
				.setView(layout)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						EditText et = (EditText) layout
								.findViewById(R.id.FindUserEdit);
						findUser(et.getText().toString());
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();
	}

	/**
	 * related to find and add friend
	 */
	@SuppressWarnings("unchecked")
	private void findUser(String s) {
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=getUser"); //
		parameter.append("&username=" + s);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "user.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				if (resp != null) {
					Log.i(LOGTAG,"findUser:"+resp);
					resp = resp.substring(resp.indexOf("\n") + 1);
					resp = resp.replaceAll("\n", "");
					int i = resp.indexOf("<user>"), j;
					if (i < 0 || (j = resp.indexOf("</user>")) < 0) {
						Util.alert(ChatsActivity.this, "δ�ҵ���Ӧ�û�");
						Log.i(LOGTAG, "USER NOT FOUND");
					} else {
						String str = resp.substring(i, j + 7);
						Log.i(LOGTAG, "user :" + str);
						Xmler.getInstance().alias("user", User.class);
						User u = (User) Xmler.getInstance().fromXML(str);

						if (u == null) {
							Log.i(LOGTAG, "user not valid");
							Util.alert(ChatsActivity.this, "�û���Ч");
						}
						Log.i(LOGTAG, "USER FOUND:" + u.getName());
						displayUser(u);
					}
				}
			}
		}.execute(parameter);
	}
	/**
	 * add friend send a add-friend request to server in a asynchronous way
	 */
	@SuppressWarnings("unchecked")
	private void addFriend(String userId) {
		new AsyncTask<String, Integer, String>() {
			@Override
			protected String doInBackground(String... userId) {
				String androidpnURL = getString(R.string.androidpnserver);
				String params = "action=addFriend&id2=" + userId[0]
						+ "&username1=" + USERNAME;
				String resp = GetPostUtil.sendPost(androidpnURL + "user.xml",
						params);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				Log.i(LOGTAG, "addfriend.onclick:" + resp);
				String status = getXmlElement(resp, "status");
				String reason = getXmlElement(resp, "reason");
				if (status == null) {
					Util.alert(ChatsActivity.this, "���ʧ��:"
							+ (reason == null ? "" : reason));
				} else if (status.equals("1")) {
					Util.alert(ChatsActivity.this, "��ӹ�ע�ɹ�");
				} else {
					Util.alert(ChatsActivity.this, "���������Ѿ��Ǻ�����");
					getFriend();
				}
			}
		}.execute(userId);
	}

	/**
	 * do pull friend List asynchronously
	 */
	@SuppressWarnings("unchecked")
	private void getFriend() {
		new AsyncTask<Object, Integer, String>() {
			@Override
			protected String doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				String androidpnURL = getString(R.string.androidpnserver);
				String params = "action=listFriend&username=" + USERNAME;
				String resp = GetPostUtil.sendPost(androidpnURL + "user.do",
						params);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				Log.i(LOGTAG,"getFriend:"+resp);
				if (!"succeed".equals(getXmlElement(resp, "result"))) {
					String reason = getXmlElement(resp, "reason");
					Util.alert(ChatsActivity.this, "��ȡͨѶ¼ʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}

				int i = resp.indexOf("<list>"), j;
				if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
					// UIUtil.alert(ChatsActivity.this,"����û�к���");//"</list>"
					friendList = Constants.friendList = new ArrayList();
				} else {
					String str = resp.substring(i, j + 7);
					Xmler.getInstance().alias("user", User.class);
					List<User> list = (List) Xmler.getInstance().fromXML(str);

					if (list == null) {
						Util.alert(ChatsActivity.this, "û���ҵ�����");
					}
					friendList = Constants.friendList = list;
					// UIUtil.alert(ChatsActivity.this,"ͨѶ¼�Ѿ�ͬ��");
				}
			}
		}.execute();
	}

	/**
	 * display a user in a alert window
	 */
	private void displayUser(final User u) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.alert_user_info, null);
		Log.i(LOGTAG, "display user:" + Xmler.getInstance().toXML(u));
		((TextView) layout.findViewById(R.id.UsernameLabel)).setText(u
				.getName());
		String idStr=u.getId()+"";
		if(idStr.length()>10) idStr=idStr.substring(0,6)+"...";
		((TextView) layout.findViewById(R.id.UserIDLabel)).setText(idStr);
		String email=u.getEmail();
		if(email.length()>10) email=email.substring(0,6)+"...";
		((TextView) layout.findViewById(R.id.UserEmailLabel)).setText(email);
		((ImageView) layout.findViewById(R.id.UserPhotoLabel))
				.setImageDrawable(getResources().getDrawable(
						Util.getPhoto(u.getName())));
		TextView foLink = (TextView) layout.findViewById(R.id.ChatWithBtn);
		foLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChatsActivity.this,
						ChatsActivity.class);
				Bundle bundle = ChatsActivity.this.getIntent().getExtras();
				bundle.putString("recipient", u.getName());
				intent.putExtras(bundle);
				startActivity(intent);
				Log.i(LOGTAG, "FRIEND ADDED");
			}
		});
		new AlertDialog.Builder(ChatsActivity.this)
				.setView(layout)
				.setPositiveButton("��ӹ�ע",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								final String s = u.getId() + "";
								addFriend(s);
							}
						})
				.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).show();

	}

	private String getXmlElement(String resp, String tag) {
		if (resp == null || tag == null)
			return null;
		int i = resp.indexOf("<" + tag + ">"), j;
		if (i < 0 || (j = resp.indexOf("</" + tag + ">")) < 0) {
			return null;
		}
		return resp.substring(i + tag.length() + 2, j);
	}
	
	///////////////////////////////////////////////////////send related/////////////////////////////////////
	/**
	 * @param to : the recipient of this chat message
	 * @param content : chat content
	 */
	private void handleSendMsg(String to,String content){
		org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message(
				to, org.jivesoftware.smack.packet.Message.Type.chat);
		msg.setBody(content);
		// send msg
		ChatInfo ci = new ChatInfo(
				(ChatsActivity.this).recipient, msg.getBody(),
				new Date(System.currentTimeMillis()), msg
						.getPacketID(), true);
		// ������Ϣ��ӵ����Ͷ���
		addMsg(msg);
		// ͬ����SessionManager
		SessionManager.addMsg((ChatsActivity.this).recipient,
				ci);
	}
	
	 /**
	  *  shows the photo to send in ImageView 
	  */
    private void showSendPic(String imgPath) {
    	ImageView mImageView=(ImageView) ChatsActivity.this.findViewById(R.id.SendImgView);
    	if (mImageView == null) {
    		Log.e("setPic", "mImageView is null");
    		return;
    	}
    	if (imgPath == null) {
    		Log.e("setPic", "imgPath is null");
    		return;
    	}
    	
    	mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        
        Log.e("ImageView size", "width: " + targetW + " height: " + targetH);
        
        if (targetW == 0 || targetH == 0) {
        	Log.e("setPic", "width or height is zero");
        	return;
        }
        
        // ��ȡͼƬ�Ĵ�С
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.i("setPic", "bmOptions.outWidth: " + bmOptions.outWidth);
        Log.i("setPic", "bmOptions.outHeight: " + bmOptions.outHeight);
        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);
        Log.i("scaleFactor", "scale: " + scaleFactor);
        
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;       
      
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
	
	///////////////////////////////////related to launch a chat, init chat-view and so on////////////////////////
	/**
	 * this class handles with chat pageView
	 */
	class ChatViewController {
		private String LOGTAG = "chatActivity";

		// private List<ChatInfo> messageList;
		private XmppManager xmppManager = Constants.xmppManager;

		/**
		 * should be called only once
		 */
		private ChatViewController() {
			recipient = USERNAME;
			initSendThread();
			SessionManager.setChatUiListener(chatHandler);
		}

		private void initSendThread() {
			packetList = Constants.packetList;
			if (packetList == null)
				packetList = Constants.packetList = new ArrayList<Pair>();
			if (smThread == null )
				smThread = new SendMsgThread(xmppManager);
			if(!smThread.isAlive()) {
				Log.i(LOGTAG, "smThread (re)start");
				smThread.start();
			}
		}

		
		/**
		 * set UI to chat pageView
		 */
		public void initView(String recipient, List messageList, View chatView)
				throws Exception {
			Log.i(LOGTAG, "chatviewController.initview");

			ChatsActivity.this.recipient = recipient;
			if (chatView != null) {
				setContentView(chatView);
				viewState = 2;
				return;
			}

			chatView = getLayoutInflater()
					.inflate(R.layout.activity_chat, null);
			setContentView(chatView);
			chatViews.put(recipient, chatView);
			viewState = 2;

			Button sendBtn = (Button) (ChatsActivity.this)
					.findViewById(R.id.SendBtn);
			Button galleryBtn=(Button) (ChatsActivity.this).
					findViewById(R.id.GalleryBtn);
			TextView tv = (TextView) (ChatsActivity.this)
					.findViewById(R.id.ChatTitleLabel);
			tv.setText(USERNAME + "��" + recipient + "�Ự��");

			// messages stores the talk contents with friends
			if (messageList == null) {
				messageList = SessionManager.cloneMsgList(recipient);
				if (messageList == null)
					throw new Exception(
							"initView:messageList null and creation failed");
				messageLists.put(recipient, messageList);
			}
		
			ListView mListView = (ListView) (ChatsActivity.this)
					.findViewById(R.id.MessageListView);
			BaseAdapter chatAdapter = new ChatInfoAdapter((ChatsActivity.this),
					messageList,mListView);
			mListView.setAdapter(chatAdapter);
			chatAdapter.notifyDataSetInvalidated();

			// ������Ϣ
			sendBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if(imgPath==null||imgPath.equals("")){//û��ͼƬ�ϴ�
						EditText mTextMessage = (EditText) (ChatsActivity.this)
								.findViewById(R.id.MessageEdit);
						String to = (ChatsActivity.this).recipient;
						String text = mTextMessage.getText().toString();
						Log.i(LOGTAG,
								"XMPPChatActivity#send.onclicklistener Sending text "
										+ text + " to " + to);
						handleSendMsg(to,text);
						mTextMessage.setText("");
					}else{
						String to=(ChatsActivity.this).recipient;
						String tmpPath=imgPath;
						imgPath=""; //��λ
						new UploadImgTask(to,tmpPath,uploadUri).execute("");
						showSendPic("");
						
					}
				}
			});
			//���ͼƬ����
			galleryBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
			        intent.setType("image/*");
			        intent.setAction(Intent.ACTION_GET_CONTENT);
			        intent.addCategory(Intent.CATEGORY_OPENABLE);
			        startActivityForResult(Intent.createChooser(intent, "ѡ��һ��ͼƬ"), 1);
				}
			});
		}

		//this handler handles with chat-view updating
		@SuppressWarnings("unchecked")
		private ChatHandler chatHandler = new ChatHandler();

	}

	
	///////////////////////////////////////////////related to send message thread///////////////////////////
	/**
	 * this list stores the packets to send
	 * send-message thread always try to get packet in this list and send it
	 */
	private List<Pair> packetList;

	@SuppressWarnings("unchecked")
	public void addMsg(Packet msg) {
		synchronized (packetList) {
			packetList.add(new Pair(msg, false));
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
					if (packetList.isEmpty())
						continue;
					packet = (Packet) packetList.get(0).first;
					// packetList.remove(0);��//we may not need to remove it now,
					// we
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
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.chat, menu);
	// return true;
	// }
	//
	// @Override
	// public void onDestroy(){
	// super.onDestroy();
	// SessionManager.removeListener(recipient);
	// }

	
	
	/////////////////////////////////////////////////////////related to chat-View update////////////////////////////
	/**
	 * @author xu zhigang
	 * this handler handle with messages which indicate new message sending or sent or received
	 * and update the chat-view
	 */
	class ChatHandler extends Handler {
		// �յ���Ϣ������Ϣ���ͳɹ���Ϣ���߷�����Ϣ
		// ͬ����ǰ����ĸ�������view�е����ݣ���Ҫʱ���µ�ǰview��ui
		@Override
		public void handleMessage(android.os.Message msg) {

			Log.d("chatHandler", "handleMessage......");
	
			Bundle b = msg.getData();
			final String theRecipient = b.getString("recipient");
			if (theRecipient == null) {
				Util.alert(ChatsActivity.this, "������Ч��Ϣ���Ự��Ϊ��");
				return;
			}
			if (!messageLists.containsKey(theRecipient)
					|| !chatViews.containsKey(theRecipient)) {
				Log.e(LOGTAG, "handleMessage:messageList or chatView not found");
				return;
			}
	
			final ChatInfoAdapter chatAdapter = (ChatInfoAdapter) ((ListView) (chatViews
					.get(theRecipient).findViewById(R.id.MessageListView)))
					.getAdapter();
			final List<ChatInfo> messageList = messageLists.get(theRecipient);
	
			switch (msg.what) {
			case 1:// msg recv or send
				final ChatInfo ci = new ChatInfo(b.getString("username"),
						b.getString("chatXml"), b.getString("id"),
						b.getBoolean("isSelf"));
				if (!ci.isComplete()) {
					Util.alert(ChatsActivity.this, "������������Ϣ");
					return;
				}
				if (!ci.isSelf()) {// &&!theRecipient.equals(recipient)){
					// �Թ㲥��ʽ֪ͨ�û����µ�������Ϣ����
					Intent intent = new Intent(Constants.ACTION_SHOW_CHAT);
					intent.putExtra("recipient", ci.getName());
					intent.putExtra("chatXml", ci.getContent());
					ChatsActivity.this.sendBroadcast(intent);
				}
	
				// �ûỰ�Ѿ���������Ϣ���У�����Ϣ��ӵ��������
				if (messageList != null) {
					// �ûỰ�ڵ�ǰui��
					if (theRecipient.equals(recipient)) {
						runOnUiThread(new Runnable(){
							public void run(){
								messageList.add(ci);
								chatAdapter.notifyDataSetChanged();
								if (!ci.isSelf()) {
									Util.alert(ChatsActivity.this, "new msg recved");
								} 
							}
						});
					}
					else messageList.add(ci);
				}
				return;
	
			case 2:// sent
				String packetID = b.getString("id");
				// �ûỰ�Ѿ���������Ϣ���У����¸���Ϣ״̬Ϊ�ѷ���
				if (messageList != null) {
					for (final ChatInfo ci_1 : messageList) {
						if (ci_1.getPacketID().equals(packetID)) {
							if (theRecipient.equals(recipient)) {
								runOnUiThread(new Runnable(){
									public void run(){
										ci_1.setSent();
										chatAdapter.notifyDataSetChanged();
									}
								});
							}
							else ci_1.setSent();
							return;
						}
					}
				}
			}
		}
	}
	
	/////////////////////////////////////related to upload a image to server and get a url back
	
	/**
	 * @author x
	 * this class is similar with submitActivity.submit
	 */
	class UploadImgTask extends AsyncTask<String,Integer,String>{
		int serverResponseCode = 0;     	
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String imgPath=null;
        String uploadServerUri=null;
        String recipient=null;
        private ProgressDialog dialog = null;
        public UploadImgTask(String to,String imgPath,String uploadUri){
        	this.imgPath=imgPath;
			this.uploadServerUri=uploadUri;
			this.recipient=to;
        }
        @Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(ChatsActivity.this);
			dialog.setTitle("�����ϴ�...");
	//		dialog.setMessage("0k/"+totalSize/1000+"k");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.show();
		}
        @Override
		protected void onProgressUpdate(Integer... progress) {
			dialog.setProgress(progress[0]);
		//	dialog.setMessage(progress[1]/1000+"k/"+totalSize/1000+"k");
		}
        protected String doInBackground(String... args) {
			int bytesRead, bytesAvailable, bufferSize;
			int maxBufferSize = 1 * 20 * 1024; //20kb
			byte[] buffer;	
			long length = 0;
			int progress;long totalSize;
			if(imgPath==null) return null;
			File sourceFile = new File(imgPath);
			if (!sourceFile.isFile()) {
		         Log.e("uploadFile", "ͼƬ�ļ�������");
		         return null;
			} 
			else totalSize = sourceFile.length();

			URL url;
			try {
				url = new URL(uploadServerUri);
				
				Log.i("uploadFile", "��url����");
				Log.i("XMPPChat","upload uri:"+url.getHost()+url.getPath());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // ��HTTP����
				// ����ÿһ��post���С��128kB����������ã����ļ��ϴ������ɹ�����
				conn.setChunkedStreamingMode(128*1024); 
				  
				conn.setConnectTimeout(15000); // 15����û��Ӧ�ͶϿ����� 
				conn.setReadTimeout(10000); 
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				  
				FileInputStream fileInputStream = new FileInputStream(sourceFile);     
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				
				//dosͳͳ��Ҫ��writeBytes�ˣ���Ϊ�ϴ�����ͼƬ������ı���Ϣ�������˾ͽ�������ͼƬ�ˣ�
				String lineEnd = "\n";
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"upfile\";filename=\""+ imgPath + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
			    do{
			    	// read file and write it into form...
			    	bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
			    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
			    	buffer = new byte[bufferSize];
				  
			    	bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
			    	dos.write(buffer, 0, bytesRead);	
			    	length+=bytesRead;
			    	Thread.sleep(200);
			    	progress = (int)((length*100)/totalSize);
			    	System.out.println("�ϴ�����length��"+length+"; progress:"+progress);
				    publishProgress(progress,(int)length);
				  }  
				while (bytesRead > 0) ;
			    // send multipart form data necesssary after file data...        
			    dos.writeBytes(lineEnd);
			    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
  
			    serverResponseCode = conn.getResponseCode();
			    BufferedReader in = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line;String resp="";
				while ((line = in.readLine()) != null)
				{
					resp += line;
				}
				return resp;
			}
			catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
        @Override
        protected void onPostExecute(String resp){
        	 Log.i(LOGTAG,"UPLOAD RESULT:"+resp);
        	 dialog.dismiss();      
        	 if(resp==null) Util.alert(ChatsActivity.this, "����ͼƬʧ��");
        	 else handleSendMsg(recipient,"<img>"+resp+"</img>");
        }
		
	}
}
