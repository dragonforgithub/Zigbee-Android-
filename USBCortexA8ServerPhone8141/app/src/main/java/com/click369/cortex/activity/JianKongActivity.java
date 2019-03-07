
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;


@SuppressLint("HandlerLeak")
public class JianKongActivity extends Activity implements OnClickListener{
	private FrameLayout guangzhaoFl,wenduFl,shiduFl;//yangqiFl,eyhtFl,trqFl,kaiguanlinagFl,yanwuFl;
	private LinearLayout jiaQuanLL,co2LL;
	private TextView titleView;

	/*
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
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_jiankong);

		guangzhaoFl = (FrameLayout)this.findViewById(R.id.jiankong_guangzhao_fl);
		wenduFl = (FrameLayout)this.findViewById(R.id.jiankong_wendu_fl);
		shiduFl = (FrameLayout)this.findViewById(R.id.jiankong_shidu_fl);
		jiaQuanLL=(LinearLayout)findViewById(R.id.jiankong_jq_fl);
		co2LL=(LinearLayout)findViewById(R.id.jiankong_co2_fl);
		wenduFl.setOnClickListener(this);
		shiduFl.setOnClickListener(this);
		jiaQuanLL.setOnClickListener(this);
		co2LL.setOnClickListener(this);
		guangzhaoFl.setOnClickListener(this);

//        trqFl = (FrameLayout)this.findViewById(R.id.jiankong_trq_fl);
//        yangqiFl = (FrameLayout)this.findViewById(R.id.jiankong_yangqi_fl);
//        eyhtFl = (FrameLayout)this.findViewById(R.id.jiankong_eryanghuatan_fl);
//        kaiguanlinagFl = (FrameLayout)this.findViewById(R.id.jiankong_kaiguanliang_fl);
//        yanwuFl = (FrameLayout)this.findViewById(R.id.jiankong_yanwu_fl);
//        trqFl.setOnClickListener(this);
//        yangqiFl.setOnClickListener(this);
//        eyhtFl.setOnClickListener(this);
//        kaiguanlinagFl.setOnClickListener(this);
//        yanwuFl.setOnClickListener(this);

		titleView = (TextView)this.findViewById(R.id.jiankong_titlt_tv);
		if(this.getIntent().hasExtra("name")){
			String text = (String)this.getIntent().getCharSequenceExtra("name");
			titleView.append("-"+text);
		}

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		//Util.uiHandler = myHandler;
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.jiankong_wendu_fl:
				changeActivity(WenDuActivity.class,"");
				break;
			case R.id.jiankong_shidu_fl:
				changeActivity(ShiDuActivity.class,"");
				break;

			case R.id.jiankong_co2_fl:
				changeActivity(CO2Activity.class,"");
				break;
			case R.id.jiankong_jq_fl:
				changeActivity(JiaQuanActivity.class,"");
				break;

			case R.id.jiankong_guangzhao_fl:
				changeActivity(GuangZhaoActivity.class,"");
				break;
/*
		case R.id.jiankong_trq_fl:
			changeActivity(QiTiActivity.class,"天然气");
			break;
		case R.id.jiankong_yangqi_fl:
			changeActivity(QiTiActivity.class,"氧气");
			break;
		case R.id.jiankong_kaiguanliang_fl:
			changeActivity(KaiGuanlLiangCaiJiActivity.class,"");
			break;
		case R.id.jiankong_yanwu_fl:
			changeActivity(QiTiActivity.class,"烟雾");
			break;
		case R.id.jiankong_eryanghuatan_fl:
			changeActivity(QiTiActivity.class,"二氧化碳");
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
