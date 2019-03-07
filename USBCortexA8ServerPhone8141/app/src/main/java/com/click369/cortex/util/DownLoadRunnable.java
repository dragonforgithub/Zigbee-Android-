package com.click369.cortex.util;

import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownLoadRunnable implements Runnable{
	Handler handler;
	String urlPath;
	int which = 0;//0代表请求在线箱子  1代表选择箱子 2代表退出箱子 3代表获取FD数据 4代表登录  5代表注册 6代表退出
	public DownLoadRunnable(Handler handler,String urlPath,int which){

		this.handler = handler;
		this.urlPath = urlPath;
		this.which = which;

	}
	@Override
	public void run() {
		try {
			String str = Util.IP+urlPath;
			System.out.println("url   "+str);
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5*1000);
			if(conn.getResponseCode() == 200){

				Log.e("ResponseCode==", 200+"");///

				byte b[] = Util.inputStream2Bytes(conn.getInputStream());
				//System.out.println("b.length   "+b.length);
				String downLoadStr = new String(b, "UTF-8");
				Log.e("downLoadStr==", downLoadStr+"");///

				switch (which) {
					case 0:
						if(b!=null&&b.length!=0){
							sendMsgToUI(handler,downLoadStr,Util.SECESS);
						}else{
							sendMsgToUI(handler,null,Util.NOBOX);
							System.out.println("no data");
						}
						break;

					case 1:
						if(b!=null){
							if(downLoadStr.trim().equals("T")){
								sendMsgToUI(handler,downLoadStr,Util.CHOICESECESS);
							}else{
								sendMsgToUI(handler,"",Util.CHOICEFAIL);
							}
						}else{
							sendMsgToUI(handler,"",Util.CHOICEFAIL);
						}
						break;
					case 2:
						if(b!=null){
							if(downLoadStr.trim().equals("T")){
								sendMsgToUI(handler,downLoadStr,Util.SECESS);
							}else{
								sendMsgToUI(handler,"",Util.FAIL);
							}
						}else{
							sendMsgToUI(handler,"",Util.FAIL);
						}
						break;
					case 3:
						if(b!=null){
							if(downLoadStr.trim().startsWith("FD")){
								sendMsgToUI(handler,downLoadStr,Util.FDDATA);
							}else{
								sendMsgToUI(handler,"",Util.FAIL);
							}
						}
						break;
					case 4://登录
						if(b!=null){
							System.out.println(downLoadStr);
							if(downLoadStr.trim().startsWith("success")){
								sendMsgToUI(handler,downLoadStr,Util.SECESS);
							}else{
								sendMsgToUI(handler,"",Util.FAIL);
							}
						}
						break;
					case 5://注册
						if(b!=null){
							System.out.println(downLoadStr);
							if(downLoadStr.trim().startsWith("success")){
								sendMsgToUI(handler,downLoadStr,Util.SECESS);
							}else if(downLoadStr.trim().startsWith("exists")){
								sendMsgToUI(handler,"",Util.EXISTS);
							}else{
								sendMsgToUI(handler,"",Util.FAIL);
							}
						}
						break;
					case 6:
						if(b!=null){
							if(downLoadStr.trim().startsWith("success")){
								sendMsgToUI(handler,"",Util.SECESS);
							}else{
								sendMsgToUI(handler,"",Util.FAIL);
							}
						}
						break;
				}

			}else{
				sendMsgToUI(handler,null,Util.FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendMsgToUI(handler,null,Util.FAIL);
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
