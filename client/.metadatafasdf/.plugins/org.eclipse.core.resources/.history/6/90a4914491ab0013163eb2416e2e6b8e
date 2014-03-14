package org.androidpn.demoapp;
import java.util.ArrayList;
import java.util.HashMap;
import org.androidpn.client.Constants;
import org.androidpn.client.NotificationDetailsActivity;
import org.androidpn.client.ServiceManager;
import org.androidpn.util.ActivityUtil;
import org.androidpn.util.IsNetworkConn;
import org.androidpn.util.RTMPConnectionUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	private XMPPConnection connection;
	List messages;
	public String USERNAME;
	public String PASSWORD;
	public String recipient="";//who you are talking with
	private Handler mHandler = new Handler();
	private EditText textMessage;
	private ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("XMPPChat","hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");
		
		textMessage = (EditText) this.findViewById(R.id.textMessage);
		listview = (ListView) this.findViewById(R.id.listMessages);
		
		Log.i("XMPPChat","hello i am oncreate Button send");
		// Set a listener to send a chat text message
		Button send = (Button) this.findViewById(R.id.sendBtn);
//		send.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				String to = recipient;
//				String text = textMessage.getText().toString();
//
//				Log.i("XMPPChatDemoActivity", "Sending text " + text + " to " + to);
//				Message msg = new Message(to, Message.Type.chat);
//				msg.setBody(text);				
//				if (connection != null) {
//					connection.sendPacket(msg);
////					messages.add(connection.getUser() + ":");
////					messages.add(text);
//					//selectItem(side);
//				}
//				textMessage.setText("");
//			}
//		});
		Log.i("XMPPCHat","connect();");
		connect();
		setContentView(R.layout.activity_chat);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	public void setReceiveHandling(XMPPConnection connection) {
		this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						messages.add(fromName + ":");
						messages.add(message.getBody());
						// Add the incoming message to the list view
//						mHandler.post(new Runnable() {
//							public void run() {
//								//selectItem(side);
//							}
//						});
					}
				}
			}, filter);
		}

	}
	
	public static final int PORT = 5222;
	public static final String SERVICE = "gmail.com";
	private ArrayList<String> emails = new ArrayList<String>();
	
	public void connect() {

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
				
	}
}
