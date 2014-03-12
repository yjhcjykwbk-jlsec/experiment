package org.androidpn.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceData {
	
	 private static final String PREF_STORE = "savedImagePath";
	

	    public static SharedPreferences getSharedPreferences(Context ctx) 
	    {
	        return PreferenceManager.getDefaultSharedPreferences(ctx);
	    }

	    public static void setString(Context ctx, String str, String key) 
	    {
	        Editor editor = getSharedPreferences(ctx).edit();
	        editor.putString(key, str);
	        editor.commit();
	    }

	    public static String getString(Context ctx, String key) 
	    {
	        return getSharedPreferences(ctx).getString(key, "");
	    }
	}


