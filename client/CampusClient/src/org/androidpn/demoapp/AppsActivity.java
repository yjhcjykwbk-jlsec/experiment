package org.androidpn.demoapp;

import java.util.List;

import org.androidpn.server.model.App;
import org.androidpn.server.model.User;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.Util;
import org.androidpn.util.Xmler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class AppsActivity extends Activity {
	static String LOGTAG="AppsActivity";
	private String USERNAME;
	private String PASSWORD;
	private List<App> appList;
	/**
	 * addSubscribe(username,appid)
	 * ����
	 */
	private void addSubscribe(String s,Long t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=addSubscribe"); //
		parameter.append("&username=" + s + "&appid="+ t);
		new AsyncTask<StringBuilder, Integer, String>() {
			@Override
			protected String doInBackground(StringBuilder... parameter) {
				/*--End--*/
				String resp = GetPostUtil.send("POST",
						getString(R.string.androidpnserver) + "subscriptions.do",
						parameter[0]);
				return resp;
			}

			@Override
			protected void onPostExecute(String resp) {
				if (!"succeed".equals( Util.getXmlElement(resp, "result"))) {
					String reason =  Util.getXmlElement(resp, "reason");
					Util.alert(AppsActivity.this, "��ӹ�עʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppsActivity.this, "��ӹ�ע�ɹ�");
				}
			}
		}.execute(parameter);
	}
	/**
	 * delSubscribe(username,appid)
	 * ȡ������
	 */
	private void delSubscribe(String s,Long t){
		StringBuilder parameter = new StringBuilder();
		parameter.append("action=delSubscribe"); //
		parameter.append("&username=" + s + "&appid="+ t);
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
					Util.alert(AppsActivity.this, "ȡ����עʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppsActivity.this, "ȡ����ע�ɹ�");
				}
			}
		}.execute(parameter);
	}
	
	private void getSubscriptions(String s){
		
	}
}
