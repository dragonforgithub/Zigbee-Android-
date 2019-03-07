
package com.click369.cortex.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.click369.cortex.activity.AutoActivity;
import com.click369.cortex.activity.ChuangLianActivity;
import com.click369.cortex.activity.KaiGuanLiangKongZhiActivity;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class ChuangLianService extends Service{
	public static boolean isstart = false;
	public static double wendu=0,guangzhao=0;
	String time;
	int addr1=1,addr2=1;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			System.out.println("111111111111111111111111handler");
			System.out.println("msg.what="+msg.what);
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					parseData((String)msg.obj);
					break;
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
				int gao = HexDump.hexStringToByteArray(datas[5])[0];
				int di = HexDump.hexStringToByteArray(datas[6])[0];
				int a1 = HexDump.hexStringToByteArray(datas[17])[0];
				int a2 = HexDump.hexStringToByteArray(datas[18])[0];
				if(datas[4].equals("09")){
					wendu=gao;
				}else if(datas[4].equals("02")){
					if(gao<0){
						gao=gao+256;
					}else{

					}
					if(di<0){
						di=di+256;
					}else{

					}
					guangzhao=gao*255+di;
				}else if(datas[4].equals("A6")){
					if(a1<0){
						addr1=a1+256;
					}else{
						addr1=a1;
					}
					if(a2<0){
						addr2=a2+256;
					}else{
						addr2=a2 ;
					}

					zhuangtai=gao;
				}

			}
		}else{
			String datas[] = dataStr.split(" ");
			int gao = HexDump.hexStringToByteArray(datas[5])[0];
			int di = HexDump.hexStringToByteArray(datas[6])[0];
			int a1 = HexDump.hexStringToByteArray(datas[17])[0];
			int a2 = HexDump.hexStringToByteArray(datas[18])[0];
			if(datas[4].equals("09")){
				wendu=gao;
				System.out.println("wendu="+wendu);
			}else if(datas[4].equals("02")){
				if(gao<0){
					gao=gao+256;
				}else{

				}
				if(di<0){
					di=di+256;
				}else{

				}
				guangzhao=gao*255+di;
				System.out.println("guangzhao="+guangzhao);
			}else if(datas[4].equals("A6")){
				if(a1<0){
					addr1=a1+256;
				}else{
					addr1=a1;
				}
				if(a2<0){
					addr2=a2+256;
				}else{
					addr2=a2 ;
				}
				System.out.println("addr1+addr2="+addr1+addr2);
			}

		}

		if(AutoActivity.guangzhao_c!=0){
			System.out.println("光照！");
			System.out.println("AutoActivity.guangzhao_c="+AutoActivity.guangzhao_c);
			if(guangzhao<AutoActivity.guangzhao_c){//开窗帘
				System.out.println("guangzhao<AutoActivity.guangzhao_c");
				int data[] = {addr1,addr2,0xA6,0x4E,0xAA,0xAA,0xAA};
				Util.sendMsgToService(data,Util.SENDDATATOSERVICE,ChuangLianService.this);
				System.out.println("光照开窗帘!");
			}else if(guangzhao>AutoActivity.guangzhao_c+300){//关窗帘
				System.out.println(guangzhao<AutoActivity.guangzhao_c+300);
				int data[] = {addr1,addr2,0xA6,0x46,0xAA,0xAA,0xAA};
				Util.sendMsgToService(data,Util.SENDDATATOSERVICE,ChuangLianService.this);
				System.out.println("光照关窗帘!");
			}
		}else if(AutoActivity.wendu_c!=0){
			System.out.println("温度！");
			if((wendu<=AutoActivity.wendu_c) && zhuangtai!=70){//关窗帘
				System.out.println("wendu<=AutoActivity.wendu_c" );
				int data[] = {addr1,addr2,0xA6,0x46,0xAA,0xAA,0xAA};
				Util.sendMsgToService(data,Util.SENDDATATOSERVICE,ChuangLianService.this);
				System.out.println("温度关窗帘");
			}else if(wendu>AutoActivity.wendu_c+5 && zhuangtai!=78){//开窗帘
				System.out.println("wendu>AutoActivity.wendu_c+10");
				int data[] = {addr1,addr2,0xA6,0x4E,0xAA,0xAA,0xAA};
				Util.sendMsgToService(data,Util.SENDDATATOSERVICE,ChuangLianService.this);
				System.out.println("温度开窗帘");

			}
		}
	}

	public void onCreate() {
		isstart = true;
		String urlPath = "lx=jsq&bn="+Util.boxNum;
		Util.isNeedConn = false;
		Util.bindMyService(this,urlPath);
		new Thread(){
			public void run(){
				while(isstart){
					try {
						String urlPath = "rdf?action=read&lx=gzd&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						urlPath = "rdf?action=read&lx=wsd&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						urlPath = "rdf?action=read&lx=jsq&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
//						urlPath = "rdf?action=read&lx=fs&bn="+Util.boxNum;
//						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						urlPath = "rdf?action=read&lx=wbl&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						SimpleDateFormat s=new SimpleDateFormat("HH:mm");
						String date=s.format(new Date());
						System.out.println("date="+date);
						System.out.println("AutoActivity.time_c="+AutoActivity.time_c);
						if(AutoActivity.time_c.equals(date)){
							System.out.println("按时开窗帘！");
							int data[] = {addr1,addr2,0xA6,0x4E,0xAA,0xAA,0xAA};
							Util.sendMsgToService(data,Util.SENDDATATOSERVICE,ChuangLianService.this);
						}
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
