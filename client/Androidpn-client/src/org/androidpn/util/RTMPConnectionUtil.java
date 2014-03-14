package org.androidpn.util;

import java.util.Date;
import java.util.Map;

import org.androidpn.demoapp.MyVideoActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.smaxe.uv.Responder;
import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.INetStream;
import com.smaxe.uv.client.License;

public class RTMPConnectionUtil {
	
	//private static final String red5_url = "rtmp://10.3.12.118";
	private static final String red5_url = "rtmp://219.223.222.160/live";//101.79.7.71
	
	public static UltraNetConnection connection;
	public static UltraNetStream netStream;
	
	public static String message;
	
	public static void ConnectRed5(Context context) {
//		License.setKey("63140-D023C-D7420-00B15-91FC7");
		connection = new UltraNetConnection();
		
		connection.configuration().put(UltraNetConnection.Configuration.INACTIVITY_TIMEOUT, -1);
        connection.configuration().put(UltraNetConnection.Configuration.RECEIVE_BUFFER_SIZE, 256 * 1024);
        connection.configuration().put(UltraNetConnection.Configuration.SEND_BUFFER_SIZE, 256 * 1024);
		
		connection.client(new ClientHandler(context));
		connection.addEventListener(new NetConnectionListener());
		connection.connect(red5_url);
	}
	
	private static class ClientHandler extends Object {
		
		private Context context;
		
		ClientHandler(Context context) {
			this.context = context;
		};				
		
	}
	
	private static class NetConnectionListener extends UltraNetConnection.ListenerAdapter {
		public NetConnectionListener() {}
		
		@Override
		public void onAsyncError(final INetConnection source, final String message, final Exception e) {
			System.out.println("NetConnection#onAsyncError: " + message + " "+ e);
		}

		@Override
		public void onIOError(final INetConnection source, final String message) {
			System.out.println("NetConnection#onIOError: " + message);
		}

		@Override
		public void onNetStatus(final INetConnection source, final Map<String, Object> info) {
			System.out.println("NetConnection#onNetStatus: " + info);
			final Object code = info.get("code");
			if (UltraNetConnection.CONNECT_SUCCESS.equals(code)) {
//				source.call("testConnection", new Responder() {
//					public void onResult(final Object result) {
//						System.out.println("Method testConnection result: " + result);
//					}
//
//					public void onStatus(final Map<String, Object> status) {
//						System.out.println("Method testConnection status: " + status);
//					}
//				});
			}
		}
	}// NetConnectionListener
	
	//invoke server method createMeeting
	public static void invokeMethodFormRed5(String toUserId) {
		Date nowDate = new Date();
		String time = nowDate.getTime() +  "" + (int)((Math.random()*100)%100);
		message = time;
		connection.call("createMeeting", responder, "", toUserId, message);
		Log.d("DEBUG", "call createMeeting");
	}
	
	private static Responder responder = new Responder() {

		@Override
		public void onResult(Object arg0) {
			// TODO Auto-generated method stub
			System.out.println("Method createMeeting result: " + arg0);
			callback_createMeeting();
		}

		@Override
		public void onStatus(Map<String, Object> arg0) {
			// TODO Auto-generated method stub
			System.out.println("Method createMeetiong status: " + arg0);
		}
		
	};
	
	private static void callback_createMeeting() {
		
		//startVideo();
	}
	
	private static void startVideo() {
		
		Log.d("DEBUG", "startVideo()");

		netStream = new UltraNetStream(connection);
		netStream.addEventListener(new UltraNetStream.ListenerAdapter() {
			@Override
            public void onNetStatus(final INetStream source, final Map<String, Object> info){
                System.out.println("Publisher#NetStream#onNetStatus: " + info);
                Log.d("DEBUG", "Publisher#NetStream#onNetStatus: " + info);
                
                final Object code = info.get("code");
                
                if (UltraNetStream.PUBLISH_START.equals(code)) {
                    if (MyVideoActivity.aCamera != null) {
                        netStream.attachCamera(MyVideoActivity.aCamera, -1 /*snapshotMilliseconds*/);
                        Log.d("DEBUG", "aCamera.start()");
                        MyVideoActivity.aCamera.startCamera();
                    } else {
                    	Log.d("DEBUG", "camera == null");
                    }
                }    
            }
			
		});

		netStream.publish(message, UltraNetStream.LIVE);//"mp4:"+User.id + message+".mp4"
	}
	


}
