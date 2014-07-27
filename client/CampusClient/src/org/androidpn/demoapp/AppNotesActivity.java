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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

public class AppNotesActivity extends Activity {
	static String LOGTAG="AppsActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<App> appList;
	private Stack<View> views;
	
	private Map<String,ChatInfoAdapter> noteAdapters; 
	//noteViews�����˵�ǰ����Ự���Զ�Ӧ����ͼ
	private Map<String,View> noteViews; 
	NoteHandler noteHandler=new NoteHandler();
	
	private View notesView;
	private ChatsAdapter notesAdapter;
	private Map<String,ChatInfo> latestNotes;//���¸�Ӧ�÷��͵���Ϣ
	//messageLists�����˻Ự�б�ÿ��Ϊ���Ự�������Ϣ��
	//������ֻ���ɵ�ǰuithread���£�����NoteManager�б�������ݱ��ֻ���ͬ��
	//ͬ������:��ǰ�̴߳���֮����NoteManager����һ�ݸ���
	//	����ÿ��Notemanager�����ݸ��£�����handler֪ͨuithread���¸����ݺ���Ӧ��ͼ
	Map<String, List> noteLists;//��Ӧ�ö�Ӧ�Ự
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
		
		//��ʼ����ϵ���б�
		getApps();
		
		latestNotes=NoteManager.cloneLatestNotes();
		notesAdapter=new ChatsAdapter(this,latestNotes);
		
		noteLists=new HashMap<String,List>();
		
	}
	
	/**
	 * �������
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//�����ǰ�ھ���Ự��ҳ����ͼ���򷵻ص��Ự�б��ҳ����ͼ
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(viewState){
				setNotesView();
				return false;
			}
		}
		//�����˳���ǰactivity
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * ����Ự�б�ҳ��
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

		// ���"�Ự�б�"�еı���������Ự
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
				//�������Ự��ͼ
				setNoteView(recipient);
			}
		});
		//���ú�̨�̶߳�"�Ự�б�"���ݸ��µ�handler
		NoteManager.setNotesUiListener(notesHandler);
	}
	//���£ã�����֣���
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
	 * ����Ựҳ��
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
			
			Button sendBtn = (Button) (AppNotesActivity.this)
					.findViewById(R.id.SendBtn);
			TextView tv = (TextView) (AppNotesActivity.this)
					.findViewById(R.id.ChatTitleLabel);
			tv.setText( appName + "������");

			ListView mListView = (ListView) (AppNotesActivity.this)
					.findViewById(R.id.MessageListView);
			
			BaseAdapter noteAdapter = new ChatInfoAdapter((AppNotesActivity.this),
					noteList,mListView);
			mListView.setAdapter(noteAdapter);
			noteAdapter.notifyDataSetInvalidated();

			// ������Ϣ
			sendBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
				}
			});
			
			NoteManager.setNoteUiListener(noteHandler);
		}
	}
	class NoteHandler extends Handler{
		//������ͼ
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
	 * ����
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
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppNotesActivity.this, "��ӹ�עʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppNotesActivity.this, "��ӹ�ע�ɹ�");
				}
			}
		}.execute(parameter);
	}
	/**
	 * delSubscribe(username,appid)
	 * ȡ������
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
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppNotesActivity.this, "ȡ����עʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppNotesActivity.this, "ȡ����ע�ɹ�");
				}
			}
		}.execute(parameter);
	}
	
	/**
	 * ��ȡӦ���б�
	 * getApps()
	 */
	private void getApps(){
		if(Constants.appList!=null){
			appList=Constants.appList;
			return;
		}
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=listApps"); //
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
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					Util.alert(AppNotesActivity.this, "��ȡӦ���б�ʧ��");
					return;
				}else {
					int i = resp.indexOf("<list>"), j;
					if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
						// UIUtil.alert(NotesActivity.this,"����û�к���");//"</list>"
						appList = Constants.appList = new ArrayList();
					} 
					else {
						String str = resp.substring(i, j + 7);
						Xmler.getInstance().alias("app", App.class);
						List<App> list = (List) Xmler.getInstance().fromXML(str);

						if (list == null) {
							Util.alert(AppNotesActivity.this, "Ӧ���б�Ϊ��");
						}else
							Util.alert(AppNotesActivity.this, "Ӧ���б��Ѿ�����");
						appList = Constants.appList = list;
						// UIUtil.alert(NotesActivity.this,"ͨѶ¼�Ѿ�ͬ��");
					}
				}
			}
		}.execute(parameter);
	}
}
