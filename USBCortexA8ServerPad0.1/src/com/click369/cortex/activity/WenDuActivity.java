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
	//list������
	SimpleAdapter sAdapter;
	//����ͼ����
	AChartView acd;
	int left,right,top,bottom;
	int count = 0;//��������ǰ���β�ƽ�ȵ��¶�ֵ
	double wendu = -100.0;
	double currtDoubleWenDu =0 ;
	
	public static final int GETLRTB = 0;//�¶����ӵ�ĳ�ʼֵ
	public static final int INIT = 1;//��ʼ
	public static final int ADD = 2;//����
	public static final int MINU = 3;//����
	@SuppressLint("HandlerLeak")
	Handler wdHandler = new Handler(){//�����¶ȼ�
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GETLRTB://�¶����ӵ�ĳ�ʼֵ
				left = wenduAddFL.getLeft();
				right = wenduAddFL.getRight();
				top = wenduAddFL.getTop();
				bottom = wenduAddFL.getBottom();
				break;
			case INIT://��ʼ
				if(top-msg.arg1*0.25>33){
					wenduAddFL.layout(left, (int)(Math.round(top-msg.arg1*0.25)), right,bottom);
				}
				break;
			case ADD://����
				if(top-msg.arg1*0.25>33){
					wenduAddFL.layout(left, (int)(Math.round(top-msg.arg1*0.25)), right,bottom);
				}
				break;
			}
		};
	};
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler(){//���պ�̨��������
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
	//�������ͼ����
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
		wenDuTv.setText("��ǰ�¶ȣ�0.0��");
		String from[] = {"left","right"};
		int to[] = {R.id.item_textView1,R.id.item_textView2};
		sAdapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);
		wenDuListView.setAdapter(sAdapter);
		//��ʼ������ͼ
		acd = new AChartView(this,wenduLinell,"�¶�����ͼ","ʱ�� S","�¶� ��",0,300,-20,50);
		acd.updateChart(0);
		currtDoubleWenDu = 25.0;
		
		//test();
		super.onCreate(savedInstanceState);
	}
	
	@Override
    protected void onResume() {
    	//����Ϊ��activity��handler
		Util.uiHandler = myHandler;
        Util.whichBlock = "09";//��ʪ�Ȱ��ӽڵ��ַ01
        count = 0;
    	super.onResume();
    	
    }
	 
	private void parseWenDu(String dataStr){
		try {
				String datas[] = dataStr.split(" ");
					//�±�Ϊ5�Ķ�Ӧ�����¶ȵ�����λ �±�Ϊ6�Ķ�Ӧ���¶ȵ�С��λ
					byte wenduByte[] = HexDump.hexStringToByteArray(datas[5]+datas[6]);
					String wenduStr = wenduByte[0]+"."+wenduByte[1];
					
					if(wendu==-100){//�ж��Ƿ�Ϊ��һ��
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
					//�����ı���Ϣ
					wenDuTv.setText("��ǰ�¶ȣ�"+wd+"��");
					//��������ͼ
					acd.updateChart(wendu);
					//����list
					getDataShowList(wd);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startChangeWd(final int mode,final double addWenDu){//����0 1 2 3 �����ַ�ʽ�����¶ȼ�
			new Thread(){
				public void run(){
					try {
						//����ͬ����ֹͬʱ�̿��������߳�
						synchronized (ACCESSIBILITY_SERVICE) {
							sendMsg(GETLRTB,0);
							if(addWenDu>0){
								//�¶������Ľ�������ͼ����
								for(int i = 1;i<=(int)(addWenDu*10);i++){
									Thread.sleep(10);
									sendMsg(mode,i);
								}
							}else{
								//�¶��½��Ľ�������ͼ����
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
	
	//���list������
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
