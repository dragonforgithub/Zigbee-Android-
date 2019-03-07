
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;


@SuppressLint("HandlerLeak")
public class YaoKongActivity extends Activity implements OnClickListener{
	private FrameLayout dianjiFL,kongtiaoFl,xiyijiFl,
			bingxiangFl,dianshiFl,dvdFl,yinxiangFl,autoFl;
	private FrameLayout yaokong_dianji_ac_fl,yaokong_kaiguanliang_light_fl,yaokong_kaiguanliang_qd_fl,yaokong_ir_fl;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_yaokong);

		dianjiFL=(FrameLayout)findViewById(R.id.yaokong_dianji_fl);//电机
		kongtiaoFl = (FrameLayout)this.findViewById(R.id.yaokong_kongtiao_fl);
		yaokong_dianji_ac_fl = (FrameLayout)this.findViewById(R.id.yaokong_dianji_ac_fl);
		yaokong_kaiguanliang_light_fl = (FrameLayout)this.findViewById(R.id.yaokong_kaiguanliang_light_fl);
		yaokong_kaiguanliang_qd_fl = (FrameLayout)this.findViewById(R.id.yaokong_kaiguanliang_qd_fl);
		yaokong_ir_fl = (FrameLayout)this.findViewById(R.id.yaokong_ir_fl);

//        bingxiangFl = (FrameLayout)this.findViewById(R.id.yaokong_bingxiang_fl);
//        autoFl = (FrameLayout)this.findViewById(R.id.yaokong_auto_fl);
		dianshiFl = (FrameLayout)this.findViewById(R.id.yaokong_dianshiji_fl);
		dvdFl = (FrameLayout)this.findViewById(R.id.yaokong_dvd_fl);
		yinxiangFl = (FrameLayout)this.findViewById(R.id.yaokong_yinxiang_fl);
		kongtiaoFl.setOnClickListener(this);
		yaokong_kaiguanliang_light_fl.setOnClickListener(this);
		yaokong_kaiguanliang_qd_fl.setOnClickListener(this);
		dianjiFL.setOnClickListener(this);
		yaokong_dianji_ac_fl.setOnClickListener(this);
		dianshiFl.setOnClickListener(this);
		dvdFl.setOnClickListener(this);
		yinxiangFl.setOnClickListener(this);
		yaokong_ir_fl.setOnClickListener(this);
//        autoFl.setOnClickListener(this);
//        bingxiangFl.setOnClickListener(this);

		titleView = (TextView)this.findViewById(R.id.yaokong_title_tv);
		if(this.getIntent().hasExtra("name")){
			String text = (String)this.getIntent().getCharSequenceExtra("name");
			titleView.append("-"+text);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.yaokong_dianji_fl:
				changeActivity(DianJiActivity.class,"");
				break;
			case R.id.yaokong_dianji_ac_fl:
				changeActivity(DianJiOfChuanLianActivity.class,"");
				break;

			case R.id.yaokong_kongtiao_fl:
				changeActivity(KongTiaoActivity.class,"");
				break;
			case R.id.yaokong_kaiguanliang_light_fl:
				changeActivity(KaiGuanLiangKongZhiActivity.class,"");
				break;
			case R.id.yaokong_kaiguanliang_qd_fl:
				changeActivity(KaiGuanLiangKongZhiQDActivity.class,"");
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
			case R.id.yaokong_ir_fl:
				changeActivity(IRKongZhiActivity.class,"");
				break;
//		case R.id.yaokong_bingxiang_fl:
//			changeActivity(ChuangLianActivity.class,"");
//			break;
//		case R.id.yaokong_auto_fl:
//			changeActivity(AutoActivity.class,"自动调节");
//			break;

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
