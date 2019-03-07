package com.click369.cortex.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SavePerfrence {
	
	public static final String RATE = "RATE";//波特率
	public static final String CHUANKOU = "CHUANKOU";//串口
	public static final String SHUJUWEI = "SHUJUWEI";//数据位
	public static final String JIOU = "JIOU";//奇偶
	public static final String TINGZHIWEI = "TINGZHIWEI";//停止位
	
	public static final String JIMIMA = "JIMIMA";//
	public static final String MIMA = "MIMA";//
	public static final String IP="IP";
	Context context;
	
	//zxf 
	public static final String RATEI = "RATEI";//波特率
	public static final String CHUANKOUI = "CHUANKOUI";//串口
	public static final String SHUJUWEII = "SHUJUWEII";//数据位
	public static final String JIOUI = "JIOUI";//奇偶
	public static final String TINGZHIWEII = "TINGZHIWEII";//停止位
	 
	
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
