package com.click369.cortex.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.service.ShengGuangService;
import com.click369.cortex.util.SavePerfrence;
import com.click369.cortex.util.Util;

//登录界面控制器
@SuppressLint({ "NewApi", "ShowToast" })
public class LoginActivity extends Activity implements OnClickListener{
	//登录退出按钮
	Button denglu,tuichu,setip_queding,setip_quxiao;
	//用户名密码输入框
	EditText etUser,etPass,setip_edittext,ip,yuming;
	public static Activity loginActivity;
	CheckBox cb;
	SavePerfrence spf;
	FrameLayout FrameLayout1;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loginActivity = this;
		setContentView(R.layout.activity_login);
		cb = (CheckBox)findViewById(R.id.login_checkBox);
		etUser = (EditText)findViewById(R.id.login_username);
		etUser.setText("admin");
		etPass = (EditText)findViewById(R.id.login_pass);
		denglu = (Button)findViewById(R.id.login_dl);
		denglu = (Button)findViewById(R.id.login_dl);
		tuichu = (Button)findViewById(R.id.login_tc);
		denglu.setOnClickListener(this);
		tuichu.setOnClickListener(this);
		spf = new SavePerfrence(this);

		if(spf.getPerfrence(SavePerfrence.JIMIMA, "n").equals("y")){
			cb.setChecked(true);
			String mima = spf.getPerfrence(SavePerfrence.MIMA, "123456");
			etPass.setText(mima);
		}
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					spf.savePerfrence(SavePerfrence.JIMIMA, "y");
				}else{
					spf.savePerfrence(SavePerfrence.JIMIMA, "n");
				}
			}
		});
//		Util.IP ="http://"+spf.getPerfrence(SavePerfrence.IP,"192.168.1.104")+":8080/wlw/";
		//showMsg("ip="+Util.IP);
//		FrameLayout1 = (FrameLayout) findViewById(id.FrameLayout1);
//		FrameLayout1.setOnLongClickListener(new OnLongClickListener() {
//
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				//dialog_yuming();
//				dialog_ip();
//				return false;
//			}
//		});
	}
	/**
	 * 杀死已有的相同的app 进程
	 * */
	private void killSameApp() {
		// TODO Auto-generated method stub
		ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得手机进程列表
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : infos) {
			System.out.println(runningAppProcessInfo.processName);
			// 去除包含Android包名跟本包名的进程
			if(runningAppProcessInfo.processName.indexOf("android") == -1 && runningAppProcessInfo.processName.indexOf(this.getPackageName()) == -1) {
				// 关闭进程
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_dl:
				dengLu();
				break;
			case R.id.login_tc:
				this.finish();
				Process.killProcess(Process.myPid());
				break;
		}
	}

	private void dengLu(){
		String userName = etUser.getText().toString().trim();
		String passWord = etPass.getText().toString().trim();
		if(!userName.equals("")){
			if(!passWord.equals("")){
				if(userName.equals("admin")){
					if(passWord.equals("123456")){
						if(spf.getPerfrence(SavePerfrence.JIMIMA, "n").equals("y")){
							spf.savePerfrence(SavePerfrence.MIMA, passWord);
						}
						Intent intent = new Intent(this,SetActivity.class);
						this.startActivity(intent);
						//this.finish();///6-23
					}else{
						showMsg(getResources().getString(R.string.incorrect_password));
					}
				}else{
					showMsg(getResources().getString(R.string.incorrect_userName));
				}
			}else{
				showMsg(getResources().getString(R.string.password_is_null));
			}
		}else{
			showMsg(getResources().getString(R.string.userName_is_null));
		}
	}

	@Override
	protected void onResume() {
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		super.onResume();
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "设置IP");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "设置域名");
		return true;

	}

	//菜单项被选择事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case Menu.FIRST+1:
				dialog_ip();
				break;
			case Menu.FIRST+2:
				dialog_yuming();
				break;

		}
		return false;

	}
	protected void dialog_ip() {
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		ip = new EditText(this);

		ip.setText(spf.getPerfrence(SavePerfrence.IP,"192.168.1.104"));


		builder.setTitle("设置ip/域名");
		builder.setView(ip);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//public static String IP = "http://192.168.1.102:8080/wlw/";
				if(ip.getText().toString().equals("")||ip.getText().toString()==null){
					showMsg(getResources().getString(R.string.ip_is_null));
				}else{
					Util.IP ="http://"+ip.getText().toString()+":8080/wlw/";
					spf.savePerfrence(SavePerfrence.IP, ip.getText().toString());
				}
//				showMsg(Util.IP);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}

		});
		builder.create().show();
	}
	protected void dialog_yuming() {
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		yuming = new EditText(this);
		yuming.setText("hcj33517.x9.fjjsp.net");
		builder.setTitle("设置域名");
		builder.setView(yuming);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//public static String IP = "http://192.168.1.102:8080/wlw/";
				if(yuming.getText().toString().equals("")||yuming.getText().toString()==null){
					showMsg("域名不能为空！");
				}else{
					Util.IP ="http://"+yuming.getText().toString()+"/";
				}
//				showMsg(Util.IP);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.create().show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ShowDialog();
		return ;
	}

	private void ShowDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("是否确认退出？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Intent servicea = new Intent(LoginActivity.this, MainZigBeeService.class);
				stopService(servicea);
				final Intent serviceb = new Intent(LoginActivity.this, MainWifiService.class);
				stopService(serviceb);
				final Intent servicec = new Intent(LoginActivity.this, ChuangLianService.class);
				stopService(servicec);
				final Intent serviced = new Intent(LoginActivity.this, DengGuangService.class);
				stopService(serviced);
				final Intent servicee = new Intent(LoginActivity.this, ShengGuangService.class);
				stopService(servicee);
				finish();
				if(LoginActivity.loginActivity!=null){
					LoginActivity.loginActivity.finish();
				}

				Process.killProcess(Process.myPid());
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
