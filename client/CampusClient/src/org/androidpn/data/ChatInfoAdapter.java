package org.androidpn.data;

import java.util.Date;
import java.util.List;

import org.androidpn.demoapp.R;
import org.androidpn.util.UIUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ChatInfoAdapter extends BaseAdapter {
	List al;
	Context c;
	static String LOGTAG="ChatInfoAdapter";
	public ChatInfoAdapter(Context c, List al) {
		this.al = al;
		this.c = c;
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
		View v = null;
		ChatInfo ci=(ChatInfo)al.get(position);
		if(ci==null) return null;
		
		LayoutInflater li = LayoutInflater.from(c); 
		//li=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  li.inflate(itemLayout, layout, true); 
		if(((ChatInfo)al.get(position)).isSelf()){
			v = li.inflate(R.layout.list_left_chat, null);
		} else 
			v= li.inflate(R.layout.list_right_chat, null);
		Date dt=ci.getTime();
		if(ci==null||li==null||v==null||dt==null){
			Log.i(LOGTAG,"null exception");
			if(ci==null) Log.i(LOGTAG,"ci null");
			if(v==null) Log.i(LOGTAG,"v null");
			if(dt==null) Log.i(LOGTAG,"dt null");
		}
		TextView tt=(TextView) v.findViewById(R.id.tvtime);
		ImageView iv=(ImageView)v.findViewById(R.id.ivicon);
		TextView tn=(TextView) v.findViewById(R.id.tvname);
		TextView tv= (TextView) v.findViewById(R.id.tvcontent);
		if(tt==null||iv==null||tn==null||tv==null) {
			Log.i(LOGTAG,"null exception 2");
			return null;
		}
		tt.setText(DateFormat.format("MM-dd hh:mm:ss",dt.getTime()));//%Y-%m-%d %H:%M:%S %W-%A    %A %H:%M:%S
		if(ci.isSelf()){
			iv.setBackgroundDrawable(
					c.getResources().getDrawable(R.drawable.photo_3));
			tn.setText("");
		}
		else {
			iv.setBackgroundDrawable(
			c.getResources().getDrawable(UIUtil.getPhoto(ci.getName())));
			tn.setText(ci.getName());
		}
		
		tv.setText(ci.getContent());
		if(ci.isSelf())
		if(!ci.isSent()){
			tv.setTextColor(Color.RED);
		} else{
			tv.setTextColor(Color.GREEN);
			Log.i("myadapter.getview","textview for "+ci.getPacketID()+" sent and it turn green");
		}

		return v;
	}
}
