package org.androidpn.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.LinearLayout;

public class VideoDisplayActivity extends Activity {

	public VideoDisplayActivity(){
		
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}

	private View createView(final String title, final String message, final String url){
		LinearLayout linearLayout = new LinearLayout(this);
		//linearLayout.setLayoutParams(params);
		
		return linearLayout;
		
		
	}
}
