package org.androidpn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler.Callback;
import android.util.Log;

public class IsNetworkConn{
	public boolean isConnected = false;
	
	public IsNetworkConn(Context context){
		ConnectivityManager cm = (ConnectivityManager)(context.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info==null||!info.isAvailable()||!info.isConnectedOrConnecting()){
			isConnected =false;
		}
//		else if (sendGet("http://www.baidu.com")) {
////			Log.i("xiaobingo", "��������");
//			isConnected = true;
//		}
		else {
			isConnected = true;
		}
	}
	
	//�ж��Ƿ����������
		public static boolean sendGet(String url){
			
			
			BufferedReader in = null;
			try
			{
				String urlName = url;
				URL realUrl = new URL(urlName);
				// �򿪺�URL֮�������
				URLConnection conn = realUrl.openConnection();
				// ����ͨ�õ���������
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				// ����ʵ�ʵ�����
				conn.connect();
				// ��ȡ������Ӧͷ�ֶ�
				Map<String, List<String>> map = conn.getHeaderFields();
				// �������е���Ӧͷ�ֶ�
				for (String key : map.keySet())
				{
					System.out.println(key + "--->" + map.get(key));
					
				}
				System.out.println(map.get("Connection").get(0));
				if (map.get("Connection").get(0).equalsIgnoreCase("close")) {
					return false;
				}
				else{
					return true;
				}
			}
			catch (Exception e)
			{
				System.out.println("����GET��������쳣��" + e);
				e.printStackTrace();
				return false;
			}
			// ʹ��finally�����ر�������
			finally
			{
				try
				{
					if (in != null)
					{
						in.close();
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					return false;
				}
			}
		}
	
}
