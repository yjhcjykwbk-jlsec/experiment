package org.androidpn.demoapp;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LectureAlarmActivity extends Activity {
	TextView alarmTitle;
	TextView alarmText;
	Button alarmClose;

	public static final int NOTIFICATION_ID=1; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture_alarm);
		alarmTitle = (TextView)findViewById(R.id.alarm_title);
		alarmText = (TextView)findViewById(R.id.alarm_text);
		alarmClose = (Button)findViewById(R.id.alarm_close);
		alarmTitle.setText("讲座要开始了！");
		String text = getIntent().getStringExtra("remindtext");
		alarmText.setText(text);
		
		final NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification n=new Notification();
		long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		n.vibrate = vibrate;
		nm.notify(NOTIFICATION_ID, n);
		
		alarmClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				nm.cancel(NOTIFICATION_ID);
				finish();
			}
			
		});
	}

	
}
