package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
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

public class WenDuActivity extends Activity implements OnClickListener{
	ListView wenDuListView;
	//	TextView addressTv,netNumTv,singleTv,wenDuTv;
	TextView wenDuTv;
	FrameLayout wenduAddFL;
	LinearLayout wenduLinell;
	ImageView wenDuIv;
	Button back;
	//list适配器
	SimpleAdapter sAdapter;
	//曲线图对象
	AChartView acd;
	int left,right,top,bottom;
	int count = 0;//用来跳过前几次不平稳的温度值
	double wendu = -100.0;
	double currtDoubleWenDu =0 ;

	public static final int GETLRTB = 0;//温度增加点的初始值
	public static final int INIT = 1;//初始
	public static final int ADD = 2;//增加
	public static final int MINU = 3;//减少
	@SuppressLint("HandlerLeak")
	Handler wdHandler = new Handler(){//更新温度计
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GETLRTB://温度增加点的初始值
					left = wenduAddFL.getLeft();
					right = wenduAddFL.getRight();
					top = wenduAddFL.getTop();
					bottom = wenduAddFL.getBottom();
					break;
				case INIT://初始
					if(top-msg.arg1*0.25>33){
						wenduAddFL.layout(left, (int)(Math.round(top-msg.arg1*0.25)), right,bottom);
					}
					break;
				case ADD://增加
					if(top-msg.arg1*0.25>33){
						wenduAddFL.layout(left, (int)(Math.round(top-msg.arg1*0.25)), right,bottom);
					}
					break;
			}
		};
	};
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.FDDATA:
					count++;
					if(msg.obj!=null&&count>5){
						parseWenDu((String)msg.obj);
					}
					break;

				default :
					break;


			}
		}
	};
	//存放曲线图数据
	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wendu);
		back = (Button)this.findViewById(R.id.wendu_back);
		back.setOnClickListener(this);
		wenduAddFL = (FrameLayout)this.findViewById(R.id.wendu_add_fl);
		wenduLinell = (LinearLayout)this.findViewById(R.id.wendu_line_ll);
		wenDuIv = (ImageView)this.findViewById(R.id.wendu_imageView);
		wenDuTv = (TextView)this.findViewById(R.id.wendu_textView);
//		addressTv = (TextView)this.findViewById(R.id.wendu_addr_tv);
//		netNumTv = (TextView)this.findViewById(R.id.wendu_netNum_tv);
//		singleTv = (TextView)this.findViewById(R.id.wendu_singelNum_tv);
		wenDuListView = (ListView)this.findViewById(R.id.wendu_listView1);
		wenDuTv.setText("当前温度：0.0℃");
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);
		wenDuListView.setAdapter(sAdapter);
		//初始化曲线图
		acd = new AChartView(this,wenduLinell,"温度走势图","时间 S","温度 ℃",0,300,-20,50);
		acd.updateChart(0);
		currtDoubleWenDu = 25.0;

		//test();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "09";//温湿度板子节点地址01
		count = 0;
		super.onResume();

	}

	private void parseWenDu(String dataStr){
		try {
			String datas[] = dataStr.split(" ");
			//下标为5的对应的是温度的整数位 下标为6的对应的温度的小数位
			byte wenduByte[] = HexDump.hexStringToByteArray(datas[5]+datas[6]);
			String wenduStr = wenduByte[0]+"."+wenduByte[1];

			if(wendu==-100){//判断是否为第一次
				wendu = Double.parseDouble(wenduStr)-20;
				currtDoubleWenDu = wendu+10;
				startChangeWd(INIT,currtDoubleWenDu);
			}else{
				wendu = Double.parseDouble(wenduStr)-20;
				if((wendu+10)-currtDoubleWenDu!=0){
					startChangeWd(ADD,(wendu+10)-currtDoubleWenDu);
					currtDoubleWenDu = wendu+10;
				}
			}

			String wenduStrNew=(wendu+"").substring(0, 4);
			double wd=Double.parseDouble(wenduStrNew);
			//设置文本信息
			wenDuTv.setText("当前温度："+wd+"℃");
			//更新曲线图
			acd.updateChart(wendu);
			//更新list
			getDataShowList(wd);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startChangeWd(final int mode,final double addWenDu){//传入0 1 2 3 按那种方式更新温度计
		new Thread(){
			public void run(){
				try {
					//加上同步防止同时刻开启多条线程
					synchronized (ACCESSIBILITY_SERVICE) {
						sendMsg(GETLRTB,0);
						if(addWenDu>0){
							//温度上升的界面柱形图处理
							for(int i = 1;i<=(int)(addWenDu*10);i++){
								Thread.sleep(10);
								sendMsg(mode,i);
							}
						}else{
							//温度下降的界面柱形图处理
							for(int i = -1;i>(int)(addWenDu*10);i--){
								Thread.sleep(10);
								sendMsg(mode,i);
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendMsg(int what,int arg1){
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		if(wdHandler!=null){
			wdHandler.sendMessage(msg);
		}
	}

	//存放list的数据
	private void getDataShowList(double wendu){
		HashMap<String,String> hmdata = new HashMap<String, String>();
		hmdata.put("left", NowTime.getNowTime());
		hmdata.put("right", wendu+"");
		data.add(hmdata);
		sAdapter.notifyDataSetChanged();
		wenDuListView.setSelection(data.size());
	}
	@Override
	public void onClick(View v) {
//		startChangeWd(ADD,3);
		this.finish();
	}

}
