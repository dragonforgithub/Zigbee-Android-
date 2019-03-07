package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
public class CO2Activity extends Activity implements OnClickListener{
	ListView co2ListView;//数据列表
	SimpleAdapter sAdapter;//数据列表适配器
	//网内地址  网络号  信道  和光照强度显示
	//TextView addressTv,netNumTv,singleTv,co2Tv;
	TextView co2Tv;
	ImageView co2Iv;
	Button back,lookData;
	LinearLayout co2Linell;
	//曲线图对象
	AChartView acd;
	int currtIntco2 =0 ;
	float co2float = 8f;
	
	public static final int GETLRTB = 0;//温度增加点的初始值
	public static final int INIT = 1;//初始
	public static final int ADD = 2;//增加
	public static final int MINU = 3;//减少
	boolean isShow = true;
	
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.FDDATA:
				parseData(msg.obj.toString());
				break;
			case Util.NETADRR:
				//addressTv.setText("网内地址："+msg.obj);
				break;
			case Util.NETNUM:
				//netNumTv.setText("网络号："+msg.obj);
				break;
			case Util.SINGLENUM:
				//singleTv.setText("信道号："+msg.obj);
				break;
			}
		}
	};
	
	//解析co2数据,2 字节 ，高八位，低八位 
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
			 int co2 = numInt[0]*256+numInt[1];
			 acd.updateChart(co2);
			 getDataShowList(co2);
			 co2Tv.setText(co2+"");
			 startChangeWd(co2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_co2);
		back = (Button)this.findViewById(R.id.co2_back);
		back.setOnClickListener(this);
		co2Iv = (ImageView)this.findViewById(R.id.co2_imageView);
		co2Tv = (TextView)this.findViewById(R.id.co2_textView);
//		addressTv = (TextView)this.findViewById(R.id.co2_addr_tv);
//		netNumTv = (TextView)this.findViewById(R.id.co2_netNum_tv);
//		singleTv = (TextView)this.findViewById(R.id.co2_singelNum_tv);
		co2ListView = (ListView)this.findViewById(R.id.co2_listView1);
		co2Linell = (LinearLayout)this.findViewById(R.id.co2_line_ll);
		
		co2Tv.setText("100");
		//简单适配器的两个参数
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);
		co2ListView.setAdapter(sAdapter);
		currtIntco2 = Math.round(co2float);
		//初始化曲线图
		acd = new AChartView(this,co2Linell,"co2浓度走势图","时间 S","浓度 ppm",0,300,0,1200);
		acd.updateChart(0);
		super.onCreate(savedInstanceState);
		
		/*
        lookData = (Button)this.findViewById(R.id.main_lookdata_btn);
        lookData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent =new Intent(CO2Activity.this,DataShowActivity.class);
				startActivity(intent);
				
			}
		});
		*/
	}
	
	@Override
    protected void onResume() {
    	//设置为本activity的handler
		Util.uiHandler = myHandler;
        Util.whichBlock = "0C";//co2板子节点 0C 
        isShow = true;
    	super.onResume();
    }
	
	@Override
	protected void onPause() {
		isShow = false;
		super.onPause();
	}
	
	private void startChangeWd(int zd){//传入0 1 2 3 按那种方式更新温度计
		if(zd==0){
			co2Iv.setImageResource(R.drawable.gz_bg12);
		}else if(zd>0&&zd<20){
			co2Iv.setImageResource(R.drawable.gz_bg11);
		}else if(zd>=20&&zd<60){
			co2Iv.setImageResource(R.drawable.gz_bg10);
		}else if(zd>=60&&zd<100){
			co2Iv.setImageResource(R.drawable.gz_bg9);
		}else if(zd>=100&&zd<180){
			co2Iv.setImageResource(R.drawable.gz_bg8);
		}else if(zd>=180&&zd<250){
			co2Iv.setImageResource(R.drawable.gz_bg7);
		}else if(zd>=250&&zd<350){
			co2Iv.setImageResource(R.drawable.gz_bg6);
		}else if(zd>=350&&zd<480){
			co2Iv.setImageResource(R.drawable.gz_bg5);
		}else{
			co2Iv.setImageResource(R.drawable.gz_bg0);
		}
	}
	
	
	//存放list的数据
	private void getDataShowList(float nongdu){
		HashMap<String,String> hmdata = new HashMap<String, String>();
		hmdata.put("left", NowTime.getNowTime());
		hmdata.put("right", nongdu+"");
		data.add(hmdata);
		sAdapter.notifyDataSetChanged();
		co2ListView.setSelection(data.size());
	}
	@Override
	public void onClick(View v) {
		isShow = false;
		this.finish();
	}
	
}
