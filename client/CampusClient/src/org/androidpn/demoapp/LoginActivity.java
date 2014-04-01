package org.androidpn.demoapp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.androidpn.client.Constants;
import org.androidpn.client.NotificationService;
import org.androidpn.client.Notifier;
import org.androidpn.client.NotificationService.LocalBinder;
import org.androidpn.client.ServiceManager;
import org.androidpn.demoapp.R;
import org.androidpn.util.ActivityUtil;
import org.androidpn.util.GetPostUtil;
import org.androidpn.util.IsNetworkConn;
import org.androidpn.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

@SuppressLint("NewApi")
public class LoginActivity extends Activity {
	private static String LOGTAG="LoginActivity";
	private SharedPreferences originSharedPrefs;
	private TextView txt_info = null;
	private Button btn_login = null;
	private Button btn_clean = null;
	private EditText userName = null;
	private EditText passWord = null;
	private ImageView logo;
	private InputMethodManager imm;
	private String encryptedPW = null;
	private boolean isInputOK = false;
	private LinearLayout loginLayout, centerLayout;
	private NotificationService mService;
	private boolean mBound;
	private CheckBox autoLogin;
	UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// 添加到activitylist里，方便最后统一退出
		ActivityUtil.getInstance().addActivity(this);

		setDisplay();

		originSharedPrefs = this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		// 判断是否联网
		IsNetworkConn isConn = new IsNetworkConn(LoginActivity.this);
		if (!isConn.isConnected) {
			Toast.makeText(getApplicationContext(), "未联网，请先联网",
					Toast.LENGTH_LONG).show();
			// userName.setEnabled(false);
			// passWord.setEnabled(false);
			// btn_login.setEnabled(false);
			// btn_clean.setEnabled(false);
		}

		userInfo = (UserInfo) getApplication();
        
		// 如果已保存用户名和密码，则直接开始连接
		if (originSharedPrefs.contains(Constants.XMPP_USERNAME)
				&& originSharedPrefs.contains(Constants.XMPP_PASSWORD)) {
			Toast.makeText(LoginActivity.this, "登陆...", Toast.LENGTH_LONG)
					.show();
			startConnect(); // 开始连接androidpn server
			Intent intent = new Intent(LoginActivity.this,
					DemoAppActivity.class);
			LoginActivity.this.startActivityForResult(intent,1);
//			LoginActivity.this.finish();
		}

		// if(originSharedPrefs.contains(Constants.XMPP_USERNAME)
		// &&originSharedPrefs.contains(Constants.XMPP_PASSWORD)){
		// userName.setText(originSharedPrefs.getString(Constants.XMPP_USERNAME,
		// null));
		// encryptedPW = originSharedPrefs.getString(Constants.XMPP_PASSWORD,
		// null);
		// passWord.setText(encryptedPW);
		// }

		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hidekeyboard();
				final String theUserName = userName.getText().toString().trim();
				String thePassWord = passWord.getText().toString().trim();

