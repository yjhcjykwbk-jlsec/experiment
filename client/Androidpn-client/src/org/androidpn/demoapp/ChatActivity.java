package org.androidpn.demoapp;
import java.io.DataOutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.NotificationService;
import org.androidpn.client.XmppManager;
import org.androidpn.server.model.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;
import android.widget.EditText;
public class ChatActivity extends Activity {
	private String LOGTAG ="chatActivity";
	List<String> messages;
	private String USERNAME;
	private String PASSWORD;
	private String recipient="";//who you are talking with
	private Handler mHandler = new Handler();
	private EditText mTextMessage;
	private ListView mListView;
	private List<User> mFriendList;
	private XmppManager xmppManager;
	private Map<String,View> viewList;//store the chat view
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Log.i(LOGTAG,"hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");//used for 8080 connection
		
		xmppManager = Constants.xmppManager;//if it is null, this will be a trouble
		
		packetList= new ArrayList<Packet>();
		
		//messages stores the talk contents with friends
		messages=new ArrayList<String>();
		messages.add("helloworld");
		
		ArrayAdapter mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,messages);
		viewList=new HashMap<String,View>();
		
		//chatUri=getString(R.string.androidpnserver+"/send.xml?"); this is too slow for chat
		mTextMessage = (EditText)this.findViewById(R.id.MessageEdit);
		
		mListView = (ListView) this.findViewById(R.id.MessageList);
		mListView .setAdapter(mAdapter);
		
		mFriendList=new ArrayList();
		
		smThread=new SendMsgThread(xmppManager);
		smThread.start();
		
		// Set a listener to send a chat text message
		Button sendBtn = (Button) this.findViewById(R.id.SendBtn);
		Button homeBtn = (Button) this.findViewById(R.id.HomeBtn);
		Button findBtn = (Button) this.findViewById(R.id.FindUserBtn);
		
		findBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText findText = (EditText) ChatActivity.this.findViewById(R.id.FindUserEdit);
				String s=findText.getText().toString();
				findUser(s);
			}
		});
		homeBtn.setBackgroundColor(Color.WHITE);
		homeBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ChatActivity.this,DemoAppActivity.class);
				startActivity(intent);
			}
		});
		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String to = recipient;
				if(to==null){//先发送给自己　做测试
					to="abc";
				}
				String text = mTextMessage.getText().toString();

				Log.i(LOGTAG,"XMPPChatActivity#send.onclicklistener Sending text " + text + " to " + to);
				Message msg = new Message("push", Message.Type.chat);
				msg.setBody(text);		
				if(xmppManager==null) {
					Log.i(LOGTAG,"XMPPChatActivity#send.onclicklistener xmppmanager is null");
					return;
				}
				else {
					//显示出来
/*					TextView v=new TextView(ChatActivity.this);
					v.setBackgroundColor(Color.BLUE);
					v.setText(msg.getBody());*/
					messages.add(msg.getBody());
					//mListView.getAdapter().get
					//mListView.addView(v);
					//添加到索引
//					viewList.put(msg.getPacketID(), v);
					//添加到发送队列
					addMsg(msg);
				}
				mTextMessage.setText("");
			}
		});
		
		xmppManager.addPacketListener(new PacketListener(){
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				if(packet instanceof Message){
					//显示接收到的消息
					/*TextView v=new TextView(ChatActivity.this);
					v.setText(((Message)packet).getBody());
					mListView.addView(v);
					//添加到索引
					viewList.put(packet.getPacketID(),v);*/
					messages.add(((Message)packet).getBody());
				}
				else if(packet instanceof IQ){
					if(((IQ)packet).getType()==IQ.Type.RESULT){
						if(((IQ)packet).getError()==null){
							//发送的消息成功被服务器接收
							/*TextView v=(TextView)(viewList.get(packet.getPacketID()));*/
//							if(v!=null)
//								v.setBackgroundColor(Color.WHITE);
							Log.i(LOGTAG,"recv a result IQ whose id is :"+packet.getPacketID());
						}
					}
				}
			}
			
		}, new PacketFilter(){
			@Override
			public boolean accept(Packet packet) {
				// TODO Auto-generated method stub
				return packet.getPacketID()!=null;
			}
		});
		new InitTask().run();
		
	}
	
	
	
  private XmlPullParser resetParser(String s) {
        try {
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(new StringReader(s));
            return parser;
        }
        catch (XmlPullParserException xppe) {
            xppe.printStackTrace();
            return null;
        }
    }
	private void findUser(String s){
		String androidpnURL = getString(R.string.androidpnserver);
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=getUser"); //
		parameter.append("&username="+s);
		/*--End--*/
		String resp = GetPostUtil.send("POST", androidpnURL
				+ "user.do", parameter);
		if(resp!=null){
			XmlPullParser parser=resetParser(resp);
//			if(parser!=null){
//				while(parser.getName()){
//					int type=parser.next();
//				}
//			}
			Log.i(LOGTAG,"parsing:"+ resp);
		}
		else Log.i(LOGTAG,"parsing null");
		//Layout formLayout=(Layout)this.findViewById(R.id.UserInfoForm);
	}
	
	
	
	
	
  /**
 * always try to send packets to server if packet in packetList
 */
    private List<Packet> packetList;
    private SendMsgThread smThread;
    public void addMsg(Packet msg){
		synchronized(packetList){
			packetList.add(msg);
		}
	}
    @SuppressWarnings("unused")
	private class SendMsgThread extends Thread{
        final XmppManager xmppManager;
        private SendMsgThread(XmppManager manager) {
            xmppManager = manager;
        }
        public void run() {
            Log.i(LOGTAG,"chatactivity#SendMsgTask#run()... ");
            XMPPConnection conn=null;
            Packet packet=null;
            while(true){
            	//since this thread is only reading the packetlist, no need to synchronized
            	//synchronized(packetList){
        		if(packetList.isEmpty()){
        			try {
						Thread.currentThread().sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			continue;
        		}
        		synchronized(packetList){
        			packet=packetList.get(0);
        		}
        		//	packetList.remove(0);　//we may not need to remove it now, we can remove when it's sent
        		Log.i(LOGTAG,"XmppManager#sendMsgTask packet in sending:"+packet.toXML());
        		xmppManager.sendMsg(packet);
        		
    			Log.i(LOGTAG,"XmppManager#sendMsgTask waiting for packet to be sent");
				Log.i(LOGTAG,"XmppManager#sendMsgTask !!!!packet sent");
				synchronized(packetList){
					packetList.remove(0);
				}
    		}
        }
  
    }
    
    
    
    
    public class InitTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(LOGTAG,"init task running");
			Roster rost=xmppManager.getConnection().getRoster();
			if(rost==null){
				Log.d(LOGTAG,"roster null");
				return;
			}
			Log.i(LOGTAG,"roster:"+rost.toString());
			Collection<RosterEntry> entries=rost.getEntries();
			for(RosterEntry entry: entries){
				User u=new User();
				u.setUsername(entry.getUser());
				Log.i(LOGTAG,"roster item:"+entry.getUser());
				mFriendList.add(new User());
			}
			Log.i(LOGTAG,"init task finished");
		}
    }
	
    
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	/*public void setReceiveHandling( ) {
		//since the connection is really hard to maintain, 
		//and it is kept by XmppManager
		XMPPConnection connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						Log.i("####","####recv:"+message.getBody());
//						XmppManager manager=Constants.xmppManager;
//						if(manager==null) return;
//						manager.sendMsg(packet);
						
//						String fromName = StringUtils.parseBareAddress(message
//								.getFrom());
//						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
//								+ " from " + fromName );
//						messages.add(fromName + ":");
//						messages.add(message.getBody());
//						// Add the incoming message to the list view
////						mHandler.post(new Runnable() {
////							public void run() {
////								//selectItem(side);
////							}
////						});
					}
				}
			}, filter);
		}

	}*/
