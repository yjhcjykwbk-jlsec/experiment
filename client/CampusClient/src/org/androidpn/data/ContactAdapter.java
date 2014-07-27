package org.androidpn.data;

import java.util.List;

import org.androidpn.demoapp.R;
import org.androidpn.server.model.App;
import org.androidpn.server.model.Contacter;
import org.androidpn.server.model.User;
import org.androidpn.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {
	List<Contacter> al;
	Context c;

	public ContactAdapter(Context c, List<Contacter> al) {
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
		Contacter u=(Contacter)al.get(arg0);
		LayoutInflater li=LayoutInflater.from(c);
		View layout=li.inflate(R.layout.list_contact, null);
		((TextView)layout.findViewById(R.id.NameLabel)).setText(u.getName());
		((TextView)layout.findViewById(R.id.DespLabel)).setText(u.getDesp());
		((ImageView)layout.findViewById(R.id.PhotoLabel)).
			setBackgroundDrawable(c.getResources().getDrawable(Util.getPhoto(u.getName())));
		return layout;
	}
}