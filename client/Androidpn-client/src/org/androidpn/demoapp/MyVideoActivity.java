package org.androidpn.demoapp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.androidpn.util.ActivityUtil;
import org.androidpn.util.IsNetworkConn;
import org.androidpn.util.RTMPConnectionUtil;
import org.androidpn.util.RemoteUtil;
import org.androidpn.util.UltraNetStream;

import com.smaxe.io.ByteArray;
import com.smaxe.uv.client.INetStream;
import com.smaxe.uv.client.NetStream;
import com.smaxe.uv.client.camera.AbstractCamera;
import com.smaxe.uv.stream.support.MediaDataByteArray;

import org.androidpn.demoapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyVideoActivity extends Activity{
	
	final String TAG = "MyVideoActivity";
	
	private boolean active;
	public static AndroidCamera aCamera;
	private Handler handler;
	private String userID;
	private Button btn_start;
	private Button btn_stop;
	private Button btn_back;
	private EditText tx_videoName;
	private TextView tx_time;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//the window without title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video);
		//��ӵ�activitylist��������ͳһ�˳�
        ActivityUtil.getInstance().addActivity(this);
		
		Bundle bd = this.getIntent().getExtras();
		userID = bd.getString("userID");
		//���ò�����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		btn_start = (Button)findViewById(R.id.btn_start);
		btn_stop = (Button)findViewById(R.id.btn_stop);
		btn_back = (Button)findViewById(R.id.btn_back);
		tx_time = (TextView)findViewById(R.id.tx_time);
		tx_videoName = (EditText)findViewById(R.id.tx_videoName);
		
		//�ж��Ƿ�����
		IsNetworkConn isConn = new IsNetworkConn(MyVideoActivity.this);
		if (!isConn.isConnected) {
			Toast.makeText(MyVideoActivity.this, "δ��������������~", Toast.LENGTH_LONG).show();
			btn_start.setEnabled(false);
			btn_stop.setEnabled(false);
		}
		btn_stop.setEnabled(false);
		handler = new Handler();
		
		aCamera = new AndroidCamera(MyVideoActivity.this);
		active = true;
		
		//�����ʼֱ��
		btn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btn_start.setEnabled(false);
				btn_stop.setEnabled(true);
				tx_videoName.setVisibility(View.GONE);
				handler.postDelayed(refreshTime, 1000);
				aCamera.startVideo(); //��ʼ������Ƶ

			}
		} );

		//���ֱֹͣ��
		btn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handler.removeCallbacks(refreshTime);
				aCamera.stopVideo();
				btn_start.setEnabled(true);
				btn_stop.setEnabled(false);
				tx_videoName.setVisibility(View.VISIBLE);
			}
		} );
		
		//�������
		btn_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				active=false;
				aCamera.camera.setPreviewCallback(null);
				aCamera.camera.stopPreview();
				aCamera.camera.release();
				aCamera.camera = null;
				MyVideoActivity.this.finish();
			}
		} );
		
		
		
	}
	
	//����back��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(MyVideoActivity.this)
			.setMessage("exit")
			.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					active = false;
					aCamera.camera.setPreviewCallback(null);
					aCamera.camera.stopPreview();
					aCamera.camera.release();
					aCamera.camera = null;
					MyVideoActivity.this.finish();
				}
			})
			.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	

	@Override
	public void onStop() {
		super.onStop();
		aCamera = null;
		if (RTMPConnectionUtil.netStream != null) {
			RTMPConnectionUtil.netStream.close();
		}
		
		Log.d("DEBUG", "onStop");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d("DEBUG", "onDestroy()");
	}
	
	public class AndroidCamera extends AbstractCamera implements SurfaceHolder.Callback, Camera.PreviewCallback {
		
		private SurfaceView surfaceView;
		private SurfaceHolder surfaceHolder;
		private Camera camera;
		
		private int width;
		private int height;
		
		private boolean init;
		
		int blockWidth;
        int blockHeight;
        int timeBetweenFrames; // 1000 / frameRate
        int frameCounter;
        byte[] previous;
		
		public AndroidCamera(Context context) {		       
		       
	        surfaceView = (SurfaceView)((Activity) context).findViewById(R.id.surfaceView);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(AndroidCamera.this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			
			width = 352;
			height = 288;
			
			//ע�͵�
			//handle = mH264encoder.initEncoder(width, height);
			
			init = false;
			Log.d("DEBUG", "AndroidCamera()");
		}
		
		private void startVideo() {
			Log.d(TAG, "startVideo()");
			
			//������Ƶ
			RTMPConnectionUtil.netStream = new UltraNetStream(RTMPConnectionUtil.connection);
			RTMPConnectionUtil.netStream.addEventListener(new NetStream.ListenerAdapter() {
				
				@Override
	            public void onNetStatus(final INetStream source, final Map<String, Object> info){
	                Log.d("DEBUG", "Publisher#NetStream#onNetStatus: " + info);	                
	                final Object code = info.get("code");	                
	                if (NetStream.PUBLISH_START.equals(code)) {
	                    if (MyVideoActivity.aCamera != null) {
	                    	RTMPConnectionUtil.netStream.attachCamera(aCamera, -1 /*snapshotMilliseconds*/);	                       
	                    } else {
	                    	Log.d("DEBUG", "camera == null");
	                    }
	                }    
	   
	            }
							
			});
			Log.i(TAG, userID+"_"+tx_videoName.getText().toString());
			RTMPConnectionUtil.netStream.publish(userID+"_"+tx_videoName.getText().toString(), NetStream.LIVE);			
			
		}
		
		private void stopVideo(){
			Log.d(TAG, "stopVideo()");
			RTMPConnectionUtil.netStream.close();
		}
		
		
		
		public void startCamera() {
			camera.startPreview();
		}
		
		public void printHexString(byte[] b) { 
			for (int i = 0; i < b.length; i++) { 
				String hex = Integer.toHexString(b[i] & 0xFF); 
				if (hex.length() == 1) { 
				hex = '0' + hex; 
			} 
				Log.i(TAG, "����16��������:"+hex.toUpperCase());
			} 
	}
		
		@Override
		public void onPreviewFrame(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
			if (!active) return;
			if (!init) {
				blockWidth = 32;
		        blockHeight = 32;
		        timeBetweenFrames = 100; // 1000 / frameRate
		        frameCounter = 0;
		        previous = null;
				init = true;
			}
			final long ctime = System.currentTimeMillis();
			//Log.i(TAG, "�ɼ���������ĳ��ȣ�"+arg0.length);
			/**���ɼ���YUV420SP����ת��ΪRGB��ʽ*/
            byte[] current = RemoteUtil.decodeYUV420SP2RGB(arg0, width, height);
            try {
//            		int byte_result = Decode(arg0);/**���ɼ�����ÿһ֡��Ƶ������H264����*/
//        			byte[] bytes1 = copyOf(out,byte_result);
//        			Log.i(TAG, "byte����ĳ��ȣ�"+bytes1.length);
            		/**����ñ�����H264����*/
                final byte[] packet = RemoteUtil.encode(current, previous, blockWidth, blockHeight, width, height);
        			fireOnVideoData(new MediaDataByteArray(timeBetweenFrames, new ByteArray(packet)));
                previous = current;
                if (++frameCounter % 10 == 0) previous = null;
                    
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final int spent = (int) (System.currentTimeMillis() - ctime);
            try {
            	//Log.i(TAG, "�̵߳ȴ���"+Math.max(0, timeBetweenFrames - spent)+" s");
				Thread.sleep(Math.max(0, timeBetweenFrames - spent));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public byte[] copyOf(byte[] arr,int len)
		{
			Class type=arr.getClass().getComponentType();
			byte[] target=(byte[])Array.newInstance(type, len);
			System.arraycopy(arr, 0, target, 0, len);
			return target;
		}
		
		/*
		private byte[] out = new byte[20*1024];
		long start = 0;
		long end = 0;
		private int Decode(byte[] yuvData){
			start = System.currentTimeMillis();
			int result = mH264encoder.encodeframe(handle, -1, yuvData, yuvData.length, out);
			end = System.currentTimeMillis();
			Log.e(TAG, "encode result:"+result+"--encode time:"+(end-start));
			if(result > 0){
				try {
					FileOutputStream file_out = new FileOutputStream ("/sdcard/x264_video_activity.264",true);
					file_out.write(out,0,result);
					file_out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			this.setPrewDataGetHandler();
			return result;
		}
		*/
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			//camera.startPreview();
			//camera.unlock();
			//startVideo();
			 aCamera.startCamera();
		     Log.d(TAG, "aCamera.start()");
			Log.d(TAG, "surfaceChanged()");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			camera = Camera.open();
			try { //��ʼ������ͷ
				camera.setPreviewDisplay(surfaceHolder);
				camera.setPreviewCallback(this);
				Camera.Parameters params = camera.getParameters();				
				/*//�ı䳯��
				if (Integer.parseInt(Build.VERSION.SDK) >= 8)
					   setDisplayOrientation(camera, 90);
					  else {
					   if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					    params.set("orientation", "portrait");
					    params.set("rotation", 90);
					   }
					   if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					    params.set("orientation", "landscape");
					    params.set("rotation", 90);
					   }
					  }
				*/
				
				params.setPreviewSize(width, height);
				//params.setPictureSize(width, height);
				camera.setParameters(params);
				
				 aCamera.startCamera();
			     Log.d(TAG, "aCamera.start()");
			     
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				aCamera.camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
			
			Log.d("DEBUG", "surfaceCreated()");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
			//mH264encoder.destory(handle);
			Log.d("DEBUG", "surfaceDestroy()");
		}
		
		//�Զ�����ʾ����
		 protected void setDisplayOrientation(Camera camera, int angle) {
			  Method downPolymorphic;
			  try {
			   downPolymorphic = camera.getClass().getMethod(
			     "setDisplayOrientation", new Class[] { int.class });
			   if (downPolymorphic != null)
			    downPolymorphic.invoke(camera, new Object[] { angle });
			  } catch (Exception e1) {
			  }
			 }		
		
		 
	} //AndroidCamera
	
	 //ˢ����Ƶֱ��ʱ��
	 private Runnable refreshTime = new Runnable() {
			
			int sec = 0;
			int min = 0;
			int hou = 0;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sec++;
				handler.postDelayed(refreshTime, 1000);
				if (sec >= 60) {
					sec = sec % 60;
					min++;
				}
				if (min >= 60) {
					min = min % 60;
					hou++;
				}
				tx_time.setText(timeFormat(hou) + ":" + timeFormat(min) + ":" + timeFormat(sec));
			}
		};
		
		private String timeFormat(int t) {
			if (t / 10 == 0) {
				return "0" + t;
			} else {
				return t + "";
			}
		}
}
