package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;

//登录界面控制器
@SuppressLint({ "NewApi", "HandlerLeak" })
public class RegActivity extends Activity implements OnClickListener{
	EditText etName,etSchool,etYuanXi,etMiMa1,etMiMa2;
	Button regBt,backBt;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FAIL:
					showMsg("注册失败");
					System.out.println("注册失败");
					break;
				case Util.EXISTS:
					showMsg("用户名已存在");
					System.out.println("用户名已存在");
					break;
				case Util.SECESS:
					System.out.println("注册成功");
					RegActivity.this.finish();
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_reg);

		etName = (EditText)this.findViewById(R.id.reg_username);
		etMiMa1 = (EditText)this.findViewById(R.id.reg_pass1);
		etMiMa2 = (EditText)this.findViewById(R.id.reg_pass2);
		etSchool = (EditText)this.findViewById(R.id.reg_userschool);
		etYuanXi = (EditText)this.findViewById(R.id.reg_useryuanxi);
		regBt = (Button)this.findViewById(R.id.reg_regbt);
		backBt = (Button)this.findViewById(R.id.reg_backbt);
		regBt.setOnClickListener(this);
		backBt.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reg_regbt:
				checkInfo();
				break;
			case R.id.reg_backbt:
				this.finish();
				break;
		}
	}

	private void checkInfo(){
		String username = etName.getText().toString().trim();
		String userschool = etSchool.getText().toString().trim();
		String useryuanxi = etYuanXi.getText().toString().trim();
		String mima1 = etMiMa1.getText().toString().trim();
		String mima2 = etMiMa1.getText().toString().trim();

		if(username.length()>0){
			if(mima1.length()>0&&mima2.length()>0){
				if(mima1.equals(mima2)){
					if(Util.isConnect(this)){
						Util.userName = username;
						Util.passWord = mima1;

						String urlPath ="userc?method=doRegister&origin=client&username="+username+"&password="+mima1;

						new Thread(new DownLoadRunnable(myHandler,urlPath,5)).start();

					}else{
						showMsg("无法连接到互联网，请检查网络");
					}
				}else{
					showMsg("密码不一致");
				}
			}else{
				showMsg("密码不能为空");
			}
		}else{
			showMsg("用户名不能为空");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

}
