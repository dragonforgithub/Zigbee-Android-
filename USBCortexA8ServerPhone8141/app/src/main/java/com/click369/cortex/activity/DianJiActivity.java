
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

/**
 * 电机 控制界面
 * */
@SuppressLint("HandlerLeak")
public class DianJiActivity extends Activity implements OnClickListener{

	private ImageView dj_zl_iv;
	private Button dj_zl_forward_bt, dj_zl_reverse_bt,
			dj_zl_stop_bt, dj_zl_jiasu_bt,dj_zl_jiansu_bt;

	//直流电机  对应 网内地址
	int zldjAddr1 = 0x0A,zldjAddr2 = 0xA4;

	Animation animation_RotateZheng;//正传动画
	Animation animation_RotateFan;//反传动画

	private boolean isFirstZheng=true;
	private boolean isFirstFan=true;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

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

		// 0xA4 直流电机
		if("A4".equals(msgs[4])){
			zldjAddr1 = bb[0];
			zldjAddr2 = bb[1];

			String state = msgs[5];

			if(state.equals("4E")){
				if (isFirstZheng) {
					dj_zl_iv.startAnimation(animation_RotateZheng);
					isFirstZheng=false;
					isFirstFan=true;
				}

			}
			if(state.equals("46")){
				if (isFirstFan) {
					dj_zl_iv.startAnimation(animation_RotateFan);
					isFirstFan=false;
					isFirstZheng=true;
				}

			}
			if(state.equals("53")){
				dj_zl_iv.clearAnimation();
				isFirstFan=true;
				isFirstZheng=true;
			}
		}

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_dianji);
		initView();
		initAnimation();
		//直流电机 控制: 节点地址 A4 对应 lx=cl2
		String urlPath = "lx=cl2&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);//绑定服务

	}

	private void initAnimation() {
		// TODO Auto-generated method stub
		animation_RotateZheng=new RotateAnimation(0.0f, +359.9f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation_RotateZheng.setRepeatCount(-1);
		animation_RotateZheng.setDuration(3000);

		animation_RotateFan=new RotateAnimation(0.0f, -359.9f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation_RotateFan.setRepeatCount(-1);
		animation_RotateFan.setDuration(3000);

		LinearInterpolator lir = new LinearInterpolator();
		animation_RotateZheng.setInterpolator(lir);
		animation_RotateFan.setInterpolator(lir);

	}

	/**直流电机 控制*/
	private void initView(){
		dj_zl_iv = (ImageView)this.findViewById(R.id.dj_zl_iv);
		dj_zl_forward_bt = (Button)this.findViewById(R.id.dj_zl_forward_bt);
		dj_zl_reverse_bt = (Button)this.findViewById(R.id.dj_zl_reverse_bt);
		dj_zl_stop_bt = (Button)this.findViewById(R.id.dj_zl_stop_bt);

		dj_zl_jiasu_bt=(Button)findViewById(R.id.dj_zl_jiasu_bt);
		dj_zl_jiansu_bt=(Button)findViewById(R.id.dj_zl_jiansu_bt);
		dj_zl_forward_bt.setOnClickListener(this);
		dj_zl_reverse_bt.setOnClickListener(this);
		dj_zl_stop_bt.setOnClickListener(this);
		dj_zl_jiasu_bt.setOnClickListener(this);
		dj_zl_jiansu_bt.setOnClickListener(this);

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

		int msg=0;
		switch (v.getId()) {

			case R.id.dj_zl_forward_bt:

				msg='N' & 0xff;
				break;
			case R.id.dj_zl_reverse_bt:

				msg='F' & 0xff;
				break;
			case R.id.dj_zl_stop_bt:

				msg='S' & 0xff;
				break;

			case R.id.dj_zl_jiasu_bt:

				msg='+' & 0xff;
				break;
			case R.id.dj_zl_jiansu_bt:

				msg='-' & 0xff;
				break;

			default:
				break;

		}

		int[] datas= {zldjAddr1,zldjAddr2,0xA4,msg,0xAA,0xAA,0xAA};
		Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,this);

	}

}
