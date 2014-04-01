/*
a * Copyright (C) 2010 Moduad Co., Ltd.
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.crypto.Mac;

import org.androidpn.demoapp.UserInfo;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = "XmppManager";

    private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

    private Context context;

    private NotificationService.TaskSubmitter taskSubmitter;

    private NotificationService.TaskTracker taskTracker;

    private SharedPreferences sharedPrefs;

    private String xmppHost;

    private int xmppPort;

    private XMPPConnection connection;

    private String username;

    private String password;

    private ConnectionListener connectionListener;

    private PacketListener notificationPacketListener;
    
    //private PacketListener msgPacketListener;
    private PacketListener chatPacketListener;

    private Handler handler;

    private List<Runnable> taskList;

    private boolean running = false;

    private Future<?> futureTask;

    private ReconnectionThread reconnection;
    
    private List<Pair<PacketListener,PacketFilter>> packetListenerList=new LinkedList<Pair<PacketListener, PacketFilter>>();

    public XmppManager(NotificationService notificationService) {
        context = notificationService;
        taskSubmitter = notificationService.getTaskSubmitter();
        taskTracker = notificationService.getTaskTracker();
        sharedPrefs = notificationService.getSharedPreferences();

        xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
        username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
        password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);
        chatPacketListener=new ChatPacketListener(this);
        
        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        reconnection = new ReconnectionThread(this,0);
       
        // packet filter
        PacketFilter chatPacketFilter=new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				// TODO Auto-generated method stub
				return packet.getPacketID() != null;
			}
        };
        PacketFilter notificationPacketFilter = new PacketTypeFilter(NotificationIQ.class);
//        		msgPacketFilter = new PacketTypeFilter(Message.class),
//        		testPacketFilter = new PacketTypeFilter(Packet.class);
        packetListenerList.add(new Pair<PacketListener, PacketFilter>(notificationPacketListener, notificationPacketFilter));
        packetListenerList.add(new Pair<PacketListener, PacketFilter>(chatPacketListener,chatPacketFilter));
    }

    public Context getContext() {
        return context;
    }

    public void connect() {
        Log.d(LOGTAG, "connect()...");
        submitLoginTask();
    }
    
    public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        terminatePersistentConnection();
    }
    
    /*
     * stop connection 
     * called by disconnect()
     */
    public void terminatePersistentConnection() {
        Log.d(LOGTAG, "terminatePersistentConnection()!!!!");
        Runnable runnable = new Runnable() {

            final XmppManager xmppManager = XmppManager.this;

            public void run() {
                if (xmppManager.isConnected()) {
                    Log.d(LOGTAG, "terminatePersistentConnection()... run()");
                    //remove connection's packet-listeners
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getNotificationPacketListener());
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getChatPacketListener());
                    xmppManager.getConnection().disconnect();
                }
                xmppManager.runTask();
            }

        };
        addTask(runnable);
    }
    
    
    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public PacketListener getNotificationPacketListener() {
        return notificationPacketListener;
    }
    
    public PacketListener getChatPacketListener() {
        return chatPacketListener;
    }

    /*
     * reconnection thread keeps connection logined in
     * by calling connect() 
     */
    public void startReconnectionThread() {
    	Log.i("xmppmanager#startreconnectinthread", "");
        synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
                Log.i(LOGTAG,"startReconnectionThread");
            }
