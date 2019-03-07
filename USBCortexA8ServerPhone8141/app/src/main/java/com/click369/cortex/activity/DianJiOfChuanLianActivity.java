
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
import android.widget.ImageView;
import com.click369.cortex.R;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

/**
 * 交流电机 控制窗帘
 * */
@SuppressLint("HandlerLeak")
public class DianJiOfChuanLianActivity extends Activity implements OnClickListener{

	private ImageView dj_jl_iv;
	private Button dj_jl_forward_bt,dj_jl_reverse_bt,dj_jl_stop_bt;

	//交流电机  对应网内地址
	int jldjAddr1 = 0x0A,jldjAddr2 = 0xA3;

	Animation animation_RotateZheng;//正传动画
	Animation animation_RotateFan;//反传动画

	private boolean isFirstJlZheng=true;
	private boolean isFirstJlFan=true;

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

		//交流电机 节点A3
		if("A3".equals(msgs[4])){
			jldjAddr1 = bb[0];
			jldjAddr2 = bb[1];
			String state = msgs[5];
			if(state.equals("4E")){
				if (isFirstJlZheng) {
					dj_jl_iv.startAnimation(animation_RotateZheng);
					isFirstJlZheng=false;
					isFirstJlFan=true;
				}
			}
			if(state.equals("46")){
				if (isFirstJlFan) {
					dj_jl_iv.startAnimation(animation_RotateFan);
					isFirstJlFan=false;
					isFirstJlZheng=true;
				}
			}
			if(state.equals("53")){
				dj_jl_iv.clearAnimation();
				isFirstJlFan=true;
				isFirstJlZheng=true;
			}

		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_dianji_chuanlian);
		initView();
		initAnimation();

		String urlPath = "lx=cl1&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);//绑定服务

	}
	/**
	 * 初始化动画特效
	 * */
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

	/**
	 * 初始化控件
	 */
	private void initView(){
		dj_jl_iv = (ImageView)this.findViewById(R.id.dj_jl_iv);
		dj_jl_forward_bt = (Button)this.findViewById(R.id.dj_jl_forward_bt);
		dj_jl_reverse_bt = (Button)this.findViewById(R.id.dj_jl_reverse_bt);
		dj_jl_stop_bt = (Button)this.findViewById(R.id.dj_jl_stop_bt);
		dj_jl_forward_bt.setOnClickListener(this);
		dj_jl_reverse_bt.setOnClickListener(this);
		dj_jl_stop_bt.setOnClickListener(this);

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


	/**交流电机 窗帘控制*/
	@Override
	public void onClick(View v) {

		int msg=0;
		switch (v.getId()) {

			case R.id.dj_jl_forward_bt:
				msg=0x4E;

				break;
			case R.id.dj_jl_reverse_bt:

				msg=0x46;

				break;
			case R.id.dj_jl_stop_bt:

				msg=0x53;
				break;

			default:
				break;

		}

		int[] datas= {jldjAddr1,jldjAddr2,0xA3,msg,0xAA,0xAA,0xAA};
		Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,this);

	}

}
