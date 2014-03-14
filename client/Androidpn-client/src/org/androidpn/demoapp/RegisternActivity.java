package org.androidpn.demoapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.EncryptedPrivateKeyInfo;

import org.androidpn.demoapp.R;
import org.androidpn.util.ActivityUtil;
import org.androidpn.util.IsNetworkConn;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * ���Ϳͻ����û�ע�Ṧ����ʱ�����ţ��û�ע��ǰ��http://push.pkusz.edu.cnע��
 * @author xiaobingo
 *
 */
public class RegisternActivity extends Activity {
	
	private TextView txt_info =null;
	private Button btn_registerButton = null;
	private Button btn_clean = null;
	private EditText userName = null;
	private EditText passWord = null;
	private EditText repassWord = null;
	private boolean isInputOK = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		//��ӵ�activitylist��������ͳһ�˳�
        ActivityUtil.getInstance().addActivity(this);
		Log.i("xiaobingo", "����ע�����");
		
		txt_info = (TextView) findViewById(R.id.info);
		userName = (EditText) findViewById(R.id.username);
		passWord = (EditText) findViewById(R.id.password);
		repassWord = (EditText) findViewById(R.id.repassword);
		btn_registerButton = (Button) findViewById(R.id.btn_register);
		btn_clean = (Button)findViewById(R.id.btn_clean);
		
		//�ж��Ƿ�����
		IsNetworkConn isConn =new IsNetworkConn(RegisternActivity.this);
		if(!isConn.isConnected){
			txt_info.setText("δ��������������");
			userName.setEnabled(false);
			passWord.setEnabled(false);
			repassWord.setEnabled(false);
			btn_registerButton.setEnabled(false);
			btn_clean.setEnabled(false);
		}
		btn_registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String theUserName = userName.getText().toString().trim();
				String thePassWord = passWord.getText().toString();
				String theRePassWord = repassWord.getText().toString();
				
				if (theUserName.equals("")) {
					userName.setHint("ѧ�Ų���Ϊ��");
					isInputOK = false;
				}
				if (thePassWord.equals("")) {
					passWord.setHint("���벻��Ϊ��");
					isInputOK = false;
				}
				if (theRePassWord.equals("")) {
					repassWord.setHint("�ظ����벻��Ϊ��");
					isInputOK = false;
				}
				if (!(thePassWord.equals(theRePassWord))) {
					Toast.makeText(RegisternActivity.this, "�����������벻һ�£�", Toast.LENGTH_LONG).show();
					passWord.setText("");
					repassWord.setText("");
					isInputOK = false;
				}
				
				if (isInputOK) {
					Toast.makeText(RegisternActivity.this, "ע��ɹ�", Toast.LENGTH_LONG).show();
					//ע��ɹ�������ѧ�ź�����
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("name", theUserName);
					String encryptedPW = null;
					//������м���
						try {
							encryptedPW = toMD5((thePassWord).getBytes("GBK"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					Log.i("xiaobingo", "MD5���ܺ�����룺"+encryptedPW);
					bundle.putString("password", encryptedPW);
					intent.putExtras(bundle);
					RegisternActivity.this.setResult(RESULT_OK, intent);
					RegisternActivity.this.finish();
				}
			}
		});
		
		btn_clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName.setText("");
				passWord.setText("");
				repassWord.setText("");				
			}
		});
	}
	
	//���û�����MD5����
	protected String toMD5(byte[] pwd) {
		// TODO Auto-generated method stub
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd);
			StringBuffer sb =new StringBuffer();
			for (byte b:md.digest()) {
				sb.append(String.format("%02x", b&0xff) );
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();return null;
		}		
		
	}
	
}
