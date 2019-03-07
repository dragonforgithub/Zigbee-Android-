
package com.click369.cortex.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class ShengGuangService extends Service{
	public static boolean isstart = false;
	int renti=0,tianranqi=0;
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
					String datas[] = (msg.obj.toString()).split(" ");
					if(datas[4].equals("B1")){
						parseData(msg.obj.toString());
					}
					break;
				case Util.NETNUM:
					System.out.println("msg.obj="+msg.obj);
					break;
				}
			}
	};
		
	private void parseData(String dataStr){
		dataStr = dataStr.trim();
		int zhuangtai=0;
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				int gao = HexDump.hexStringToByteArray(datas[5])[0];
				int di = HexDump.hexStringToByteArray(datas[7])[0];
				int a1 = HexDump.hexStringToByteArray(datas[17])[0];
				int a2 = HexDump.hexStringToByteArray(datas[18])[0];

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
				
					renti=gao;
				
//					tianranqi=di;
				
				if(renti==78){
					 int data[] = {addr1,addr2,0xB1,0xAA,0xAA,0xAA,0x4E};
					 sendMsgToService(data,Util.JIAJU);
				}else if(renti==70){
					 int data[] = {addr1,addr2,0xB1,0xAA,0xAA,0xAA,0x46};
					 sendMsgToService(data,Util.JIAJU);
				}
			}
		}else{
			String datas[] = dataStr.split(" ");
			int gao = HexDump.hexStringToByteArray(datas[5])[0];
			int di = HexDump.hexStringToByteArray(datas[7])[0];
			int a1 = HexDump.hexStringToByteArray(datas[17])[0];
			int a2 = HexDump.hexStringToByteArray(datas[18])[0];

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
			
				renti=gao;
			
//				tianranqi=di;
			
			System.out.println("tianranqi="+tianranqi);
			if(renti==78){
				 int data[] = {addr1,addr2,0xB1,0xAA,0xAA,0xAA,0x4E};
				 sendMsgToService(data,Util.JIAJU);
			}else if(renti==70){
				 int data[] = {addr1,addr2,0xB1,0xAA,0xAA,0xAA,0x46};
				 sendMsgToService(data,Util.JIAJU);
			}
			
		}
					
   }
	
    public void onCreate() {
    	isstart = true;
    	System.out.println("Util.whichBlock 111111111111111111111");
    	System.out.println("Util.whichBlock = showdata;");
    	new Thread(){
    		public void run(){
    			while(isstart){
	    			try {
	    				System.out.println("Util.whichBlock 111111111111111111111");
	    				Util.sgHandler = myHandler;
	    				Util.sgwhichBlock = "showdata";						
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
    	System.out.println("onStartCommand");
    	return super.onStartCommand(intent, flags, startId);
    }
    public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
    
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("onBind");
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
					Message msg = Message.obtain();
					msg.what = what;
					msg.obj = datas;
					MainZigBeeService.myHandler.sendMessage(msg);
			}else{
				showMsg(getResources().getString(R.string.service_not_start));
			}
	}
	    
}
