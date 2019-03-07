package com.click369.cortex.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class NowTime {
	static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	public static String getNowTime(){
		Date date= new Date();
		return sdf.format(date);
	}
}
