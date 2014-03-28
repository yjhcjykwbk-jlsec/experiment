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

import android.content.Intent;
import android.util.Log;

/** 
 * A thread class for recennecting the server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ReconnectionThread extends Thread {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ReconnectionThread.class);

    private final XmppManager xmppManager;

    private int waiting;

    ReconnectionThread(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        this.waiting = 0;
    }

    public void run() {
    	waiting=0;
        try {
            while (!isInterrupted()) {
                Log.d(LOGTAG, "Trying to reconnect in " + waiting()
                        + " seconds");
            	Intent intent=new Intent(Constants.RECONNECTION_THREAD_START);
            	intent.putExtra("waiting", waiting());
            	xmppManager.getContext().sendBroadcast(intent);
                Thread.sleep((long) waiting() * 1000L);//waiting() to waiting
                xmppManager.connect();
                waiting++;
                if(waiting>15) waiting=15;
            }
            Log.d(LOGTAG,"reconnection interrupted and wait for next restart");
        } catch (final InterruptedException e) {
            xmppManager.getHandler().post(new Runnable() {
                public void run() {
                    xmppManager.getConnectionListener().reconnectionFailed(e);
                }
            });
        }
    }

    private int waiting() {
        if (waiting > 20) {
            return 60;
        }
        if (waiting > 13) {
            return 30;
        }
        return waiting <= 7 ? 1 : 6;
    }
}
