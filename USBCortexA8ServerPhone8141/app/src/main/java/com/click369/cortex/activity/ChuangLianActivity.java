
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class ChuangLianActivity extends Activity implements OnClickListener{
	private Button oneStart,oneStop,oneClose,twoStart,twoStop,twoClose,threeStart,threeStop,threeClose;
	private ImageView oneStartIv,oneStopIv,oneCloseIv,twoStartIv,twoStopIv,twoCloseIv,threeStartIv,threeStopIv,threeCloseIv;

	int cl1Addr1 = 0,cl1Addr2 = 0;
	int cl2Addr1 = 0,cl2Addr2 = 0;
	int cl3Addr1 = 0,cl3Addr2 = 0;
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

	int a = 0,b=0,c = 0;
	private void parseData(String msg){
		String msgs[] = msg.trim().split(" ");
		byte []bb = HexDump.hexStringToByteArray(msgs[17]+msgs[18]);
		Util.addr1 = bb[0];//十进制
		Util.addr2 = bb[1];
		if("A3".equals(msgs[4])){
//        	 Util.DOWNURLPATH = "lx=cl2&bn="+Util.boxNum;
			a = 1;
			cl1Addr1 = bb[0];
			cl1Addr2 = bb[1];
		}else if("A4".equals(msgs[4])){
//        	 Util.DOWNURLPATH = "lx=cl3&bn="+Util.boxNum;
			b=1;
			cl2Addr1 = bb[0];
			cl2Addr2 = bb[1];
		}else if("A5".equals(msgs[4])){
			cl3Addr1 = bb[0];
			cl3Addr2 = bb[1];
			c = 1;
		}
		//如果得到所以的编号就让服务中的线程停止
		if(a ==1&&b ==1&&c ==1){
			isGetFD = false;
//        	 MainWifiService.myHandler.sendEmptyMessage(Util.STOP);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_chuanglian);
		initView();
		String urlPath = "lx=cl1&bn="+Util.boxNum;
		Util.isNeedConn = false;
		Util.bindMyService(this,urlPath);

		startGetFD();
	}

	boolean isGetFD = true;
	private void startGetFD(){
		new Thread(){
			public void run(){
				while(isGetFD){
					try {
						String urlPath = "rdf?action=read&lx=cl1&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						urlPath = "rdf?action=read&lx=cl2&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						urlPath = "rdf?action=read&lx=cl3&bn="+Util.boxNum;
						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
						Thread.sleep(1000*2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	private void initView(){
		oneStartIv = (ImageView)this.findViewById(R.id.cl_start_one_iv);
		oneStopIv = (ImageView)this.findViewById(R.id.cl_stop_one_iv);
		oneCloseIv = (ImageView)this.findViewById(R.id.cl_close_one_iv);

		twoStartIv = (ImageView)this.findViewById(R.id.cl_start_two_iv);
		twoStopIv = (ImageView)this.findViewById(R.id.cl_stop_two_iv);
		twoCloseIv = (ImageView)this.findViewById(R.id.cl_close_two_iv);

		threeStartIv = (ImageView)this.findViewById(R.id.cl_start_three_iv);
		threeStopIv = (ImageView)this.findViewById(R.id.cl_stop_three_iv);
		threeCloseIv = (ImageView)this.findViewById(R.id.cl_close_three_iv);

		oneStart = (Button)this.findViewById(R.id.cl_start_one_bt);
		oneStop = (Button)this.findViewById(R.id.cl_stop_one_bt);
		oneClose = (Button)this.findViewById(R.id.cl_close_one_bt);

		twoStart = (Button)this.findViewById(R.id.cl_start_two_bt);
		twoStop = (Button)this.findViewById(R.id.cl_stop_two_bt);
		twoClose = (Button)this.findViewById(R.id.cl_close_two_bt);

		threeStart = (Button)this.findViewById(R.id.cl_start_three_bt);
		threeStop = (Button)this.findViewById(R.id.cl_stop_three_bt);
		threeClose = (Button)this.findViewById(R.id.cl_close_three_bt);

		oneStart.setOnClickListener(this);
		oneStop.setOnClickListener(this);
		oneClose.setOnClickListener(this);
		twoStart.setOnClickListener(this);
		twoStop.setOnClickListener(this);
		twoClose.setOnClickListener(this);
		threeStart.setOnClickListener(this);
		threeStop.setOnClickListener(this);
		threeClose.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		super.onResume();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		isGetFD = false;
		Util.unBindMyService(this);
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		int jieidna = 0xA3;
		int data1 = 0x01;//控制状态  （）
		int addr1 = 0;
		int addr2 = 0;
		switch (v.getId()) {
			case R.id.cl_start_one_bt:
				jieidna = 0xA3;
				data1 = 0x02;
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				showLight(oneStartIv,oneStopIv,oneCloseIv);
				break;
			case R.id.cl_stop_one_bt:
				jieidna = 0xA3;
				data1 = 'S' & 0xff;
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				showLight(oneStopIv,oneStartIv,oneCloseIv);
				break;
			case R.id.cl_close_one_bt:
				jieidna = 0xA3;
				data1 = 0x01;
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				showLight(oneCloseIv,oneStopIv,oneStartIv);
				break;
///
			case R.id.cl_start_two_bt:
				jieidna = 0xA4;
				data1 = 0x02;
				addr1 = cl2Addr1;
				addr2 = cl2Addr2;
				showLight(twoStartIv,twoStopIv,twoCloseIv);
				break;
			case R.id.cl_stop_two_bt:
				jieidna = 0xA4;
				data1 = 'S' & 0xff;
				addr1 = cl2Addr1;
				addr2 = cl2Addr2;
				showLight(twoStopIv,twoStartIv,twoCloseIv);
				break;
			case R.id.cl_close_two_bt:
				jieidna = 0xA4;
				data1 = 0x01;
				addr1 = cl2Addr1;
				addr2 = cl2Addr2;
				showLight(twoCloseIv,twoStopIv,twoStartIv);
				break;

			case R.id.cl_start_three_bt:
				jieidna = 0xA5;
				data1 = 0x02;
				addr1 = cl3Addr1;
				addr2 = cl3Addr2;
				showLight(threeStartIv,threeStopIv,threeCloseIv);
				break;
			case R.id.cl_stop_three_bt:
				jieidna = 0xA5;
				data1 = 'S' & 0xff;
				addr1 = cl3Addr1;
				addr2 = cl3Addr2;
				showLight(threeStopIv,threeStartIv,threeCloseIv);
				break;
			case R.id.cl_close_three_bt:
				jieidna = 0xA5;
				data1 = 0x01;
				addr1 = cl3Addr1;
				addr2 = cl3Addr2;
				showLight(threeCloseIv,threeStopIv,threeStartIv);
				break;

		}
		int datasrl[] = {addr1,addr2,jieidna,data1,0XAA,0XAA,0xAA};
		Util.sendMsgToService(datasrl,Util.SENDDATATOSERVICE,this);
	}

	private void showLight(ImageView oniv,ImageView offiv1,ImageView offiv2){
		offiv1.setImageResource(R.drawable.design_icon_off);
		offiv2.setImageResource(R.drawable.design_icon_off);
		oniv.setImageResource(R.drawable.design_icon_on);
	}
}
