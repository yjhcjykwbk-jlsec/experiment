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

    public Integer waiting;

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
                
                //every 3 seconds, check if need reconnection immediately(waiting change to 0)
                int wait=waiting();
                for(int i=0;i<wait;i+=5){
                	if(waiting==0) break;
                	Thread.sleep((long) 5 * 1000L);//waiting() to waiting
                }
                
                xmppManager.connect();
                
//                if(waiting()>20){
//                	xmppManager.getContext().sendBroadcast(new Intent(Constants.RECONNECTION_THREAD).putExtra("type", "reconnectionAlive").putExtra("wait", waiting()));
//                }
                waiting++;
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
        if (waiting > 8) {
            return 100;
        }
        if (waiting>3) {
            return 15;
        }
        if(waiting>1){
        	return 10;
        }
        return 2;
    }
    /**
     * then you know the reconnection thread has run how long since last time wait=0
     * @return
     */
    /*    
     * private int getRunTime(){
    	int t=0;
    	if(waiting>15) t=170+40*(waiting-15);
    	else if(waiting>10) t=70+20*(waiting-10);
    	else if(waiting>4) t=10+10*(waiting-4);
    	else t=2*(waiting+1);
    	return t;
    }*/
}
