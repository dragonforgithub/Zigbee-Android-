
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
public class DVDActivity extends Activity implements OnClickListener{
	Button kaiguanBt,chucangBt,zhuyeBt,caidanBt,xuanxiangBt,shangBt,xiaBt,zuoBt,
			youBt,okBt,backBt,stopBt,playBt,zimuBt,tiaozhuanBt,yinpinBt,yinliangBt,chongfuBt,shangyiquBt,xiayiquBt,quanpingBt;

	int jishus[] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x9,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x10,0x11,0x12,0x13,0x14,0x15};
	//	int oushus[] = {0x02,0x04,0x06,0x08,0x0A,0x0C,0x0E,0x10,0x12,0x14,0x16,0x18,0x1A,0x1C,0x1E,0x20,0x22,0x24,0x26,0x28,0x2A};
	boolean isOn = true;//电视是否打卡？
	int addr1,addr2;
	int tag=1;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					String res = ((String)msg.obj).trim();
					String msgs[] = res.trim().split(" ");
					if(msgs[4].equals("AA") && msgs[0].equals("FD")){
						byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
						addr1=bb[0];
						addr2=bb[1];
					}
					break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dvd);
		initBt();
//        String urlPath = "downLoad?leixing=gz&name="+Util.boxNum;
//        String urlPath = "lx=dvd&bn="+Util.boxNum;
//        Util.isNeedConn = true;
//		Util.bindMyService(this,urlPath);
	}

	private void initBt(){
		//chucangBt,zhuyeBt,caidanBt,xuanxiangBt,shangBt,xiaBt,zuoBt,
		//youBt,okBt,backBt,stopBt,playBt,zimuBt,tiaozhuanBt,yinpinBt,yinliangBt,chongfuBt;
		kaiguanBt = (Button)this.findViewById(R.id.dvd_kaiguan_ib);
		chucangBt = (Button)this.findViewById(R.id.dvd_jinchucang_bt);
		zhuyeBt = (Button)this.findViewById(R.id.dvd_zhuye_bt);
		caidanBt = (Button)this.findViewById(R.id.dvd_menu_bt);
		xuanxiangBt = (Button)this.findViewById(R.id.dvd_xuanxiang_bt);
		shangBt = (Button)this.findViewById(R.id.dvd_shang_bt);
		xiaBt = (Button)this.findViewById(R.id.dvd_xia_bt);
		zuoBt = (Button)this.findViewById(R.id.dvd_zuo_bt);
		youBt = (Button)this.findViewById(R.id.dvd_you_bt);
		okBt = (Button)this.findViewById(R.id.dvd_ok_bt);
		backBt = (Button)this.findViewById(R.id.dvd_back_bt);
		stopBt = (Button)this.findViewById(R.id.dvd_stop_bt);
		playBt = (Button)this.findViewById(R.id.dvd_play_zanting_bt);
		zimuBt = (Button)this.findViewById(R.id.dvd_zimu_bt);
		tiaozhuanBt = (Button)this.findViewById(R.id.dvd_tiaozhuan_bt);
		yinpinBt = (Button)this.findViewById(R.id.dvd_yinpin_bt);
		yinliangBt = (Button)this.findViewById(R.id.dvd_yinliang_bt);
		chongfuBt = (Button)this.findViewById(R.id.dvd_chongfu_bt);
		shangyiquBt = (Button)this.findViewById(R.id.dvd_shangyiqu_bt);
		xiayiquBt = (Button)this.findViewById(R.id.dvd_xiayiqu_bt);
		quanpingBt = (Button)this.findViewById(R.id.dvd_quanping_bt);

		tiaozhuanBt.setOnClickListener(this);tiaozhuanBt.setTag(19);
		kaiguanBt.setOnClickListener(this);kaiguanBt.setTag(0);
		chucangBt.setOnClickListener(this);chucangBt.setTag(1);
		zhuyeBt.setOnClickListener(this);zhuyeBt.setTag(2);
		caidanBt.setOnClickListener(this);caidanBt.setTag(3);
		xuanxiangBt.setOnClickListener(this);xuanxiangBt.setTag(4);
		shangBt.setOnClickListener(this);shangBt.setTag(5);
		xiaBt.setOnClickListener(this);xiaBt.setTag(6);
		zuoBt.setOnClickListener(this);zuoBt.setTag(7);
		youBt.setOnClickListener(this);youBt.setTag(8);
		okBt.setOnClickListener(this);okBt.setTag(9);
		backBt.setOnClickListener(this);backBt.setTag(10);
		stopBt.setOnClickListener(this);stopBt.setTag(11);
		playBt.setOnClickListener(this);playBt.setTag(12);
		shangyiquBt.setOnClickListener(this);shangyiquBt.setTag(13);
		xiayiquBt.setOnClickListener(this);xiayiquBt.setTag(14);
		zimuBt.setOnClickListener(this);zimuBt.setTag(15);

		tiaozhuanBt.setOnClickListener(this);tiaozhuanBt.setTag(16);
		yinpinBt.setOnClickListener(this);yinpinBt.setTag(17);
		quanpingBt.setOnClickListener(this);quanpingBt.setTag(18);

		yinliangBt.setOnClickListener(this);yinliangBt.setTag(19);
		chongfuBt.setOnClickListener(this);chongfuBt.setTag(20);
	}

	int pressTag = -1;
	private void pressButton(int aa){//根据按键的速度来判断发送奇偶
//    	if(tag == 1){
//    	   //发送偶数
//    	   int data = oushus[aa];
//    	   int datas[] = {addr1,addr2,0xAA,data,0xAA,0xAA,0xAA};
//           sendMsgToService(datas,Util.JIAJU);
//    	   tag=2;
//    	}else if(tag==2){
		int data = jishus[aa];
		int datas[] = {addr1,addr2,0xAA,data,0xAA,0xAA,0xAA};
		sendMsgToService(datas,Util.JIAJU);
//    		tag=1;
//    	}

	}

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
		int aa = (Integer)v.getTag();

		pressButton(aa);
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
//					showMsg(getResources().getString(R.string.no_network_address));
//				}
		}else{
			showMsg(getResources().getString(R.string.service_not_start));
		}
	}
	public void showMsg(String text){
		Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
	}

}
