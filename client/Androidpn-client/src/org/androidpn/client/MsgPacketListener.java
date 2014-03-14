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

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.util.Log;

/** 
 * This class notifies the receiver of incoming notifcation packets asynchronously.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class MsgPacketListener implements PacketListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(MsgPacketListener.class);

    private final XmppManager xmppManager;

    public MsgPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {
        Log.d(LOGTAG, "MsgPacketListener.processPacket()...");
        if(packet==null){
        	Log.d(LOGTAG,"packet is null");
        }
        else Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
        if (packet instanceof Message) {
            Message msg = (Message) packet;
/*
            if (msg.getChildElementXML().contains(
                    "androidpn:iq:notification")) {
                String notificationId = notification.getId();
                String notificationApiKey = notification.getApiKey();
                String notificationTitle = notification.getTitle();
                String notificationMessage = notification.getMessage();
                //                String notificationTicker = notification.getTicker();
                String notificationUri = notification.getUri();
                String notificationFrom = notification.getFrom();
                String packetId = notification.getPacketID();

                Intent intent = new Intent(Constants.ACTION_SHOW_NOTIFICATION);
                intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
                intent.putExtra(Constants.NOTIFICATION_API_KEY,
                        notificationApiKey);
                intent
                        .putExtra(Constants.NOTIFICATION_TITLE,
                                notificationTitle);
                intent.putExtra(Constants.NOTIFICATION_MESSAGE,
                        notificationMessage);
                intent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
                intent.putExtra(Constants.NOTIFICATION_FROM, notificationFrom);
                intent.putExtra(Constants.PACKET_ID, packetId);
                
                //TODO FIXME 发送收到通知回执
                IQ result = NotificationIQ.createResultIQ(notification);
                
                try{
                	xmppManager.getConnection().sendPacket(result);
                }catch(Exception e){}
                
                xmppManager.getContext().sendBroadcast(intent); */
//            }
        Log.i("MsgPacketListener#processpacket",msg.getFrom()+":"+msg.getBody());
        }
    }
}
