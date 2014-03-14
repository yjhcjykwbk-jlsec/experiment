package org.androidpn.demoapp;

import java.io.IOException;
import java.util.Vector;

import org.androidpn.barcode.CameraManager;
import org.androidpn.barcode.CaptureActivityHandler;
import org.androidpn.barcode.InactivityTimer;
import org.androidpn.barcode.ResultButtonListener;
import org.androidpn.barcode.ResultHandler;
import org.androidpn.barcode.ResultHandlerFactory;
import org.androidpn.barcode.ViewfinderView;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class BarcodeCaptureActivity extends Activity implements Callback {

	private static final String TAG = BarcodeCaptureActivity.class.getSimpleName();
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Dialog resultshowDlg;
	private CameraManager cameraManager;
	private int testpause;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barcode_capture);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		testpause = 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		viewfinderView.setVisibility(View.VISIBLE);
		handler = null;
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		testpause++;
		Log.d(TAG, "the onPause function is called:"+testpause);
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		cameraManager.closeDriver();
	    if (!hasSurface) {
	        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
	        SurfaceHolder surfaceHolder = surfaceView.getHolder();
	        surfaceHolder.removeCallback(this);
	      }
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result rawresult, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		ResultHandler resulthandler = ResultHandlerFactory.makeResultHandler(this, rawresult);
		String resultString = rawresult.getText();
		//FIXME
		if (resultString.equals("")) {
			Toast.makeText(BarcodeCaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			resultshowDlg = new Dialog(BarcodeCaptureActivity.this);
			resultshowDlg.setTitle("扫描结果");
			resultshowDlg.setCancelable(false);
			resultshowDlg.setContentView(R.layout.resultdlg);
			WindowManager.LayoutParams layoutdlg = resultshowDlg.getWindow().getAttributes();
			layoutdlg.width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
			resultshowDlg.getWindow().setAttributes(layoutdlg);
			TextView resultview = (TextView)resultshowDlg.findViewById(R.id.resultview);
			Button actionbutton = (Button)resultshowDlg.findViewById(R.id.actionbutton);
			Button cancelbutton = (Button)resultshowDlg.findViewById(R.id.cancelbutton);
			resultview.setText(resulthandler.getDisplayContents());
			actionbutton.setText(resulthandler.getButtonText(0));
			actionbutton.setOnClickListener(new ResultButtonListener(resulthandler, 0));
			cancelbutton.setText("取消");
			cancelbutton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					resultshowDlg.cancel();
					onPause();
					onResume();
				}
			});
			resultshowDlg.show();
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}
	
	public CameraManager getCameraManager(){
		return cameraManager;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}