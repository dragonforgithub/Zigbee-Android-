package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.util.AChartView;
import com.click369.cortex.util.NowTime;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("HandlerLeak")
public class ZhongLiActivity extends Activity implements OnClickListener{
	ListView zlListView;
	TextView addressTv,netNumTv,singleTv,zlzTv;
	Button back;
	ImageView showImg;
	LinearLayout zhongLiLinell;
	SimpleAdapter sAdapter;
	//曲线图对象
	AChartView acd;

	public static final int GETLRTB = 0;//温度增加点的初始值
	public static final int INIT = 1;//初始
	public static final int ADD = 2;//增加
	public static final int MINU = 3;//减少
	boolean isShow = true;
	boolean start = false,startChange = true;

	int imgId[] = {R.drawable.shuidi_1,R.drawable.shuidi_2,R.drawable.shuidi_3,R.drawable.shuidi_4,R.drawable.shuidi_5,
			R.drawable.shuidi_6,R.drawable.shuidi_7,R.drawable.shuidi_8,R.drawable.shuidi_9,R.drawable.shuidi_10,
			R.drawable.shuidi_11,R.drawable.shuidi_12};
	int imgNum =0;
	Handler zlHandler = new Handler(){//更新温度计
		public void handleMessage(android.os.Message msg) {
			imgNum++;
			if(imgNum <=11){
				showImg.setImageResource(imgId[imgNum]);
			}else{
				imgNum =0;
				showImg.setImageResource(imgId[imgNum]);
			}
		};
	};
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FDDATA:
					parseData(msg.obj.toString());
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

	//解析重力数据
	private void parseData(String dataStr){
		try {
			String msg[]= dataStr.split(" ");
			int i = 1;
			if(msg[5].equals("00")){//正数

			}else if(msg[5].equals("80")){//负数
				i = -1;
			}
			byte data[] = HexDump.hexStringToByteArray(msg[6]+msg[7]);
			//有可能会出现超过128的数字  在此会变为负数 需要转换为0-256之间的数字
			int numInt[] = new int[2];
			numInt[0] = data[0];
			numInt[1] = data[1];
			if(data[0]<0){
				numInt[0] = 256+data[0];
			}
			if(data[1]<0){
				numInt[1] = 256+data[1];
			}
			int sd = (numInt[0]*256+numInt[1])*i;
			acd.updateChart(sd);
			zlzTv.setText("Z："+sd+"mg");
			getDataShowList(sd);

			if(!start){
				start = true;
				changeImg();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_zhongli);
		back = (Button)this.findViewById(R.id.zl_back);
		back.setOnClickListener(this);
		showImg = (ImageView)this.findViewById(R.id.zl_show_img);
		showImg.setAlpha(0.9f);
		zlzTv = (TextView)this.findViewById(R.id.zl_z_tv);

		addressTv = (TextView)this.findViewById(R.id.zhongli_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.zhongli_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.zhongli_singelNum_tv);
		zhongLiLinell = (LinearLayout)this.findViewById(R.id.zhongli_line_ll);
		zlListView = (ListView)this.findViewById(R.id.zl_listView1);
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);
		zlListView.setAdapter(sAdapter);
		//初始化曲线图
		acd = new AChartView(this,zhongLiLinell,"重力加速度走势图","时间 S","重力mg",0,300,-2000,2000);
		acd.updateChart(0);

		//changeImg();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "04";//重力加速板子节点地址
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		startChange = false;
		this.finish();
	}
	//存放list的数据
	private void getDataShowList(int zhongli){
		HashMap<String,String> hmdata = new HashMap<String, String>();
		hmdata.put("left", NowTime.getNowTime());
		hmdata.put("right", zhongli+"");
		data.add(hmdata);
		sAdapter.notifyDataSetChanged();
		zlListView.setSelection(data.size());
	}

	private void changeImg(){
		new Thread(){
			public void run(){
				while(startChange){
					try {
						Thread.sleep(100);
						zlHandler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
