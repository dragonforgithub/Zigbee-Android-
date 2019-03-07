package com.click369.cortex.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class UpLoadRunnable implements Runnable{
	Handler handler;
	String urlPath;
	String upLoadStr;
	public UpLoadRunnable(Handler handler,String upLoadStr,String urlPath){
		this.handler = handler;
		this.urlPath = urlPath;
		this.upLoadStr = upLoadStr;

	}
	@Override
	public void run() {
		try {
			byte msg[] = upLoadStr.getBytes("UTF-8");
			String strUrl = Util.IP + urlPath;
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//请求方式
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5*1000);
			//设置允许用流写出
			conn.setDoOutput(true);
			//必须有内容设置 如果纯文本可以把application/soap去掉或换成text
			conn.setRequestProperty("Content-Type", "application/soap,charset=utf-8");
			//发送文本时最好带上长度  没有也能发送成功
			conn.setRequestProperty("Content-Length", String.valueOf(msg.length));
			OutputStream os = conn.getOutputStream();
			os.write(msg);
			os.close();
			if(conn.getResponseCode() == 200){
				sendMsgToUI(handler,null,1);
			}else{
				sendMsgToUI(handler,null,0);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMsgToUI(Handler handler,Object obj,int what){
		if(handler!=null){
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = obj;
			handler.sendMessage(msg);
		}
	}

}
