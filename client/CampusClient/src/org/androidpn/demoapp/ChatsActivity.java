package org.androidpn.demoapp;

import java.util.HashMap;
import java.util.Map;

import org.androidpn.data.ChatsAdapter;
import org.androidpn.data.ChatInfo;
import org.androidpn.data.SessionManager;
import org.androidpn.util.UIUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChatsActivity extends Activity{
	private static String LOGTAG="ChatsActivity";
	private String USERNAME;
	private String PASSWORD;
	private Map<String,ChatInfo> latestChats;
	private MyHandler handler=new MyHandler();
	ChatsAdapter adapter;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chats);
		Log.i(LOGTAG, "hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");// used for 8080 connection
		
		ListView lv=(ListView) this.findViewById(R.id.ChatListView);
		latestChats=SessionManager.cloneLatestChats();
		adapter=new ChatsAdapter(this,latestChats);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				ChatsAdapter adapter=(ChatsAdapter) arg0.getAdapter();
				ChatInfo ci=(ChatInfo) adapter.getItem(arg2);
				Intent intent=new Intent(ChatsActivity.this,ChatActivity.class);
				intent.putExtras(ChatsActivity.this.getIntent().getExtras());
				startActivity(intent);
			}
		});
		
		try {
			SessionManager.addChatsListener(handler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UIUtil.alert(this,"Òì³£:already have an chats-ui watching SessionManager");
		}
	}
	protected void onDestory(){
		SessionManager.removeChatsListener();
		super.onDestroy();
	}
	class MyHandler extends Handler{
		public void dispatchMessage(Message msg){
			//¸üÐÂ
			Bundle b=msg.getData();
			String recipient=b.getString("recipient");
			ChatInfo ci=new ChatInfo(b.getString("username"),b.getString("chatXml"),b.getString("id"),false);
			latestChats.put(recipient, ci);
			adapter.notifyDataSetChanged();
		}
	}
}
