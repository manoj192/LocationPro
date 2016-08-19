package com.locationpro.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;

/**
 * Created by asanthan on 12/1/2014.
 */
public class PrefUtil {

    public static final String TENTKOTTA = "JOURNAL";

    
    
    public static void saveSession(String key, String value , Context context){
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getSession(String key,Context context){
		SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
		return prefs.getString(key, "0");
	}
	public static boolean saveArray(Context context,ArrayList<String> sKey,String key)
	{
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.putInt(key, sKey.size()); /* sKey is an array */
		//Status_size

		for(int i=0;i<sKey.size();i++)
		{

			editor.remove(key + i);
			editor.putString(key + i, sKey.get(i));
		}
            System.out.println("saved array in map"+sKey);
		return editor.commit();
	}

	public static boolean saveArrayinpos(Context context,ArrayList<String> sKey,String key,int pos,String profilemode)
	{
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.putInt(key, sKey.size()); /* sKey is an array */
		//Status_size

		for(int i=0;i<sKey.size();i++)
		{
			if (i==pos){
				editor.remove(key + i);
				editor.putString(key + i, profilemode);
			}

		}
		//System.out.println("saved array in map"+sKey);
		return editor.commit();
	}

	public static ArrayList<String> loadArray(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
		ArrayList<String> sKey=new ArrayList<String>();
		sKey.clear();
		int size = prefs.getInt(key, 0);

		for(int i=0;i<size;i++)
		{
			sKey.add(prefs.getString(key + i, null));
		}
			return sKey;
	}
	public static void clearSession(Context context){
		Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
}
