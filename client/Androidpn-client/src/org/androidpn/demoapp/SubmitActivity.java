package org.androidpn.demoapp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import org.androidpn.data.PreferenceData;
import org.androidpn.util.ActivityUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * �ϴ�ͼƬ������
 * @author xiaobingo
 *
 */
public class SubmitActivity extends Activity {

	private String userID="";
	private String imagePath = "";
	private String name = "";
	private String description = "";
	private String photoName = "";
	private String sendingOk = "";
	private String submissionOk = "";
	private String upLoadServerUri = "";
	private String fileName = "";
	private String uploader = "";
	private String uploadDescription = "";
	private String uploadPhotoName = "";
	private String param = "";
	private HttpURLConnection conn = null;
	private DataOutputStream dos = null;
	private File sourceFile;
	private long totalSize = 0; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.submit);
      //��ӵ�activitylist��������ͳһ�˳�
        ActivityUtil.getInstance().addActivity(this);
        
        Bundle bundle = this.getIntent().getExtras();
		userID = bundle.getString("userID");
		
    	upLoadServerUri = getString(R.string.upload_uri);
        imagePath = PreferenceData.getString(this, getString(R.string.photoPathKey));
        name = PreferenceData.getString(this, getString(R.string.nameKey));
        description = PreferenceData.getString(this, getString(R.string.descriptionKey));
        photoName = PreferenceData.getString(this, getString(R.string.photoNameKey));
        sendingOk = PreferenceData.getString(this, getString(R.string.sending_ok));
        
        //if no image exists return to main activity
        if (imagePath == null || imagePath.length() == 0){
        	Intent intent = new Intent (this, UploadActivity.class);
        	startActivity(intent);
        }
        
        Log.d("Submit Activity, Image path", imagePath); 
        //δ�ϴ�����ʼ�ϴ�
        if (sendingOk.length() == 0){
        	initUpload();
        	FileUploadTask uploadTask = new FileUploadTask();
        	uploadTask.execute(); //ִ���ϴ�
        }
        
    }
    
