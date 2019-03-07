package com.click369.cortex.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import com.click369.cortex.service.MainWifiService;

public class Util {

	public static Handler uiHandler;//外界传入的handler用来和界面交互
	//从设置界面传入
	public static int rate = 38400, stopBit=1,jiou=0,dataBits=0;
	//网内地址
	public static int addr1,addr2;
	public static int FRESHTIME = 3*1000;
	public static String whichBlock = "30";//用来过滤不同的板子信号  默认是30
	//user?action=doLogin&origin=client&
//		public static String IP= "http://hcj33517.x10.fjjsp01.com/";
	public static String IP= "http://hcj335171.x9.fjjsp01.com/";
	//		public static String IP = "http://testwlw.java.fjjsp01.com/";
//		public static String IP = "http://192.168.1.109:8080/wlw/";
	public static final String UPURLSTR = IP+"sdf?action=write&bn=";
	//		public static final String UPURLSTR = "http://192.168.2.69:8083/wlw/upLoad";
	public static final String DOWNURLSTR = IP+"rdf?action=read&";
	//		public static final String DOWNURLSTR = "http://192.168.2.69:8083/wlw/";
	public static String DOWNURLPATH = "";
	public static String UPURLPATH = "";
	public static String userName = "";
	public static String passWord = "";
	public static String boxNum = "40";
	/**
	 *是否需要不停的请求数据  在service中使用
	 **/
	public static boolean isNeedConn = true;
	//图表的xy数字大小
	public static int XYLBELSSIZE = 20;
	//温度增加的大小 根据分辨率设置不同的增加值
	public static float WENDUADD = 0.6f;


	public static final String NONETWORKSTR = "请打开网络后点击此处重新连接";
	public static final String NOSERVERRESTR = "网络请求失败点击此处重新请求";

	public static final int NONETWORK = 3333;
	public static final int FAIL = 1111;
	public static final int SECESS = 2222;
	public static final int EXISTS = 2221;
	public static final int CHOICESECESS = 4444;
	public static final int CHOICEFAIL = 5555;
	public static final int NOBOX = 6666;


	public static final int NETADRR = 1000;
	public static final int NETNUM = 1100;
	public static final int SINGLENUM = 1200;
	public static final int ALLDATA = 1300;
	public static final int FDDATA = 1400;
	public static final int FCDATA = 1500;
	public static final int USBCONN = 1600;
	public static final int SERVICEDOWNTHREADSTOP = 1700;

	public static final int STOP = 10;
	public static final int START = 20;
	public static final int RESTART = 30;
	public static final int STOPALL = 40;
	public static final int CLOSELIGHT = 50;
	public static final int OPENLIGHT = 60;
	public static final int CHANGECANSHU = 70;
	public static final int WRITEDATA = 80;

	public static final int SENDDATATOSERVICE = 104;
	public static final int SENDAUTODATATOSERVICE = 105;


	public static byte[] inputStream2Bytes(InputStream inStream) {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int rc = 0;
		try {
			while ((rc = inStream.read(buff, 0, 1024)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			inStream.close();
			return swapStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final ServiceConnection connection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			System.out.println("onServiceDisconnected");
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			System.out.println("onServiceConnected");
		}
	};

	public static void bindMyService(Context context,String url){

		DOWNURLPATH = url;
		Intent service = new Intent(context,MainWifiService.class);
		context.bindService(service, connection, Service.BIND_AUTO_CREATE);

	}

	public static void unBindMyService(Context context){
		try {
			context.unbindService(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isConnect(Context context){
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取状态
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State hipri = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI).getState();
		//判断wifi已连接的条件
		if(wifi == State.CONNECTED||wifi==State.CONNECTING
				||mobile == State.CONNECTED||hipri == State.CONNECTED){
			return true;
		}else{
			return false;
		}
	}


	//datas的顺序 1、网内地址1 2、网内地址2  3、板子节点  4、资源节点  5、控制信息1  6、控制信息2 7、控制信息3
	public static void sendMsgToService(int datas[],int what,Context context){

		if(MainWifiService.myHandler!=null){
			//if(datas[0]!=0||datas[1]!=0){
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = datas;
			MainWifiService.myHandler.sendMessage(msg);

			//}else{
			//	showMsg("无法获取网内地址，服务器未返回数据",context);
			//}
		}else{
			showMsg("服务未启动，可能由于网络不通，请检查网络",context);
		}
	}
	//自动控制信息发送
	public static void sendAutoControlMsgToService(int datas[],int what,Context context){
		if(MainWifiService.myHandler!=null){
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = datas;
			MainWifiService.myHandler.sendMessage(msg);
		}else{
			showMsg("服务未启动，可能由于网络不通，请检查网络",context);
		}
	}

	static Toast t = null;
	public static void showMsg(String text,Context context){
		if(t==null||!t.getView().isShown()){
			t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			t.show();
		}
	}


	//把asset中的apk拷贝到sd中进行安装
	static File file;
	public static void copyAssetToSd(Context context){
		file = new File(Environment.getExternalStorageDirectory(),"easyn_p1.apk");
		try {
			if(!isAppInstall(context)){

				if(!file.exists()){
					AssetManager ass= context.getAssets();
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
				installApp(context);
			}else{
				try {
					if(file.exists()){
						file.delete();
					}
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ComponentName componentName = new ComponentName(
							"com.easyn.EasyN_P1",
							"com.easyn.EasyN_P1.SplashScreenActivity");
					intent.setComponent(componentName);
					context.startActivity(intent);
				} catch (Exception e) {
					//showMsg("手机未安装监控软件");
					e.printStackTrace();
					Toast.makeText(context, "手机未安装监控软件",Toast.LENGTH_SHORT).show();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNowInstall = false;
	private static void installApp(final Context context){
//	    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
//	    	builder.setTitle("提示");
//	    	builder.setMessage("手机没有安装监控软件，点击确定安装（无需流量）!");
//	    	builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//	    		@Override
//	    		public void onClick(DialogInterface dialog, int which) {
		if(!isNowInstall){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intent);
			isNowInstall = true;
		}
//	    		}
//	    	});
//	    	builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//	    		@Override
//	    		public void onClick(DialogInterface dialog, int which) {
//
//	    		}
//	    	});
//	    	builder.create().show();
	}


	public static boolean isAppInstall(Context context){
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.easyn.EasyN_P1", 0);
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

	public static boolean isRunCammera(Context context){
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals("com.easyn.EasyN_P1") && info.baseActivity.getPackageName().equals("com.easyn.EasyN_P1")) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}
	/**
	 * 保持屏幕常亮，不锁屏
	 * */
	public static void keepScreenWake(Activity activity){
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

}
