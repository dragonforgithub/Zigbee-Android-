
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class IRKongZhiActivity extends Activity implements OnClickListener{
	//‘F’表示电灯停止工作；‘N’表示电灯开始工作。
	private Button ir_bt1,ir_bt2,ir_bt3;
	private TextView ir_tv;

	int IRAddr1 = 0x0A,IRAddr2 = 0xA5;//红外

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
							paresData(fds[i]);
						}
					}else{
						paresData(res);
					}
					break;

				default:
					break;

			}
		}
	};
	private void paresData(String msg){

		String msgs[] = msg.trim().split(" ");
		byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
		Util.addr1 = bb[0];//十进制
		Util.addr2 = bb[1];

		if("A5".equals(msgs[4])){
			IRAddr1 = bb[0];
			IRAddr2 = bb[1];

		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_ir_kongzhi);
		initView();
		String urlPath = "lx=cl3d&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);

	}


	private void initView(){

		ir_bt1 = (Button)this.findViewById(R.id.ir_bt1);
		ir_bt2 = (Button)this.findViewById(R.id.ir_bt2);
		ir_bt3 = (Button)this.findViewById(R.id.ir_bt3);
		ir_tv = (TextView)this.findViewById(R.id.ir_tv);

		ir_bt1.setOnClickListener(this);
		ir_bt2.setOnClickListener(this);
		ir_bt3.setOnClickListener(this);

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

		Util.unBindMyService(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int msg=0xAA;
		String msgStr="AA";
		switch (v.getId()) {

			case R.id.ir_bt1:
				msg=0x4E;
				msgStr="4E";
				break;
			case R.id.ir_bt2:
				msg=0x46;
				msgStr="46";

				break;
			case R.id.ir_bt3:
				msg=0x53;
				msgStr="53";
				break;

			default:
				break;
		}

		ir_tv.setText(msgStr+"");
		int[] datas = {IRAddr1,IRAddr2,0xA5,msg,0xAA,0xAA,0xAA};
		Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,IRKongZhiActivity.this);

	}

}