//    private void initUpload(){
//    	try {
//    		uploader = new String(name.getBytes("GB2312"),"8859_1"); //֧������
//    		uploadDescription = new String(description.getBytes("GB2312"),"8859_1"); //֧������
//    		uploadPhotoName = new String(photoName.getBytes("GB2312"),"8859_1"); //֧������
//	    	param="?action=getUpload"+"&name=" + uploader+
//	    			"&description="+uploadDescription+"&photoName="+uploadPhotoName;
//	    	upLoadServerUri +=  param;
//	    	Log.i("xiaobingo", "�ϴ�ͼƬ��URL�ǣ�"+upLoadServerUri);
//    	}catch (UnsupportedEncodingException e){
//    		Log.e("SubmitActivity, UnsupportedEncodingException", e.getMessage());
//    	}
//      	
//        fileName = imagePath;
//        conn = null;
//        dos = null;       
//   
//        sourceFile = new File(imagePath);
//        if (!sourceFile.isFile()) {
//         Log.e("uploadFile", "ͼƬ�ļ�������");
//         submissionOk = "0";
//        }else {
//			totalSize = sourceFile.length();
//		}
//        
//    }
    
    private void initUpload(){
    	try {
    		uploader = new String(name.getBytes("GB2312"),"8859_1"); //֧������
    		uploadDescription = new String(description.getBytes("GB2312"),"8859_1"); //֧������
    		uploadPhotoName = new String(photoName.getBytes("GB2312"),"8859_1"); //֧������
	    	param="?url=fromandroidpn"+"&name=" + uploader+
	    			"&desp="+uploadDescription+"&photoName="+uploadPhotoName;
	    	upLoadServerUri +=  param;
	    	Log.i("xiaobingo", "�ϴ�ͼƬ��URL�ǣ�"+upLoadServerUri);
    	}catch (UnsupportedEncodingException e){
    		Log.e("SubmitActivity, UnsupportedEncodingException", e.getMessage());
    	}
      	
        fileName = imagePath;
        conn = null;
        dos = null;       
   
        sourceFile = new File(imagePath);
        if (!sourceFile.isFile()) {
         Log.e("uploadFile", "ͼƬ�ļ�������");
         submissionOk = "0";
        }else {
			totalSize = sourceFile.length();
		}
        
    }
      
  //����back��,�ص���ҳ
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		
  		if (keyCode == KeyEvent.KEYCODE_BACK) {  					
  					SubmitActivity.this.finish();
  					Intent intent = new Intent(this, DemoAppActivity.class);
  			    	startActivity(intent);
  			return true;
  		} else {
  			return super.onKeyDown(keyCode, event);
  		}
  	}
  	    
    
    class FileUploadTask extends AsyncTask<Object, Integer, String> {
    	private ProgressDialog dialog = null;
    	    	
    	int serverResponseCode = 0;     	
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
    	
        
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SubmitActivity.this);
			dialog.setTitle("�����ϴ�...");
			dialog.setMessage("0k/"+totalSize/1000+"k");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.show();
		}
    	
		@Override
		protected String doInBackground(Object... arg0) {
            try { // open a URL connection to the Servlet            	
            	
                long length = 0;
                int progress;
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 20 * 1024; //20kb
                URL url = new URL(upLoadServerUri);
                Log.i("uploadFile", "��url����");
                Log.i("XMPPChat","upload uri:"+url.getHost()+url.getPath());
                conn = (HttpURLConnection) url.openConnection(); // ��HTTP����
                // ����ÿһ��post���С��128kB����������ã����ļ��ϴ������ɹ�����
                conn.setChunkedStreamingMode(128*1024); 
                
                conn.setConnectTimeout(15000); // 15����û��Ӧ�ͶϿ����� 
                conn.setReadTimeout(10000); 
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                
                FileInputStream fileInputStream = new FileInputStream(sourceFile);     
                dos = new DataOutputStream(conn.getOutputStream());
      
                //dosͳͳ��Ҫ��writeBytes�ˣ���Ϊ�ϴ�����ͼƬ������ı���Ϣ�������˾ͽ�������ͼƬ�ˣ�
                String lineEnd = "\n";
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"upfile\";filename=\""+ fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                do{
                	// read file and write it into form...
                	bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    
                	bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
                	dos.write(buffer, 0, bytesRead);	
                    length+=bytesRead;
                    Thread.sleep(200);
                    progress = (int)((length*100)/totalSize);
                    System.out.println("�ϴ�����length��"+length+"; progress:"+progress);
                    publishProgress(progress,(int)length);
                }  
                while (bytesRead > 0) ;
                // send multipart form data necesssary after file data...        
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                publishProgress(100,(int)length);
                
                // Responses from the server (code and message)               
                Log.i("uploadFile", "getResponseCode()");
//                serverResponseCode = conn.getResponseCode();
//                String serverResponseMessage = conn.getResponseMessage();
//                 
//                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
//                if(serverResponseCode == 200){
//                	submissionOk = "OK";
//                }              
                
                //close the streams //
                fileInputStream.close();
                dos.flush();                
                dos.close();
               //sendCount++;
                 
           } catch (MalformedURLException ex) { 
               //dialog.dismiss();  
               ex.printStackTrace();
               Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
               submissionOk = "1";
           } catch (SocketTimeoutException ste){
           	Log.e("Upload file to server Exception", "Exception : " + ste.getMessage()); 
           	submissionOk = "2";
           } catch (Exception e) {
               //dialog.dismiss(); 
               e.printStackTrace();
               Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e); 
               submissionOk = "3";
           }
           dialog.dismiss();      
           //return serverResponseCode;
			return submissionOk;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			dialog.setProgress(progress[0]);
			dialog.setMessage(progress[1]/1000+"k/"+totalSize/1000+"k");
		}
		
		@Override
		protected void onPostExecute(String result) {
			String sendingOkKey = getString(R.string.sending_ok); 
	        PreferenceData.setString(SubmitActivity.this, submissionOk, sendingOkKey);
			try {
				if("OK".equals(submissionOk)){
					Toast.makeText(SubmitActivity.this, "�ϴ��ɹ���", Toast.LENGTH_LONG).show();
					//�����ϴ��ɹ�ֱ��ת������ϴ�����
		        	
		        	/* �ϴ��ɹ���ת���ϴ��ķ�����url��������ʱ��ת��
			        Intent webintent = new Intent(this, WebViewActivity.class);	
			        Bundle bd = new Bundle();
					bd.putString("userID", name);
					webintent.putExtras(bd);
					startActivity(webintent);	
					*/
				}else if ("0".equals(submissionOk)) {
					Toast.makeText(SubmitActivity.this, "ͼƬ������!", Toast.LENGTH_LONG).show();
				}else if ("1".equals(submissionOk)) {
					Toast.makeText(SubmitActivity.this, "URL��ʽ����!", Toast.LENGTH_LONG).show();
				}else if ("2".equals(submissionOk)) {
					Toast.makeText(SubmitActivity.this, "���ӷ�������ʱ", Toast.LENGTH_LONG).show();
				}else if ("3".equals(submissionOk)) {
					Toast.makeText(SubmitActivity.this, "�ϴ�����δ֪���� ", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				try {
					//return to index view
					Intent intent = new Intent(SubmitActivity.this, DemoAppActivity.class);
//			    	Bundle bd = new Bundle();
//					bd.putString("userID", userID);
//					uploadIntent.putExtras(bd);
			    	startActivity(intent);
					dialog.dismiss();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
    	
    }
}

