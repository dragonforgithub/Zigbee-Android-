
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
public class KaiGuanLiangKongZhiActivity extends Activity implements OnClickListener{
	//‘F’表示电灯停止工作；‘N’表示电灯开始工作。
	private Button deng1,deng2;
	private ImageView deng1Iv,deng2Iv;
	//private EditText autowenduEt,autoshiduEt,autoguangzhaoEt;

	private static final String TAG=KaiGuanLiangKongZhiActivity.class.getSimpleName();

	int dengAddr1 = 0x0A,dengAddr2 = 0xA9;//灯
	private boolean isLight1Off=true;
	private boolean isLight2Off=true;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
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

		if("A9".equals(msgs[4])){
			dengAddr1 = bb[0];
			dengAddr2 = bb[1];

			if(msgs[5].equals("46")){
				deng1Iv.setImageResource(R.drawable.design_icon_off);
				isLight1Off=true;
			}else{
				deng1Iv.setImageResource(R.drawable.design_icon_on);
				isLight1Off=false;
			}
			if(msgs[6].equals("46")){
				deng2Iv.setImageResource(R.drawable.design_icon_off);
				isLight2Off=true;
			}else{
				deng2Iv.setImageResource(R.drawable.design_icon_on);
				isLight2Off=false;
			}

		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_kaiguanliangkongzhi);
		initView();
		String urlPath = "lx=dd&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);

	}


	private void initView(){
		deng1Iv = (ImageView)this.findViewById(R.id.kg_deng_one_iv);deng1Iv.setTag(0);
		deng2Iv = (ImageView)this.findViewById(R.id.kg_deng_two_iv);deng2Iv.setTag(0);
		deng1 = (Button)this.findViewById(R.id.kg_deng_one_bt);
		deng2 = (Button)this.findViewById(R.id.kg_deng_two_bt);

		deng1.setOnClickListener(this);
		deng2.setOnClickListener(this);

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

			case R.id.kg_deng_one_bt:

				int msg=0xAA;
				if (isLight1Off) {
					msg=0x4E;
				}else{
					msg=0x46;
				}
				int[] datas = {dengAddr1,dengAddr2,0xA9,msg,0xAA,0xAA,0xAA};
				Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,KaiGuanLiangKongZhiActivity.this);

				break;
			case R.id.kg_deng_two_bt:

				int msg2=0xAA;
				if (isLight2Off) {
					msg2=0x4E;
				}else{
					msg2=0x46;
				}
				int[] datasOff = {dengAddr1,dengAddr2,0xA9,0xAA,msg2,0xAA,0xAA};
				Util.sendMsgToService(datasOff,Util.SENDDATATOSERVICE,KaiGuanLiangKongZhiActivity.this);

				break;


			default:
				break;
		}

	}

}
