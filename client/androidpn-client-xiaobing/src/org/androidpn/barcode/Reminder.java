package org.androidpn.barcode;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Reminder {
	private long time;
	private String text;

	public Reminder(long time, String text) {
		this.time = time;
		this.text = text;
	}

	public long getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setReminder(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(context.ALARM_SERVICE);

		Intent intent = new Intent(context,ReminderReceiver.class);
		
		intent.putExtra("remindtext", text);
		
		// create a PendingIntent that will perform a broadcast
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		// just use current time as the Alarm time.
		Calendar c = Calendar.getInstance();
		// schedule an alarm
		Log.d("Calendar", "current min is:" + c.getTimeInMillis());
		Log.d("Calendar", "time min is:" + time);
//		am.set(AlarmManager.RTC_WAKEUP, time, pi);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 10000, pi);
		Log.d("Reminder", "Reminder set is success!");
	}

}
