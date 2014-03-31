package org.androidpn.data;

import java.util.Date;
import java.util.List;

import org.androidpn.demoapp.R;
import org.androidpn.util.Util;

import com.google.zxing.client.android.AsyncLoadImage.AsyncImageLoader;
import com.google.zxing.client.android.AsyncLoadImage.AsyncImageLoader.ImageCallback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ChatInfoAdapter extends BaseAdapter {
	List al;
	Context c;
	static String LOGTAG="ChatInfoAdapter";
	AsyncImageLoader loader=new AsyncImageLoader();
	ListView listView;
	public ChatInfoAdapter(Context c, List al , ListView view) {
		this.al = al;
		this.c = c;
		this.listView=view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return al.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return (ChatInfo) al.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatInfo ci=(ChatInfo)al.get(position);
		if(ci==null) return null;
		
		View v = null;
		if(convertView!=null) v=convertView;
		else{
			LayoutInflater li = LayoutInflater.from(c); 
			//li=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  li.inflate(itemLayout, layout, true); 
			if(ci.isSelf()){
				if(ci.hasPic)
					v = li.inflate(R.layout.list_left_pic, null);
				else 
					v = li.inflate(R.layout.list_left_chat, null);
			} else {
				if(ci.hasPic)
					v=li.inflate(R.layout.list_right_pic, null);
				else
					v= li.inflate(R.layout.list_right_chat, null);
			}
		}
		
		Date dt=ci.getTime();
		if(ci==null||v==null||dt==null){
			Log.i(LOGTAG,"null exception");
			if(ci==null) Log.i(LOGTAG,"ci null");
			if(v==null) Log.i(LOGTAG,"v null");
			if(dt==null) Log.i(LOGTAG,"dt null");
		}
		TextView tt=(TextView) v.findViewById(R.id.tvtime);
		ImageView iv=(ImageView)v.findViewById(R.id.ivicon);
		TextView tn=(TextView) v.findViewById(R.id.tvname);
		TextView tv= (TextView) v.findViewById(R.id.tvcontent);
		ImageView tp=(ImageView) v.findViewById(R.id.tvpic);
		
		if(tt==null||iv==null||tn==null) {
			Log.i(LOGTAG,"null exception 2");
			return null;
		}
		
		if(ci.isSelf()){
			iv.setBackgroundDrawable(
					c.getResources().getDrawable(R.drawable.photo_3));
			tn.setText("");
		}
		else {
			iv.setBackgroundDrawable(
			c.getResources().getDrawable(Util.getPhoto(ci.getName())));
			tn.setText(ci.getName());
		}
		
		tt.setText(DateFormat.format("MM-dd hh:mm:ss",dt.getTime()));//%Y-%m-%d %H:%M:%S %W-%A    %A %H:%M:%S
		if(ci.hasPic){
			Log.i(LOGTAG,"it is pic:"+ci.getContent());
//			if(tv!=null) tv.setVisibility(tv.GONE);
			tp.setMinimumHeight(30);
			
			String s=ci.getContent().substring(5);
			s=s.substring(0,s.length()-1-5);
			tp.setTag(s);
			Drawable drawable=loader.loadDrawable(s, new ImageCallback(){
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					ImageView tp=(ImageView) listView.findViewWithTag(imageUrl);
					if(tp!=null){
						tp.setImageDrawable(imageDrawable);
					}
				}
			});
			if(drawable==null) tp.setImageResource(R.drawable.refresh);
			else tp.setImageDrawable(drawable);
		}
		else{	
			Log.i(LOGTAG,"it is text:"+ci.getContent());
			
			tv.setText(ci.getContent());
			if(ci.isSelf())
			if(!ci.isSent()){
				tv.setTextColor(Color.RED);
			} else{
				tv.setTextColor(Color.GREEN);
				Log.i("myadapter.getview","textview for "+ci.getPacketID()+" sent and it turn green");
			}
		}
		return v;
	}
}
