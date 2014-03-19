package org.androidpn.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.androidpn.client.NotificationService.LocalBinder;
import org.androidpn.data.ChatInfo;
import org.androidpn.data.SessionManager;
import org.androidpn.demoapp.ChatActivity;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SessionService extends Service {
	private static String LOGTAG = "SessionService";
	private LocalBinder myBinder = new LocalBinder();
	private XmppManager manager = Constants.xmppManager;

	public SessionService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		manager.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				if (packet instanceof Message) {
					// 显示接收到的消息
					String jid = packet.getFrom();
					String username = jid.substring(0, jid.indexOf('@'));
					Log.i(LOGTAG, "recv packet from:" + username);
					SessionManager.addMsg(
						username,new ChatInfo(username, 
							((Message) packet).getBody(), new Date(System
							.currentTimeMillis()), packet
							.getPacketID(), false));

					Intent intent = new Intent(Constants.ACTION_SHOW_CHAT);
					intent.putExtra("recipient", username);
					intent.putExtra("message",((Message)packet).getBody());
					
				} else if (packet instanceof IQ) {
					if (((IQ) packet).getType() == IQ.Type.RESULT) {
						if (((IQ) packet).getError() == null) {
							// 发送的消息成功被服务器接收
							SessionManager.msgSent(packet.getPacketID());
							Log.i(LOGTAG, "recv a result IQ whose id is :"
									+ packet.getPacketID());
							Intent intent = new Intent(
									Constants.ACTION_CHAT_SENT);
							intent.putExtra("packetid", packet.getPacketID());
						}
					}
				}
			}
		}, new PacketFilter() {
			@Override
			public boolean accept(Packet packet) {
				// TODO Auto-generated method stub
				return packet.getPacketID() != null;
			}
		});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(LOGTAG, "onbind");
		return myBinder;
	}

	public class LocalBinder extends Binder {
		// this getService function should be changed to public
		public SessionService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return SessionService.this;
		}
	}

}
