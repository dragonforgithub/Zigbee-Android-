
package com.click369.cortex.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.util.Util;


@SuppressLint("HandlerLeak")
public class AnFangActivity extends Activity implements OnClickListener{
	private FrameLayout hongwaiFl,shexiangFl,jiankong_trq_fl,jinjihujiaoFl;//menjinFl ,jiankong_yanwu_fl,shengguang_f1
	private TextView titleView;
	File file;
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
		setContentView(R.layout.activity_anfang);

		file = new File(Environment.getExternalStorageDirectory(),"easyn_p1.apk");

		hongwaiFl = (FrameLayout)this.findViewById(R.id.anfang_hongwai_fl);
		shexiangFl = (FrameLayout)this.findViewById(R.id.anfang_shexiangtou_fl);
		jiankong_trq_fl = (FrameLayout) findViewById(id.jiankong_trq_fl);
		jinjihujiaoFl=(FrameLayout)findViewById(R.id.anfang_jinjihujiao_fl);

		hongwaiFl.setOnClickListener(this);
		shexiangFl.setOnClickListener(this);
		jiankong_trq_fl.setOnClickListener(this);
		jinjihujiaoFl.setOnClickListener(this);

		//menjinFl = (FrameLayout)this.findViewById(R.id.anfang_menjin_fl);
		//menjinFl.setOnClickListener(this);
		//jiankong_yanwu_fl = (FrameLayout) findViewById(id.jiankong_yanwu_fl);
		//jiankong_yanwu_fl.setOnClickListener(this);
		//shengguang_f1 = (FrameLayout) findViewById(id.anfang_shengguang_fl);
		//shengguang_f1.setOnClickListener(this);
//        titleView = (TextView)this.findViewById(R.id.anfang_title_tv);
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
			case R.id.anfang_hongwai_fl://红外报警
				changeActivity(HongWaiBaoJingActivity.class,"");
				break;

			case R.id.anfang_shexiangtou_fl://视频监控
				copyAssetToSd();
				break;
			case R.id.jiankong_trq_fl://天然气报警
				changeActivity(TRQActivity.class,"天然气");

				break;
			case R.id.anfang_jinjihujiao_fl://紧急呼叫 报警
				changeActivity(JinJiHuJiaoActivity.class,"");
				break;

			 /*
		 case R.id.anfang_menjin_fl:
			changeActivity(MenJinActivity.class,"");
			break;
	     case R.id.anfang_shengguang_fl:
	    	 changeActivity(ShengGuangActivity.class,"");
			 break;

		 case R.id.jiankong_yanwu_fl:
		 	changeActivity(QiTiActivity.class,"烟雾");
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

	//把asset中的apk拷贝到sd中进行安装
	public void copyAssetToSd(){
		try {
			if(!isAppInstall()){

				if(!file.exists()){
					AssetManager ass= this.getAssets();
					InputStream is = ass.open("easyn_p1.apk");
					FileOutputStream fos = new FileOutputStream(file);
					byte b[] = new byte[1024*10];
					int len = 0;
					while((len = is.read(b))!=-1){
						fos.write(b, 0, len);
					}
					fos.close();
					is.close();
				}
				installApp();
			}else{
				try {
					if(file.exists()){
						file.delete();
					}
					Intent intent = new Intent(Intent.ACTION_MAIN);
					ComponentName componentName = new ComponentName(
							"com.easyn.EasyN_P1",
							"com.easyn.EasyN_P1.SplashScreenActivity");
					intent.setComponent(componentName);
					startActivity(intent);
				} catch (Exception e) {
					showMsg("手机未安装监控软件");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void installApp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("手机没有安装监控软件，点击确定安装（无需流量）!");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		});
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}


	public boolean isAppInstall(){
		PackageInfo packageInfo;
		try {
			packageInfo = this.getPackageManager().getPackageInfo("com.easyn.EasyN_P1", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if(packageInfo ==null){
			System.out.println("没有安装");
			return false;
		}else{
			System.out.println("已经安装");
			return true;
		}
	}
}
