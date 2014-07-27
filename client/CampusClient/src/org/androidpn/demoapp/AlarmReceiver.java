package org.androidpn.demoapp;

import org.androidpn.client.Constants;
import org.androidpn.client.Notifier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * this repeat connection action
 * @author x
 *
 */
public class AlarmReceiver extends android.content.BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
        if(Constants.xmppManager==null) return;
        Constants.xmppManager.connect();
//		Constants.xmppManager.getContext().sendBroadcast(new Intent(Constants.KEEP_RECONNECT)); 
//		new Notifier(context).notifyMsg("保持重连进程", "在运行");
//		new Notifier(context.getApplicationContext()).notifyMsg("保持重连进程", "在运行");
//		new Notifier(Constants.xmppManager.getContext()).notifyMsg("保持重连进程", "在运行");
//		new Notifier(Constants.xmppManager.getContext().getApplicationContext()).notifyMsg("保持重连进程", "在运行");
		Log.i("alarmReceiver","onReceive");
		Constants.xmppManager.getContext().sendBroadcast(new Intent(Constants.XMPP_CONNECTING).
    			putExtra("from","AlarmReceiver").
				putExtra("type", "keepFromService"));
	}
}