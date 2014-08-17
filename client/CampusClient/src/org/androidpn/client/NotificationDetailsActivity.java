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

import java.util.HashMap;
import java.util.ResourceBundle;

import org.androidpn.demoapp.R;
import org.androidpn.demoapp.UserInfo;
import org.androidpn.util.GetPostUtil;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.xbill.DNS.DNSKEYRecord.Flags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Activity ��ʾ֪ͨ��Ϣ����.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

	private Context context;
	String notificationId;
	String notificationApiKey;
	String notificationTitle;
	String notificationMessage;
	String notificationUri;
	String notificationFrom;
	String packetId;
	String bodyHtml;
	String htmlPost = "</body></html>";
	String htmlPre = "<!DOCTYPE html>" + "<html lang=\"en\">"
			+ "<head><meta charset=\"utf-8\">" + "</head>"
			+ "<body style='margin:0; pading:0;"
			+ " background-color: #71D5CA;'>";

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationDetailsActivity.class);

	private String callbackActivityPackageName;

	private String callbackActivityClassName;

	private String rtmpUrl;

	private String fileName;

	private int msgStart, msgEnd;

	private int mobileWidthPix, mobileHeightPix, mobileWidth, mobileHeight,
			densityDPI;
	float density;

	private LinearLayout.LayoutParams contentParams, videoParams, titleParams,
			buttonParams, responseParams;
	private ScrollView scrollView;
	private LinearLayout responseLayout;
	private boolean flag = false;
	private SharedPreferences originSharedPrefs;

	public NotificationDetailsActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		originSharedPrefs = this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// ��Ļ��
		mobileWidthPix = dm.widthPixels;
		// ��Ļ��
		mobileHeightPix = dm.heightPixels;

		density = dm.density; // ��Ļ�ܶȣ�0.75/1.0/1.5��
		Log.i("xiaobingo", "density�ǣ�" + density);
		// densityDPI = dm.densityDpi; //��Ļ�ܶ�DPI ��120/160/240��
		mobileWidth = (int) (mobileWidthPix / density + 0.5f);
		mobileHeight = (int) (mobileHeightPix / density + 0.5f);
		Log.i("xiaobingo", "��Ļ��" + mobileWidth);
		Log.i("xiaobingo", "��Ļ�ߣ�" + mobileHeight);

		SharedPreferences sharedPrefs = this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		callbackActivityPackageName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
		callbackActivityClassName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
		Log.i(LOGTAG, "callbackActivity�ǣ�" + callbackActivityClassName);
		context = Constants.xmppManager.getContext();

		Intent intent = getIntent();
		if (intent.getStringExtra("ItemTitle") != null) {
			// ������ҳ��DemoAppActivity��������intent������֪ͨҳ��Notifierת������intent
			Bundle bundle = this.getIntent().getExtras();
			notificationTitle = bundle.getString("ItemTitle");
			notificationMessage = bundle.getString("ItemMessage");
			notificationUri = bundle.getString("ItemUri");
		} else {
			// ����֪ͨҳ��Notifier������intent����Ҫ����userInfo����
			notificationId = intent.getStringExtra(Constants.NOTIFICATION_ID);
			notificationApiKey = intent
					.getStringExtra(Constants.NOTIFICATION_API_KEY);
			notificationTitle = intent
					.getStringExtra(Constants.NOTIFICATION_TITLE);
			notificationMessage = intent
					.getStringExtra(Constants.NOTIFICATION_MESSAGE);
			notificationUri = intent.getStringExtra(Constants.NOTIFICATION_URI);
			notificationFrom = intent
					.getStringExtra(Constants.NOTIFICATION_FROM);
			packetId = intent.getStringExtra(Constants.PACKET_ID);

			Log.d(LOGTAG, "notificationId=" + notificationId);
			Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
			Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
			Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
			Log.d(LOGTAG, "notificationUri=" + notificationUri);
			Log.d(LOGTAG, "notificationFrom=" + notificationFrom);

			// TODO FIXME ���Ͳ鿴��ִ
			IQ result = new IQ() {
				@Override
				public String getChildElementXML() {
					return null;
				}
			};
			result.setType(Type.RESULT);
			result.setPacketID(packetId);
			result.setTo(notificationFrom);
			try {
				Constants.xmppManager.getConnection().sendPacket(result);
			} catch (Exception e) {
			}

			// ����֪ͨ�������Ϣ��userInfo�У��Ա㱣����DemoAppActivity�������ʷ��¼��
			UserInfo userInfo = (UserInfo) context.getApplicationContext();
			// userInfo.setMyNotifierTitle(title);
			// userInfo.setMyNotifierMessage(message);
			// userInfo.setMyNotifierUri(uri);
			HashMap<String, String> addMap = new HashMap<String, String>();
			addMap.put("ItemTitle", notificationTitle);
			addMap.put("ItemMessage", notificationMessage);
			addMap.put("ItemUri", notificationUri);
			userInfo.addMyNotifier(addMap);
		}

		View rootView = createView(notificationTitle, notificationMessage,
				notificationUri);
		setContentView(rootView);
	}

	// ����֪ͨ��ϸ��Ϣ����
	@SuppressLint("NewApi")
	private View createView(final String title, String message, final String uri) {
		final LinearLayout linearLayout = new LinearLayout(this);
		scrollView = new ScrollView(this);
		// linearLayout.setBackgroundColor(0xffeeeeee);
		Resources res = getResources();

		// ���ñ���ͼ
		Drawable dw = res.getDrawable(R.drawable.bg3);
		linearLayout.setBackgroundDrawable(dw);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(5, 5, 5, 5);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		linearLayout.setLayoutParams(layoutParams);

		TextView textTitle = new TextView(this);
		textTitle.setText(title);
		textTitle.setTextSize(21);
		// textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		textTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		textTitle.setTextColor(0xffffff00);
		textTitle.setGravity(Gravity.CENTER);

		titleParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		titleParams.setMargins(5, 5, 5, 10);
		textTitle.setLayoutParams(titleParams);
		linearLayout.addView(textTitle);

		// �����ж�uri�����uri����video˵������Ƶ��Ϣ��ת����Ƶ����
		// �������Ƶ��Ϣ
		if (message.startsWith("����������Ƶ")) {
			Log.i("xiaobingo", "������Ƶ");
			WebView messageDetial = new WebView(this);
			messageDetial.getSettings().setJavaScriptEnabled(true);
			messageDetial.getSettings().setAllowFileAccess(true);
			//messageDetial.getSettings().setPluginsEnabled(true);
			messageDetial.getSettings().setSupportZoom(true);
			messageDetial.getSettings().setAppCacheEnabled(true);
			String former = message.split("xiaobingo")[0];
			String after = message.split("xiaobingo")[1];
			System.out.println("afterString:" + after);
			int urlStart = former.indexOf("=") + 1;
			rtmpUrl = former.substring(urlStart);
			fileName = after;
			// ��Ƶֱ��
			if (rtmpUrl.contains("live")) {
				msgEnd = fileName.indexOf(".flv");
				fileName = fileName.substring(0, msgEnd);
				// ֱ����Ƶ���Ӻ�׺
			}
			// ��Ƶ�㲥
			else {
				fileName = "flv:" + fileName; // flv:��ù��02.flv
			}
			Log.i("xiaobingo", "message�ǣ�" + message);
			Log.i("xiaobingo", "rtmpUrl�ǣ�" + rtmpUrl);
			Log.i("xiaobingo", "fileName:" + fileName);

			String htmlCode = "<embed "
					+ "type=\"application/x-shockwave-flash\""
					+ "id=\"player1\" " + "name=\"player1\" "
					+ "src=\"http://push.pkusz.edu.cn" + "/mediaplayer.swf\""
					+ "width=\"" + mobileWidth + "\"" + " height=\""
					+ mobileHeight / 2 + "\"" + " flashvars=@FILESRC@"
					+ "allowfullscreen=\"true\""
					+ "allowscripaccess=\"always\"" + "/>	";
			Log.i("xiaobingo", "htmlCode:" + htmlCode);
			bodyHtml = htmlCode;
			bodyHtml = bodyHtml.replaceAll("@FILESRC@", "\"file=" + fileName
					+ "&streamer=" + rtmpUrl + "\"");
			messageDetial.loadDataWithBaseURL("http://127.0.0.1", htmlPre
					+ bodyHtml + htmlPost, "text/html", "UTF-8", null);

			videoParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			videoParams.setMargins(1, 3, 3, 5);
			scrollView.setLayoutParams(videoParams);
			scrollView.addView(messageDetial);
		} else {
			TextView textDetails = new TextView(this);
			textDetails.setText(message);
			textDetails.setTextSize(17);
			// textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			textDetails.setTextColor(0xeeffeeee);
			textDetails.setGravity(Gravity.FILL);

			contentParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					(int) (mobileHeightPix * 0.7));
			contentParams.setMargins(5, 3, 5, 5);
			scrollView.setLayoutParams(contentParams);
			scrollView.addView(textDetails);

		}

		linearLayout.addView(scrollView);

		// �����ϸ��ť�����ӵ�url�����ﻹ�뱣��֪ͨ��ʷ��¼
		Button okButton = new Button(this);
		okButton.setText("�鿴��ϸ");
		okButton.setTextSize(16);
		// okButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		okButton.setTextColor(0xff6699ff);
		Drawable dr = res.getDrawable(R.drawable.button3);
		okButton.setBackgroundDrawable(dr);

		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent;
				if (uri != null
						&& uri.length() > 0
						&& (uri.startsWith("http:") || uri.startsWith("https:")
								|| uri.startsWith("tel:") || uri
									.startsWith("geo:"))) {
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					intent.setClassName("com.android.browser",
							"com.android.browser.BrowserActivity");
				} else {
					intent = new Intent().setClassName(
							callbackActivityPackageName,
							callbackActivityClassName);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				}

				NotificationDetailsActivity.this.startActivity(intent);
				NotificationDetailsActivity.this.finish();
			}
		});

		// �ظ�button�¼�
		final Button btn_response = new Button(this);
		btn_response.setText("��Ҫ����");
		btn_response.setTextSize(16);
		// btn_response.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		btn_response.setTextColor(0xff6699ff);
		btn_response.setBackgroundDrawable(dr);

		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setGravity(Gravity.CENTER);
		buttonLayout.addView(okButton); // ��ӡ��鿴��ϸ����ť
		buttonLayout.addView(btn_response);// ��ӡ���Ҫ���ԡ���ť
		buttonParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		buttonParams.setMargins(3, 3, 3, 3);
		buttonLayout.setLayoutParams(buttonParams);
		// innerLayout.setGravity(1);
		linearLayout.addView(buttonLayout);

		// ��Ҫ�����¼�
		final EditText responseText = new EditText(this);
		responseText.setWidth((int) (mobileWidthPix * 0.6));
		Button btn_send = new Button(this);
		btn_send.setText("����");
		btn_send.setTextSize(16);
		btn_send.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		btn_send.setTextColor(0xff6699ff);
		btn_send.setBackgroundDrawable(dr);
		responseLayout = new LinearLayout(this);
		responseLayout.setGravity(Gravity.CENTER);
		responseLayout.addView(responseText);
		responseLayout.addView(btn_send);
		responseParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				(int) (mobileHeightPix * 0.12));
		responseParams.setMargins(3, 3, 3, 3);
		responseLayout.setLayoutParams(responseParams);
		btn_response.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// button���һ��,��ʾ������
				if (!flag) {
					contentParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							(int) (mobileHeightPix * 0.6));
					contentParams.setMargins(5, 3, 5, 5);
					scrollView.setLayoutParams(contentParams);
					linearLayout.addView(responseLayout);
				}
				// button�ٵ��һ�Σ�ȥ��������
				else {
					contentParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							(int) (mobileHeightPix * 0.7));
					contentParams.setMargins(5, 3, 5, 5);
					scrollView.setLayoutParams(contentParams);
					linearLayout.removeView(responseLayout);
				}
				flag = !flag;
			}
		});

		// ��������button�¼�
		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String myResponse = responseText.getText().toString();
				String androidpnURL = "http://219.223.222.232/bbs-api/?";
				/*--ƴ��POST�ַ���--*/
				StringBuilder parameter = new StringBuilder();
				parameter.append("action=newreply");
				parameter.append("&tid=");
				Log.d("notificationUri", "notificationUri is:" + notificationUri);
				parameter.append(notificationUri.substring(
						notificationUri.indexOf("&tid=") + 5));
				parameter.append("&message=");
				parameter.append(myResponse);
				parameter.append("&username=admin&password=123");
				// androidpnURL += "action=newreply";
				// androidpnURL += "&tid=58&message=";
				// androidpnURL += myResponse;
				// androidpnURL += "&username=admin&password=123";
				// parameter.append("&androidName=admin&");
				// parameter.append(originSharedPrefs.getString(Constants.XMPP_USERNAME,
				// "δ֪�û�"));
				// parameter.append("&reply=");
				// parameter.append(myResponse);
				/*--End--*/
				Log.i("LoginActivity", androidpnURL);
				String resp = GetPostUtil.send("POST", androidpnURL, parameter);
				Log.i("LoginActivity", "resp:" + resp);
				responseText.setText(""); // ���������
			}
		});

		// linearLayout.setGravity(Gravity.BOTTOM);
		return linearLayout;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		NotificationDetailsActivity.this.finish();
	}

	// protected void onPause() {
	// super.onPause();
	// finish();
	// }
	//
	// protected void onStop() {
	// super.onStop();
	// finish();
	// }
	//
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// }
	//
	// protected void onNewIntent(Intent intent) {
	// setIntent(intent);
	// }

}
