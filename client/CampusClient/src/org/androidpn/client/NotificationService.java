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

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.androidpn.demoapp.AlarmReceiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Service that continues to run in background and respond to the push 
 * notification events from the server. This should be registered as service
 * in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationService extends Service {

    private static final String LOGTAG = "NotificationService";

    public static final String SERVICE_NAME = "org.androidpn.client.NotificationService";

    private TelephonyManager telephonyManager;

    //    private WifiManager wifiManager;
    //
    //    private ConnectivityManager connectivityManager;

    private BroadcastReceiver notificationReceiver;

    private BroadcastReceiver connectivityReceiver;

    private PhoneStateListener phoneStateListener;

    private ExecutorService executorService;

    private TaskSubmitter taskSubmitter;

    private TaskTracker taskTracker;

    private XmppManager xmppManager;

    private SharedPreferences sharedPrefs;

    private String deviceId;
    
    private LocalBinder myBinder = new LocalBinder();
    
   
    public NotificationService() {
    	/**
    	 * notificationreceiver是BroadcastReceiver的子类
    	 * 用于接收推送广播并用notificationManager通知用户
    	 */
        notificationReceiver = new NotificationReceiver();
        
        /**
         * connectivityReceiver用于接收手机网络状态的广播
         * 来管理XMPPmanager与服务器的连接于断开
         */
        connectivityReceiver = new ConnectivityReceiver(this);
        
        phoneStateListener = new PhoneStateChangeListener(this);
        
        //线程池
        executorService = Executors.newSingleThreadExecutor();
        
        //向上面的线程池提交一个task
        taskSubmitter = new TaskSubmitter(this);
        
        //任务计数器，维护当前工作的task
        taskTracker = new TaskTracker(this);
    }

    @Override
    public void onCreate() {
        Log.d(LOGTAG, "onCreate()...");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        // Get deviceId
        deviceId = telephonyManager.getDeviceId();
       // deviceId = telephonyManager.getSubscriberId();
        Log.d(LOGTAG, "deviceID:"+deviceId);
        // Log.d(LOGTAG, "deviceId=" + deviceId);
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.DEVICE_ID, deviceId);
        editor.commit(); 

        // If running on an emulator
        if (deviceId == null || deviceId.trim().length() == 0
                || deviceId.matches("0+")) { 
            if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
                deviceId = sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID,
                        "");
            } else {
                deviceId = (new StringBuilder("EMU")).append(
                        (new Random(System.currentTimeMillis())).nextLong())
                        .toString();
                editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
                editor.commit();
            }
        }
        Log.d(LOGTAG, "deviceId=" + deviceId);

        
        
        xmppManager = new XmppManager(this);
        //将xmppManager对象放入全局变量中，方便其他地方使用
        Constants.xmppManager = xmppManager;
        taskSubmitter.submit(new Runnable() {
            public void run() {
                NotificationService.this.start();
            }
        });
//        Toast.makeText(getApplicationContext(),"notificationService created",Toast.LENGTH_LONG);
        keepReconnect();
        
        sendBroadcast(new Intent(Constants.SERVICE_CREATED)); 
        
    }
   
	/**
	 * Keep a reconnection
	 * @author:x
	 */
	 AlarmManager am;
	 PendingIntent pIntent;
	 private void keepReconnect(){
    	Intent intent=new Intent(this,AlarmReceiver.class);
    	pIntent=PendingIntent.getBroadcast(this, 1, intent, 0);//getBroadcast(this, 0, intent, 0);
    	 //触发服务的起始时间，根据传来的时间启动
        long triggerAtTime = SystemClock.elapsedRealtime()+20*1000;
        am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
        //期待的效果是：即使系统待机，依然能够定时：每两分钟检查一次连接
        //因为系统待机的时候，普通的定时thread一般会被挂起．
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,600*1000, pIntent);
	 }
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOGTAG, "onStart()...");
//        Toast.makeText(getApplicationContext(),"notificationService start",Toast.LENGTH_LONG);
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "onDestroy()...");
        sendBroadcast(new Intent(Constants.SERVICE_DESTROYED)); 
 		am.cancel(pIntent);
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "onBind()...");
//        Toast.makeText(getApplicationContext(),"notificationService bind",Toast.LENGTH_LONG);
        sendBroadcast(new Intent(Constants.SERVICE_ONBIND)); 
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOGTAG, "onRebind()...");
//        Toast.makeText(getApplicationContext(),"notificationService rebind",Toast.LENGTH_LONG);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOGTAG, "onUnbind()...");
//        Toast.makeText(getApplicationContext(),"notificationService unbind",Toast.LENGTH_LONG);
        sendBroadcast(new Intent(Constants.SERVICE_ONUNBIND)); 
        return true;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }

    public TaskTracker getTaskTracker() {
        return taskTracker;
    }

    public XmppManager getXmppManager() {
        return xmppManager;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPrefs;
    }

    public String getDeviceId() {
        return deviceId;
    }
    
    public void connect() {
        Log.d(LOGTAG, "connect()...");
        //all asynchronously
        taskSubmitter.submit(new Runnable() {
            public void run() {
                NotificationService.this.getXmppManager().connect();
            }
        });
    }

    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                NotificationService.this.getXmppManager().disconnect();
            }
        });
    }
    
    /**
     * 为notificationReceiver注册收听intent-broadcast
     * 在notificationReceiver.onReceive中处理，其处理方式是notifier.notify
     */
    private void registerNotificationReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
        filter.addAction(Constants.ACTION_SHOW_CHAT);
        //@Todo
        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
        
