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

import org.androidpn.demoapp.DemoAppActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class NotificationReceiver extends BroadcastReceiver {

    private static final String LOGTAG = "NotificationReceiver";

    //    private NotificationService notificationService;

    public NotificationReceiver() {
    }

    //    public NotificationReceiver(NotificationService notificationService) {
    //        this.notificationService = notificationService;
    //    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(LOGTAG, "NotificationReceiver.onReceive() action="+action);
 
        //通知到来 
        if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
            String notificationId = intent
                    .getStringExtra(Constants.NOTIFICATION_ID);
            String notificationApiKey = intent
                    .getStringExtra(Constants.NOTIFICATION_API_KEY);
            String notificationTitle = intent
                    .getStringExtra(Constants.NOTIFICATION_TITLE);
            String notificationMessage = intent
                    .getStringExtra(Constants.NOTIFICATION_MESSAGE); 
            String notificationUri = intent
                    .getStringExtra(Constants.NOTIFICATION_URI);
            String notificationFrom = intent
            		.getStringExtra(Constants.NOTIFICATION_FROM);
            String packetId = intent
    				.getStringExtra(Constants.PACKET_ID);
            
            Log.d(LOGTAG, "notificationId=" + notificationId);
            Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
            Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
            Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
            Log.d(LOGTAG, "notificationUri=" + notificationUri);

            Notifier notifier = new Notifier(context);
            notifier.notify(notificationId, notificationApiKey,
                    notificationTitle, notificationMessage, notificationUri,notificationFrom,packetId);
        }
        //新回复
        else if(Constants.ACTION_SHOW_CHAT.equals(action)){
        	String chatXml=intent.getStringExtra("chatXml"),
        			recipient=intent.getStringExtra("recipient");
        	Notifier notifier=new Notifier(context);
        	notifier.notify(recipient,chatXml);
        }
        
        else if(Constants.XMPP_CONNECTED.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("连接信息","已经连接");//XmppConnected();
        }
        
        else if(Constants.XMPP_CONNECTING.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("连接信息","正在连接");
        }
        
        else if(Constants.XMPP_CONNECTION_CLOSED.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("连接信息","连接关闭");
        }
        else if(Constants.XMPP_CONNECTION_ERROR.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("连接信息","连接发生错误，正在重连");
        }
        else if(Constants.XMPP_CONNECT_FAILED.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("连接信息","连接失败");
        }
        //点击通知
        else if (Constants.ACTION_NOTIFICATION_CLICKED.equals(action)) {
        	Log.i(LOGTAG, "点击通知");
        
        }
        //清除通知
        else if (Constants.ACTION_NOTIFICATION_CLEARED.equals(action)) {
			Log.i(LOGTAG, "清除通知");
		}
        
        else if(Constants.RECONNECTION_THREAD.equals(action)){
        	int wait=intent.getIntExtra("wait", 0);
//        	Intent intent1=new Intent(NotificationReceiver.this,DemoAppActivity.class);
//        	intent1.putExtra("action", "reconnection").putExtra("wait",wait);
        	String type=intent.getStringExtra("type");
        	if(type.equals("reconnectionStart")){
        		new Notifier(context).notifyMsg("重连进程", "启动");
        	}
        	else if(type.equals("reconnectionAlive")){
        		new Notifier(context).notifyMsg("重连进程", "在运行中...");
        	}
//        	else
//        		new Notifier(context).notifyMsg("连接进程", "将会在"+wait+"秒后测试连接");
        }
        else if(Constants.KEEP_RECONNECT.equals(action)){
        	Notifier notifier=new Notifier(context);
        	notifier.notifyMsg("保持重连","正在运行");//XmppConnected();
        }
        
        else if(Constants.SERVICE_CREATED.equals(action)){
        	new Notifier(context).notifyMsg("后台服务信息", "oncreate");
        }
        else if(Constants.SERVICE_DESTROYED.equals(action)){
        	new Notifier(context).notifyMsg("后台服务信息", "已销毁");
        }
//        else if(Constants.SERVICE_ONBIND.equals(action)){
//        	new Notifier(context).notifyMsg("后台服务信息", "onbind");
//        }
//        else if(Constants.SERVICE_ONUNBIND.equals(action)){
//        	new Notifier(context).notifyMsg("后台服务信息", "onunbind");
//        }
    }

}
