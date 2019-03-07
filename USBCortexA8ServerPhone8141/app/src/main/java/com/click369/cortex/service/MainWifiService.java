
package com.click369.cortex.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class MainWifiService extends Service{
	boolean isRead = true;
	boolean isConn = true;

	public static Handler myHandler;//外界用来控制后台的handler
	public Thread downLoadThread;
	private String uploadStr = "";
	public void onCreate() {
		isRead = true;
		isConn = true;

		myHandler =new Handler(){//外界用来控制后台的handler
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
					case Util.STOP:
						stopConn();
						break;
					case Util.START:

						break;
					case Util.RESTART:
						startConn();
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
					case Util.SENDDATATOSERVICE://上传数据
						sendToBlock(msg);
						break;
					case Util.SENDAUTODATATOSERVICE:////自动调节模式的发送方法
						sendAutoDataToBlock(msg);
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

		if(Util.isNeedConn){//如果需要不停的请求数据再开启线程
			startConn();
		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//每次启动服务或者重新设置参数就刷新各种参数
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendToBlock(Message msg){
		int datas1[] = (int [])msg.obj;
		if(datas1!=null){
			byte linedata[] =sendByte(datas1[0],datas1[1],datas1[2],datas1[3],datas1[4],datas1[5],datas1[6]);
			uploadStr = HexDump.dumpHexString(linedata);
			System.out.println(uploadStr);
			new Thread(runUpLoadThread).start();

		}
	}
	private void sendAutoDataToBlock(Message msg){//自动调节模式的发送方法
		int datas[] = (int [])msg.obj;
		if(datas!=null){
			byte byteDatas[] = {(byte)datas[0],(byte)datas[1],(byte)datas[2],(byte)datas[3],(byte)datas[4],(byte)datas[5]};
			uploadStr = HexDump.dumpHexString(byteDatas);
			new Thread(runUpLoadThread).start();
		}
	}

	//写出数据 需要拼接 sDriver.write(sendByte(addr1,addr2,0x01,('N' & 0xff),(0 & 0xff)), 100);
	private byte[] sendByte(int addr1,int addr2,int jiedian,int ziyuanbh,int msg1,int msg2,int msg3){
		byte[] linedata = new byte[16];
		//帧头
		linedata[0] = (byte) (0xFD);///测试
		linedata[1] = (byte) (0x0A);///
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
		return linedata;

	}
	Runnable  runUpLoadThread  = new Runnable(){
		public void run(){
			try {
				byte msg[] = uploadStr.getBytes("UTF-8");
				//
				String strUrl = Util.UPURLSTR+ Util.boxNum;
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
				if(conn.getResponseCode() ==200){
					byte b[] = Util.inputStream2Bytes(conn.getInputStream());
					System.out.println("上传成功，服务器返回结果："+new String(b, "UTF-8"));
				}
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "发送失败,请检查网络连接", Toast.LENGTH_SHORT).show();
			}
		}
	};

	int noResCount = 0;
	Runnable downLoadRun = new Runnable(){
		public void run(){
			while(isRead&&isConn){
				try {

					String str = Util.DOWNURLSTR+Util.DOWNURLPATH ;
					System.out.println("Runnable_url:"+str);///
					URL url = new URL(str);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5*1000);
					byte b[] = Util.inputStream2Bytes(conn.getInputStream());

					if(b!=null){
						String downLoadStr = new String(b, "UTF-8")+"";

						System.out.println(downLoadStr);///
						if(downLoadStr.contains("FC")){
							getAddre(downLoadStr);
						}else if(downLoadStr.contains("FD")){
							getAddre(downLoadStr);//得到网内地址并发送FD数据到界面
						}
						timer.sendEmptyMessage(0);//判断网络是否联通
						Thread.sleep(Util.FRESHTIME);
					}else{
						System.out.println("no data");
						if(noResCount>=3){
							isRead = false;
							sendMsgToUI(null,Util.SERVICEDOWNTHREADSTOP);
						}
						noResCount++;
					}
				} catch (Exception e) {
					isRead = false;
					sendMsgToUI(null,Util.SERVICEDOWNTHREADSTOP);
					e.printStackTrace();
				}
			}
		}
	};

	public void stopConn() {
		isRead = false;
		if(downLoadThread!=null){
			downLoadThread.interrupt();
		}
	}

	protected void startConn() {//在此启动wifi连接 不断读入数据
		isRead = true;
		if(Util.isConnect(MainWifiService.this)){

			if(downLoadThread ==null){
				downLoadThread = new Thread(downLoadRun);
				downLoadThread.start();
			}else if(!downLoadThread.isAlive()){
				downLoadThread = new Thread(downLoadRun);
				downLoadThread.start();
			}
		}else{
			sendMsgToUI("请打开网络后点击此处重新连接",Util.NONETWORK);
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
//    			sendMsgToUI("null",Util.NETADRR);
			//在此判断有没有wifi
			if(Util.isConnect(MainWifiService.this)){
				isConn = true;
			}else{
				isConn = false;
				isRead = false;
				showMsg("网络无法连接，请检查网络!!!");
			}
		}
	};

	/**
	 * 从获取到的数据中得到内网地址 网络号 信道号
	 **/
	private void getAddre(String s){
		try {
			if(s.length()>60){//&&s.length()<70如果是红外报警  可能一次会传来三条数据长度会大于70
				String msg[]= s.split(" ");
				if(s.startsWith("FC")){
					//sendMsgToUI(msg[1]+msg[2]+"",Util.NETNUM);
					//sendMsgToUI(msg[3]+"",Util.SINGLENUM);
				}else if(s.startsWith("FD")){
					if(msg.length>16){
//					   if(Util.whichBlock.equals(msg[4])){
						//十六进制字符串转为十进制数据
						byte []bb = HexDump.hexStringToByteArray(msg[1]+msg[4]);
						Util.addr1 = bb[0];//十进制
						Util.addr2 = bb[1];
						//sendMsgToUI(msg[1]+""+msg[4],Util.NETADRR);
						sendMsgToUI(s,Util.FDDATA);
						//Log.e("285 网内地址==", msg[1]+"  "+msg[4]);///
						//System.out.println(".addr.........."+Util.addr1+"   "+Util.addr2);
						System.out.println(".msg[1]+msg[4].........."+msg[1]+"   "+msg[4]);
						//长时间没有接受到数据则发送null到界面更改网内地址
//				           timer.removeMessages(0);
//				           timer.sendEmptyMessageDelayed(0, 6000);
//					   }
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
