package com.click369.cortex.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SavePerfrence {
	
	public static final String RATE = "RATE";//������
	public static final String CHUANKOU = "CHUANKOU";//����
	public static final String SHUJUWEI = "SHUJUWEI";//����λ
	public static final String JIOU = "JIOU";//��ż
	public static final String TINGZHIWEI = "TINGZHIWEI";//ֹͣλ
	
	public static final String JIMIMA = "JIMIMA";//
	public static final String MIMA = "MIMA";//
	public static final String IP="IP";
	Context context;
	
	//zxf 
	public static final String RATEI = "RATEI";//������
	public static final String CHUANKOUI = "CHUANKOUI";//����
	public static final String SHUJUWEII = "SHUJUWEII";//����λ
	public static final String JIOUI = "JIOUI";//��ż
	public static final String TINGZHIWEII = "TINGZHIWEII";//ֹͣλ
	 
	
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
