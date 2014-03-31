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
import android.os.Handler;
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

    private Integer waiting;

    ReconnectionThread(XmppManager xmppManager,Integer waiting) {
        this.xmppManager = xmppManager;
        this.waiting = waiting;
    }

    public void run() {
    	waiting=0;
        try {
        	xmppManager.getContext().sendBroadcast(new Intent(Constants.RECONNECTION_THREAD).putExtra("type", "reconnectionStart").putExtra("wait", waiting()));
            while (!isInterrupted()) {
                Log.d(LOGTAG, "Trying to reconnect in " + waiting()
                        + " seconds");
                xmppManager.getContext().sendBroadcast(new Intent(Constants.RECONNECTION_THREAD).putExtra("type", "reconnection").putExtra("wait", waiting()));
                Thread.sleep((long) waiting() * 1000L);//waiting() to waiting
                xmppManager.connect();
                waiting++;
                if(waiting%60==0){
                	xmppManager.getContext().sendBroadcast(new Intent(Constants.RECONNECTION_THREAD).putExtra("type", "reconnectionAlive").putExtra("wait", waiting()));
                }
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
    public Handler handler=new Handler();
    public void setWait(int wait){
    	this.waiting=wait;
    }
    private int waiting() {
    	if(waiting>18){
    		return 120;
    	}
        if (waiting > 12) {
            return 45;
        }
        if (waiting >7) {
            return 25;
        }
        if(waiting>2){
        	return 5;
        }
        return 1;
    }
}
