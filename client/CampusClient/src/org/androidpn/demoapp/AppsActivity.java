package org.androidpn.demoapp;

import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.Util;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class AppsActivity extends Activity {
	static String LOGTAG="AppsActivity";
	
	/**
	 * addSubscribe(username,appname)
	 * 订阅
	 */
	private void addSubscribe(String s,String t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=addSubscribe"); //
		parameter.append("&username=" + s + "&appname="+ t);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscribe.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppsActivity.this, "添加关注失败:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppsActivity.this, "添加关注成功");
				}
			}
		}.execute(parameter);
	}
	/**
	 * delSubscribe(username,appname)
	 * 取消订阅
	 */
	private void delSubscribe(String s,String t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=delSubscribe"); //
		parameter.append("&username=" + s + "&appname="+ t);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscribe.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppsActivity.this, "取消关注失败:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppsActivity.this, "取消关注成功");
				}
			}
		}.execute(parameter);
	}
}
