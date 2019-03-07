package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;


@SuppressLint("HandlerLeak")
public class YaoKongActivity extends Activity implements OnClickListener{
	private FrameLayout kongtiaoFl,xiyijiFl,bingxiangFl,dianshiFl,dvdFl,yinxiangFl,autoFl,dianyuanFl,dianjiFl,yaokong_IR_fl;
	private TextView titleView;
	private Button lookData;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:

					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_yaokong);

		kongtiaoFl = (FrameLayout)this.findViewById(R.id.yaokong_kongtiao_fl);
		dianshiFl = (FrameLayout)this.findViewById(R.id.yaokong_dianshiji_fl);
		dvdFl = (FrameLayout)this.findViewById(R.id.yaokong_dvd_fl);
		yinxiangFl = (FrameLayout)this.findViewById(R.id.yaokong_yinxiang_fl);
		yaokong_IR_fl = (FrameLayout)this.findViewById(R.id.yaokong_IR_fl);

		dianyuanFl = (FrameLayout)this.findViewById(R.id.yaokong_dianyuan_fl);
		dianjiFl = (FrameLayout)this.findViewById(R.id.yaokong_dianji_fl);
		lookData=(Button)this.findViewById(R.id.yaokong_btn_lookdata);

		kongtiaoFl.setOnClickListener(this);
		dianshiFl.setOnClickListener(this);
		dvdFl.setOnClickListener(this);
		yinxiangFl.setOnClickListener(this);
		dianyuanFl.setOnClickListener(this);
		dianjiFl.setOnClickListener(this);
		yaokong_IR_fl.setOnClickListener(this);//红外
		lookData.setOnClickListener(this);
//        xiyijiFl = (FrameLayout)this.findViewById(R.id.yaokong_kaiguanliang_fl);
//        bingxiangFl = (FrameLayout)this.findViewById(R.id.yaokong_bingxiang_fl);
//        autoFl = (FrameLayout)this.findViewById(R.id.yaokong_auto_fl);
//        xiyijiFl.setOnClickListener(this);
//        bingxiangFl.setOnClickListener(this);
//        autoFl.setOnClickListener(this);
//        titleView = (TextView)this.findViewById(R.id.yaokong_title_tv);
//        if(this.getIntent().hasExtra("name")){
//	        String text = (String)this.getIntent().getCharSequenceExtra("name");
//	        titleView.append("-"+text);
//        }
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.yaokong_kongtiao_fl://空调
				changeActivity(KongTiaoActivity.class,"");
				break;
			case R.id.yaokong_dianyuan_fl://电源 照明 控制
				changeActivity(KaiGuanLiangKongZhiActivity.class,"");
				break;
			case R.id.yaokong_dianshiji_fl:
				changeActivity(DianShiActivity.class,"电视机");
				break;
			case R.id.yaokong_dvd_fl:
				changeActivity(DVDActivity.class,"DVD");
				break;
			case R.id.yaokong_yinxiang_fl:
				changeActivity(YinXiangActivity.class,"音箱");
				break;
			case R.id.yaokong_dianji_fl://直流 交流电机控制
				changeActivity(DianJiActivity.class,"");
				break;
			case R.id.yaokong_btn_lookdata:

				Intent intent =new Intent(YaoKongActivity.this,DataShowActivity.class);
				startActivity(intent);

				break;
			case R.id.yaokong_IR_fl:
				changeActivity(IRActivity.class,"");
				break;
				/*
			case R.id.yaokong_bingxiang_fl:
				changeActivity(ChuangLianActivity.class,"");
				break;
			case R.id.yaokong_auto_fl:
				changeActivity(AutoActivity.class,"自动调节");
				break;
				*/

			default :
				break;
		}

	}

	//界面跳转
	private void changeActivity(@SuppressWarnings("rawtypes") Class cls,String data){
		Intent intent = new Intent(this, cls);
		intent.putExtra("form", data);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
