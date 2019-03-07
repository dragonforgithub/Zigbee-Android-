
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class DianShiActivity extends Activity implements OnClickListener{
	Button kaiguanBt,jingyinBt,suofangbt,pindaoaddBt,pindaominBt,yinliangaddBt,yinliangminBt,menuBt,
			threedbt,bt0,bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,quanpingbt,shengyinbt,picturebt,leftbt,rightbt,
			topbt,bottombt,quedingbt,xinyuanbt;

	int jishus[] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1A,0x1B,0x1C};
	//	int oushus[] = {0x02,0x04,0x06,0x08,0x0A,0x0C,0x0E,0x10,0x12,0x14,0x16,0x18,0x1A,0x1C,0x1E,0x20,0x22,0x24,0x26,0x28};
	int clicknum[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	boolean isOn = true;//电视是否打卡？
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					String res = ((String)msg.obj).trim();
					if(res.contains("\r\n")){
						String fds[] = res.split("\r\n");
						for(int i = 0;i<fds.length;i++){
							parseData(fds[i]);
						}
					}else{
						parseData(res);
					}
					break;
			}
		}
	};
	private void parseData(String msg){
		String msgs[] = msg.trim().split(" ");
		byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
		Util.addr1 = bb[0];//十进制
		Util.addr2 = bb[1];
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_dianshi);
		initBt();
//        String urlPath = "downLoad?leixing=gz&name="+Util.boxNum;
		String urlPath = "lx=ds&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);
	}

	private void initBt(){
		kaiguanBt = (Button)this.findViewById(R.id.dianshi_switch_ib);
		jingyinBt = (Button)this.findViewById(R.id.dianshi_jingyin_bt);
		pindaoaddBt = (Button)this.findViewById(R.id.dianshi_pindao_add_bt);
		pindaominBt = (Button)this.findViewById(R.id.dianshi_pindao_min_bt);
		yinliangaddBt = (Button)this.findViewById(R.id.dianshi_yinliang_add_bt);
		yinliangminBt = (Button)this.findViewById(R.id.dianshi_yinliang_min_bt);
		menuBt = (Button)this.findViewById(R.id.dianshi_menu_bt);
		bt0 = (Button)this.findViewById(R.id.dianshi_0_bt);
		bt1 = (Button)this.findViewById(R.id.dianshi_1_bt);
		bt2 = (Button)this.findViewById(R.id.dianshi_2_bt);
		bt3 = (Button)this.findViewById(R.id.dianshi_3_bt);
		bt4 = (Button)this.findViewById(R.id.dianshi_4_bt);
		bt5 = (Button)this.findViewById(R.id.dianshi_5_bt);
		bt6 = (Button)this.findViewById(R.id.dianshi_6_bt);
		bt7 = (Button)this.findViewById(R.id.dianshi_7_bt);
		bt8 = (Button)this.findViewById(R.id.dianshi_8_bt);
		bt9 = (Button)this.findViewById(R.id.dianshi_9_bt);

		suofangbt = (Button) findViewById(id.dianshi_suofang_bt);
		threedbt = (Button) findViewById(id.dianshi_3d_bt);
		quanpingbt = (Button) findViewById(id.dianshi_quanping_bt);
		shengyinbt = (Button) findViewById(id.dianshi_shengying_bt);
		picturebt = (Button) findViewById(id.dianshi_picture_bt);
		leftbt = (Button) findViewById(id.dianshi_left_bt);
		rightbt = (Button) findViewById(id.dianshi_right_bt);
		topbt = (Button) findViewById(id.dianshi_top_bt);
		bottombt = (Button) findViewById(id.dianshi_bottom_bt);
		quedingbt = (Button) findViewById(id.dianshi_queding_bt);
		xinyuanbt = (Button) findViewById(id.dianshi_xinyuan_bt);

		kaiguanBt.setOnClickListener(this);kaiguanBt.setTag(0);//开机
		suofangbt.setOnClickListener(this);suofangbt.setTag(1);//缩放
		jingyinBt.setOnClickListener(this);jingyinBt.setTag(2);//静音


		bt1.setOnClickListener(this);bt1.setTag(3);//
		bt2.setOnClickListener(this);bt2.setTag(4);//
		bt3.setOnClickListener(this);bt3.setTag(5);//
		bt4.setOnClickListener(this);bt4.setTag(6);//
		bt5.setOnClickListener(this);bt5.setTag(7);//
		bt6.setOnClickListener(this);bt6.setTag(8);//
		bt7.setOnClickListener(this);bt7.setTag(9);//
		bt8.setOnClickListener(this);bt8.setTag(10);//
		bt9.setOnClickListener(this);bt9.setTag(11);//
		bt0.setOnClickListener(this);bt0.setTag(12);//

		threedbt.setOnClickListener(this);threedbt.setTag(13);//3d
		quanpingbt.setOnClickListener(this);quanpingbt.setTag(14);//全屏
		shengyinbt.setOnClickListener(this);shengyinbt.setTag(15);//声音
		picturebt.setOnClickListener(this);picturebt.setTag(16);//图像
		topbt.setOnClickListener(this);topbt.setTag(17);//上
		bottombt.setOnClickListener(this);bottombt.setTag(18);//下
		leftbt.setOnClickListener(this);leftbt.setTag(19);//左
		rightbt.setOnClickListener(this);rightbt.setTag(20);//右
		quedingbt.setOnClickListener(this);quedingbt.setTag(21);//确定
		yinliangaddBt.setOnClickListener(this);yinliangaddBt.setTag(22);//音量加
		yinliangminBt.setOnClickListener(this);yinliangminBt.setTag(23);//音量减
		pindaoaddBt.setOnClickListener(this);pindaoaddBt.setTag(24);//频道加
		pindaominBt.setOnClickListener(this);pindaominBt.setTag(25);//频道减
		menuBt.setOnClickListener(this);menuBt.setTag(26);//菜单
		xinyuanbt.setOnClickListener(this);xinyuanbt.setTag(27);//信源

	}

	int pressTag = -1;
	private void pressButton(int tag){//根据按键的速度来判断发送奇偶
		if(tag == pressTag){
			pressHandler.removeMessages(tag);
			//发送偶数
//    			int data = oushus[tag];
//    			int datas[] = {Util.addr1,Util.addr2,0xA1,data,0xAA,0XAA,0xAA};
//        		Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,DianShiActivity.this);
			pressTag = -1;
		}else{
			pressHandler.sendEmptyMessageDelayed(tag, 600);
			pressTag  = tag;
		}

	}

	Handler pressHandler = new Handler(){
		public void handleMessage(Message msg) {
			//发送奇数
			int data = jishus[msg.what];
			int datas[] = {Util.addr1,Util.addr2,0xA1,data,0xAA,0xAA,0xAA};
			Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,DianShiActivity.this);
			pressTag = -1;
		};
	};

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		super.onResume();
	}
	@Override
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		int tag = (Integer)v.getTag();
		clicknum[tag]++;
		int data = 0;
		if(clicknum[tag]%2==0){
//			data = oushus[tag];
		}else{
			data = jishus[tag];
		}
		int datas[] = {Util.addr1,Util.addr2,0xA1,data,0xAA,0xAA,0xAA};
		Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,DianShiActivity.this);
//		pressButton(tag);
	}

}
