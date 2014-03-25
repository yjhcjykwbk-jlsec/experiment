package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.data.ContactAdapter;
import org.androidpn.server.model.User;
import org.androidpn.util.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends Activity {
	private static String LOGTAG="ContactActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<User> friendList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");//
		setContentView(R.layout.activity_contact);
		
		friendList=Constants.friendList;
		if(friendList==null) friendList=new ArrayList<User>();
		ListView contactList = (ListView) this
				.findViewById(R.id.ContactListView);
		ContactAdapter adapter=new ContactAdapter(this,friendList);
		contactList.setAdapter(adapter);
		contactList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				User u=(User)arg0.getAdapter().getItem(arg2);
				if(u!=null&&u.getName()!=null){
					Intent intent=new Intent(ContactActivity.this,ChatsActivity.class);
					Bundle bundle=ContactActivity.this.getIntent().getExtras();
					bundle.putString("recipient", u.getName());
					intent.putExtras(bundle);
					startActivity(intent);
				}else{
					UIUtil.alert(ContactActivity.this, "该用户无效，或无法启动会话");
				}
			}
		});
	}
}

