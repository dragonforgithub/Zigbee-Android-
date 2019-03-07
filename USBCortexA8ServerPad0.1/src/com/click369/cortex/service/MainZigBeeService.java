
package com.click369.cortex.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;


@SuppressLint("HandlerLeak")
public class MainZigBeeService extends Service{
    private final String TAG = MainZigBeeService.class.getSimpleName();
    public static UsbSerialDriver sDriver = null;
    private String netNumStr = "",singelNumStr = "";
    boolean isRead = true;
    boolean isUploadBoxNum = false;
    int boxnum = 40;
    
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    
    public static Handler myHandler;//����������ƺ�̨��handler
    public void onCreate() {
    	myHandler =new Handler(){//����������ƺ�̨��handler
        	public void handleMessage(android.os.Message msg) {
        		switch (msg.what) {
    			case Util.STOP:
    				stopIoManager();
    				break;
    			case Util.START:
    				startIoManager();
    				break;
    			case Util.RESTART:
    				onDeviceStateChange();
    				break;
    			case Util.STOPALL:
    				MainZigBeeService.this.stopSelf();
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
    			case Util.JIAJU:
    			case Util.JIDIANQI:	//�̵��� ����
    			case Util.CHUANGLIAN:	//����������� ����
    			case Util.DSTWO5OPEN:
    				sendToBlock(msg);
    				break;
    			case Util.WRITEDATA:
    				try {//д����ʽ 1,���ڵ�ַ1 2.���ڵ�ַ2 3�����Ӻ�   4����Դ��� 5��������Ϣ1 6��������Ϣ2 7��������Ϣ3
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
//    						data[i] = (byte)msgs[i].charAt(0);
    						System.out.println("data["+i+"] ="+data[i]);
    					}
    					//������Ϣ
    					sendByte(data[0],data[1],data[2],data[3],data[4],data[5],data[6]);
    					} catch (Exception e) {
						e.printStackTrace();
					}
    				break;
    			}
        	}
        };
        start();
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	//ÿ��������������������ò�����ˢ�¸��ֲ���
    	setRateAndMore();
    	return super.onStartCommand(intent, flags, startId);
    }
    
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "onRunError");
        }
        @Override
        public void onNewData(final byte[] data) {
            new Thread(){
            	public void run(){
                    MainZigBeeService.this.updateReceivedData(data);
                }
            }.start();
        }
    };
    
    private void sendToBlock(Message msg){
    	int datas1[] = (int [])msg.obj;
		if(datas1!=null){
			sendByte(datas1[0],datas1[1],datas1[2],datas1[3],datas1[4],datas1[5],datas1[6]);
		}
    }
    //д������ ��Ҫƴ�� sDriver.write(sendByte(addr1,addr2,0x01,('N' & 0xff),(0 & 0xff)), 100);
    private byte[] sendByte(int addr1,int addr2,int jiedian,int ziyuanbh,int msg1,int msg2,int msg3){
        byte[] linedata = new byte[16];
        //֡ͷ
        linedata[0] = (byte) (0xFD);
        linedata[1] = (byte) (0x0A);
        //������ַ
        linedata[2] = (byte) (addr1);
        linedata[3] = (byte) (addr2);
        //�ڵ�
        linedata[4] = (byte) (jiedian);
        //������Ϣ
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
        //���У��
        linedata[15] = (byte) (0xAA);
        
        System.out.println("addr1"+addr1);
        System.out.println("addr2"+addr2);
        System.out.println("jiedian"+jiedian);
        System.out.println("ziyuanbh"+ziyuanbh);
        System.out.println("msg1"+msg1);
        System.out.println("msg2"+msg2);
        System.out.println("msg3"+msg3);
        
        writeData(linedata);
        return linedata;
    }
    
    private void writeData(byte linedata[]){
    	
        try {
        	if (sDriver != null) {
        		sDriver.write(linedata,100);
        		String writeData=HexDump.dumpHexString(linedata);//ʮ������ �ַ���
    			
    			showMsg("writeData="+writeData);
				
        	}else{
        		showMsg("�޷�������Ϣ��û���ҵ��豸����...");
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    protected void stop() {
        stopIoManager();
        if (sDriver != null) {
            try {
                sDriver.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sDriver = null;
        }
        if(upLoadThread!=null&&upLoadThread.isAlive()){
        	
		}
    }
    
    private void setRateAndMore(){//����rate ���ֲ���
        try {
                if(sDriver!=null){
                    sDriver.setParameters(Util.rate, Util.dataBits,Util.stopBit, Util.jiou);
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
    }
    
    protected void start() {
        Log.d(TAG, "Resumed, sDriver=" + sDriver);
        if (sDriver == null) {
            //showMsg("û���豸,������豸");
        } else {
            try {
                sDriver.open();
                //115200  38400,8, UsbSerialDriver.STOPBITS_1
                sDriver.setParameters(Util.rate, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "�����豸����: " + e.getMessage(), e);
                showMsg("�����豸����: " + e.getMessage());
                try {
                    sDriver.close();
                } catch (IOException e1) {
                	e1.printStackTrace();
                }
                sDriver = null;
                return;
            }
        }
        
        onDeviceStateChange();
    }

    //ֹͣ��usb��ȡ����
    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "����ֹͣ manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }
    
    //��ʼ��USB��ȡ����
    private void startIoManager() {
        if (sDriver != null) {
            Log.i(TAG, "��������  manager ..");
            mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    //����
    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    //������յ������ݣ����յ������ݿ����ǵ�����  ����Ҫһ����ʾ21�� �����ڴ�ƴ�ӣ�������Ӧ����  ��������͵�ǰ̨����
    int labelCount = 0;
    StringBuilder sb = new StringBuilder();
    private synchronized void updateReceivedData(byte[] data) {
        if(isRead){
        	String s = HexDump.dumpHexString(data);
        	if(sb.length() == 0){
        		if(!s.startsWith("F")){
        			return;
        		}
        	}
        	sb.append(s);
        	if(sb.length()>=63){
        		String recStr ="";
        		if(sb.length()==63){
        			recStr = sb.toString();
        			sb.delete(0, sb.length());
        		}else{
        			recStr = sb.substring(0, 63);
        			sb.delete(0, sb.length());
        		}
        		
        		getAddreAndSendToUI(recStr);
        		
        	}
        }
    }
    
//    int i= 0;
    String strUrl = Util.IP+"rdf?bn="+boxnum;//�ϴ����ӱ��
    Runnable  runUpLoadThread  = new Runnable(){
		public void run(){
	    	try {
	    		
	    		byte msg[] = uploadStr.getBytes("UTF-8");
	    		System.out.println("msg.length  "+msg.length);
	    		
//	    		String strUrl = "http://192.168.2.69:8083/wlw/padUpLoad?name="+netNumStr;//�ϴ����ӱ��
//	    		String strUrl = "http://192.168.2.69:8083/WEBUpLoadFileTest/test?name="+netNumStr;//�ϴ����ӱ��
	    		System.out.println("���󡣡�"+strUrl);
	    		URL url = new URL(strUrl);
	    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    		//����ʽ
	    		conn.setRequestMethod("POST");
	    		conn.setConnectTimeout(5*1000);
	    		//������������д��
	    		conn.setDoOutput(true);
	    		//�������������� ������ı����԰�application/soapȥ���򻻳�text
	    		conn.setRequestProperty("Content-Type", "application/soap,charset=utf-8");
	    		//�����ı�ʱ��ô��ϳ���  û��Ҳ�ܷ��ͳɹ�
	    		conn.setRequestProperty("Content-Length", String.valueOf(msg.length));
	    		OutputStream os = conn.getOutputStream();
	    		os.write(msg);
	    		os.flush();
	    		os.close();
	    		if(conn.getResponseCode() == 200){
		    		byte b[] = inputStream2Bytes(conn.getInputStream());
		    		System.out.println("b.size = "+b.length);
		    		String recMsg = new String(b, "UTF-8");
		    		recMsg = recMsg.trim();
		    		System.out.println("recMsg  :"+recMsg+"\r\n �Ƿ�FE��ͷ=    "+recMsg.startsWith("FE"));
		    		if(recMsg.startsWith("FD")||recMsg.startsWith("FC")||recMsg.startsWith("FE")){//�ж϶��������ΪFE ��ʽ������
		    			recMsg=recMsg.replaceAll(" ", "");//��Ҫ�Ƴ��ո���ת��Ϊ�ֽ�����
		    			byte sendBytes[] = HexDump.hexStringToByteArray(recMsg);
		    			System.out.println("sendBytes.length  "+sendBytes.length);
		    			writeData(sendBytes);//��zigbeeд��ȥ
		    			System.out.println("write out");
		    			
		    		}
	    		}
	    		conn.disconnect();
	    	} catch (Exception e) {
	    		//e.printStackTrace();
	    		//showMsg(getResources().getString(R.string.uploaded_data_error));
	    		Log.e(TAG, getResources().getString(R.string.uploaded_data_error));
	    	}
		}
	};
	
    StringBuilder sbSend = new StringBuilder();
    String uploadStr = "";
    Thread upLoadThread =null;
    private void postToHttp(String str){
    	 
    	//if(Util.isConnect(this)){ ///
    	if(Util.isConnectBaiDu()){
    		
	    	sbSend.append(str+"\r\n");
	    	if(sbSend.toString().length()>300){
	    		uploadStr = new String(sbSend.toString());
	    		
	    		Log.e("uploadStr=", uploadStr);///
	    		
	    		sbSend.delete(0, sbSend.length());
	    		upLoadThread = new Thread(runUpLoadThread);
	    		if(!upLoadThread.isAlive()){
	    			upLoadThread.start();
	    		}
	    	}
    	}else{
    		Log.e(TAG,"�������ӳ���");
    		
    	}
    	
    	
    }
    //�ͻ��˺ͷ�������Ҫ�õ��Ķ�ȡ������
    public byte[] inputStream2Bytes(InputStream inStream) {
    	ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
    	byte[] buff = new byte[1024];
    	int rc = 0;
    	try {
    		while ((rc = inStream.read(buff, 0, 1024)) > 0) {
    			swapStream.write(buff, 0, rc);
    			swapStream.flush();
    		}
    		inStream.close();
    		//Log.e("swapStream=", swapStream.toByteArray().length+"");
    		
    		return swapStream.toByteArray();
    	} catch (IOException e) {
    		e.printStackTrace();
    		//Log.e("IOException=", "IOException");
    		return null;
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
    private void sendMsgToCL(Object obj,int what){
 	    if(Util.clHandler!=null){
 		Message msg = Message.obtain();
 		msg.what = what;
 		msg.obj = obj;
 		Util.clHandler.sendMessage(msg);
 	   }
     }
    
    private void sendMsgToL(Object obj,int what){
 	    if(Util.lHandler!=null){
 		Message msg = Message.obtain();
 		msg.what = what;
 		msg.obj = obj;
 		Util.lHandler.sendMessage(msg);
 	   }
     }
    private void sendMsgTosg(Object obj,int what){
 	    if(Util.sgHandler!=null){
 		Message msg = Message.obtain();
 		msg.what = what;
 		msg.obj = obj;
 		Util.sgHandler.sendMessage(msg);
 	   }
     }
    
    Handler timer = new Handler(){
    	public void handleMessage(Message msg) {
    			sendMsgToUI("null",Util.NETADRR);
    	}
    };
    //�ӻ�ȡ���������еõ�������ַ ����� �ŵ���
    boolean isF=true;
    private void getAddreAndSendToUI(String s){
        try {
        	
			if(s.length()>60&&s.length()<65){
				
			   if (isF) {
				   s="FC 00 06 10 54 28 AA AA AA AA AA AA AA AA AA AA AA AA AA AA AA ";
				   isF=false;
			   }else{
				   //s="FD 0B 00 00 09 55 22 35 40 AA AA AA AA AA 28 78 AA 88 88 AA 12 ";///13:11
				   
			   }
			   String msg[]= s.split(" ");
			   
			   //postToHttp(s);//�ϴ���������
			   
			   if(s.startsWith("FC")){
				   int num = HexDump.hexStringToByteArray(msg[1])[0]+HexDump.hexStringToByteArray(msg[2])[0];
				   netNumStr = num+"";//�����
				   singelNumStr = msg[3]+"";
				   sendMsgToUI(msg[1]+msg[2]+"",Util.NETNUM);
				   sendMsgToUI(singelNumStr,Util.SINGLENUM);
				   sendMsgToUI(s,Util.FCDATA);
				   
				   if(!isUploadBoxNum){
					   boxnum = HexDump.hexStringToByteArray(msg[5])[0];//��ȡ���ӱ��40
					   strUrl = Util.IP+"rnf?bn="+boxnum+"&netnum="+netNumStr+"&singlenum="+singelNumStr;//�ϴ����ӱ��
					   //strUrl = Util.IP+"rnf?bn=40&netnum=6&singlenum=10";//�ϴ����ӱ��
			           
					   uploadStr = "";
					   Thread upLoadThread = new Thread(runUpLoadThread);
			    	   upLoadThread.start();
					   isUploadBoxNum = true;
					   
				   }
			   }else if(s.startsWith("FD")){
				   if(msg.length>=16){/// msg.length>16
					   
					   if(Util.whichBlock.equals(msg[4])||Util.whichBlock.equals("showdata")){
						   //ʮ�������ַ���תΪʮ��������
				           byte []bb = HexDump.hexStringToByteArray(msg[1]+msg[4]);
				           Util.addr1 = bb[0];//ʮ����
				           Util.addr2 = bb[1];
				           sendMsgToUI(msg[1]+""+msg[4],Util.NETADRR);//���ڵ�ַ
				           sendMsgToUI(s,Util.FDDATA);
				           //��ʱ��û�н��ܵ���������null������������ڵ�ַ
				           timer.removeMessages(0);
				           timer.sendEmptyMessageDelayed(0, 6000);
				           
					   }
					   if(Util.clwhichBlock.equals(msg[4])||Util.clwhichBlock.equals("showdata")){
						   //ʮ�������ַ���תΪʮ��������
				           byte []bb = HexDump.hexStringToByteArray(msg[1]+msg[4]);				          
				           sendMsgToCL(s,Util.FDDATA);
				           //��ʱ��û�н��ܵ���������null������������ڵ�ַ
				           timer.removeMessages(0);
				           timer.sendEmptyMessageDelayed(0, 6000);
					   }
					   if(Util.lwhichBlock.equals(msg[4])||Util.lwhichBlock.equals("showdata")){
						   //ʮ�������ַ���תΪʮ��������
				           byte []bb = HexDump.hexStringToByteArray(msg[1]+msg[4]);				          
				           sendMsgToL(s,Util.FDDATA);
				           //��ʱ��û�н��ܵ���������null������������ڵ�ַ
				           timer.removeMessages(0);
				           timer.sendEmptyMessageDelayed(0, 6000);
					   }
					   if(Util.sgwhichBlock.equals(msg[4])||Util.sgwhichBlock.equals("showdata")){
						   //ʮ�������ַ���תΪʮ��������
				           byte []bb = HexDump.hexStringToByteArray(msg[1]+msg[4]);				          
				           sendMsgTosg(s,Util.FDDATA);
				           //��ʱ��û�н��ܵ���������null������������ڵ�ַ
				           timer.removeMessages(0);
				           timer.sendEmptyMessageDelayed(0, 6000);
					   }
					   //���ڴ˴������ �������⵼��ARM�����޷���ʾ����
					   strUrl = Util.IP+"rdf?action=write&bn="+boxnum;//�ϴ����ӱ��
					   if(isUploadBoxNum){//�����Ӻ���ȶ������͵�������֮�����ϴ�����
						   postToHttp(s);//�ϴ���������
					   }
					   
				   }
			   }
			   
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void showMsg(String text){
		//Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}
    
}
