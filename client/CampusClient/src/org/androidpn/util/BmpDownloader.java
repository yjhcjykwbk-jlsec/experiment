package org.androidpn.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class BmpDownloader extends AsyncTask {
	String url;
	String fid;// file id or name
	static String LOGTAG = "BmpDownloader";
	ImageView imageView = null;

	public BmpDownloader(ImageView imageView, String url, String fid) {
		this.url = url;
		this.fid = fid;
		this.imageView = imageView;
	}

	public BmpDownloader(String url, String fid) {
		this.url = url;
		this.fid = fid;
	}

	// URL u = new URL(url);
	// HttpURLConnection conn = (HttpURLConnection) u.openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream inputStream = conn.getInputStream();
	// Bitmap bmp = BitmapFactory.decodeStream(inputStream);

	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		Bitmap bmp = null;
		try {
			//download
			InputStream is = new java.net.URL(url).openStream();
			bmp = BitmapFactory.decodeStream(is);

			//save to sd
			File file = new File(fid);
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();

			Log.i(LOGTAG, "Image saved tosd");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.w(LOGTAG, "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.w(LOGTAG, "IOException");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	protected void onPostExecute(Bitmap result) {
		if (imageView != null&& result!=null)
			imageView.setImageBitmap(result);
	}
}