package org.androidpn.demoapp;

import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.server.model.User;
import org.androidpn.server.model.App;
import org.androidpn.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AppWebActivity extends Activity{
	private static String LOGTAG = "AppWebActivity";
	private String USERNAME;
	private String PASSWORD;
	private App app=null;
	private String appName;
	private List<App> appList;
	private WebView webView;
	@Override
	protected void onNewIntent(Intent intent) {
		// 进入与某个好友的会话
		if (intent.getStringExtra("appName") != null) {
			 appName=intent.getStringExtra("appName");
		}else{
			Util.alert(AppWebActivity.this,"获取应用信息失败");
			this.finish();
		}
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		appList=Constants.appList;
		if(appList==null||appName==null){
			Util.alert(AppWebActivity.this,"获取应用信息失败");
			this.finish();
		}
		for(App app:appList){
			if(app.getName()==appName){
				this.app=app;
			}
		}
		if(this.app==null){
			Util.alert(AppWebActivity.this,"获取应用信息失败");
			this.finish();
		}
		
		 setContentView(R.layout.web_view);
		 webView  = (WebView) findViewById(R.id.webview);
		 WebSettings webSettings = webView.getSettings();
		 webSettings.setSavePassword(false);
		 webSettings.setSaveFormData(false);
		 webSettings.setJavaScriptEnabled(true);
		 webSettings.setSupportZoom(false);
		
		//mWebView.setWebChromeClient(new MyWebChromeClient());
		//mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
		//mWebView.loadUrl("ihiu");
		
		}
	
	protected void onResume(){
		super.onResume();
		webView.loadUrl(app.getUrl());        
	}

}
