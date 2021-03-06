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
import java.util.List;
import java.util.Map;

import org.androidpn.server.model.App;
import org.androidpn.server.model.User;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.util.Pair;

/**
 * Static constants for this package.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Constants {

    public static final String SHARED_PREFERENCE_NAME = "client_preferences";

    // PREFERENCE KEYS

    public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

    public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

    public static final String API_KEY = "API_KEY";

    public static final String VERSION = "VERSION";

    public static final String XMPP_HOST = "XMPP_HOST";

    public static final String XMPP_PORT = "XMPP_PORT";

    public static final String XMPP_USERNAME = "XMPP_USERNAME";

    public static final String XMPP_PASSWORD = "XMPP_PASSWORD";

    // public static final String USER_KEY = "USER_KEY";

    public static final String DEVICE_ID = "DEVICE_ID";

    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

    public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

    public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

    public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

    public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

    public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

    // NOTIFICATION FIELDS

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";
    
    public static final String PACKET_ID = "PACKET_ID";
    
    public static final String NOTIFICATION_FROM = "NOTIFICATION_FROM";

    // INTENT ACTIONS

    public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";
    
    public static final String ACTION_SHOW_CHAT = "org.androidpn.client.SHOW_CHAT";

	public static final String ACTION_CHAT_SENT = "org.androidpn.client.CHAT_SENT";
	
	  public static final String ACTION_CHAT_CLICKED = "org.androidpn.client.CHAT_CLICKED";

    public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";

    public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";

    public static XmppManager xmppManager = null;
    
    public static List<User> friendList=null;
    
	public static List<App> appList;
	
    public static List<Pair> packetList=null;
    
    //public static NotificationService notificationService=null;
    
    public static ServiceManager serviceManager = null;
    
    public static final String USER_SUBSCRIPTION = "USER_SUBSCRIPTION"; //�û��Ķ�����Ŀ

	public static final String XMPP_CONNECTED = "org.androidpn.client.XMPP_CONNECTED";
	public static final String XMPP_CONNECTING ="org.androidpn.client.XMPP_CONNECTING";
	public static final String XMPP_CONNECT_FAILED = "org.androidpn.client.XMPP_CONNECT_FAILED";
	public static final String XMPP_CONNECTION_CLOSED = "org.androidpn.client.XMPP_CONNECTION_CLOSED";
	public static final String XMPP_CONNECTION_ERROR = "org.androidpn.client.XMPP_CONNECTION_ERROR";
	
	public static final String RECONNECTION_THREAD = "org.androidpn.client.RECONNECTION_THREAD";

	public static final String SERVICE_CREATED =  "org.androidpn.client.SERVICE_CREATED";
	public static final String SERVICE_ONBIND =  "org.androidpn.client.SERVICE_ONBIND";
	public static final String SERVICE_DESTROYED =  "org.androidpn.client.SERVICE_DESTROYED";
	public static final String SERVICE_ONUNBIND =  "org.androidpn.client.SERVICE_ONUNBIND";

	public static final String KEEP_RECONNECT = "org.androidpn.client.KEEP_RECONNECT";



}
