
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;


@SuppressLint("HandlerLeak")
public class ChuanGanQiActivity extends Activity implements OnClickListener{
	
    private ImageView wenduIv,guangzhaoIv,shiduIv,tianranqiIv,zhongliTv;
    private Button lookData,back;
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
        setContentView(R.layout.activity_chuanganqi);
//        lookData = (Button)this.findViewById(R.id.main_lookdata_btn);
//        back = (Button)this.findViewById(R.id.main_back_btn);
        wenduIv = (ImageView)this.findViewById(R.id.main_wendu_ImageView);
        wenduIv.setOnClickListener(this);
        guangzhaoIv = (ImageView)this.findViewById(R.id.guangzhao_imageView);
        guangzhaoIv.setOnClickListener(this);
        shiduIv = (ImageView)this.findViewById(R.id.shidu_imageView);
        shiduIv.setOnClickListener(this);
//        tianranqiIv = (ImageView)this.findViewById(R.id.tianranqi_imageView);
//        tianranqiIv.setOnClickListener(this);
//        zhongliTv = (ImageView)this.findViewById(R.id.zhongli_imageView);
//        zhongliTv.setOnClickListener(this);
//        lookData.setOnClickListener(this);
//        back.setOnClickListener(this);
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
		case R.id.main_wendu_ImageView:
			changeActivity(WenDuActivity.class);
			break;
		case R.id.guangzhao_imageView:
			changeActivity(GuangZhaoActivity.class);
			break;
		case R.id.shidu_imageView:
			changeActivity(ShiDuActivity.class);
			break;
//		case R.id.tianranqi_imageView:
//			changeActivity(TRQActivity.class);
//			break;
//		case R.id.zhongli_imageView:
//			changeActivity(ZhongLiActivity.class);
//			break;
//		case R.id.main_lookdata_btn:
//			changeActivity(DataShowActivity.class);
//			break;
//		case R.id.main_back_btn:
//			this.finish();
//			break;
		}
		
	}
	
	//界面跳转
	private void changeActivity(@SuppressWarnings("rawtypes") Class cls){
		Intent intent = new Intent(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
	}
	
    public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
