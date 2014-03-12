package org.androidpn.barcode;

import org.androidpn.demoapp.LectureAlarmActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, LectureAlarmActivity.class);
        context.startActivity(intent);
        Log.d("ReminderReceiver", "ReminderReceiver set is success!");
	}

}
