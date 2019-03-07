
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class YinXiangActivity extends Activity implements OnClickListener{
	Button kaiguanBt,modeBt,bt0,bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,muteBt,eqBt,repBt,usbBt,qumuaddBt,qumuminBt,playBt,yinliangaddBt,yinliangminBt;
	
//	int jishus[] = {0x01,0x03,0x05,0x07,0x09,0x0B,0x0D,0x0F,0x11,0x13,0x15,0x17,0x19,0x1B,0x1D,0x1F,0x21,0x23,0x25,0x27};
//	int oushus[] = {0x02,0x04,0x06,0x08,0x0A,0x0C,0x0E,0x10,0x12,0x14,0x16,0x18,0x1A,0x1C,0x1E,0x20,0x22,0x24,0x26,0x28};
	int anjian[] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x10,0x11,0x12,0x13,0x14,0x15,0x16};
	boolean isOn = true;//电视是否打卡？
	int addr1,addr2;
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
			case Util.FDDATA:
				 String res = ((String)msg.obj).trim();
				 String msgs[] = res.trim().split(" ");
				if(msgs[4].equals("AB") && msgs[0].equals("FD")){
					byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
					addr1=bb[0];
					addr2=bb[1];
					if(addr1<0){
						addr1=addr1+256;
					}else{
						
					}
					if(addr2<0){
						addr2=addr2+256;
					}else{
						
					}
					System.out.println("addr1+addr2="+addr1+addr2);
				}
				break;
			}
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinxiang);
        initBt();
//        String urlPath = "downLoad?leixing=gz&name="+Util.boxNum;
//        String urlPath = "lx=yx&bn="+Util.boxNum;
//        Util.isNeedConn = true;
//		Util.bindMyService(this,urlPath);
    }
    
    private void initBt(){
    	//kaiguanBt,modeBt,bt0,bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,muteBt,eqBt,repBt,usbBt,qumuaddBt,qumuminBt,playBt,yinliangaddBt,yinliangminBt;
    	
    	kaiguanBt = (Button)this.findViewById(R.id.yinxiang_kaiguan_ib);
    	modeBt = (Button)this.findViewById(R.id.yinxiang_mode_bt);

		bt0 = (Button)this.findViewById(R.id.yinxiang_0_bt);
		bt1 = (Button)this.findViewById(R.id.yinxiang_1_bt);
		bt2 = (Button)this.findViewById(R.id.yinxiang_2_bt);
		bt3 = (Button)this.findViewById(R.id.yinxiang_3_bt);
		bt4 = (Button)this.findViewById(R.id.yinxiang_4_bt);
		bt5 = (Button)this.findViewById(R.id.yinxiang_5_bt);
		bt6 = (Button)this.findViewById(R.id.yinxiang_6_bt);
		bt7 = (Button)this.findViewById(R.id.yinxiang_7_bt);
		bt8 = (Button)this.findViewById(R.id.yinxiang_8_bt);
		bt9 = (Button)this.findViewById(R.id.yinxiang_9_bt);
		
    	muteBt = (Button)this.findViewById(R.id.yinxiang_mute_bt);
    	eqBt = (Button)this.findViewById(R.id.yinxiang_eq_bt);
    	repBt = (Button)this.findViewById(R.id.yinxiang_rep_bt);
    	usbBt = (Button)this.findViewById(R.id.yinxiang_usb_bt);
    	qumuaddBt = (Button)this.findViewById(R.id.yinxiang_shangyiqu_bt);
    	qumuminBt = (Button)this.findViewById(R.id.yinxiang_xiayiqu_bt);
    	playBt = (Button)this.findViewById(R.id.yinxiang_play_bt);
    	yinliangaddBt = (Button)this.findViewById(R.id.yinxiang_yinliang_add_bt);
    	yinliangminBt = (Button)this.findViewById(R.id.yinxiang_yinliang_min_bt);
    	
		kaiguanBt.setOnClickListener(this);kaiguanBt.setTag(0);
		modeBt.setOnClickListener(this);modeBt.setTag(1);
		bt0.setOnClickListener(this);bt0.setTag(2);
		bt1.setOnClickListener(this);bt1.setTag(3);
		bt2.setOnClickListener(this);bt2.setTag(4);
		bt3.setOnClickListener(this);bt3.setTag(5);
		bt4.setOnClickListener(this);bt4.setTag(6);
		bt5.setOnClickListener(this);bt5.setTag(7);
		bt6.setOnClickListener(this);bt6.setTag(8);
		bt7.setOnClickListener(this);bt7.setTag(9);
		bt8.setOnClickListener(this);bt8.setTag(10);
		bt9.setOnClickListener(this);bt9.setTag(11);
		muteBt.setOnClickListener(this);muteBt.setTag(12);
		eqBt.setOnClickListener(this);eqBt.setTag(13);
		repBt.setOnClickListener(this);repBt.setTag(14);
		usbBt.setOnClickListener(this);usbBt.setTag(15);
		qumuaddBt.setOnClickListener(this);qumuaddBt.setTag(16);
		qumuminBt.setOnClickListener(this);qumuminBt.setTag(17);
		playBt.setOnClickListener(this);playBt.setTag(18);
		yinliangaddBt.setOnClickListener(this);yinliangaddBt.setTag(19);
		yinliangminBt.setOnClickListener(this);yinliangminBt.setTag(20);
    }
    
    int pressTag = -1;
    private void pressButton(int tag){//根据按键的速度来判断发送奇偶
    	if(tag == pressTag){
    			pressHandler.removeMessages(tag);
    			//发送偶数
    			int data = anjian[tag];
    			int datas[] = {addr1,addr2,0xAB,data,0xAA,0xAA,0xAA};
        		sendMsgToService(datas,Util.JIAJU);
    			pressTag = -1;
    	}else{
    		pressHandler.sendEmptyMessageDelayed(tag, 600);
        	pressTag  = tag;
    	}
    	
    }
    
    Handler pressHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		//发送奇数
    		int data = anjian[msg.what];
    		int datas[] = {addr1,addr2,0xAB,data,0xAA,0xAA,0xAA};
    		sendMsgToService(datas,Util.JIAJU);
    		pressTag = -1;
    	};
    };
    
    @Override
    protected void onResume() {
    	//设置为本activity的handler
    	Util.uiHandler = myHandler;
    	Util.whichBlock = "showdata";
    	super.onResume();
    	
    }
    @Override
	protected void onStop() {
//		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		int tag = (Integer)v.getTag();
		pressButton(tag);
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
    public void showMsg(String text){
 		Toast.makeText(this, text,200).show();
 	}


}
