/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.androidpn.demoapp.ChatsActivity;
import org.androidpn.demoapp.DemoAppActivity;
import org.androidpn.demoapp.UserInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/** 
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = "Notifier";

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private SharedPreferences sharedPrefs;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Context getContext() {
        return context;
    }
    
    public void notify(String recipient,String chatXml){
    	if(isNotificationEnabled()){
    		 if (isNotificationToastEnabled()) {
                 Toast.makeText(context, "recved:"+chatXml, Toast.LENGTH_LONG).show();
             }
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
             notification.tickerText = recipient+":"+chatXml.substring(0,20<chatXml.length()?20:chatXml.length())+"..."; //通知栏显示的文字，可以改为title
             
             Intent intent = new Intent(context,
                     ChatsActivity.class);
             intent.putExtra("recipient", recipient);
             
             PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
                     intent, PendingIntent.FLAG_UPDATE_CURRENT);
             notification.setLatestEventInfo(context, "您收到新消息", notification.tickerText,
                     contentIntent);
             notificationManager.notify(random.nextInt(), notification);
    	}
    }
    
    public void notify(String notificationId, String apiKey, String title,
            String message, String uri,String from,String packetId) {
        Log.d(LOGTAG, "notify()...");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + apiKey);
        Log.d(LOGTAG, "notificationTitle=" + title);
        Log.d(LOGTAG, "notificationMessage=" + message);
        Log.d(LOGTAG, "notificationUri=" + uri);

        if (isNotificationEnabled()) {
            // Show the toast
            if (isNotificationToastEnabled()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

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
            notification.tickerText = message; //通知栏显示的文字，可以改为title
           
            
            Intent intent = new Intent(context,
                    NotificationDetailsActivity.class);
            intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            intent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            intent.putExtra(Constants.NOTIFICATION_TITLE, title);
            intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            intent.putExtra(Constants.NOTIFICATION_URI, uri);
            intent.putExtra(Constants.NOTIFICATION_FROM, from);
            intent.putExtra(Constants.PACKET_ID, packetId);
            
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //点击通知栏通知后转向的activity，是NotificationDetailsActivity
            PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setLatestEventInfo(context, title, message,
                    contentIntent);
            notificationManager.notify(random.nextInt(), notification);

            //            Intent clickIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLICKED);
            //            clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
            //            clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            //            clickIntent.putExtra(Constants.NOTIFICATION_URI, uri);
            //            //        positiveIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clickIntent, 0);
            //
            //            notification.setLatestEventInfo(context, title, message,
            //                    clickPendingIntent);
            //
            //            Intent clearIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLEARED);
            //            clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            //        negativeIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clearIntent, 0);
            //            notification.deleteIntent = clearPendingIntent;
            //
            //            notificationManager.notify(random.nextInt(), notification);

        } else {
            Log.w(LOGTAG, "Notificaitons disabled.");
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

	public void notifyXmppConnected() {
		Log.i(LOGTAG,"xmppconnected");
	   Intent intent = new Intent(context,
               DemoAppActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
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
        notification.tickerText ="xmpp连接上了";
        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
                contentIntent);
        notificationManager.notify(random.nextInt(), notification);
		
	}

	public void notifyXmppConnecting() {
		Log.i(LOGTAG,"xmppconnecting");
	  Intent intent = new Intent(context,
               DemoAppActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
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
        notification.tickerText ="xmpp连接中";
        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
                contentIntent);
        notificationManager.notify(random.nextInt(), notification);
			
	}

	public void notifyXmppConnectionClosed() {
		 Log.i(LOGTAG,"xmppconnectClosed");
		// TODO Auto-generated method stub
		 Intent intent = new Intent(context,
	               DemoAppActivity.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
	                intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
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
	        notification.tickerText ="xmpp连接关闭";
	        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
	                contentIntent);
	        notificationManager.notify(random.nextInt(), notification);
	}

	public void notifyXmppConnectionError() {
		// TODO Auto-generated method stub
		 Intent intent = new Intent(context,
	               DemoAppActivity.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
	                intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
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
	        notification.tickerText ="xmpp连接出现错误，即将重连";
	        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
	                contentIntent);
	        notificationManager.notify(random.nextInt(), notification);
		
	}

	public void notifyXmppConnectFailed() {
		Log.i(LOGTAG,"xmppconnectFailed");
		 Intent intent = new Intent(context,
	               DemoAppActivity.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
	                intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
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
	        notification.tickerText ="xmpp连接失败";
	        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
	                contentIntent);
	        notificationManager.notify(random.nextInt(), notification);
	}

	public void notifyReconnectionThreadStart(int wait) {
		 Intent intent = new Intent(context,
	               DemoAppActivity.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(context, random.nextInt(),
	                intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
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
	        notification.tickerText ="将于"+wait+"秒后重连";
	        notification.setLatestEventInfo(context, "连接消息", notification.tickerText,
	                contentIntent);
	        notificationManager.notify(random.nextInt(), notification);
	}
}