//	public static final int PORT = 5222;
//	public static final String SERVICE = "gmail.com";
//	private ArrayList<String> emails = new ArrayList<String>();
	
	/*public void connect() {

		final ProgressDialog dialog = ProgressDialog.show(this,
				"Connecting...", "Please wait...", false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String HOST =getString(R.string.androidpnserver);
				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						HOST, PORT, SERVICE);
				XMPPConnection connection = new XMPPConnection(connConfig);

				try {
					connection.connect();
					Log.i("XMPPChatDemoActivity",
							"Connected to " + connection.getHost());
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to connect to "
							+ connection.getHost());
					Log.e("XMPPChatDemoActivity", ex.toString());
					setReceiveHandling(null);
					runOnUiThread(new Runnable() 
					{
					   public void run() 
					   {
						   Toast.makeText(getApplicationContext(), "Failed to connect. Please try again.", Toast.LENGTH_SHORT).show();
						   onBackPressed();    
					   }
					}); 
				}
				try {
					// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
//					Log.i("XMPPChat","login with "+USERNAME+":"+PASSWORD);
//					connection.login(USERNAME, PASSWORD);
//					Log.i("XMPPChatDemoActivity",
//							"Logged in as " + connection.getUser());

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					setReceiveHandling(connection);

					Roster roster = connection.getRoster();
					Collection<RosterEntry> entries = roster.getEntries();
					for (RosterEntry entry : entries) {
						Log.d("XMPPChatDemoActivity",
								"--------------------------------------");
						Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
						Log.d("XMPPChatDemoActivity",
								"User: " + entry.getUser());
						// String array for AutoCompleteTextView
						//get the friends and add to the friendlist
						emails.add(entry.getUser());
						Log.d("XMPPChatDemoActivity",
								"Name: " + entry.getName());
						Log.d("XMPPChatDemoActivity",
								"Status: " + entry.getStatus());
						Log.d("XMPPChatDemoActivity",
								"Type: " + entry.getType());
						Presence entryPresence = roster.getPresence(entry
								.getUser());

						Log.d("XMPPChatDemoActivity", "Presence Status: "
								+ entryPresence.getStatus());
						Log.d("XMPPChatDemoActivity", "Presence Type: "
								+ entryPresence.getType());
						Presence.Type type = entryPresence.getType();
						if (type == Presence.Type.available)
							Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
						Log.d("XMPPChatDemoActivity", "Presence : "
								+ entryPresence);

					}
				} 
//				catch (XMPPException ex) {
//					Log.e("XMPPChatDemoActivity", "Failed to log in as "
//							+ USERNAME);
//					Log.e("XMPPChatDemoActivity", ex.toString());
//					setReceiveHandling(null);
//					runOnUiThread(new Runnable() 
//					{
//					   public void run() 
//					   {
//						   Toast.makeText(getApplicationContext(), "Failed to log in as "
//									+ USERNAME + ". Please reenter information and try again.", Toast.LENGTH_SHORT).show();
//						   onBackPressed();    
//					   }
//					}); 
//				}
				catch( Exception e){
					e.printStackTrace();
					onDestroy();
				}

				dialog.dismiss();
			}
		});
		t.start();
		dialog.show();
				
	}*/
}
