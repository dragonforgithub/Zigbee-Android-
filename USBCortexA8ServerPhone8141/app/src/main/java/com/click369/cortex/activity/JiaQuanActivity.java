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
public class JiaQuanActivity extends Activity implements OnClickListener{
	ListView jqListView;//数据列表
	SimpleAdapter sAdapter;//数据列表适配器
	//网内地址  网络号  信道  和光照强度显示
	TextView addressTv,netNumTv,singleTv,jqTv,failShowTv;
	ImageView jqIv;
	LinearLayout jqLinell;
	//曲线图对象
	AChartView acd;
	int currtIntjq =0 ;
	float jqfloat = 8f;
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

	//解析光照数据
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
			int jqDensity = numInt[0]*256+numInt[1];
			acd.updateChart(jqDensity);
			getDataShowList(jqDensity);
			jqTv.setText("当前甲醛浓度："+jqDensity+" ppm");
			//startChangeWd(jqDensity);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_jiaquan);

		//jqIv = (ImageView)this.findViewById(R.id.jq_imageView);
		addressTv = (TextView)this.findViewById(R.id.jq_addr_tv);
		jqTv = (TextView)this.findViewById(R.id.jq_textView);
		failShowTv = (TextView)this.findViewById(R.id.jq_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);
		netNumTv = (TextView)this.findViewById(R.id.jq_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.jq_singelNum_tv);
		jqListView = (ListView)this.findViewById(R.id.jq_listView1);
		jqLinell = (LinearLayout)this.findViewById(R.id.jq_line_ll);

		jqTv.setText("当前浓度：0 ppm");
		//简单适配器的两个参数
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.datalist_item, from, to);
		jqListView.setAdapter(sAdapter);
		currtIntjq = Math.round(jqfloat);
		//初始化曲线图
		acd = new AChartView(this,jqLinell,"甲醛浓度走势图","时间 S","浓度 ppm",0,300,0,1000);
		acd.updateChart(0);
//		String urlPath = "downLoad?leixing=gz&name="+Util.boxNum;
		String urlPath = "lx=yw&bn="+Util.boxNum;
		Util.isNeedConn = true;
		Util.bindMyService(this,urlPath);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "0D";//甲醛 板子节点
		isShow = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isShow = false;
		super.onPause();
	}
	/*
	private void startChangeWd(int zd){//传入0 1 2 3 按那种方式更新温度计
		if(zd==0){
			jqIv.setImageResource(R.drawable.jq_bg12);
		}else if(zd>0&&zd<20){
			jqIv.setImageResource(R.drawable.jq_bg11);
		}else if(zd>=20&&zd<60){
			jqIv.setImageResource(R.drawable.jq_bg10);
		}else if(zd>=60&&zd<100){
			jqIv.setImageResource(R.drawable.jq_bg9);
		} else{
			jqIv.setImageResource(R.drawable.jqz_bg0);
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
		jqListView.setSelection(data.size());
	}

	@Override
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.jq_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}

}
