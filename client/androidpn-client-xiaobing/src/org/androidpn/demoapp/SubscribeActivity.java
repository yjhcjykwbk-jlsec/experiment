package org.androidpn.demoapp;

import org.androidpn.client.Constants;
import org.androidpn.util.ActivityUtil;
import org.androidpn.util.IsNetworkConn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.view.View.OnClickListener;

public class SubscribeActivity extends Activity {
	private SharedPreferences originSharedPrefs;
	private CheckBox cb_all;
	private CheckBox cb_news;
	private CheckBox cb_notification;
	private CheckBox cb_cieVideo;
	private CheckBox cb_hsbcVideo;
	private CheckBox cb_stlVideo;
	private CheckBox cb_renwenVideo;
	private CheckBox cb_schoolVideo;
	private CheckBox cb_leisureVideo;
	private Button btn_subsub;
	private ProgressDialog dialogProgress;
	private Thread submitThread;
	private Runnable submitRunnable;
	private Handler handler;
	
	private boolean isSubAll=true;
	private String SubAll="all";
	private String SubLeisureVideo="video_leisurevideo";
	private String SubSchoolVideo="video_schoolvideo";
	private String SubCieVideo="video_cievideo";
	private String SubHsbcVideo="video_hsbcvideo";
	private String SubStlVideo="video_stlvideo";
	private String SubRenwenVideo="video_renwenvideo";
	private String SubNews="news_yaowen";
	private String SubNotification="pkusz_notification";
	private String userID="";
	private String subscriptions="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
		//添加到activitylist里，方便最后统一退出
        ActivityUtil.getInstance().addActivity(this);
        
		Log.i("xiaobingo", "进入订阅界面");
		Bundle bundle = this.getIntent().getExtras();
		userID = bundle.getString("userID");
		
