
package com.click369.cortex.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class HongWaiService extends Service{
	public static boolean isstart = false;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					parseData((String)msg.obj);
					break;
//				case 11:
//					if(!Util.isRunCammera(HongWaiService.this)){
//						Util.copyAssetToSd(HongWaiService.this);
//					}
//					break;
				case Util.NETNUM:
					System.out.println("msg.obj="+msg.obj);
					break;
			}
		}
	};

	//红外报警一次传回三条数据
	private void parseData(String dataStr){
		dataStr = dataStr.trim();
		int zhuangtai=70;
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
				if("B1".equals(datas[4])){

				}else if("B2".equals(datas[4])){

				}else if("B3".equals(datas[4])){

				}
			}
		}else{
			String datas[] = dataStr.split(" ");
			zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
			System.out.println("zhuangtai1111111111111111"+zhuangtai);
			if("B1".equals(datas[4])){

			}else if("B2".equals(datas[4])){

			}else if("B3".equals(datas[4])){

			}
		}

		if(zhuangtai == 78 && !Util.isRunCammera(this)){
			Util.copyAssetToSd(this);
		}
	}

	public void onCreate() {
		isstart = true;
		System.out.println("HongWaiSerVice2222222222222");
		new Thread(){
			public void run(){
				while(isstart){
					try {
						System.out.println("HongWaiSerVice3333333333333");
						String urlPath = "rdf?action=read&lx=bj&bn="+Util.boxNum;
						System.out.println("HongWaiSerVice4444444444");
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						System.out.println("HongWaiSerVice55555555555555");
//						myHandler.sendEmptyMessage(11);
						Thread.sleep(1000*3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		isstart = false;
		super.onDestroy();
	}

}
