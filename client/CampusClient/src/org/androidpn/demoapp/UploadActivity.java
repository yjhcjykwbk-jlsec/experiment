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
	private static final String JPEG_FILE_PREFIX = "camera_"; //拍照上传图片命名的前缀，以区分拍照上传还是本地上传，本地上传无前缀
	/* Suffix for image file name  */
	private static final String JPEG_FILE_SUFFIX = ".jpg"; //拍照上传图片命名的后缀，统一jpg格式
	
	/* A taken photo will be displayed here */
	private ImageView mImageView;
	
	private String userID="";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
      //添加到activitylist里，方便最后统一退出
        ActivityUtil.getInstance().addActivity(this);
        
        Log.i("xiaobingo", "进入上传界面");
		Bundle bundle = this.getIntent().getExtras();
		userID = bundle.getString("userID");
        //显示图片控件
        mImageView = (ImageView)findViewById(R.id.imageViewWindow); 
        
    	String sendingOkKey = getString(R.string.sending_ok); 
    	//初始化
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
     * 点击拍照上传
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
    		Toast.makeText(UploadActivity.this, "图像路径错误，摄像头无法启动", Toast.LENGTH_LONG).show();
    	}
    	    	 
    }
    
    
    /*
     * 点击本地上传
     */
    public void startGalleryActivity(View view){

    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择一张图片"), ACTION_GALLERY_PHOTO);
    	
    }
    
    /*
     * 创建文件来保存照片
     */
    private File createPhotoFile() throws IOException{
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timestamp + "_";
		File album = getPhotoAlbumDirectory();
		if (album == null)
			return null;
		//创建拍照生成的图片文件，统一.jpg格式。以日期命名
		File imageFile = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, album);
		return imageFile;

    }
    
    /* 获取相册文件 */
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
     * 照完相或者从相册中选完图片后，返回结果图片
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
 	
    	
    	// 从相册中选完图片
    	if (requestCode == ACTION_GALLERY_PHOTO && resultCode == RESULT_OK){
    		
    		Uri selectedImageURI = data.getData();
    		mPhotoPath = getRealPathFromURI(selectedImageURI);
    		//if image exists, show the "next" button
    		//if (mPhotoPath != null)
    		//	showNextButton = true;    		
    		//从相册选取图片的路径
    		Log.i("xiaobingo:OnActivityResult, gallery photo path:", mPhotoPath); 
    	}
    	//摄像头照完相
    	else if (requestCode == ACTION_TAKE_PHOTO && resultCode == RESULT_OK) {
	
    		try {
    			//保存图片到手机
    			saveImage(); 
    			//"next" button will be shown to user
    	    	//showNextButton = true;
    			//拍照的图片路径
    			Log.i("xiaobingo:onActivityResult, camera photo path:", mPhotoPath);  
    		}
    		catch (NullPointerException np){
    			Toast.makeText(UploadActivity.this, "无法保存照片", Toast.LENGTH_LONG).show();
    			Log.e("onActivityResult", "NullPointerException when saving photo file");
    		}

	    	Log.i("onActivityResult", "Next button should be now visible");
	    	showImage();
    		
    	} 


    	
    }
    
    /*
     * 解析从相册中选取的图片的路径
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
        
        // 获取图片的大小
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
     * 当UploadActivity加载完毕后被调用
     * 调用 setPic() 显示最新的图片. 
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

    //开始上传
    public void startSubmit(View view){
    	//保存照片路径
    	String photoKey = getString(R.string.photoPathKey); 
    	PreferenceData.setString(this, mPhotoPath, photoKey);
    	
    	//要上传的图片具体路径photoKey
    	Log.i("My PreferenceData:", PreferenceData.getString(this, photoKey)); 

    	//获取要上传的图片名字
    	String uploadPhotoUri = PreferenceData.getString(this, photoKey);
    	int index = uploadPhotoUri.lastIndexOf("/");
    	String photoName = uploadPhotoUri.substring(index+1);
    	Log.i("xiaobingo,要上传的图片名字：", photoName);
    	
    	//要上传的图片文件大小
    	File uploadImageFile = new File(uploadPhotoUri);
    	System.out.println("xiaobingo,上传的图片大小(Kbytes):"+uploadImageFile.length()/1000);		
    	
    	String nameKey = getString(R.string.nameKey); 
    	String descriptionKey = getString(R.string.descriptionKey); 
    	String photoNameKey = getString(R.string.photoNameKey); 
    	
    	String name = userID;
    	
    	EditText descriptionText = (EditText)findViewById(R.id.edit_description);
    	String description = descriptionText.getText().toString();
    	
    	if (description == null || description.length() == 0
    			|| name == null || name.length() == 0) {
    		Toast.makeText(UploadActivity.this, "请输入对图片的描述", Toast.LENGTH_SHORT).show();
    		return;
    	} else if (description.length() > 100 || name.length() > 100) {
    		Toast.makeText(UploadActivity.this, "超出最大输入的100个字符长度", Toast.LENGTH_SHORT).show();
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
    
    //按下back键,回到主页
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