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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.AChartView;
import com.click369.cortex.util.NowTime;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

public class WenDuActivity extends Activity implements OnClickListener{
	ListView wenDuListView;
	ProgressBar wdPb;
	TextView addressTv,netNumTv,singleTv,wenDuTv,failShowTv;
	FrameLayout wenduAddFL;
	LinearLayout wenduLinell;
	ImageView wenDuIv;
	//list适配器
	SimpleAdapter sAdapter;
	//曲线图对象
	AChartView acd;
	int left,right,top,bottom;
	//	int count = 0;//用来跳过前几次不平稳的温度值
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

					break;
				case ADD://增加

					break;
				default :
					break;
			}
		};
	};

	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler(){//接收后台服务数据
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case Util.FDDATA:
					parseWenDu((String)msg.obj);
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
	//存放曲线图数据
	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_wendu);

		wdPb = (ProgressBar)this.findViewById(R.id.pb);
		wdPb.setMax(60);
		wenduLinell = (LinearLayout)this.findViewById(R.id.wendu_line_ll);
		wenDuIv = (ImageView)this.findViewById(R.id.wendu_imageView);

		failShowTv = (TextView)this.findViewById(R.id.wendu_urlfail_tv);
		failShowTv.setVisibility(View.GONE);
		failShowTv.setOnClickListener(this);

		wenDuTv = (TextView)this.findViewById(R.id.wendu_textView);
		addressTv = (TextView)this.findViewById(R.id.wendu_addr_tv);
		netNumTv = (TextView)this.findViewById(R.id.wendu_netNum_tv);
		singleTv = (TextView)this.findViewById(R.id.wendu_singelNum_tv);
		wenDuListView = (ListView)this.findViewById(R.id.wendu_listView1);
		wenDuTv.setText("当前温度：0.0℃");
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.datalist_item, from, to);
		wenDuListView.setAdapter(sAdapter);
		//初始化曲线图
		acd = new AChartView(this,wenduLinell,"温度走势图","时间 S","温度 ℃",0,300,-10,50);
		acd.updateChart(0);
		currtDoubleWenDu = 25.0+5;

//		String urlPath = "downLoad?leixing=wd&bn="+Util.boxNum;
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
	protected void onStop() {
		Util.unBindMyService(this);
		super.onStop();
	}

	private void parseWenDu(String dataStr){
		try {
			String datas[] = dataStr.split(" ");
			//下标为5的对应的是温度的整数位 下标为6的对应的温度的小数位
			byte wenduByte[] = HexDump.hexStringToByteArray(datas[5]+datas[6]);
			String wenduStr = wenduByte[0]+"."+wenduByte[1];
			if(wendu==-100){//判断是否为第一次
				wendu = Double.parseDouble(wenduStr);
				currtDoubleWenDu = wendu+10+5;
//						startChangeWd(INIT,currtDoubleWenDu);
			}else{
				wendu = Double.parseDouble(wenduStr)-20;
				if((wendu+10)-currtDoubleWenDu!=0){
//							startChangeWd(ADD,(wendu+10)-currtDoubleWenDu);
					currtDoubleWenDu = wendu+10+5;
				}
			}
			wdPb.setProgress((int)currtDoubleWenDu);

			String wenduStrNew=(wendu+"").substring(0, 4);
			double wd=Double.parseDouble(wenduStrNew);
			//设置文本信息
			wenDuTv.setText("当前温度："+wd+"℃");
			//更新曲线图
			acd.updateChart(wd);
			//更新list
			getDataShowList(wd);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		switch (v.getId()) {
			case R.id.wendu_urlfail_tv:
				failShowTv.setVisibility(View.GONE);
				//重新启动网络线程
				MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
//		if(t!=null&&t.isAlive()){
//			t.interrupt();
//		}
		this.finish();
		super.onBackPressed();
	}
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
