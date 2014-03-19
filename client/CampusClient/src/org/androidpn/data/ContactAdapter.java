package org.androidpn.data;

import java.util.List;

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

public class ContactAdapter extends BaseAdapter {
	List al;
	Context c;

	public ContactAdapter(Context c, List al) {
		this.al = al;
		this.c = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return al.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return al.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub\
		User u=(User)al.get(arg0);
		LayoutInflater li=LayoutInflater.from(c);
		View layout=li.inflate(R.layout.list_contact, null);
		((TextView)layout.findViewById(R.id.UsernameLabel_1)).setText(u.getUsername());
		((ImageView)layout.findViewById(R.id.UserPhotoLabel_1)).
			setBackgroundDrawable(c.getResources().getDrawable(UIUtil.getPhoto(u.getUsername())));
		return layout;
	}
}