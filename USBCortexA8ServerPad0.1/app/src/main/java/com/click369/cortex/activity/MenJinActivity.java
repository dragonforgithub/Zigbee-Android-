
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class MenJinActivity extends Activity implements OnClickListener{
	//网内地址  网络号  信道  和光照强度显示
	TextView addressTv,netNumTv,singleTv,failShowTv;
	private ImageView lockIv;
	private TextView lockTv;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					parseData((String)msg.obj);
					break;
//			case Util.SERVICEDOWNTHREADSTOP://后台线程停止  通知用户重新启动线程
//				failShowTv.setText(Util.NOSERVERRESTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NONETWORK://后台线程停止  通知用户重新启动线程
//				failShowTv.setText(Util.NONETWORKSTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NETADRR:
//				addressTv.setText("网内地址："+msg.obj);
//				break;
//			case Util.NETNUM:
//				netNumTv.setText("网络号："+msg.obj);
//				break;
//			case Util.SINGLENUM:
//				singleTv.setText("信道号："+msg.obj);
//				break;
			}
		}
	};

	private void parseData(String dataStr){
		String datas[] = dataStr.split(" ");
//		int jiedian = HexDump.hexStringToByteArray(datas[4])[0];
//		System.out.println("jiedian = "+jiedian+"  0xB2"+0xB2 +"    "+(jiedian == 0xB2));
		if("B4".equals(datas[4])){
			int zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
			if(zhuangtai == 0){
				lockIv.setImageResource(R.drawable.lock_off);
				lockTv.setText("关");
			}else if(zhuangtai == 1){
				lockIv.setImageResource(R.drawable.lock_on);
				lockTv.setText("开");
			}
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menjin);
		lockIv = (ImageView)this.findViewById(R.id.menjin_zhuangtai_iv);
		lockTv = (TextView)this.findViewById(R.id.menjin_zhuangtai_tv);
		lockIv.setImageResource(R.drawable.lock_off);
		lockTv.setText("关");
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock ="showdata";
		super.onResume();
	}
	@Override
	protected void onStop() {
//		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {

	}
}
