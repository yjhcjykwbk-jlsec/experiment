package org.androidpn.demoapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ReleaseInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release_info);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.release_info, menu);
		return true;
	}

}
