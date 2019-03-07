package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.service.HongWaiService;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;

//登录界面控制器
@SuppressLint({ "NewApi", "HandlerLeak" })
public class EnterActivity extends Activity implements OnClickListener{
	FrameLayout huanJingFl,yaoKongFl,anFangFl;
	private TextView titleView;
	String boxNum;
	EnterActivity enterActivity;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.SECESS:
				case Util.FAIL:
					closeActivityAndService();
					break;
				default:
					break;
			}

		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_enter);

		enterActivity = this;
		huanJingFl = (FrameLayout)this.findViewById(R.id.enter_huanjing_fl);
		yaoKongFl = (FrameLayout)this.findViewById(R.id.enter_yaokong_fl);
		anFangFl = (FrameLayout)this.findViewById(R.id.enter_anfang_fl);
		titleView = (TextView)this.findViewById(R.id.enter_titlt_tv);
		huanJingFl.setOnClickListener(this);
		yaoKongFl.setOnClickListener(this);
		anFangFl.setOnClickListener(this);

		if(this.getIntent().hasExtra("name")){
			boxNum = (String)this.getIntent().getCharSequenceExtra("name");
			titleView.append("-"+boxNum);
		}
	}
	/**
	 * 关闭所有 服务以及 登录 选择平台界面
	 * */
	protected void closeActivityAndService() {
		// TODO Auto-generated method stub

		Intent hongwaiServicea  = new Intent(EnterActivity.this,HongWaiService.class);
		EnterActivity.this.stopService(hongwaiServicea);
		Intent chuanglianServicea  = new Intent(EnterActivity.this,ChuangLianService.class);
		EnterActivity.this.stopService(hongwaiServicea);
		Intent DengGuangServicea  = new Intent(EnterActivity.this,DengGuangService.class);
		EnterActivity.this.stopService(DengGuangServicea);
		EnterActivity.this.finish();

		if(LoginActivity.loginActivity!=null){
			LoginActivity.loginActivity.finish();
		}
		if(ChoiceBoxActivity.choiceBoxActivity!=null){
			ChoiceBoxActivity.choiceBoxActivity.finish();
		}

		Process.killProcess(android.os.Process.myPid());
		//System.out.println("Process.killProcess(android.os.Process.myPid());2");

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 1, 0, "设置网址");
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		return super.onOptionsItemSelected(item);
//	}
//
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void changeActivity(@SuppressWarnings("rawtypes") Class cls) {
		Intent intent = new Intent(this,cls);
		intent.putExtra("name", boxNum);
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

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
		builder.setTitle("提示");
		builder.setMessage("是否注销并退出物联网平台？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					Util.isNowInstall = false;
					String urlPath ="userc?method=quit&origin=client&username="+Util.userName+"&bn="+Util.boxNum;
					new Thread(new DownLoadRunnable(myHandler,urlPath,6)).start();
					Process.killProcess(android.os.Process.myPid());///

				}catch(Exception e){
					closeActivityAndService();
				}

			}
		});
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

}