//            else{
//            	reconnection.handler.post(new Runnable(){
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						reconnection.setWait(6);
//					}
//            	});
//            }
        }
    }
    public void delayReconnectionThread() {
       reconnection.waiting=100;
    }
    
    public XmppManager shiftReconnectionThread(){
    	if(!isAuthenticated()){
    		//start reconnect as soon as possible
    		reconnection.waiting=0;
    	}
    	return this;
    }

    public Handler getHandler() {
        return handler;
    }

    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }

    public List<Runnable> getTaskList() {
        return taskList;
    }

    public Future<?> getFutureTask() {
        return futureTask;
    }

    /*
     * first get a task out of the task queue's front 
     * then give this task to tasksubmitter to run
     * remember this means a task is done, in and only which condition, it can do the next task
     */
    public void runTask() {
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);//give to tasksubmitter
//                if (futureTask == null) {
//                    taskTracker.decrease();
//                }
            }
        }
        taskTracker.decrease();
    }

    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }
    /*
     * judge if the connection is usable ( can send and receive normal packet ) now
     */
    public boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }
    
    /*
     * judge the user is registered to the xmpp server?
     */
    private boolean isRegistered() {
    	//这里修改为，如果share
    	//return (Constants.XMPP_USERNAME.equals("") || Constants.XMPP_PASSWORD.equals(""));
        return sharedPrefs.contains(Constants.XMPP_USERNAME) && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }
    
    /*
     * notice connecttask may renew a connection
     */
    private void submitConnectTask() {
       // Log.d(LOGTAG, "submitConnectTask()...");
        addTask(new ConnectTask());
    }
    
    /*
     * notice: submitConnectTask -> connecttask may renew a connection
     */
    private void submitRegisterTask() {
      //  Log.d(LOGTAG, "submitRegisterTask()...");
        submitConnectTask();
        addTask(new RegisterTask());
    }

    /*
     * login to the xmppserver ( everytime:
     * first may submitconnecttask
     * then may run a register task  (contains a connect task)
     * then run a login in task  
     */
    private void submitLoginTask() {
       // Log.d(LOGTAG, "submitLoginTask()...");
        submitRegisterTask();
        addTask(new LoginTask());
    }
    /*
     * first add a task to the task-queue's tail
     * then get out a task from the queue's front and give it to tasksubmitter to run
     */
    private void addTask(Runnable runnable) {
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    //taskTracker.decrease();
                }
            } else {
            	runTask();//synchronized: first erase the task queue's front
                taskList.add(runnable);//then append runnable to task queue's tail
            }
        }
    }

    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }
    
    //no more used
