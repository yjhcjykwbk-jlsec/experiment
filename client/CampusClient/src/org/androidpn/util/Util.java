package org.androidpn.util;

import org.androidpn.client.Constants;
import org.androidpn.demoapp.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/*
 * some common function code related to ui
 */
public class Util {
	
	public static int getPhoto(String username){
		int s=username==null?0:username.hashCode()%4;
		switch(s){
			case 1: return R.drawable.photo_1; 
			case 2: return R.drawable.photo_2; 
			case 3: return R.drawable.photo_3; 
			default: return R.drawable.photo_4; 
		}
	}
	/*
	 * alert a window in context c
	 */
	public static void alert(Context c, String s){
		new AlertDialog.Builder(c).setIcon(
			 android.R.drawable.ic_dialog_info).setTitle("结果").setMessage(s).
			 setPositiveButton("确定",
			 new OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
			   // TODO Auto-generated method stub
			  }
			  }).show();
	}
	/*
	 * exit application
	 * 
	 */
	public static void exit(Context c){
		SharedPreferences originSharedPrefs = c.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = originSharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit(); 
		Constants.serviceManager.stopService();
		ActivityUtil.getInstance().exit();
	}
}
