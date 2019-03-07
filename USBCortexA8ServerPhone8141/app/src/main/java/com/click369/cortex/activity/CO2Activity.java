package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.AChartView;
import com.click369.cortex.util.NowTime;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class CO2Activity extends Activity implements OnClickListener{
	ListView co2ListView;//数据列表
	SimpleAdapter sAdapter;//数据列表适配器
	//网内地址  网络号  信道  和光照强度显示
	TextView addressTv,netNumTv,singleTv,co2Tv,failShowTv;
	ImageView co2Iv;
	LinearLayout co2Linell;
	//曲线图对象
	AChartView acd;
	int currtIntco2 =0 ;
	float co2float = 8f;
	/*
	public static final int GETLRTB = 0;//温度增加点的初始值
	public static final int INIT = 1;//初始
	public static final int ADD = 2;//增加
	public static final int MINU = 3;//减少
	*/
	boolean isShow = true;

	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FDDATA:
					parseData(msg.obj.toString());//根据 后台 服务数据 设置 界面数据信息
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
				default :
					break;
			}
		}
	};

	//解析co2数据 气体 都是 高八位，低八位
	private void parseData(String dataStr){
		try {
			String msg[]= dataStr.split(" ");
			byte data[] = HexDump.hexStringToByteArray(msg[5]+msg[6]);
			//有可能会出现超过128的数字  在此会变为负数 需要转换为0-256之间的数字
			int numInt[] = new int[2];
			numInt[0] = data[0];
			numInt[1] = data[1];
			if(data[0]<0){
				numInt[0] = 256+data[1];
			}
			if(data[1]<0){
				numInt[1] = 256+data[1];
			}
			int co2density = numInt[0]*256+numInt[1];
			acd.updateChart(co2density);
			getDataShowList(co2density);
			co2Tv.setText("当前co2浓度："+co2density+" ppm");
			//startChangeWd(co2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_co2);

		//co2Iv = (ImageView)this.findViewById(R.id.co2_imageView);
		addressTv = (TextView)this.findViewById(R.id.co2_addr_tv);
		co2Tv = (TextView)this.findViewById(R.id.co2_textView);
		failShowTv = (TextView)this.findViewById(R.id.co2_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);
		netNumTv = (TextView)this.findViewById(R.id.co2_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.co2_singelNum_tv);
		co2ListView = (ListView)this.findViewById(R.id.co2_listView1);
		co2Linell = (LinearLayout)this.findViewById(R.id.co2_line_ll);

		co2Tv.setText("当前浓度：0 ppm");
		//简单适配器的两个参数
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.datalist_item, from, to);
		co2ListView.setAdapter(sAdapter);

		currtIntco2 = Math.round(co2float);
		//初始化曲线图
		acd = new AChartView(this,co2Linell,"co2浓度走势图","时间 S","浓度 ppm",0,300,200,2000);
		acd.updateChart(0);
//		String urlPath = "downLoad?leixing=gz&name="+Util.boxNum;
		String urlPath = "lx=yy&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "0C";//co2板子节点
		isShow = true;
		super.onResume();

	}

	@Override
	protected void onPause() {
		isShow = false;
		super.onPause();
	}

	/*
	private void startChangeWd(int zd){//更新co2浓度ppm

		if(zd==0){
			co2Iv.setImageResource(R.drawable.co2_bg12);
		}else if(zd>0&&zd<20){
			co2Iv.setImageResource(R.drawable.co2_bg11);
		}else if(zd>=20&&zd<60){
			co2Iv.setImageResource(R.drawable.co2_bg10);
		}else if(zd>=60&&zd<100){
			co2Iv.setImageResource(R.drawable.co2_bg9);
		}else if(zd>=100&&zd<180){
			co2Iv.setImageResource(R.drawable.co2_bg8);
		} else{
			co2Iv.setImageResource(R.drawable.co2_bg0);
		}
	}
	 */

	//存放list的数据
	private void getDataShowList(float zhaodu){
		HashMap<String,String> hmdata = new HashMap<String, String>();
		hmdata.put("left", NowTime.getNowTime());
		hmdata.put("right", zhaodu+"");
		data.add(hmdata);
		sAdapter.notifyDataSetChanged();
		co2ListView.setSelection(data.size());
	}

	@Override
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.co2_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}

}
