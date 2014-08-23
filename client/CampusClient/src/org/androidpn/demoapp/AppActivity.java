package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.data.ContactAdapter;
import org.androidpn.server.model.App;
import org.androidpn.server.model.Contacter;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.Util;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
		
		getApps();
		if(appList==null) appList=new ArrayList<App>();
		for(App app : appList){
			Log.i(LOGTAG,app.toString());
		}
		ListView appView = (ListView) this
				.findViewById(R.id.ContactListView);
		ContactAdapter adapter=new ContactAdapter(this,(List) appList);
		appView .setAdapter(adapter);
		appView .setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(LOGTAG,"item "+arg2+" clicked");
				App u=(App)arg0.getAdapter().getItem(arg2);
				if(u!=null&&u.getName()!=null){
					Intent intent=new Intent(AppActivity.this,AppPlatFormActivity.class);
					Bundle bundle=AppActivity.this.getIntent().getExtras();
					bundle.putString("appName", u.getName());
					intent.putExtras(bundle);
					setResult(RESULT_OK,intent);
					AppActivity.this.finish();
				}else{
					Util.alert(AppActivity.this, "该应用无效，或无法启动会话");
				}
			}
		});
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
					Util.alert(AppActivity.this, "获取应用列表失败");
					return;
				}else {
					int i = resp.indexOf("<list>"), j;
					if (i < 0 || (j = resp.indexOf("</list>")) < 0) {
						Util.alert(AppActivity.this,"没有找到应用");//"</list>"
						appList = Constants.appList = new ArrayList();
					} 
					else {
						String str = resp.substring(i, j + 7);
						Xmler.getInstance().alias("app", App.class);
						List<App> list = (List) Xmler.getInstance().fromXML(str);

						if (list == null) {
							Util.alert(AppActivity.this, "应用列表为空");
						}else
							Util.alert(AppActivity.this, "应用列表已经更新");
						appList = Constants.appList = list;
						// UIUtil.alert(NotesActivity.this,"通讯录已经同步");
					}
				}
			}
		}.execute(params);
	}
	
}

