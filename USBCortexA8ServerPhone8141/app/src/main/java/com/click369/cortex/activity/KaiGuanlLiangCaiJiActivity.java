
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
public class KaiGuanlLiangCaiJiActivity extends Activity implements OnClickListener{
	//网内地址  网络号  信道  和光照强度显示
	private	TextView addressTv,netNumTv,singleTv,failShowTv;
	private ImageView iv1,iv2,iv3;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					parseData((String)msg.obj);
					System.out.println((String)msg.obj);
					break;
				case Util.SERVICEDOWNTHREADSTOP://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NOSERVERRESTR);
					failShowTv.setVisibility(View.VISIBLE);
					break;
				case Util.NONETWORK://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NONETWORKSTR);
					failShowTv.setVisibility(View.VISIBLE);
					break;
				case Util.NETADRR:
					addressTv.setText("网内地址："+msg.obj);
					break;
				case Util.NETNUM:
					netNumTv.setText("网络号："+msg.obj);
					break;
				case Util.SINGLENUM:
					singleTv.setText("信道号："+msg.obj);
					break;
			}
		}
	};
	private void parseData(String dataStr){
		String datas[] = dataStr.split(" ");
		if("0D".equals(datas[4])){
			int zhuangtai1 = HexDump.hexStringToByteArray(datas[5])[0];
			int zhuangtai2 = HexDump.hexStringToByteArray(datas[6])[0];
			int zhuangtai3 = HexDump.hexStringToByteArray(datas[7])[0];
			System.out.println(zhuangtai1+"  "+zhuangtai2+"  "+zhuangtai3+"   "+('N' & 0Xff));
			changeLight(iv1,zhuangtai1);
			changeLight(iv2,zhuangtai2);
			changeLight(iv3,zhuangtai3);
		}
	}

	private void changeLight(ImageView iv,int zhuangtai){
		if(zhuangtai == ('N' & 0Xff)){
			iv.setImageResource(R.drawable.design_icon_off);
		}else if(zhuangtai == ('F' & 0Xff)){
			iv.setImageResource(R.drawable.design_icon_on);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaiguanliangcaiji);
		iv1 = (ImageView)this.findViewById(R.id.kaiguancaiji_iv1);
		iv2 = (ImageView)this.findViewById(R.id.kaiguancaiji_iv2);
		iv3 = (ImageView)this.findViewById(R.id.kaiguancaiji_iv3);
		failShowTv = (TextView)this.findViewById(R.id.kaiguancaiji_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);
		addressTv = (TextView)this.findViewById(R.id.kaiguancaiji_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.kaiguancaiji_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.kaiguancaiji_singelNum_tv);

		String urlPath = "lx=yy&bn="+Util.boxNum;//语音采集
		Util.isNeedConn = true;
//        String urlPath = "downLoad?leixing=kgcj&bn="+Util.boxNum;
//        Util.addr1 = 13;
//        Util.addr2 = 52;
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
			case R.id.kaiguancaiji_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}
}
