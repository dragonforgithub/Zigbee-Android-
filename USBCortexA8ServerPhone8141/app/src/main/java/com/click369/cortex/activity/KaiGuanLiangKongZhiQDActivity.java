
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class KaiGuanLiangKongZhiQDActivity extends Activity implements OnClickListener{
	//‘F’表示电灯停止工作；‘N’表示电灯开始工作。
	private Button dianyuan1;
	private ImageView iv_dy;

	private static final String TAG=KaiGuanLiangKongZhiQDActivity.class.getSimpleName();

	int dyAddr1 = 0x0A,dyAddr2 = 0xA8;//强电
	private boolean isDyOff=true;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

				case Util.FDDATA:
					String res = ((String)msg.obj).trim();

					Log.i(TAG, res);///

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

		if("A8".equals(msgs[4])){//电源 节点
			dyAddr1 = bb[0];
			dyAddr2 = bb[1];
			String clStr = msgs[5];
			if(HexDump.hexStringToByteArray(clStr)[0] == 'F'){
				iv_dy.setImageResource(R.drawable.design_icon_off);
				isDyOff=true;
			}else{
				iv_dy.setImageResource(R.drawable.design_icon_on);
				isDyOff=false;
			}
		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_kaiguanliangkongzhi_qd);
		initView();
		String urlPath = "lx=wbl&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);

	}


	private void initView(){
		iv_dy=(ImageView)this.findViewById(R.id.kg_dy_one_iv);iv_dy.setTag(0);
		dianyuan1=(Button)findViewById(R.id.kg_dy_one_bt);
		dianyuan1.setOnClickListener(this);

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

		switch (v.getId()) {

			case R.id.kg_dy_one_bt://电源

				int msg3=0xAA;
				if (isDyOff) {
					msg3=0x4E;
				}else{
					msg3=0x46;
				}
				int[] data = {dyAddr1,dyAddr2,0xA8,msg3,0x00,0xAA,0xAA};
				Util.sendMsgToService(data,Util.SENDDATATOSERVICE,KaiGuanLiangKongZhiQDActivity.this);

				break;

			default:
				break;
		}
	}

}