//      filter.addAction(Constants.XMPP_CONNECTED);
    	filter.addAction(Constants.XMPP_CONNECTION_CLOSED);
    	filter.addAction(Constants.XMPP_CONNECT_FAILED);
    	filter.addAction(Constants.XMPP_CONNECTION_ERROR);
//    	filter.addAction(Constants.XMPP_CONNECTING);
    	
    	filter.addAction(Constants.RECONNECTION_THREAD);
    	filter.addAction(Constants.KEEP_RECONNECT);
    	
//        filter.addAction(Constants.SERVICE_CREATED);
//        filter.addAction(Constants.SERVICE_DESTROYED);
//        filter.addAction(Constants.SERVICE_ONBIND);
//        filter.addAction(Constants.SERVICE_ONUNBIND);
        registerReceiver(notificationReceiver, filter);
    }

    private void unregisterNotificationReceiver() {
        unregisterReceiver(notificationReceiver);
    }
    
    /**
     * filter out the connectivity intent
     * and register a observer connectivityReceiver on intent
     */
    private void registerConnectivityReceiver() {
        Log.d(LOGTAG, "registerConnectivityReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        IntentFilter filter = new IntentFilter();
        // filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        Log.d(LOGTAG, "unregisterConnectivityReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(connectivityReceiver);
    }

    private void start() {
        Log.d(LOGTAG, "start()...");
        registerNotificationReceiver();
        registerConnectivityReceiver();
        // Intent intent = getIntent();
        // startService(intent);
        xmppManager.connect();
    }

    private void stop() {
        Log.d("NotificationServiece", "stop()...");
        unregisterNotificationReceiver();
        unregisterConnectivityReceiver();
        xmppManager.disconnect();
        executorService.shutdown();
    }

    /**
     * Class for summiting a new runnable task.
     */
    public class TaskSubmitter {

        final NotificationService notificationService;

        public TaskSubmitter(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @SuppressWarnings("unchecked")
        public Future submit(Runnable task) {
            Future result = null;
            if (!notificationService.getExecutorService().isTerminated()
                    && !notificationService.getExecutorService().isShutdown()
                    && task != null) {
                result = notificationService.getExecutorService().submit(task);
            }
            return result;
        }
    }

    /**
     * Class for monitoring the running task count.
     */
    public class TaskTracker {

        final NotificationService notificationService;

        public int count;

        public TaskTracker(NotificationService notificationService) {
            this.notificationService = notificationService;
            this.count = 0;
        }

        public void increase() {
            synchronized (notificationService.getTaskTracker()) {
                notificationService.getTaskTracker().count++;
                //Log.d(LOGTAG, "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (notificationService.getTaskTracker()) {
                notificationService.getTaskTracker().count--;
                //Log.d(LOGTAG, "Decremented task count to " + count);
            }
        }
    }
    
    
    public class LocalBinder extends Binder {
    	//this getService function should be changed to public
    	public NotificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NotificationService.this;
        }
    }

}
