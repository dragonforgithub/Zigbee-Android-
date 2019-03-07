package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

//登录界面控制器
@SuppressLint("HandlerLeak")
public class EnterActivity extends Activity implements OnClickListener{
	FrameLayout huanJingFl,yaoKongFl,anFangFl,wendu_bt;
	private TextView titleView;
	String boxNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_enter);

		huanJingFl = (FrameLayout)this.findViewById(R.id.enter_huanjing_fl);
		yaoKongFl = (FrameLayout)this.findViewById(R.id.enter_yaokong_fl);
		anFangFl = (FrameLayout)this.findViewById(R.id.enter_anfang_fl);
		huanJingFl.setOnClickListener(this);
		yaoKongFl.setOnClickListener(this);
		anFangFl.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		//Util.uiHandler = myHandler;
		//Util.whichBlock = "09";//温湿度板子节点地址
		super.onResume();
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void changeActivity(@SuppressWarnings("rawtypes") Class cls) {
		Intent intent = new Intent(this,cls);
		this.startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.enter_huanjing_fl:
				changeActivity(JianKongActivity.class);
				break;
			case R.id.enter_yaokong_fl:
				changeActivity(YaoKongActivity.class);
				break;
			case R.id.enter_anfang_fl:
				changeActivity(AnFangActivity.class);
				break;

			default :
				break;

		}
	}

}

