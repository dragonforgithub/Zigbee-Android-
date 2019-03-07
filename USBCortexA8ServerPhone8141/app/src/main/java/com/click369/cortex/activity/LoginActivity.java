package com.click369.cortex.activity;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.SavePerfrence;
import com.click369.cortex.util.Util;


//登录界面控制器
@SuppressLint({ "NewApi", "HandlerLeak" })
public class LoginActivity extends Activity implements OnClickListener{
	//登录退出按钮
	Button denglu,tuichu,setip_queding,setip_quxiao;
	//用户名密码输入框
	EditText etUser,etPass,setip_edittext,ip,yuming;
	TextView zcTv;
	public static Activity loginActivity;
	CheckBox cb;
	SavePerfrence spf;

	String password;
	String username;
	Dialog alter;
	Button chongzhi,chongzhi_jia,chongzhi_jian,chongzhi_queding,chongzhi_quxiao,ruku,chuku;//充值
	EditText chongzhi_edittext;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FAIL:
					showMsg("登录失败");
					break;
				case Util.SECESS:
					if(spf.getPerfrence(SavePerfrence.JIMIMA, "n").equals("y")){
						spf.savePerfrence(SavePerfrence.MIMA, password);
						spf.savePerfrence(SavePerfrence.USERNAME, username);
					}
					Intent intent = new Intent(LoginActivity.this,ChoiceBoxActivity.class);
					LoginActivity.this.startActivity(intent);
					LoginActivity.this.finish();
					break;

			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		loginActivity = this;
		setContentView(R.layout.activity_login);

		cb = (CheckBox)findViewById(R.id.login_checkBox);
		zcTv = (TextView)findViewById(R.id.login_zc_tv);
		etUser = (EditText)findViewById(R.id.login_username);
		etUser.setText("admin");
		etPass = (EditText)findViewById(R.id.login_pass);
		denglu = (Button)findViewById(R.id.login_dl);
		denglu = (Button)findViewById(R.id.login_dl);
		tuichu = (Button)findViewById(R.id.login_tc);
		denglu.setOnClickListener(this);
		tuichu.setOnClickListener(this);
		zcTv.setOnClickListener(this);
		spf = new SavePerfrence(this);
		if(spf.getPerfrence(SavePerfrence.JIMIMA, "n").equals("y")){
			cb.setChecked(true);
			String username = spf.getPerfrence(SavePerfrence.USERNAME, "admin");
			String mima = spf.getPerfrence(SavePerfrence.MIMA, "123456");
			Util.passWord = mima;
			Util.userName = username;
			etPass.setText(mima);
			etUser.setText(username);

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
		//Util.IP ="http://"+spf.getPerfrence(SavePerfrence.IP,"192.168.1.104")+":8080/wlw/";
		setFontSize();
		//showMsg("aaaa"+Util.IP);
	}

	private void setFontSize(){//根据不同的分辨率设置不同的字体大小
		int width = initSize();
		if(width <= 480){
			Util.XYLBELSSIZE = 10;
			Util.WENDUADD = 0.29f;
		}else if(width <= 640){
			Util.XYLBELSSIZE = 14;
			Util.WENDUADD = 0.4f;
		}else if(width <= 960){
			Util.XYLBELSSIZE = 18;
			Util.WENDUADD = 0.51f;
		}else if(width <= 1030){
			Util.XYLBELSSIZE = 23;
			Util.WENDUADD = 0.6f;
		}
		System.out.println("Util.WENDUADD  =="+Util.WENDUADD);
	}

	@SuppressLint("NewApi")
	private int initSize(){
		Display display = getWindowManager().getDefaultDisplay();
		String version = Build.VERSION.RELEASE;
		if(version.startsWith("4")){
			Point p = new Point();
			display.getSize(p);
			return p.x;
		}else{
			return  display.getWidth();
		}
	}

	///////////
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_zc_tv:
				Intent intent = new Intent(this,RegActivity.class);
				this.startActivity(intent);
				break;
			case R.id.login_dl:
				dengLu();
//			myHandler.sendEmptyMessage(Util.SECESS);
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

		if(userName.equals("admin")){
			if(passWord.equals("123456")){
				if(Util.isConnect(this)){

					this.username = userName;
					this.password = passWord;
					String urlPath ="userc?method=doLogin&origin=client&username="+userName+"&password="+passWord;
					new Thread(new DownLoadRunnable(myHandler,urlPath,4)).start();

				}else{
					showMsg("无法连接到互联网，请检查网络,或点击菜单设置ip");
				}
			}else{
				showMsg("密码错误！");
			}
		}else{
			showMsg("用户名错误！");
		}
	}

	@Override
	protected void onResume() {

		if(!Util.userName.equals("")){
			etUser.setText(Util.userName);
			if(!Util.passWord.equals("")){
				etPass.setText(Util.passWord);
			}
		}
		super.onResume();
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	///
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "设置IP");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "设置域名");
		return true;

	}

	/**
	 * 菜单项被选择事件
	 **/

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
		builder.setTitle("设置ip");
		builder.setView(ip);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//public static String IP = "http://192.168.1.102:8080/wlw/";
				if(ip.getText().toString().equals("")||ip.getText().toString()==null){
					showMsg("ip不能为空！");
				}else{
					Util.IP ="http://"+ip.getText().toString()+":8080/wlw/";
					spf.savePerfrence(SavePerfrence.IP, ip.getText().toString());
				}
//			showMsg(Util.IP);
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
		yuming.setText(spf.getPerfrence(SavePerfrence.IP,"192.168.1.104"));
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
					spf.savePerfrence(SavePerfrence.IP, yuming.getText().toString());
				}
//			showMsg(Util.IP);
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

}
