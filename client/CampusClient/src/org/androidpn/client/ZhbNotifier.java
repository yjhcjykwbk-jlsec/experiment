package org.androidpn.client;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ZhbNotifier {

	private static final String LOGTAG = LogUtil.makeLogTag(ZhbNotifier.class);
	private static final Random random = new Random(System.currentTimeMillis());
	private SharedPreferences sharedPrefs;
	private Context context;
	private NotificationManager notificationManager;
	
	public ZhbNotifier(Context context) {
        this.context = context;
		this.sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		 this.notificationManager = (NotificationManager) context
	                .getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public Notification zhbNotify(String notificationId, String apiKey, String title,
            String message, String uri,String from,String packetId) {
        Log.d(LOGTAG, "notify()...");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + apiKey);
        Log.d(LOGTAG, "notificationTitle=" + title);
        Log.d(LOGTAG, "notificationMessage=" + message);
        Log.d(LOGTAG, "notificationUri=" + uri);
        
        if (isNotificationEnabled()) {
        	 // Notification
            Notification notification = new Notification();
            notification.icon = getNotificationIcon();
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = message; //֪ͨ����ʾ�����֣����Ը�Ϊtitle
            
          //���֪ͨ
            Intent clickIntent = new Intent(
                    Constants.ACTION_NOTIFICATION_CLICKED);
            clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
            clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            clickIntent.putExtra(Constants.NOTIFICATION_URI, uri);
            //        positiveIntent.setData(Uri.parse((new StringBuilder(
            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //                "/").append(System.currentTimeMillis()).toString()));
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
                    context, 0, clickIntent, 0);

            notification.setLatestEventInfo(context, title, message, clickPendingIntent);
//
		//���֪ͨ
            Intent clearIntent = new Intent(
                    Constants.ACTION_NOTIFICATION_CLEARED);
            clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //        negativeIntent.setData(Uri.parse((new StringBuilder(
            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //                "/").append(System.currentTimeMillis()).toString()));
            PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
                    context, 0, clearIntent, 0);
            notification.deleteIntent = clearPendingIntent;

            notificationManager.notify(random.nextInt(), notification);

            return notification;
        } else {
        		Log.w(LOGTAG, "Notificaitons disabled.");
        		return null;
        }
       
}


	private int getNotificationIcon() {
	    return sharedPrefs.getInt(Constants.NOTIFICATION_ICON, 0);
	}

	private boolean isNotificationEnabled() {
	    return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
	            true);
	}
	
	private boolean isNotificationSoundEnabled() {
	    return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
	}
	
	private boolean isNotificationVibrateEnabled() {
	    return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
	}
	
	private boolean isNotificationToastEnabled() {
	    return sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
	}

}