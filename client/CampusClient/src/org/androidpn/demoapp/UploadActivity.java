package org.androidpn.demoapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.androidpn.util.ActivityUtil;
import org.androidpn.util.IsNetworkConn;
import org.androidpn.data.PreferenceData;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.FROYO)
public class UploadActivity extends Activity {
	/* Path and file name for the photo  */
	private String mPhotoPath;
	private static final String PHOTOPATH_KEY = "PhotoPath";
	/* Name used for saving the mPhotoPath to Bundle */
	private static final String PHOTOPATH = "photopath";
	/* Name used for saving the showNextButton to Bundle */
	private static final String SHOWNEXTBUTTON = "showNextButton";
	
	/* whether Next button should be shown or not */
	private boolean showNextButton = false;
	
	/* Code for device's primary camera (big)  */
	private static final int ACTION_TAKE_PHOTO = 1; 
	
	private static final int ACTION_GALLERY_PHOTO = 2;
	
	/* Prefix for image file name  */
	private static final String JPEG_FILE_PREFIX = "camera_"; //�����ϴ�ͼƬ������ǰ׺�������������ϴ����Ǳ����ϴ��������ϴ���ǰ׺
	/* Suffix for image file name  */
	private static final String JPEG_FILE_SUFFIX = ".jpg"; //�����ϴ�ͼƬ�����ĺ�׺��ͳһjpg��ʽ
	
	/* A taken photo will be displayed here */
	private ImageView mImageView;
	
	private String userID="";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
      //��ӵ�activitylist��������ͳһ�˳�
        ActivityUtil.getInstance().addActivity(this);
        
        Log.i("xiaobingo", "�����ϴ�����");
		Bundle bundle = this.getIntent().getExtras();
		userID = bundle.getString("userID");
        //��ʾͼƬ�ؼ�
        mImageView = (ImageView)findViewById(R.id.imageViewWindow); 
        
