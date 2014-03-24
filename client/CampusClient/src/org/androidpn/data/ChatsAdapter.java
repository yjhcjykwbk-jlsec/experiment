package org.androidpn.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidpn.demoapp.R;
import org.androidpn.server.model.User;
import org.androidpn.util.UIUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatsAdapter extends BaseAdapter {
	Map<String,ChatInfo> am;
	Context c;

	public ChatsAdapter(Context c, Map am) {
		this.am = am;
		this.c = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return am.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		List list = new ArrayList(am.values()); 
		return list.get(arg0);
//		Iterator iter = am.entrySet().iterator();
//		int s=0;Map.Entry entry=null;
//		while(iter.hasNext()&&s<=arg0){
//			entry= (Map.Entry) iter.next();s++;
//		}
//		return entry==null?null:entry.getValue();
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub\
		ChatInfo u=(ChatInfo)getItem(arg0);
		LayoutInflater li=LayoutInflater.from(c);
		View layout=li.inflate(R.layout.list_chats, null);
		((TextView)layout.findViewById(R.id.RecipientNameLabel)).setText(u.getRecipient());
		((ImageView)layout.findViewById(R.id.RecipientPhotoLabel)).
			setBackgroundDrawable(c.getResources().getDrawable(UIUtil.getPhoto(u.getRecipient())));
		((TextView)layout.findViewById(R.id.ChatDigestLabel)).setText(u.getContent());
		return layout;
	}
}