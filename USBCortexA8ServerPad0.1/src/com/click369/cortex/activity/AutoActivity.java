
package com.click369.cortex.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.util.Util;



@SuppressLint("HandlerLeak")
public class AutoActivity extends Activity implements OnClickListener{
	private Button autochuanglian,autodengguang;
	TextView  failShowTv;
	LinearLayout clLL,dgLL;
	private EditText autochuanglianEt,autodengguangEt,autochuangliantimeEt,autodengguangtimeEt,auto_chuanglianauto_wd;
	public static int guangzhao_c=0,guangzhao_l=0,wendu_c=0,renti_l=0;
	public static String time_c="0",time_l="0";
	public static boolean chuanglian_b=false,light_b=false;
	CheckBox renti_checkBox;
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
//			case Util.SERVICEDOWNTHREADSTOP://后台线程停止  通知用户重新启动线程
//				failShowTv.setText(Util.NOSERVERRESTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NONETWORK://后台线程停止  通知用户重新启动线程
//				failShowTv.setText(Util.NONETWORKSTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
			case Util.FDDATA:
				System.out.println((String)msg.obj);
				break;
			case Util.NETADRR:
//				addressTv.setText("网内地址："+msg.obj);
				break;
			case Util.NETNUM:
//				netNumTv.setText("网络号："+msg.obj);
				break;
			case Util.SINGLENUM:
//				singleTv.setText("信道号："+msg.obj);
				break;
			}
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autokongzhi);
        autochuanglianEt = (EditText)this.findViewById(R.id.auto_chuanglianauto_et);//autowenduIv.setTag(0);
    	autodengguangEt = (EditText)this.findViewById(R.id.auto_dengguangauto_et);//autoshiduIv.setTag(0);
    	autochuangliantimeEt = (EditText)this.findViewById(R.id.auto_chuanglianauto_time_et);//autowenduIv.setTag(0);
    	autodengguangtimeEt = (EditText)this.findViewById(R.id.auto_dengguangauto_time_et);//autoshiduIv.setTag(0);
    	auto_chuanglianauto_wd = (EditText) findViewById(id.auto_chuanglianauto_wd);
    	clLL = (LinearLayout)this.findViewById(R.id.auto_cl_ll);
    	dgLL = (LinearLayout)this.findViewById(R.id.auto_dg_ll);
    	autochuanglian = (Button)this.findViewById(R.id.auto_chuanglianauto_bt);
    	autodengguang = (Button)this.findViewById(R.id.auto_dengguangauto_bt);
 	
		autochuanglian.setOnClickListener(this);
		autodengguang.setOnClickListener(this);
		renti_checkBox = (CheckBox) findViewById(id.renti_checkBox);
		renti_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					renti_l=1;
				}else{
					renti_l=0;
				}
			}
		});
		if(ChuangLianService.isstart){
			autochuanglian.setText("已打开窗帘自动调节");
		}else{
			autochuanglian.setText("已关闭窗帘自动调节");
		}
		
		if(DengGuangService.isstart){
			autodengguang.setText("已打开灯光自动调节");
		}else{
			autodengguang.setText("已关闭灯光自动调节");
		}
    }
    
    @Override
    protected void onResume() {
    	//设置为本activity的handler
    	Util.uiHandler = myHandler;
    	super.onResume();
    }
    @Override
	protected void onStop() {
//		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.auto_chuanglianauto_bt:
			if(!ChuangLianService.isstart){
				clickBt_chuanglian();
				Intent chuanglianService  = new Intent(this,ChuangLianService.class);
				this.startService(chuanglianService);
				autochuanglian.setText("已打开窗帘自动调节");
				System.out.println("已打开窗帘自动调节");

			}else{
				Intent chuanglianService  = new Intent(this,ChuangLianService.class);
				this.stopService(chuanglianService);
				autochuanglian.setText("已关闭窗帘自动调节");
				Util.clHandler = myHandler;
				Util.clwhichBlock = "";
				AutoActivity.time_c="0";
			}
			
			break;
		case R.id.auto_dengguangauto_bt:
			if(!DengGuangService.isstart){
				clickBt_light();
				Intent dengguangService  = new Intent(this,DengGuangService.class);
				this.startService(dengguangService);
				autodengguang.setText("已打开灯光自动调节");
			}else{
				Intent dengguangService  = new Intent(this,DengGuangService.class);
				this.stopService(dengguangService);
				autodengguang.setText("已关闭灯光自动调节");
				Util.lHandler = myHandler;
				Util.lwhichBlock = "";
				AutoActivity.time_l="0";
			}
			
			break;				
		  default:
			break;
		}
	}
	public void clickBt_chuanglian(){
		if(auto_chuanglianauto_wd.getText().toString()!=""){
			wendu_c=Integer.parseInt(auto_chuanglianauto_wd.getText().toString());
		}
		if(autochuanglianEt.getText().toString()!=""){
			guangzhao_c=Integer.parseInt(autochuanglianEt.getText().toString());
		}
		if(autochuangliantimeEt.getText().toString()!=""){
			time_c=autochuangliantimeEt.getText().toString();
		}
		
	}
	public void clickBt_light(){
		if(autodengguangEt.getText().toString()!=""){
			guangzhao_l=Integer.parseInt(autodengguangEt.getText().toString());
		}
		if(autodengguangtimeEt.getText().toString()!=""){
			time_l=autodengguangtimeEt.getText().toString();
		}
		
		
	}
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
