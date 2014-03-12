package org.androidpn.barcode;

import java.util.Calendar;

import org.androidpn.demoapp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.result.LectureParsedResult;
import com.google.zxing.client.result.ParsedResult;

@SuppressLint("ShowToast")
public class LectureResultHandler extends ResultHandler{
	Activity activity;
	LectureParsedResult result;
	private static final int[] buttons = {
	      R.string.lecture_set_remind,
	      R.string.button_open_browser,
	  };
	
	public LectureResultHandler(Activity activity, ParsedResult result) {
	    super(activity, result);
	    this.activity = activity;
	    this.result = (LectureParsedResult)result;
	}

	@Override
	public int getButtonCount() {
		return buttons.length;
	}

	@Override
	public int getButtonText(int index) {
		
		return buttons[index];
	}

	@Override
	public void handleButtonPress(int index) {
		
		String startTime = result.getStarttime();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		int year = Integer.parseInt(startTime.substring(0, 4));
		int month = Integer.parseInt(startTime.substring(4, 6)) - 1;
		int day = Integer.parseInt(startTime.substring(6, 8));
		int hour = Integer.parseInt(startTime.substring(8, 10));
		int minute = Integer.parseInt(startTime.substring(10));
		calendar.set(year, month, day, hour, minute);
		Calendar c = Calendar.getInstance();
		Log.d("Calendar", "calendar is:" + calendar.getTime());
		Log.d("Calendar", "current calendar is:" + c.getTime());
		Log.d("Calendar", "year is:" + (year+1));
		Log.d("Calendar", "month is:" + month);
		Log.d("Calendar", "day is:" + day);
		Log.d("Calendar", "hour is:" + hour);
		Log.d("Calendar", "minute is:" + minute);
		Reminder reminder = new Reminder(calendar.getTimeInMillis(),result.getDisplayResult());
		reminder.setReminder(activity.getApplicationContext());
		Toast.makeText(activity.getApplicationContext(), "…Ë÷√≥…π¶£°", Toast.LENGTH_LONG).show();
		activity.finish();
		
	}

	@Override
	public int getDisplayTitle() {
		return R.string.result_lecture;
	}

}
