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
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.AChartView;
import com.click369.cortex.util.NowTime;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class ShiDuActivity extends Activity implements OnClickListener{
	ListView sdListView;
	TextView addressTv,netNumTv,singleTv, sdTv,failShowTv;
	ImageView sdIv;
	LinearLayout shiduLinell;
	//曲线图对象
	AChartView acd;

	ListView shiDuListView;
	//list适配器
	SimpleAdapter sAdapter;

	int currtIntShiDu =0 ;
	float shidufloat = 8f;

	public static final int GETLRTB = 0;//温度增加点的初始值
	public static final int INIT = 1;//初始
	public static final int ADD = 2;//增加
	public static final int MINU = 3;//减少
	boolean isShow = true;
	int imgId [] = {R.drawable.shidu_0,R.drawable.shidu_1,R.drawable.shidu_2,R.drawable.shidu_3,
			R.drawable.shidu_4,R.drawable.shidu_5,R.drawable.shidu_6,R.drawable.shidu_7,
			R.drawable.shidu_8,R.drawable.shidu_9,R.drawable.shidu_10};
	Handler gzHandler = new Handler(){//更新温度计
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GETLRTB://温度增加点的初始值
					break;
				case INIT://初始
					sdIv.setImageResource(imgId[msg.arg1]);
					break;
				case ADD://增加
					break;
				case MINU://减少
					break;
			}
		};
	};
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FDDATA:
//				acd.updateChart(35);
					System.out.println("msg.obj.toString() "+msg.obj.toString());
					parseData(msg.obj.toString());
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

	//解析湿度数据
	private void parseData(String dataStr){
		try {
			String msg[]= dataStr.split(" ");
			byte data[] = HexDump.hexStringToByteArray(msg[7]+msg[8]);
//			 float sd = Float.parseFloat(msg[7]+"."+msg[8]);
			float sd = Float.parseFloat(data[0]+"."+data[1]);
			acd.updateChart(sd);
			sdTv.setText("当前湿度："+sd+"%");
			getDataShowList(sd);
			startChangeSd((int)sd);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_shidu);

		sdIv = (ImageView)this.findViewById(R.id.shidu_imageView);
		failShowTv = (TextView)this.findViewById(R.id.shidu_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);

		sdTv = (TextView)this.findViewById(R.id.shidu_textView);
		addressTv = (TextView)this.findViewById(R.id.shidu_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.shidu_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.shidu_singelNum_tv);
		sdListView = (ListView)this.findViewById(R.id.shidu_listView1);
		shiduLinell = (LinearLayout)this.findViewById(R.id.shidu_line_ll);
		shiDuListView = (ListView)this.findViewById(R.id.shidu_listView1);
		sdTv.setText("当前湿度：0%");
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.datalist_item, from, to);
		sdListView.setAdapter(sAdapter);

		currtIntShiDu = Math.round(shidufloat)+10;

		//初始化曲线图
		acd = new AChartView(this,shiduLinell,"湿度走势图","时间 S","湿度 RH%",0,300,0,100);
		acd.updateChart(0);

//		String urlPath = "downLoad?leixing=sd&bn="+Util.boxNum;
		String urlPath = "lx=wsd&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "09";//温湿度板子节点地址
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
	private void startChangeSd(int zd){
		if(zd==0){
			sdIv.setImageResource(R.drawable.shidu_0);
		}else if(zd>0&&zd<10){
			sdIv.setImageResource(R.drawable.shidu_1);
		}else if(zd>=10&&zd<20){
			sdIv.setImageResource(R.drawable.shidu_2);
		}else if(zd>=20&&zd<30){
			sdIv.setImageResource(R.drawable.shidu_3);
		}else if(zd>=30&&zd<40){
			sdIv.setImageResource(R.drawable.shidu_4);
		}else if(zd>=40&&zd<50){
			sdIv.setImageResource(R.drawable.shidu_5);
		}else if(zd>=50&&zd<60){
			sdIv.setImageResource(R.drawable.shidu_6);
		}else if(zd>=60&&zd<70){
			sdIv.setImageResource(R.drawable.shidu_7);
		}else if(zd>=70&&zd<80){
			sdIv.setImageResource(R.drawable.shidu_8);
		}else if(zd>=80&&zd<90){
			sdIv.setImageResource(R.drawable.shidu_9);
		}else if(zd>=90&&zd<=100){
			sdIv.setImageResource(R.drawable.shidu_10);
		}else{
			sdIv.setImageResource(R.drawable.shidu_10);
		}
	}

	public void sendMsg(int what,int arg1){
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		if(gzHandler!=null){
			gzHandler.sendMessage(msg);
		}
	}

	//存放list的数据
	private void getDataShowList(float shidu){
		HashMap<String,String> hmdata = new HashMap<String, String>();
		hmdata.put("left", NowTime.getNowTime());
		hmdata.put("right", shidu+"%");
		data.add(hmdata);
		sAdapter.notifyDataSetChanged();
		shiDuListView.setSelection(data.size());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.shidu_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}
}
