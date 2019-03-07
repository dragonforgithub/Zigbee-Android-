
package com.click369.cortex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class MainWifiService extends Service{
	boolean isRead = true;
	boolean isConn = true;
	public static Socket clientsocket;
	public static BufferedReader inreader;
	public static InputStream inPut;
	public static OutputStream outPut;

	public static Handler myHandler;//外界用来控制后台的handler
	public void onCreate() {
		myHandler =new Handler(){//外界用来控制后台的handler
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
					case Util.STOP:
						stopConn();
						break;
					case Util.START:
						new Thread(){
							public void run(){
								startConn();
							}
						}.start();
						break;
					case Util.RESTART:
//    				onDeviceStateChange();
						break;
					case Util.STOPALL:
						MainWifiService.this.stopSelf();
						break;
					case Util.CLOSELIGHT:
						sendByte(Util.addr1,Util.addr2,0x30,0x01,('N' & 0xff),(0 & 0xff),0xAA);
						break;
					case Util.OPENLIGHT:
						sendByte(Util.addr1,Util.addr2,0x30,0x01,('F' & 0xff),(0 & 0xff),0xAA);
						break;
					case Util.CHANGECANSHU:
						setRateAndMore();
						break;
					case Util.DSONE1OPEN:
					case Util.DSONE2OPEN2:
					case Util.DSONE2BT1:
					case Util.DSONE2BT2:
					case Util.DSONE2BT3:
					case Util.DSONE2OPEN1:
					case Util.DSONE3OPEN:
					case Util.DSONE5OPEN:

					case Util.DSTWO1OPEN:
					case Util.DSTWO1QUEDING:
					case Util.DSTWO4OPEN:
					case Util.DSTWO5OPEN:
						sendToBlock(msg);
						break;
					case Util.WRITEDATA:
						try {//写出格式 1,网内地址1 2.网内地址2 3、板子号   4、资源编号 5、数据信息1 6、数据信息2 7、数据信息3
							String msgStr = (String)msg.obj;
							String msgs[] = msgStr.split(" ");
//    					int length = msgs.length;
							byte data[] = new byte[7];
							data[6] = (byte) 0xAA;
							data[5] = (byte) 0xAA;
							data[4] = (byte) 0xAA;
							data[3] = (byte) 0xAA;
							data[2] = (byte) 0xAA;
							data[1] = (byte) 0xAA;
							data[0] = (byte) 0xAA;
							for(int i=0;i<msgs.length;i++){
								data[i] = HexDump.hexStringToByteArray(msgs[i])[0];
							}
							//发送信息
							sendByte(data[0],data[1],data[2],data[3],data[4],data[5],data[6]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
				}
			}
		};
		new Thread(){
			public void run(){
				startConn();
			}
		}.start();
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//每次启动服务或者重新设置参数就刷新各种参数
		setRateAndMore();
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendToBlock(Message msg){
		int datas1[] = (int [])msg.obj;
		if(datas1!=null){
			sendByte(datas1[0],datas1[1],datas1[2],datas1[3],datas1[4],datas1[5],datas1[6]);
		}
	}
	//写出数据 需要拼接 sDriver.write(sendByte(addr1,addr2,0x01,('N' & 0xff),(0 & 0xff)), 100);
	private byte[] sendByte(int addr1,int addr2,int jiedian,int ziyuanbh,int msg1,int msg2,int msg3){
		byte[] linedata = new byte[16];
		//帧头
		linedata[0] = (byte) (0xFE);
		linedata[1] = (byte) (0x0B);
		//内网地址
		linedata[2] = (byte) (addr1);
		linedata[3] = (byte) (addr2);
		//节点
		linedata[4] = (byte) (jiedian);
		//数据信息
		linedata[5] = (byte) (ziyuanbh);
		linedata[6] = (byte) (msg1);
		linedata[7] = (byte) (msg2);
		linedata[8] = (byte) (msg3);
		linedata[9] = (byte) (0xAA);
		linedata[10] = (byte) (0xAA);
		linedata[11] = (byte) (0xAA);
		linedata[12] = (byte) (0xAA);
		linedata[13] = (byte) (0xAA);
		linedata[14] = (byte) (0xAA);
		//异或校验
		linedata[15] = (byte) (0xAA);

		try {
			if (outPut != null) {
				outPut.write(linedata);
				//sDriver.write(linedata,100);
			}else{
				showMsg("无法发送信息，没有找到设备...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linedata;
	}

	public void stopConn() {
		if (clientsocket != null) {
			try {
				clientsocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clientsocket = null;
		}
		if (inPut != null) {
			try {
				inPut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inPut = null;
		}
		if (outPut != null) {
			try {
				outPut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outPut = null;
		}
		isRead = false;
	}

	private void setRateAndMore(){//设置rate 各种参数
		if(clientsocket!=null){
			//sDriver.setParameters(Util.rate, Util.dataBits,Util.stopBit, Util.jiou);
		}
	}

	protected void startConn() {//在此启动wifi连接 不断读入数据
		try {
			if (clientsocket == null) {
				clientsocket = new Socket("10.10.100.254", 8899);
				// 获取输入流
				inPut = clientsocket.getInputStream();
				// 获取输出流
				outPut = clientsocket.getOutputStream();
				//showMsg("没有设备,请插入设备");
			} else {
				if(!clientsocket.isClosed()){
					clientsocket.close();
				}
				clientsocket = new Socket("10.10.100.254", 8899);
				// 获取输入流
				inPut = clientsocket.getInputStream();
				// 获取输出流
				outPut = clientsocket.getOutputStream();
			}
		} catch (Exception e) {
			showMsg("未找到对应的ip，请检查网络连接是否正确");
			e.printStackTrace();
		}
		if(clientsocket!=null&&inPut!=null){
			new Thread(){
				public void run(){
					while(inPut!=null&&isConn){
						try {
							byte buffer[] = new byte[2];
							inPut.read(buffer);
							updateReceivedData(buffer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					};
				}
			}.start();
		}
	}

	//处理接收到的数据（接收到的数据可能是单个的  由于要一次显示21条 所以在此拼接，并做相应处理）  处理完后发送到前台界面
	int labelCount = 0;
	StringBuilder sb = new StringBuilder();
	private void updateReceivedData(byte[] data) {
		if(isRead){
			synchronized (data) {
				String s = HexDump.dumpHexString(data);
				if(s.startsWith("F")||s.startsWith(" F")){//如果是F开头 就意味着一条数据的开始 FC  FD
					if(sb.length()>1){
						sb.append("\r\n");
						if(sb.toString().length()>65){//防止一次接收多组数据
							String moreSB[] =  sb.toString().split("FD");
							for(String ss : moreSB){
								if(ss.length()>42){
									if(Util.whichBlock.equals("")){
										if(ss.contains("FC")){
											sendMsgToUI(ss,Util.ALLDATA);
										}else{
											sendMsgToUI("FD"+ss,Util.ALLDATA);
										}
									}
									getAddre("FD"+ss);
								}
							}
						}else{
							if(Util.whichBlock.equals("")&&sb.toString().length()>42){
								sendMsgToUI(sb.toString(),Util.ALLDATA);
							}
							getAddre(sb.toString());
						}
						sb.delete(0, sb.length());
					}
					sb.append(s);
				}else{
					sb.append(s);
				}
			}
		}
	}

	private void sendMsgToUI(Object obj,int what){
		if(Util.uiHandler!=null){
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = obj;
			Util.uiHandler.sendMessage(msg);
		}
	}


	Handler timer = new Handler(){
		public void handleMessage(Message msg) {
			sendMsgToUI("null",Util.NETADRR);
			//在此判断有没有wifi
			ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			//获取状态
			State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			//判断wifi已连接的条件
			if(wifi == State.CONNECTED||wifi==State.CONNECTING){
				isConn = true;
			}else{
				isConn = true;
			}
		}
	};
	//从获取到的数据中得到内网地址 网络号 信道号
	private void getAddre(String s){
		try {
			if(s.length()>60&&s.length()<70){
				String msg[]= s.split(" ");
				if(s.startsWith("FC")){
//				   byte data[] = HexDump.hexStringToByteArray(msg[1]+msg[2]+msg[3]);
					sendMsgToUI(msg[1]+msg[2]+"",Util.NETNUM);
					sendMsgToUI(msg[3]+"",Util.SINGLENUM);
//				   sendMsgToUI(data[0]+data[1]+"",NETNUM);
//				   sendMsgToUI(data[2]+"",SINGLENUM);
				}else if(s.startsWith("FD")){
					if(msg.length>16){
						if(Util.whichBlock.equals(msg[4])){
							//十六进制字符串转为十进制数据
							byte []bb = HexDump.hexStringToByteArray(msg[17]+msg[18]);
							Util.addr1 = bb[0];//十进制
							Util.addr2 = bb[1];
							sendMsgToUI(msg[17]+""+msg[18],Util.NETADRR);
							sendMsgToUI(s,Util.FDDATA);
							//长时间没有接受到数据则发送null到界面更改网内地址
							timer.removeMessages(0);
							timer.sendEmptyMessageDelayed(0, 6000);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		stopConn();
		super.onDestroy();
	}

}
