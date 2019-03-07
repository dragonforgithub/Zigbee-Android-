package com.click369.cortex.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SavePerfrence {
	public static final String JIMIMA = "JIMIMA";//
	public static final String USERNAME = "USERNAME";//
	public static final String MIMA = "MIMA";//
	public static  String IP="IP";
	Context context;
	
	public SavePerfrence(Context context){
		this.context =  context;
	}
	
	public void savePerfrence(String fileName,String value){
		SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = spf.edit();
		editor.putString(fileName, value);
		editor.commit();
	}
	public String getPerfrence(String fileName,String defaultValue){
		SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String str = spf.getString(fileName,defaultValue);
		return str;
	}
}
