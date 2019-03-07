
package com.click369.cortex.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.click369.cortex.activity.AutoActivity;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class DengGuangService extends Service{
	public static boolean isstart = false;
	double renti=0,guangzhao=0;
	String time;
	double t=0;//20分钟
	int addr1=1,addr2=1;
	Calendar start;
	Calendar end;
	long startTime;
	long endTime;
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
		int zhuangtai=0;
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				int gao = HexDump.hexStringToByteArray(datas[5])[0];
				int di = HexDump.hexStringToByteArray(datas[6])[0];
				int a1 = HexDump.hexStringToByteArray(datas[17])[0];
				int a2 = HexDump.hexStringToByteArray(datas[18])[0];
				if(datas[4].equals("B1")){
					renti=gao;
					if(renti==70){
						end = Calendar.getInstance();
						endTime = end.getTimeInMillis();
						System.out.println("endTime-startTime="+(endTime-startTime));
						if(endTime-startTime>=120000){
							t=20;
						}else{
							t=0;
						}
					}else if(renti==78){
						start = Calendar.getInstance();
						startTime = start.getTimeInMillis();
					}
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
				}else if(datas[4].equals("A8")){
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
			if(datas[4].equals("B1")){
				renti=gao;
				if(renti==70){
					end = Calendar.getInstance();
					endTime = end.getTimeInMillis();
					System.out.println("endTime-startTime="+(endTime-startTime));
					if(endTime-startTime>=120000){
						t=20;
					}else{
						t=0;
					}
				}else if(renti==78){
					start = Calendar.getInstance();
					startTime = start.getTimeInMillis();
				}
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
			}else if(datas[4].equals("A8")){
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

		if(AutoActivity.renti_l!=0){
			System.out.println("人体！");
			System.out.println("tenti="+(int)renti);
			if((int)renti==70 && t==20 && zhuangtai==78){//关灯
				int data[] = {addr1,addr2,0xA8,0x46,0xAA,0xAA,0xAA};
				sendMsgToService(data,Util.JIAJU);
				System.out.println("人体关灯");
				t=0;
			}else if((int)renti==78 && zhuangtai==70){//开灯
				System.out.println("人体开灯");
				int data[] = {addr1,addr2,0xA8,0x4E,0xAA,0xAA,0xAA};
				sendMsgToService(data,Util.JIAJU);
				start = Calendar.getInstance();
				startTime = start.getTimeInMillis();
			}
		}else if(AutoActivity.guangzhao_l!=0){
			System.out.println("光照！");
			if(guangzhao<=AutoActivity.guangzhao_l){//开灯3
				int data[] = {addr1,addr2,0xA8,0x4E,0x4E,0x4E,0xAA};
				sendMsgToService(data,Util.JIAJU);
				System.out.println("开灯3");
			}else  if(AutoActivity.guangzhao_l+50<guangzhao && guangzhao<=AutoActivity.guangzhao_l+200){//开灯2
				int data[] = {addr1,addr2,0xA8,0x4E,0x4E,0x46,0xAA};
				sendMsgToService(data,Util.JIAJU);
				System.out.println("开灯2");
			} else if(AutoActivity.guangzhao_l+250<guangzhao&& guangzhao<=AutoActivity.guangzhao_l+400){//开灯1
				int data[] = {addr1,addr2,0xA8,0x4E,0x46,0x46,0xAA};
				sendMsgToService(data,Util.JIAJU);
				System.out.println("开灯1");
			}else if(AutoActivity.guangzhao_l+450<guangzhao){
				int data[] = {addr1,addr2,0xA8,0x46,0x46,0x46,0xAA};
				sendMsgToService(data,Util.JIAJU);
				System.out.println("开灯1");
			}
		}
	}

	public void onCreate() {
		isstart = true;

		new Thread(){
			public void run(){
				while(isstart){
					try {
						Util.lHandler = myHandler;
						Util.lwhichBlock = "showdata";
						SimpleDateFormat s=new SimpleDateFormat("HH:mm");
						String date=s.format(new Date());
						if(AutoActivity.time_l.equals(date)){
							int data[] = {addr1,addr2,0xA8,0x4E,0xAA,0xAA,0xAA};
							sendMsgToService(data,Util.JIAJU);
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
	public void showMsg(String text)
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
	//datas的顺序 1、网内地址1 2、网内地址2  3、板子节点  4、资源节点  5、控制信息1  6、控制信息2 7、控制信息3
	private void sendMsgToService(int datas[],int what){
		if(MainZigBeeService.myHandler!=null){
//				if(a){
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = datas;
			MainZigBeeService.myHandler.sendMessage(msg);
//                    showMsg(anniu);

//				}else{
//					showMsg("无法获取工程设计板的网内地址，可能由于该板未连接");
//				}
		}else{
			showMsg(getResources().getString(R.string.service_not_start));
		}
	}
}
