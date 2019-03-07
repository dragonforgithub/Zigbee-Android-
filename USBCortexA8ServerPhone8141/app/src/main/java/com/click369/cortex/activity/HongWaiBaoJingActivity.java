
package com.click369.cortex.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.service.HongWaiService;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class HongWaiBaoJingActivity extends Activity implements OnClickListener{
	private ImageView oneIv,twoIv,threeIv;
	//网内地址  网络号  信道  和光照强度显示  46正常 4E报警
	TextView addressTv,netNumTv,singleTv,failShowTv;

	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.SERVICEDOWNTHREADSTOP://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NOSERVERRESTR);
					failShowTv.setVisibility(View.VISIBLE);
					break;
				case Util.NONETWORK://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NONETWORKSTR);
					failShowTv.setVisibility(View.VISIBLE);
					break;
				case Util.FDDATA:
					parseData((String)msg.obj);
					break;
			/*case Util.NETADRR:
				addressTv.setText("网内地址："+msg.obj);
				break;
			case Util.NETNUM:
				netNumTv.setText("网络号："+msg.obj);
				break;
			case Util.SINGLENUM:
				singleTv.setText("信道号："+msg.obj);
				break;*/
				default:
					break;
			}
		}
	};
	//红外报警一次传回三条数据
	private void parseData(String dataStr){
		dataStr = dataStr.trim();
		//System.out.println("aaaaaaaaaa="+dataStr);
		int zhuangtai=17;
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
				if("B1".equals(datas[4])){
					changeLight(oneIv,zhuangtai);
				}

			}
		}else{
			String[] datas = dataStr.split(" ");
			zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
			if("B1".equals(datas[4])){
				changeLight(oneIv,zhuangtai);
			}
		}
	}

	private void changeLight(ImageView iv,int zhuangtai){
		if(zhuangtai == 70){
			iv.setImageResource(R.drawable.design_icon_off);
		}else if(zhuangtai == 78){
			iv.setImageResource(R.drawable.design_icon_on);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Util.keepScreenWake(this);

		setContentView(R.layout.activity_hongwaibaojing);
		oneIv = (ImageView)this.findViewById(R.id.bj_iv1);
		failShowTv = (TextView)this.findViewById(R.id.bj_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);
		addressTv = (TextView)this.findViewById(R.id.bj_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.bj_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.bj_singelNum_tv);
//        String urlPath = "downLoad?leixing=bj&name="+Util.boxNum;
		String urlPath = "lx=bj&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		super.onResume();
	}
	@Override
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bj_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}


}
