package org.androidpn.demoapp;
import org.androidpn.demoapp.R;
import org.androidpn.util.ActivityUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class WebViewActivity extends Activity {
	
	private WebView mWebView;
	private String userID="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
      //添加到activitylist里，方便最后统一退出
        ActivityUtil.getInstance().addActivity(this);
        
        Bundle bundle = this.getIntent().getExtras();
		userID = bundle.getString("userID");
		Button btn_uploadAgain = (Button)findViewById(R.id.btnBackToUpload);
		//再次上传
		btn_uploadAgain.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent uploadIntent = new Intent(WebViewActivity.this, UploadActivity.class);
		    	Bundle bd = new Bundle();
				bd.putString("userID", userID);
				uploadIntent.putExtras(bd);
		    	startActivity(uploadIntent);
			}
		});
		
        mWebView  = (WebView) findViewById(R.id.webview);
        
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        //mWebView.setWebChromeClient(new MyWebChromeClient());
        //mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        //mWebView.loadUrl("ihiu");
        mWebView.loadUrl(getString(R.string.list_uri));        
        
        
    }

    
  //按下back键,回到上传界面
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {  					
			Intent intent = new Intent(this, UploadActivity.class);
	    	Bundle bd = new Bundle();
			bd.putString("userID", userID);
			intent.putExtras(bd);
	    	startActivity(intent);
		return true;
	} else {
		return super.onKeyDown(keyCode, event);
	}
	}
}