//    private void addPacketListener(PacketListener listener,PacketFilter filter){
//    	 packetListenerList.add(new Pair<PacketListener, PacketFilter>(listener,filter));
//    	 if(connection!=null) connection.addPacketListener(listener, filter);
//    }
    
    private void bindPacketListeners(XMPPConnection con){
    	for(Pair<PacketListener,PacketFilter> p : packetListenerList){
    		con.addPacketListener(p.first, p.second);
    	}
    }
    
    /**
     * connect to the xmppserver if currently not connected
     * above action will new the connection
     */
    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "ConnectTask.run()...");

            if (!xmppManager.isConnected()) {
                // Create the configuration for this new connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        xmppHost, xmppPort);
                // connConfig.setSecurityMode(SecurityMode.disabled);
                connConfig.setSecurityMode(SecurityMode.required);
                connConfig.setSASLAuthenticationEnabled(false);
                connConfig.setCompressionEnabled(false);

                XMPPConnection connection = new XMPPConnection(connConfig);
                xmppManager.setConnection(connection);

                try {
                    // Connect to the server
                    connection.connect();
                    Log.i(LOGTAG, "ConnectTask# XMPP renew a connection and connect successfully");

                    // packet provider
                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification",
                            new NotificationIQProvider());

                } catch (XMPPException e) {
                    Log.e(LOGTAG, "XMPP connection failed", e);
                    //once the connection is not successful, then invoke the reconnection thread 
                    //which will try to add connect task to the tasklist for reconnection later
                    xmppManager.startReconnectionThread();
                }
                //stop blocking...
                xmppManager.runTask();

            } else {
                Log.i(LOGTAG, "XMPP connected already");
                xmppManager.runTask();
            }
        }
    }

    /**
     * register to the server if never logged in (in other words, not registered) 
     * check the shared memory to see if the client is registered
     */
    private class RegisterTask implements Runnable {

        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "RegisterTask.run()...");

            if (!xmppManager.isRegistered()) {
            	
                //final String newUsername = newRandomUUID();
                //final String newPassword = newRandomUUID();
            	//这里读取自己保存的UserInfo
            	/*
            	 * UserInfo is in the shared memory shared by the activities and back-ground tasks
            	 */
            	UserInfo userInfo= (UserInfo)getContext().getApplicationContext();
            	final String newUsername = userInfo.getMyUserName();
            	final String newPassword = userInfo.getMyUserPWD();
            	Log.i("xiaobingo", "XmppManager中获得username："+newUsername);
            	Log.i("xiaobingo", "XmppManager中获得userpassword："+newPassword);
            	//registration is a packet used for registration, and its type is set request
                Registration registration = new Registration();
                
                //packetFilter filters out the packet 
                //which is the server's reply of the registration (the packet id will be the same)
                //and the packet type is IQ
                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {
                	//then parse the packet and get the result of the registration
                    public void processPacket(Packet packet) {
                        Log.d("RegisterTask.PacketListener", 
                                "processPacket().....");
                        Log.d("RegisterTask.PacketListener", "packet="
                                + packet.toXML());

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (!response.getError().toString().contains(
                                        "409")) {
                                    Log.e(LOGTAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError()
                                                            .getCondition());
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                            	//successfully get the registration reply
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);
                                Log.d(LOGTAG, "username=" + newUsername);
                                Log.d(LOGTAG, "password=" + newPassword);
                                
                                //put the registration result into shared memory
                                Editor editor = sharedPrefs.edit();
                                editor.putString(Constants.XMPP_USERNAME,
                                        newUsername);
                                editor.putString(Constants.XMPP_PASSWORD,
                                        newPassword);
                                editor.commit();
                                Log.i(LOGTAG,"Account registered successfully");
                                xmppManager.runTask();//stop blocking( remember task queue is a block queue)
                            }
                        }
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                // registration.setTo(xmppHost);
                // Map<String, String> attributes = new HashMap<String, String>();
                // attributes.put("username", rUsername);
                // attributes.put("password", rPassword);
                // registration.setAttributes(attributes);
                registration.addAttribute("username", newUsername);
                registration.addAttribute("password", newPassword);
                connection.sendPacket(registration);

            } else {
                Log.i(LOGTAG, "Account registered already");
                xmppManager.runTask();
            }
        }
    }
    
    

    /**
     * Everytime a new connection must login before being usable.
     */
    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "LoginTask.run()...");

            if (!xmppManager.isAuthenticated()) {
                Log.d(LOGTAG, "username=" + username);
                Log.d(LOGTAG, "password=" + password);

                try {//登陆验证
                	 getContext().sendBroadcast(new Intent(Constants.XMPP_CONNECTING).
                     		putExtra("from", "XmppManager").
                     			putExtra("type", "connecting"));
//                	 
                    xmppManager.getConnection().login(
                            xmppManager.getUsername(),
                            xmppManager.getPassword(), XMPP_RESOURCE_NAME);
                    Log.d(LOGTAG, "Loggedn in successfully");

                    // add connection listener to the new logined (which means usable) connection
                    if (xmppManager.getConnectionListener() != null) {
                        xmppManager.getConnection().addConnectionListener(
                                xmppManager.getConnectionListener());
                    }
                    
                    //bind the packet listeners: include notificationpacketlistener and messagepacketlistener
                    bindPacketListeners(connection);
                    
                    //once the connection has been built up, then 
                    //just run a thread inside the connection to keep it alive.
                    //question: when the old connection has already been destroyed,
                    //where will the old keep-alive thread go?
                    //this thread will be gone with its host-object: the old conn's packet-writer
                    getConnection().startKeepAliveThread(xmppManager);
                    //pause reconnection since connected
                    // this should not pause, since you can go offline at any time , and you need to reconnect. 
                    delayReconnectionThread();
                    //tell some one online now
                    getContext().sendBroadcast(new Intent(Constants.XMPP_CONNECTED).
                    		putExtra("from", "XmppManager").
                    			putExtra("type", "connected"));
                    
                } catch (XMPPException e) {
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
                    if (errorMessage != null
                            && errorMessage
                                    .contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                        xmppManager.reregisterAccount();
                        return;
                    }
                   
                    //you should not run reconnection immediately, since it's not proper
                    //and the tasklist can not be blocked ,
                    //if could , i think it will be better
                    //once the connection is not successful, then invoke the reconnection thread 
                    //which will try to add connect task to the tasklist for reconnection later
                    xmppManager.startReconnectionThread();

                } catch (Exception e) {
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    xmppManager.startReconnectionThread();
                }

                xmppManager.runTask();
            } else {
                Log.i(LOGTAG, "Logged in already");
                xmppManager.runTask();
            }

        }
    }
    
    /*
     * send a packet out to someone
     */
    public void sendMsg(Packet msg){
    	shiftReconnectionThread();
    	submitLoginTask();
    	addTask(new SendMsgTask(msg));
    }
    

    
    private class SendMsgTask implements Runnable{
    	final Packet packet;
    	final XmppManager xmppManager;

        private SendMsgTask(Packet p) {
             this.xmppManager = XmppManager.this;
             this.packet=p;
        }
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("xmppmanager#sendmsgtask","run and sending packet");
			xmppManager.getConnection().sendPacket(packet);
		}
    }

}
