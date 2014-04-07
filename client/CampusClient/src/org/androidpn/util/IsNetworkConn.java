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
////			Log.i("xiaobingo", "连上网了");
//			isConnected = true;
//		}
		else {
			isConnected = true;
		}
	}
	
	//判断是否真的联网了
		public static boolean sendGet(String url){
			
			
			BufferedReader in = null;
			try
			{
				String urlName = url;
				URL realUrl = new URL(urlName);
				// 打开和URL之间的连接
				URLConnection conn = realUrl.openConnection();
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				// 建立实际的连接
				conn.connect();
				// 获取所有响应头字段
				Map<String, List<String>> map = conn.getHeaderFields();
				// 遍历所有的响应头字段
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
				System.out.println("发送GET请求出现异常！" + e);
				e.printStackTrace();
				return false;
			}
			// 使用finally块来关闭输入流
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
