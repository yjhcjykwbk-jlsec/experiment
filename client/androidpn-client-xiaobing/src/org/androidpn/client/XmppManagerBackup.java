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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.security.auth.PrivateCredentialPermission;

import org.androidpn.demoapp.DemoAppActivity;
import org.androidpn.demoapp.R;
import org.androidpn.demoapp.LoginActivity;
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
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManagerBackup extends Activity {

    private static final String LOGTAG = LogUtil.makeLogTag(XmppManagerBackup.class);

    private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";
    public static String getName,getPWD,newUsername,newPassword;
    private Context context;
   // private Context myContext;

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

    private Handler handler;

    private List<Runnable> taskList;

    private boolean running = false;

    private Future<?> futureTask;

    private Thread reconnection;

	public XmppManagerBackup(NotificationService notificationService) {
        context = notificationService;
        
        //task�ύ������������ά����������
        taskSubmitter = notificationService.getTaskSubmitter();
        //task�ļ�����
        taskTracker = notificationService.getTaskTracker();
        sharedPrefs = notificationService.getSharedPreferences();
        
        //��ȡ������Ϣ
        xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
        username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
        password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

        /*����xmpp����״̬�ļ��������鿴���뷢��Xmpp����״̬��5��
         * 1 connectionClosed
         * 2 connectionClosedOnError
         * 3 reconnectingIn
         * 4 reconnectionFailed
         * 5 reconnectionSuccessful
         */
       // connectionListener = new PersistentConnectionListener(this);
        
        /* ���������ͼ�����
         * �������������Ϣ���ͣ�NotificationPacketListener��
         * �Լ������ã���ͨ��XmppManager���͹㲥
         */
        //notificationPacketListener = new NotificationPacketListener(this);

        //��XMPP���쳣�������ӷ�����ʱ�����ڼ䷢���쳣�Ļ����������handler�д���
        handler = new Handler();
        //�������
        taskList = new ArrayList<Runnable>();
        
        /* ��xmppManager���쳣��������Ͽ�����ʱ
         * ReconnectionThread����һ����ʱ���ڳ�����������
         * Ҳ����˵����PersistentConnectionListener�������������쳣�Ͽ�����
         * �����ReconnectionThread���������ӵķ����Խ������ӳ���
         */
        //reconnection = new ReconnectionThread(this);
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

    public void terminatePersistentConnection() {
        Log.d(LOGTAG, "terminatePersistentConnection()...");
        Runnable runnable = new Runnable() {

            final XmppManagerBackup xmppManager = XmppManagerBackup.this;

            public void run() {
                if (xmppManager.isConnected()) {
                    Log.d(LOGTAG, "terminatePersistentConnection()... run()");
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getNotificationPacketListener());
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

    public void startReconnectionThread() {
        synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }
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

    public void runTask() {
        Log.d(LOGTAG, "runTask()...");
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            }
        }
        taskTracker.decrease();
        Log.d(LOGTAG, "runTask()...done");
    }

    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sharedPrefs.contains(Constants.XMPP_USERNAME)
                && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }

    private void submitConnectTask() {
        Log.d(LOGTAG, "submitConnectTask()...");
        addTask(new ConnectTask());
    }

    private void submitRegisterTask() {
        Log.d(LOGTAG, "submitRegisterTask()...");
        submitConnectTask();
        addTask(new RegisterTask());
    }

    private void submitLoginTask() {
        Log.d(LOGTAG, "submitLoginTask()...");
        submitRegisterTask();
       // addTask(new LoginTask());
    }

    private void addTask(Runnable runnable) {
        Log.d(LOGTAG, "addTask(runnable)...");
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            } else {
                taskList.add(runnable);
            }
        }
        Log.d(LOGTAG, "addTask(runnable)... done");
    }

    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    /**
     * A runnable task to connect the server. 
     */
    private class ConnectTask implements Runnable {

        final XmppManagerBackup xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManagerBackup.this;
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
                    Log.i(LOGTAG, "XMPP connected successfully");

                    // packet provider
                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification",
                            new NotificationIQProvider());

                } catch (XMPPException e) {
                    Log.e(LOGTAG, "XMPP connection failed", e);
                }

                xmppManager.runTask();

            } else {
                Log.i(LOGTAG, "XMPP connected already");
                xmppManager.runTask();
            }
        }
    }

    /**
     * A runnable task to register a new user onto the server. 
     */
    private class RegisterTask implements Runnable {

		final XmppManagerBackup xmppManager;

        private RegisterTask() {
            xmppManager = XmppManagerBackup.this;
        }

        public void run() {
            Log.i(LOGTAG, "RegisterTask.run()...");

            //��������û���ע��,����ע�����,����ѧ�š����롢ȷ������
            if (!xmppManager.isRegistered()) {
            	
            	Log.i("xiaobingo","��ʼ��ת����");
            	
            	//Intent intent = new Intent();
            	//intent.setClass(XmppManager.this, RegisterActivity.class);
            	Intent intent = new Intent(context,LoginActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(intent);
            	
            	
            	// ��ע���û�����������û��������룬������Ը�Ϊ�Լ����û���������
                newUsername = getName ; //�û�����Ϊѧ��
                newPassword = getPWD;
                Log.i("xiaobingo", "�û����ǣ�"+newUsername);
                Log.i("xiaobingo","�����ǣ�"+newPassword);
                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {

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
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);
                                Log.d(LOGTAG, "username=" + newUsername);
                                Log.d(LOGTAG, "password=" + newPassword);

                                Editor editor = sharedPrefs.edit();
                                editor.putString(Constants.XMPP_USERNAME,
                                        newUsername);
                                editor.putString(Constants.XMPP_PASSWORD,
                                        newPassword);
                                editor.commit();
                                Log
                                        .i(LOGTAG,
                                                "Account registered successfully");
                                xmppManager.runTask();
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

    //XmppManager����RegisterActivity�ش��û�����������
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub    	switch (resultCode){
		case 1:
				Bundle b = data.getExtras();
				getName = b.getString("name");
				getPWD = b.getString("password");
                Log.i("xiaobingo", "���յ����û����ǣ�"+getName);
                Log.i("xiaobingo","���յ��������ǣ�"+getPWD);;

		}    
}}
    
    /**
     * A runnable task to log into the server. 
     */
/*
    private class LoginTask implements Runnable {

        final XmppManagerBackup xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManagerBackup.this;
        }

        public void run() {
            Log.i(LOGTAG, "LoginTask.run()...");

            if (!xmppManager.isAuthenticated()) {
                Log.d(LOGTAG, "username=" + username);
                Log.d(LOGTAG, "password=" + password);

                try {
                    xmppManager.getConnection().login(
                            xmppManager.getUsername(),
                            xmppManager.getPassword(), XMPP_RESOURCE_NAME);
                    Log.d(LOGTAG, "Loggedn in successfully");

                    // connection listener
                    if (xmppManager.getConnectionListener() != null) {
                        xmppManager.getConnection().addConnectionListener(
                                xmppManager.getConnectionListener());
                    }

                    // packet filter
                    PacketFilter packetFilter = new PacketTypeFilter(
                            NotificationIQ.class);
                    // packet listener
                    PacketListener packetListener = xmppManager
                            .getNotificationPacketListener();
                    connection.addPacketListener(packetListener, packetFilter);
                    
                    getConnection().startKeepAliveThread(xmppManager);

                } catch (XMPPException e) {
                    Log.e(LOGTAG, "LoginTask.run()... xmpp error");
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
                    xmppManager.startReconnectionThread();

                } catch (Exception e) {
                    Log.e(LOGTAG, "LoginTask.run()... other error");
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

}
*/