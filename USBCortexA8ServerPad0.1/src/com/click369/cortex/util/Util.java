package com.click369.cortex.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.click369.cortex.service.MainWifiService;
import com.hoho.android.usbserial.util.HexDump;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Util {
	
		public static Handler uiHandler;//��紫���handler�����ͽ��潻��
		public static Handler clHandler;//��������
		public static Handler lHandler;//������
		public static Handler sgHandler;//��������
		
		public static Handler hjHandler;//������������
		public static String  hjwhichBlock = "30";//�������˲�ͬ�İ����ź�  Ĭ����30
		
		
		//�����ý��洫��
		public static int rate = 38400, stopBit=1,jiou=0,dataBits=0;
		//���ڵ�ַ
		public static int addr1,addr2;
		public static String whichBlock = "30";//�������˲�ͬ�İ����ź�  Ĭ����30
		public static String clwhichBlock = "30";//�������˲�ͬ�İ����ź�  Ĭ����30
		public static String lwhichBlock = "30";//�������˲�ͬ�İ����ź�  Ĭ����30
		public static String sgwhichBlock = "30";//�������˲�ͬ�İ����ź�  Ĭ����30
		
		
//		public static final String IP = "http://testwlw.java.fjjsp01.com/";
		public static String IP = "http://hcj335171.x9.fjjsp01.com/";
//		public static String IP= "http://hcj33517.x10.fjjsp01.com/";
//		public static  String IP = "http://192.168.1.109:8080/wlw/";
		public static final int NETADRR = 1000;
	    public static final int NETNUM = 1100;
	    public static final int SINGLENUM = 1200;
	    public static final int ALLDATA = 1300;
	    public static final int FDDATA = 1400;
	    public static final int FCDATA = 1500;
	    public static final int USBCONN = 1600;
	    
	    public static final int STOP = 10;
	    public static final int START = 20;
	    public static final int RESTART = 30;
	    public static final int STOPALL = 40;
	    public static final int CLOSELIGHT = 50;
	    public static final int OPENLIGHT = 60;
	    public static final int CHANGECANSHU = 70;
	    public static final int WRITEDATA = 80;
	    
	    public static final int DSONE1OPEN = 81;
	    public static final int DSONE2OPEN1 = 82;
	    public static final int DSONE2OPEN2 = 83;
	    public static final int DSONE2FAN = 84;
	    public static final int DSONE2ZHENG = 85;
	    public static final int DSONE3OPEN = 86;
	    public static final int DSONE4OPEN = 87;
	    public static final int DSONE5OPEN = 88;
	    
	    public static final int DSONE2BT1 = 89;
	    public static final int DSONE2BT2 = 90;
	    public static final int DSONE2BT3 = 91;
	    
	    public static final int DSTWO1OPEN = 101;
	    public static final int DSTWO1QUEDING = 102;
	    
	    public static final int DSTWO4OPEN = 103;
	    public static final int DSTWO5OPEN = 104;
	    public static final int JIAJU=105;
	    
	    public static final int JIDIANQI=106;//�̵��� 
	    public static final int CHUANGLIAN=107;//����������� ����
	    
	    
	    public static boolean isConnect(Context context){
	    	ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
	    	//��ȡ״̬
			State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			State hipri = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI).getState();
			//State state=manager.getActiveNetworkInfo().getState();  
			//�ж�wifi�����ӵ�����
			if(wifi == State.CONNECTED||wifi==State.CONNECTING
					||mobile == State.CONNECTED||hipri == State.CONNECTED){
				return true;
			}else{
				return false;
				
			}
			 
	    }
	    static boolean isConn=false;
	    public static final boolean isConnectBaiDu(){
	    	 	
	    		 new Thread(new Runnable() { 
					@Override
					public void run() {
					
					try {
						//System.out.println("���󡣡�https://www.baidu.com");
    		    		URL url = new URL("https://www.baidu.com");
    		    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		    		//����ʽ
    		    		conn.setRequestMethod("GET");
    		    		conn.setConnectTimeout(5*1000);
    		    		
    		    		if(conn.getResponseCode() == 200){ 
    			    		System.out.println("����������");
    			    		isConn=true;
    		    		}
    		    		
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("�����쳣");
						isConn=false;
					}
    		    		
					}
				}).start();
		    	  
	    	  return isConn;  
	    }
	    
	    /*
	    * @author zxf
	     * @category �ж��Ƿ����������ӣ���ͨ���������ж������������Ƿ����ӣ����������Ͼ�������
	     * @return
	     */ 
	    public static final boolean ping() { 
	     
	        String result = null; 
	        try { 
	                String ip = "58.217.200.112";// ping �ĵ�ַ�����Ի����κ�һ�ֿɿ������� 
	                Process p = Runtime.getRuntime().exec("ping -c 3 -w 1000 " + ip);// ping��ַ3�� 
	                // ��ȡping�����ݣ����Բ��� 
	                InputStream input = p.getInputStream(); 
	                BufferedReader in = new BufferedReader(new InputStreamReader(input)); 
	                StringBuffer stringBuffer = new StringBuffer(); 
	                String content = ""; 
	                while ((content = in.readLine()) != null) { 
	                        stringBuffer.append(content); 
	                }
	                Log.i("------ping-----", "result content : " + stringBuffer.toString()); 
	                // ping��״̬ 
	                int status = p.waitFor(); 
	                if (status == 0) { 
	                        result = "success"; 
	                        return true; 
	                } else { 
	                        result = "failed"; 
	                } 
	        } catch (IOException e) { 
	                result = "IOException"; 
	        } catch (InterruptedException e) { 
	                result = "InterruptedException"; 
	        } finally { 
	                Log.i("----result---", "result = " + result); 
	        } 
	        return false; 
	    }
	    
	    ///
	    public static String boxNum = "";
	    public static String DOWNURLPATH = "";
	    public static boolean isNeedConn = true;//�Ƿ���Ҫ��ͣ����������  ��service��ʹ��
	    
	    private static final ServiceConnection connection = new ServiceConnection() {  
	        public void onServiceDisconnected(ComponentName name) {  
	            System.out.println("onServiceDisconnected");
	        }  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	        	System.out.println("onServiceConnected");
	        }  
		};
		
	    public static void bindMyService(Context context,String url){
			DOWNURLPATH = url;
			Intent service = new Intent(context,MainWifiService.class);
			context.bindService(service, connection, Service.BIND_AUTO_CREATE);
		}
		
	    public static void unBindMyService(Context context){
			try {
				context.unbindService(connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 
	    
}
