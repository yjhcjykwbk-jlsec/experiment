package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.data.ContactAdapter;
import org.androidpn.server.model.App;
import org.androidpn.server.model.Contacter;
import org.androidpn.server.model.User;
import org.androidpn.util.Util;

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

public class AppActivity extends Activity {
	private static String LOGTAG="ContactActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<App> appList;
	private ContactAdapter adapter;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		USERNAME = getIntent().getStringExtra("userID");
		PASSWORD = getIntent().getStringExtra("Pwd");//
		setContentView(R.layout.activity_contact);
		
		appList=Constants.appList;
		if(appList==null) appList=new ArrayList<App>();
		ListView appList = (ListView) this
				.findViewById(R.id.ContactListView);
		ContactAdapter adapter=new ContactAdapter(this,(List<Contacter>) appList);
		appList.setAdapter(adapter);
		appList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				App u=(App)arg0.getAdapter().getItem(arg2);
				if(u!=null&&u.getName()!=null){
					Intent intent=new Intent(AppActivity.this,AppNotesActivity.class);
					Bundle bundle=AppActivity.this.getIntent().getExtras();
					bundle.putString("appName", u.getName());
					intent.putExtras(bundle);
//					startActivity(intent);
					//返回聊天
					setResult(RESULT_OK,intent);
					AppActivity.this.finish();
				}else{
					Util.alert(AppActivity.this, "该应用无效，或无法启动会话");
				}
			}
		});
	}
	
}