		cb_all = (CheckBox)findViewById(R.id.subAll);
		cb_news = (CheckBox)findViewById(R.id.subNews);
		cb_notification = (CheckBox)findViewById(R.id.subNotification);
		cb_cieVideo = (CheckBox)findViewById(R.id.subCieVideo);
		cb_hsbcVideo = (CheckBox)findViewById(R.id.subHsbcVideo);
		cb_stlVideo = (CheckBox)findViewById(R.id.subStlVideo);
		cb_renwenVideo = (CheckBox)findViewById(R.id.subRenwenVideo);
		cb_schoolVideo = (CheckBox)findViewById(R.id.subSchoolVideo);
		cb_leisureVideo = (CheckBox)findViewById(R.id.subLeisureVideo);
		btn_subsub = (Button)findViewById(R.id.btn_subsub);
		dialogProgress = new ProgressDialog(SubscribeActivity.this);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1==0) { //取消
					submitThread.interrupt();
        			Toast.makeText(SubscribeActivity.this, "用户 "+userID+" 中止提交", Toast.LENGTH_SHORT).show();
				}else if (msg.arg1==1) { //成功
					dialogProgress.dismiss();
					Toast.makeText(SubscribeActivity.this, "用户 "+userID+" 提交成功", Toast.LENGTH_LONG).show();
				}else if (msg.arg1==2) { //失败
					dialogProgress.dismiss();
        			Toast.makeText(SubscribeActivity.this, "用户 "+userID+" 提交失败", Toast.LENGTH_LONG).show();
				}
			}			
		};
		
		submitRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int code=0;
				 String androidpnURL = getString(R.string.androidpnserver);
	             // 用POST方式发送
	        		/*--拼接POST字符串--*/
	        		StringBuilder parameter = new StringBuilder();
	        		parameter.append("action=get"); // 
	        		parameter.append("&subscriber=");
	        		parameter.append(userID);
	        		parameter.append("&subscriptions=");
	        		parameter.append(subscriptions);
	        		/*--End--*/
	        		
	        		String resp = GetPostUtil.send("POST", androidpnURL + "notification.do", parameter);
	        		Log.i("xiaobingo", "订阅响应："+resp);
	        		Message msg = handler.obtainMessage();	 
	        		if (resp.contains("subscribe:success")) { //订阅成功
						code = 1;
						msg.arg1 = 1;
						msg.sendToTarget();
					}else { //订阅失败
						msg.arg1 = 2;
						msg.sendToTarget();
					}
	        		 SubscribeActivity.this.finish();
			}
		};
		
		//订阅所有
		cb_all.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				isSubAll=isChecked;
				if (!isSubAll) {
					SubAll = null;
					cb_news.setChecked(false);
					cb_notification.setChecked(false);
					cb_cieVideo.setChecked(false);
					cb_hsbcVideo.setChecked(false);
					cb_stlVideo.setChecked(false);
					cb_renwenVideo.setChecked(false);
					cb_schoolVideo.setChecked(false);
					cb_leisureVideo.setChecked(false);
				}
				else {
					SubAll="all";
					SubLeisureVideo="video_leisurevideo";
					SubSchoolVideo="video_schoolvideo";
					SubCieVideo="video_cievideo";
					SubHsbcVideo="video_hsbcvideo";
					SubStlVideo="video_stlvideo";
					SubRenwenVideo="video_renwenvideo";
					SubNews="news_yaowen";
					SubNotification="pkusz_notification";
					cb_news.setChecked(true);
					cb_notification.setChecked(true);
					cb_cieVideo.setChecked(true);
					cb_hsbcVideo.setChecked(true);
					cb_stlVideo.setChecked(true);
					cb_renwenVideo.setChecked(true);
					cb_schoolVideo.setChecked(true);
					cb_leisureVideo.setChecked(true);
				}
			}			
		});
		
		//订阅信息学院视频
		cb_cieVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubCieVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubCieVideo = "video_cievideo";
				}
			}			
		});
		
		//订阅汇丰商学院视频
		cb_hsbcVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubHsbcVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubHsbcVideo = "video_hsbcvideo";
				}
			}			
		});
		
		//订阅国际法学院视频
		cb_stlVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubStlVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubStlVideo = "video_stlvideo";
				}
			}			
		});
		
		//订阅人文学院视频
		cb_renwenVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubRenwenVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubRenwenVideo = "video_renwenvideo";
				}
			}			
		});
		
		//订阅学校视频
		cb_schoolVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubSchoolVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubSchoolVideo = "video_schoolvideo";
				}
			}			
		});
		
		//订阅休闲视频
		cb_leisureVideo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubLeisureVideo= null;
					cb_all.setChecked(false);
				}
				else {
					SubLeisureVideo = "video_leisurevideo";
				}
			}			
		});
		
		//订阅南燕要闻
		cb_news.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubNews= null;
					cb_all.setChecked(false);
				}
				else {
					SubNews = "news_yaowen";
				}
			}			
		});
		
		//订阅通知公告
		cb_notification.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					SubNotification= null;
					cb_all.setChecked(false);
				}
				else {
					SubNotification = "pkusz_notification";
				}
			}			
		});
		
		//提交订阅或者取消订阅
		btn_subsub.setOnClickListener(new OnClickListener() { 			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//判断联网
        		IsNetworkConn isConn = new IsNetworkConn(SubscribeActivity.this);
        		if (!isConn.isConnected) {
        			Toast.makeText(SubscribeActivity.this, "未联网，请先联网~", Toast.LENGTH_LONG).show();
        			return;
				}
				subscriptions = SubAll + ";" + SubNews + ";" + SubNotification + ";" + SubSchoolVideo + ";" + SubCieVideo + ";" + SubHsbcVideo + ";" + SubStlVideo + ";" + SubRenwenVideo + ";" + SubLeisureVideo;
				Log.i("xiaobingo", "订阅的有："+subscriptions);
				submitThread = new Thread(submitRunnable);
				submitThread.start();
				//订阅改变了，修改本地订阅记录
				Editor editor = originSharedPrefs.edit();
				editor.putString(Constants.USER_SUBSCRIPTION, subscriptions);
				editor.commit(); //保存订阅
			}
		});
	}
	
	//在onresume中设置每一次恢复订阅activity时，都要判断加载用户已订阅的内容
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//先重置下
		cb_news.setChecked(false);
		cb_notification.setChecked(false);
		cb_cieVideo.setChecked(false);
		cb_hsbcVideo.setChecked(false);
		cb_stlVideo.setChecked(false);
		cb_renwenVideo.setChecked(false);
		cb_schoolVideo.setChecked(false);
		cb_leisureVideo.setChecked(false);
		
		originSharedPrefs = this.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (originSharedPrefs.contains(Constants.USER_SUBSCRIPTION)) {
			String thisSubscriptions = originSharedPrefs.getString(Constants.USER_SUBSCRIPTION, null); //读取保存的用户订阅记录
			//String[] thisSubscription = thisSubscriptions.split("&&");
			if (thisSubscriptions.contains("all")) {
				cb_all.setChecked(true);
			}
			if (thisSubscriptions.contains("video_leisurevideo")) {
				cb_leisureVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("video_schoolvideo")) {
				cb_schoolVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("video_cievideo")) {
				cb_cieVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("video_hsbcvideo")) {
				cb_hsbcVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("video_stlvideo")) {
				cb_stlVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("video_renwenvideo")) {
				cb_renwenVideo.setChecked(true);
			}
			if (thisSubscriptions.contains("news_yaowen")) {
				cb_news.setChecked(true);
			}
			if (thisSubscriptions.contains("pkusz_notification")) {
				cb_notification.setChecked(true);
			}
		}

	}
	
}