    	String sendingOkKey = getString(R.string.sending_ok); 
    	//��ʼ��
    	PreferenceData.setString(this, "", sendingOkKey);        

    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(PHOTOPATH, mPhotoPath);
		//outState.putBoolean(SHOWNEXTBUTTON, showNextButton);		
		super.onSaveInstanceState(outState);
		Log.i("onSaveInstanceState", "mPhotoPath: " + mPhotoPath);
		
	}    

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mPhotoPath = savedInstanceState.getString(PHOTOPATH);
		//showNextButton = savedInstanceState.getBoolean(SHOWNEXTBUTTON);
			
    	super.onRestoreInstanceState(savedInstanceState);
		Log.i("onRestoreInstanceState", "mPhotoPath: " + mPhotoPath);
		
	}
    
    /*
     * ��������ϴ�
     */
    public void startCameraActivity(View view){
    	boolean imagePathOk = false;
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	File file = null;
    	try {
    		file = createPhotoFile();
    		mPhotoPath = file.getAbsolutePath();
    		String cameraPhotoFileNameString = file.getName();
    		if (mPhotoPath != null) 
    			imagePathOk = true;
    	}
    	catch (IOException e){
    		
    	}
    	
    	if (imagePathOk){
    		Log.i("UploadActivity", "mPhotoPath: " + mPhotoPath);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);

    	} else {
    		Toast.makeText(UploadActivity.this, "ͼ��·����������ͷ�޷�����", Toast.LENGTH_LONG).show();
    	}
    	    	 
    }
    
    
    /*
     * ��������ϴ�
     */
    public void startGalleryActivity(View view){

    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "ѡ��һ��ͼƬ"), ACTION_GALLERY_PHOTO);
    	
    }
    
    /*
     * �����ļ���������Ƭ
     */
    private File createPhotoFile() throws IOException{
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timestamp + "_";
		File album = getPhotoAlbumDirectory();
		if (album == null)
			return null;
		//�����������ɵ�ͼƬ�ļ���ͳһ.jpg��ʽ������������
		File imageFile = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, album);
		return imageFile;

    }
    
    /* ��ȡ����ļ� */
    private File getPhotoAlbumDirectory(){
    	File albumDirectory = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			String albumName = getString(R.string.albumName);
			albumDirectory = new File(Environment.getExternalStoragePublicDirectory(
							    Environment.DIRECTORY_PICTURES), 
							  albumName);
			
			if (albumDirectory != null) {
				if (! albumDirectory.mkdirs()) {
					if (! albumDirectory.exists()){
						Log.d("CameraSample", "failed to create directory");
					}
				}
			}
			
		} else {	
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
			return null;
		}
    	return albumDirectory;
    }
    
    /*
     * ��������ߴ������ѡ��ͼƬ�󣬷��ؽ��ͼƬ
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
 	
    	
    	// �������ѡ��ͼƬ
    	if (requestCode == ACTION_GALLERY_PHOTO && resultCode == RESULT_OK){
    		
    		Uri selectedImageURI = data.getData();
    		mPhotoPath = getRealPathFromURI(selectedImageURI);
    		//if image exists, show the "next" button
    		//if (mPhotoPath != null)
    		//	showNextButton = true;    		
    		//�����ѡȡͼƬ��·��
    		Log.i("xiaobingo:OnActivityResult, gallery photo path:", mPhotoPath); 
    	}
    	//����ͷ������
    	else if (requestCode == ACTION_TAKE_PHOTO && resultCode == RESULT_OK) {
	
    		try {
    			//����ͼƬ���ֻ�
    			saveImage(); 
    			//"next" button will be shown to user
    	    	//showNextButton = true;
    			//���յ�ͼƬ·��
    			Log.i("xiaobingo:onActivityResult, camera photo path:", mPhotoPath);  
    		}
    		catch (NullPointerException np){
    			Toast.makeText(UploadActivity.this, "�޷�������Ƭ", Toast.LENGTH_LONG).show();
    			Log.e("onActivityResult", "NullPointerException when saving photo file");
    		}

	    	Log.i("onActivityResult", "Next button should be now visible");
	    	showImage();
    		
    	} 


    	
    }
    
    /*
     * �����������ѡȡ��ͼƬ��·��
     */
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver()
                   .query(contentURI, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaColumns.DATA); 
        return cursor.getString(idx); 
    }
    
    
    private void showImage(){
    	setPic();
    }
    
    
    /* shows the latest photo in ImageView */
    private void setPic() {
    	
    	if (mImageView == null) {
    		Log.e("setPic", "mImageView is null");
    		return;
    	}
    	if (mPhotoPath == null) {
    		Log.e("setPic", "mPhotoPath is null");
    		return;
    	}
    	
    	mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        
        Log.e("ImageView size", "width: " + targetW + " height: " + targetH);
        
        if (targetW == 0 || targetH == 0) {
        	Log.e("setPic", "width or height is zero");
        	return;
        }
        
        // ��ȡͼƬ�Ĵ�С
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.i("setPic", "bmOptions.outWidth: " + bmOptions.outWidth);
        Log.i("setPic", "bmOptions.outHeight: " + bmOptions.outHeight);
        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);
        Log.i("scaleFactor", "scale: " + scaleFactor);
        
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;       
      
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    
    private void saveImage() throws NullPointerException{
    	
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File file = new File(mPhotoPath);
		
		Log.i("saveImage", "saving mPhotoPath file");
	    Uri contentUri = Uri.fromFile(file);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
		
    }
    
    /*
     * ��UploadActivity������Ϻ󱻵���
     * ���� setPic() ��ʾ���µ�ͼƬ. 
     * Next-button will be shown, if image has been selected
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
    	Log.i("onWindowFocusChanged", "onWindowFocusChanged Called");    
    	if (mPhotoPath != null){
    		Log.i("onWindowFocusChanged", "onWindowFocusChanged path not null"); 
	    	setPic();	    		    	
	    	/*
	    	if (showNextButton) {
		        Button buttonNext = (Button)findViewById(R.id.btnNextActivity);
		    	buttonNext.setVisibility(View.VISIBLE);
	    	}
	    	*/
    	}
    }

    //��ʼ�ϴ�
    public void startSubmit(View view){
    	//������Ƭ·��
    	String photoKey = getString(R.string.photoPathKey); 
    	PreferenceData.setString(this, mPhotoPath, photoKey);
    	
    	//Ҫ�ϴ���ͼƬ����·��photoKey
    	Log.i("My PreferenceData:", PreferenceData.getString(this, photoKey)); 

    	//��ȡҪ�ϴ���ͼƬ����
    	String uploadPhotoUri = PreferenceData.getString(this, photoKey);
    	int index = uploadPhotoUri.lastIndexOf("/");
    	String photoName = uploadPhotoUri.substring(index+1);
    	Log.i("xiaobingo,Ҫ�ϴ���ͼƬ���֣�", photoName);
    	
    	//Ҫ�ϴ���ͼƬ�ļ���С
    	File uploadImageFile = new File(uploadPhotoUri);
    	System.out.println("xiaobingo,�ϴ���ͼƬ��С(Kbytes):"+uploadImageFile.length()/1000);		
    	
    	String nameKey = getString(R.string.nameKey); 
    	String descriptionKey = getString(R.string.descriptionKey); 
    	String photoNameKey = getString(R.string.photoNameKey); 
    	
    	String name = userID;
    	
    	EditText descriptionText = (EditText)findViewById(R.id.edit_description);
    	String description = descriptionText.getText().toString();
    	
    	if (description == null || description.length() == 0
    			|| name == null || name.length() == 0) {
    		Toast.makeText(UploadActivity.this, "�������ͼƬ������", Toast.LENGTH_SHORT).show();
    		return;
    	} else if (description.length() > 100 || name.length() > 100) {
    		Toast.makeText(UploadActivity.this, "������������100���ַ�����", Toast.LENGTH_SHORT).show();
    		return;    		
    	}
    	PreferenceData.setString(this, name, nameKey);
    	PreferenceData.setString(this, description, descriptionKey);
    	PreferenceData.setString(this, photoName, photoNameKey);
    	Log.i("Saved name:", PreferenceData.getString(this, nameKey));
    	Log.i("Saved desc:", PreferenceData.getString(this, descriptionKey));
    	
    	Intent intent = new Intent (this, SubmitActivity.class);
    	Bundle bd = new Bundle();
		bd.putString("userID", userID);
		intent.putExtras(bd);
    	startActivity(intent);
    }
    
    //����back��,�ص���ҳ
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {  		
  		if (keyCode == KeyEvent.KEYCODE_BACK) {  					
  					UploadActivity.this.finish();
  					Intent intent = new Intent(this, DemoAppActivity.class);
  			    	startActivity(intent);
  			return true;
  		} else {
  			return super.onKeyDown(keyCode, event);
  		}
  	}
  	
	public void barcodeScanStart(View view){
  		Intent intent = new Intent(this,BarcodeCaptureActivity.class);
  		startActivity(intent);
  		
  	}
}