				if (theUserName.equals("")) {
					userName.setHint("用户名不得为空");
				}
				if (thePassWord.equals("")) {
					passWord.setHint("密码不得为空");
				}
				if (!theUserName.isEmpty() && !thePassWord.isEmpty()) {
					isInputOK = true;
				}
				if (isInputOK) {
					// 密码进行加密
					// if(encryptedPW == null){
					try {
						encryptedPW = toMD5((thePassWord).getBytes("GBK"));
//						passWord.setText(encryptedPW);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// }
					Log.i("xiaobingo", "MD5加密后的密码：" + encryptedPW);

					// 发送用户名和密码到android server进行验证
					// 用POST方式发送
					/*--拼接POST字符串--*/
					StringBuilder parameter = new StringBuilder();
					parameter.append("action=checkUser"); //
					parameter.append("&androidName=");
					parameter.append(theUserName);
					parameter.append("&androidPwd=");
					parameter.append(encryptedPW);
					/*--End--*/
					AsyncTask<StringBuilder,Integer,String> loginTask=new AsyncTask<StringBuilder,Integer,String>(){
						@Override
						protected String doInBackground(StringBuilder... args) {
							// TODO Auto-generated method stub
							StringBuilder parameter=args[0];
							String androidpnURL=getString(R.string.androidpnserver);
							String resp = GetPostUtil.send("POST", androidpnURL
									+ "user.do", parameter);
							return resp;
						}
						@Override
						protected void onPostExecute(String resp){
							// 验证用户名密码成功
							if (resp.contains("check:success")) {
								// 下面将获得的用户名和密码保存在UserInfo对象中
								userInfo.setMyUserName(theUserName);
								userInfo.setMyUserPWD(encryptedPW);
								Log.i("xiaobingo",
										"userInfo的用户名" + userInfo.getMyUserName());
								Log.i("xiaobingo",
										"userInfo的密码" + userInfo.getMyUserPWD());
								// 获取的用户名和密码写入保存
								Editor editor = originSharedPrefs.edit();
								editor.putString(Constants.XMPP_USERNAME, theUserName);
								editor.putString(Constants.XMPP_PASSWORD, encryptedPW);
								editor.commit();
								// 开始连接androidpn server
								startConnect();

								// 登陆成功，传输用户名和密码
								Intent intent = new Intent(LoginActivity.this,
										DemoAppActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("name", theUserName);
								bundle.putString("password", encryptedPW);
								intent.putExtras(bundle);
								LoginActivity.this.startActivity(intent);
								setContentView(R.layout.transition);
								// LoginActivity.this.finish();
							}
							// 密码错误
							else if (resp.contains("check:password failure")) {
								Toast.makeText(getApplicationContext(), "密码错误！",
										Toast.LENGTH_SHORT).show();
							}
							// 用户不存在
							else if (resp.contains("check:not exist")) {
								Toast.makeText(getApplicationContext(), "该用户不存在！",
										Toast.LENGTH_SHORT).show();
							}
							Log.i("LoginActivity", "resp:" + resp);
						}
					};
					loginTask.execute(parameter);
				}
			}
		});

		btn_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName.setText("");
				passWord.setText("");
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.i(LOGTAG,"onActivityResult");
		LoginActivity.this.finish();
	}

	/*
	 * Start to connect to the androidpn server.
	 * this is very import for it keeps the xmpp connection 
	 */
	private void startConnect() {
		// TODO Auto-generated method stub
		ServiceManager serviceManager = new ServiceManager(this);
		// 将serviceManager放入Constants中，供全局调用，比如在退出登陆时候调用serviceManager的stopService()
		Constants.serviceManager = serviceManager;
		serviceManager.setNotificationIcon(R.drawable.notification);
		
		//serviceManager.startService();
		
		//this is very important, 
		Intent intent = new Intent(this,NotificationService.class);
		startService(intent);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
	}

	/*
	 * Encrypt the password to MD5.
	 */
	protected String toMD5(byte[] pwd) {
		// TODO Auto-generated method stub
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd);
			StringBuffer sb = new StringBuffer();
			for (byte b : md.digest()) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * Set the display of the components.
	 */
	private void setDisplay() {
		userName = (EditText) findViewById(R.id.username);
		passWord = (EditText) findViewById(R.id.password);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_clean = (Button) findViewById(R.id.btn_clean);
		autoLogin = (CheckBox) findViewById(R.id.autologin);
		logo = (ImageView) findViewById(R.id.logo);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		loginLayout = (LinearLayout) findViewById(R.id.login_layout);
		centerLayout = (LinearLayout) findViewById(R.id.centerlayout);
		LinearLayout.LayoutParams centerlayout_param = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		centerlayout_param.width = getWindowManager().getDefaultDisplay()
				.getWidth() * 5 / 6;
		centerlayout_param.gravity = Gravity.CENTER_HORIZONTAL;

		centerLayout.setLayoutParams(centerlayout_param);

		loginLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				loginLayout.requestFocus();
				hidekeyboard();
				return false;
			}
		});

		loginLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						Rect r = new Rect();

						loginLayout.getWindowVisibleDisplayFrame(r);
						int heightDiff = loginLayout.getRootView().getHeight()
								- r.height();
						if (heightDiff > 100) {
							logo.setVisibility(View.GONE);
						} else {
							logo.setVisibility(View.VISIBLE);
						}
					}
				});
	}
	
	private void hidekeyboard(){
		imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);
		logo.setVisibility(View.VISIBLE);
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection myConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	Log.i("loginactivity#serviceconnection#onservicedisconnected","service is disconnected");
            LocalBinder binder = (LocalBinder) service;
            //Constants.notificationService=
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            //Constants.notificationService=
        	Log.i("loginactivity#serviceconnection#onservicedisconnected","service is disconnected");
        }
    };
    @Override
    public void onDestroy(){
    	//if not unbound, will invoke the exception: Activity has leaked ServiceConnection that was originally bound here
    	unbindService(myConnection);
    	Toast.makeText(this, "loginActivity has destroyed",Toast.LENGTH_SHORT).show();
    	super.onDestroy();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, " 退出");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
		{	 
			Util.exit(this);
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}