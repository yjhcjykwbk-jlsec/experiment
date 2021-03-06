package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.androidpn.client.Constants;
import org.androidpn.server.model.App;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.Util;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.androidpn.data.ChatInfo;
import org.androidpn.data.ChatInfoAdapter;
import org.androidpn.data.NoteManager;
import org.androidpn.data.ChatsAdapter;  

public class AppPlatFormActivity extends Activity {
	static String LOGTAG="AppNotesActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<App> appList;
	private Stack<View> views;
	
	private Map<String,ChatInfoAdapter> noteAdapters; 
	//noteViews保存了当前多个会话各自对应的视图
	private Map<String,View> noteViews; 
	NoteHandler noteHandler=new NoteHandler();
	private View notesView;
	private ChatsAdapter notesAdapter;
	private Map<String,ChatInfo> latestNotes;//最新各应用发送的消息
	Map<String, List> noteLists;//各应用对应会话
	NotesHandler notesHandler=new NotesHandler();
	Boolean viewState=true;
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOGTAG, "hello i am oncreate");
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");// used for 8080 connection
		
		//初始化应用列表
		getApps();
		
		latestNotes=NoteManager.cloneLatestNotes();
		notesAdapter=new ChatsAdapter(this,latestNotes);
		noteLists=new HashMap<String,List>();
		setNotesView();
	}
	
	/**
	 * 点击从本activity返回
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//如果当前在具体会话的页面视图，则返回到会话列表的页面视图
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(viewState){
				setNotesView();
				return false;
			}
		}
		//否则退出当前activity
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 进入会话列表页面
	 */
	private void setNotesView() {
		viewState=false;
		
		Log.i(LOGTAG, "setNotesView setNotesView");
		
		if (notesView != null) {
			setContentView(notesView);
			notesAdapter.notifyDataSetChanged();
			return;
		}
		notesView = getLayoutInflater().inflate(R.layout.activity_chats, null);
		setContentView(notesView);

		Log.i(LOGTAG, "setNotesView setListView");
		ListView lv = (ListView) this.findViewById(R.id.ChatListView);

		if (notesAdapter == null)
			notesAdapter = new ChatsAdapter(this, latestNotes);
		lv.setAdapter(notesAdapter);
		
		// 点击"会话列表"中的表项进入具体会话
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//adapterview->adapter->data(ci),
				Log.i(LOGTAG, "item " + arg2 + " clicked");
				ChatsAdapter adapter = (ChatsAdapter) arg0.getAdapter();
				ChatInfo ci = (ChatInfo) adapter.getItem(arg2);

				String recipient = ci.getRecipient();
				if (recipient == null) {
					Log.e(LOGTAG, "assert lv.setOnclickLIstener failed");
					return;
				}
				//进入具体会话视图
				setNoteView(recipient);
			}
		});
		Button appLstBtn=(Button)this.findViewById(R.id.FriendListBtn);
		appLstBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getApps();
				if (appList == null) {
					Util.alert(AppPlatFormActivity.this, "应用列表拉取失败，请检查网络状况");
					return;
				}
				Intent intent = new Intent(AppPlatFormActivity.this,
						AppActivity.class);
				intent.putExtras(AppPlatFormActivity.this.getIntent().getExtras());
				startActivityForResult(intent, 0);
			}
		});
		appLstBtn.setText("应用列表");
		
		//设置后台线程对"会话列表"数据更新的handler
		NoteManager.setNotesUiListener(notesHandler);
	}
	
	//更新应用列表中的最近消息
	class NotesHandler extends Handler{
		public void handleMessage(Message msg){
			Bundle b = msg.getData();
			final String recipient = b.getString("recipient");
			final ChatInfo ci = new ChatInfo(b.getString("username"),
					b.getString("chatXml"), b.getString("id"),
					b.getBoolean("isSelf"));
			latestNotes.put(recipient, ci);
			//if (viewState == 1)
			notesAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 进入某个应用的会话页面
	 */
	private void setNoteView(String appName) {
		viewState=true;
		
		if(noteViews.get(appName)!=null){
			View view=noteViews.get(appName);
			this.setContentView(view);
			views.push(view);
		}else{
			List noteList=null; 
			if (noteLists.containsKey(appName)) {
				noteList = noteLists.get(appName);
			}
			// messages stores the talk contents with friends
			if (noteList == null) {
				noteList = NoteManager.cloneNoteList(appName);
				noteLists.put(appName, noteList);
			}
						
			View noteView = getLayoutInflater()
					.inflate(R.layout.activity_chat, null);
			setContentView(noteView);
			noteViews.put(appName, noteView);
			
			Button sendBtn = (Button) (AppPlatFormActivity.this)
					.findViewById(R.id.SendBtn);
			TextView tv = (TextView) (AppPlatFormActivity.this)
					.findViewById(R.id.ChatTitleLabel);
			tv.setText( appName + "服务中");

			ListView mListView = (ListView) (AppPlatFormActivity.this)
					.findViewById(R.id.MessageListView);
			
			BaseAdapter noteAdapter = new ChatInfoAdapter((AppPlatFormActivity.this),
					noteList,mListView);
			mListView.setAdapter(noteAdapter);
			noteAdapter.notifyDataSetInvalidated();

			// 发送消息
			sendBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
				}
			});
			
			NoteManager.setNoteUiListener(noteHandler);
		}
	}
	
	
	//更新某个应用的会话列表页面
	class NoteHandler extends Handler{
		//更新视图
		@Override
		public void handleMessage(Message msg){
			Bundle b = msg.getData();
			final String recipient = b.getString("recipient");
		
			List noteList=noteLists.get(recipient);
			View view=noteViews.get(recipient);
			if(noteList==null||view==null) return;
			final ChatInfoAdapter noteAdapter = (ChatInfoAdapter) 
					((ListView) (view.findViewById(R.id.MessageListView)))
					.getAdapter();
			
			switch (msg.what) {
				case 1:// note recved
					final ChatInfo ci = new ChatInfo(b.getString("username"),
							b.getString("chatXml"), b.getString("id"),
							b.getBoolean("isSelf"));
					noteList.add(ci);
					noteAdapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * addSubscribe(username,appid)
	 * 订阅某个应用
	 */
	private void addSubscribe(String s,Long t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=addSubscribe"); //
		parameter.append("&username=" + s + "&appid="+ t);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscriptions.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				Log.i(LOGTAG,"addSubs:"+resp);
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppPlatFormActivity.this, "添加关注失败:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppPlatFormActivity.this, "添加关注成功");
				}
			}
		}.execute(parameter);
	}
	
	
	/**
	 * delSubscribe(username,appid)
	 * 取消订阅某个应用
	 */
	private void delSubscribe(String s,Long t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=delSubscribe"); //
		parameter.append("&username=" + s + "&appid="+ t);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscriptions.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				Log.i(LOGTAG,"delSubs:"+resp);
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppPlatFormActivity.this, "取消关注失败:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppPlatFormActivity.this, "取消关注成功");
				}
			}
		}.execute(parameter);
	}
	
	
	/**
	 * 获取应用列表
	 */
	private void getApps(){
		if(Constants.appList!=null){
			appList=Constants.appList;
			return;
		}
		StringBuilder params = new StringBuilder();
		params.append("action=listApps&username="+USERNAME); //
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscriptions.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				Log.i(LOGTAG,"getApps:"+resp);
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					Util.alert(AppPlatFormActivity.this, "获取应用列表失败");
					return;
				}else {
					int i = resp.indexOf("<list>"), j;
					if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
						Util.alert(AppPlatFormActivity.this,"没有找到应用");//"</list>"
						appList = Constants.appList = new ArrayList();
					} 
					else {
						String str = resp.substring(i, j + 7);
						Xmler.getInstance().alias("app", App.class);
						List<App> list = (List) Xmler.getInstance().fromXML(str);

						if (list == null) {
							Util.alert(AppPlatFormActivity.this, "应用列表为空");
						}else
							Util.alert(AppPlatFormActivity.this, "应用列表已经更新");
						appList = Constants.appList = list;
						// UIUtil.alert(NotesActivity.this,"通讯录已经同步");
					}
				}
			}
		}.execute(params);
	}
}
