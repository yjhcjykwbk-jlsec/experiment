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
	 * ����
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
	 * delSubscribe(username,appname)
	 * ȡ������
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
					Util.alert(AppsActivity.this, "ȡ����עʧ��:"
							+ (reason == null ? "" : reason));
					return;
				}else {
					Util.alert(AppsActivity.this, "ȡ����ע�ɹ�");
				}
			}
		}.execute(parameter);
	}
}
