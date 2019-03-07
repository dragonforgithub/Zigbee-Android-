package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.AChartView;
import com.click369.cortex.util.NowTime;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class QiTiActivity extends Activity implements OnClickListener{
	TextView addressTv,netNumTv,singleTv,showNdTv,qiTiTitle,failShowTv;
	int leixing=0;
	boolean isShow = true;
	private ImageView qt_iv1;
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FDDATA:
					String datas[] = (msg.obj.toString()).split(" ");
					if(datas[4].equals("B3")){
						parseData(msg.obj.toString());
					}
					break;
				case Util.SERVICEDOWNTHREADSTOP://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NOSERVERRESTR);
					failShowTv.setVisibility(View.VISIBLE);
					break;
				case Util.NONETWORK://后台线程停止  通知用户重新启动线程
					failShowTv.setText(Util.NONETWORKSTR);
					failShowTv.setVisibility(View.VISIBLE);
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

	//解析天然气数据
	private void parseData(String dataStr){
		try {
			dataStr = dataStr.trim();

			if(dataStr.contains("\r\n")){
				String rootdatas[] = dataStr.split("\r\n");
				int length = rootdatas.length;
				for(int i = 0;i<length;i++){
					String datas[] = rootdatas[i].split(" ");
					//理解：从 报警数据 中获取 天然气报警状态  ，leixing==6 ,第七位 值 表示天然气报警状态
					setTRQimg(datas);
				}
			}else{
				String datas[] = dataStr.split(" ");
				setTRQimg(datas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTRQimg(String[] datas) {
		// TODO Auto-generated method stub
		int zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
		if(zhuangtai==78){
			qt_iv1.setImageResource(R.drawable.design_icon_on);
		}else if(zhuangtai==70){
			qt_iv1.setImageResource(R.drawable.design_icon_off);
		}
	}

	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Util.keepScreenWake(this);
		setContentView(R.layout.activity_qt);

		failShowTv = (TextView)this.findViewById(R.id.qt_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);

		qiTiTitle = (TextView)this.findViewById(R.id.qiti_title);
//		showNdTv = (TextView)this.findViewById(R.id.qt_nd_tv);
		addressTv = (TextView)this.findViewById(R.id.qt_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.qt_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.qt_singelNum_tv);

		qt_iv1 = (ImageView) findViewById(id.qt_iv1);

//		String urlPath = "downLoad?leixing=trq&bn="+Util.boxNum;
		String title ="天然气";
		String urlPath = "lx=bj&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);

		if(this.getIntent().hasExtra("form")){
			title = (String)this.getIntent().getCharSequenceExtra("form");
			qiTiTitle.setText(title+"浓度");
			if(title.equals("天然气")){
				leixing = 5;
				urlPath = "lx=bj&bn="+Util.boxNum;
//				urlPath = "downLoad?leixing=trq&name="+Util.boxNum;
			}
//			else if(title.equals("氧气")){
//				leixing = 0;
////				urlPath = "lx=yq&bn="+Util.boxNum;
////				urlPath = "downLoad?leixing=yq&name="+Util.boxNum;
//			}else if(title.equals("二氧化碳")){
//				leixing = 0;
////				urlPath = "lx=eyht&bn="+Util.boxNum;
////				urlPath = "downLoad?leixing=eyht&name="+Util.boxNum;
//			}else if(title.equals("烟雾")){
//				leixing = 7;
////				urlPath = "lx=yw&bn="+Util.boxNum;
////				urlPath = "downLoad?leixing=yw&name="+Util.boxNum;
//			}
			Util.isNeedConn = true;
		}

		Util.bindMyService(this,urlPath);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isShow = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}

	public void sendMsg(int what,int arg1){
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.qt_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}

}
