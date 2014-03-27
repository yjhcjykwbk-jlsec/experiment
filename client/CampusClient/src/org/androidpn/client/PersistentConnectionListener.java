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

import org.androidpn.demoapp.UserInfo;
import org.jivesoftware.smack.ConnectionListener;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/** 
 * A listener class for monitoring connection closing and reconnection events.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class PersistentConnectionListener implements ConnectionListener {

    private static final String LOGTAG = "PersistentConnectionListener";
    private final XmppManager xmppManager;

	public PersistentConnectionListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void connectionClosed() {
        Log.d(LOGTAG, "connectionClosed()...");
    }

    @Override
    /*
     * close a connetion and reconnect the connection
     * @see org.jivesoftware.smack.ConnectionListener#connectionClosedOnError(java.lang.Exception)
     */
    public void connectionClosedOnError(Exception e) {
        Log.d(LOGTAG, "connectionClosedOnError()...");
        if (xmppManager.getConnection() != null
                && xmppManager.getConnection().isConnected()) {
            xmppManager.getConnection().disconnect();
        }
        Log.i(LOGTAG,"connection restart");
        //broadcast connect-error-event to other threads
        Intent intent=new Intent(Constants.XMPP_CONNECTION_ERROR);
        xmppManager.getContext().sendBroadcast(intent); 
        xmppManager.startReconnectionThread();
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(LOGTAG, "reconnectingIn()...");
        Intent intent=new Intent(Constants.XMPP_CONNECTING);
        xmppManager.getContext().sendBroadcast(intent); 
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d(LOGTAG, "reconnectionFailed()!!!");
        Intent intent=new Intent(Constants.XMPP_CONNECT_FAILED);
        xmppManager.getContext().sendBroadcast(intent); 
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(LOGTAG, "reconnectionSuccessful()...");
        //broadcast connected-event to other threads
        Intent intent=new Intent(Constants.XMPP_CONNECTED);
        xmppManager.getContext().sendBroadcast(intent); 
    }

}
