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
    
	boolean isShow = true;
	private ImageView qt_iv1;
	Handler myHandler = new Handler(){//���պ�̨��������
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.FDDATA:
				String datas[] = (msg.obj.toString()).split(" ");
				if(datas[4].equals("B1")){
					parseData(msg.obj.toString());
				}
				
				break;
//			case Util.SERVICEDOWNTHREADSTOP://��̨�߳�ֹͣ  ֪ͨ�û����������߳�
//				failShowTv.setText(Util.NOSERVERRESTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NONETWORK://��̨�߳�ֹͣ  ֪ͨ�û����������߳�
//				failShowTv.setText(Util.NONETWORKSTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NETADRR:
//				addressTv.setText("���ڵ�ַ��"+msg.obj);
//				break;
//			case Util.NETNUM:
//				netNumTv.setText("����ţ�"+msg.obj);
//				break;
//			case Util.SINGLENUM:
//				singleTv.setText("�ŵ��ţ�"+msg.obj);
//				break;
			}
		}
	};
	
	//������Ȼ������
	private void parseData(String dataStr){
		 try {
			 dataStr = dataStr.trim();
				int zhuangtai=70;
				if(dataStr.contains("\r\n")){
					String rootdatas[] = dataStr.split("\r\n");
					int length = rootdatas.length;
					for(int i = 0;i<length;i++){
						String datas[] = rootdatas[i].split(" ");
						zhuangtai = HexDump.hexStringToByteArray(datas[7])[0];
						if(zhuangtai==78){
							qt_iv1.setImageResource(R.drawable.design_icon_on);
						}else if(zhuangtai==70){
							qt_iv1.setImageResource(R.drawable.design_icon_off);
						}
					}
				}else{
					String datas[] = dataStr.split(" ");
					zhuangtai = HexDump.hexStringToByteArray(datas[7])[0];
					if(zhuangtai==78){
						qt_iv1.setImageResource(R.drawable.design_icon_on);
					}else if(zhuangtai==70){
						qt_iv1.setImageResource(R.drawable.design_icon_off);
					}
				}				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_qt);
		
//		failShowTv = (TextView)this.findViewById(R.id.qt_urlfail_tv);
//		failShowTv.setVisibility(View.GONE);
//		failShowTv.setOnClickListener(this);
//		
		qiTiTitle = (TextView)this.findViewById(R.id.qiti_title);
//		showNdTv = (TextView)this.findViewById(R.id.qt_nd_tv);
//		addressTv = (TextView)this.findViewById(R.id.qt_addr_tv);
//		netNumTv = (TextView)this.findViewById(R.id.qt_netNum_tv);
//		singleTv = (TextView)this.findViewById(R.id.qt_singelNum_tv);		

		qt_iv1 = (ImageView) findViewById(id.qt_iv1);
	/*	
		String urlPath = "downLoad?leixing=eyht&bn="+Util.boxNum;
		String title ="������̼";
		if(this.getIntent().hasExtra("form")){
			title = (String)this.getIntent().getCharSequenceExtra("form");
			qiTiTitle.setText(title+"Ũ��");
			if(title.equals("��Ȼ��")){
				urlPath = "lx=trq&bn="+Util.boxNum;
				urlPath = "downLoad?leixing=trq&name="+Util.boxNum;
			}else if(title.equals("����")){
				urlPath = "lx=yq&bn="+Util.boxNum;
				urlPath = "downLoad?leixing=yq&name="+Util.boxNum;
			}else if(title.equals("������̼")){
				urlPath = "lx=eyht&bn="+Util.boxNum;
				urlPath = "downLoad?leixing=eyht&name="+Util.boxNum;
			}else if(title.equals("����")){
				urlPath = "lx=yw&bn="+Util.boxNum;
				urlPath = "downLoad?leixing=yw&name="+Util.boxNum;
			}
			Util.isNeedConn = true;
		}		
		
		Util.bindMyService(this,urlPath);
		*/
		super.onCreate(savedInstanceState);
	}
	
	@Override
    protected void onResume() {
    	//����Ϊ��activity��handler
		Util.uiHandler = myHandler;
        Util.whichBlock = "showdata";//��Ȼ�����ӽڵ�
    	super.onResume();
    }
	
	@Override
	protected void onPause() {
		isShow = false;
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		//Util.unBindMyService(this);///
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
//		case R.id.qt_urlfail_tv:
//			failShowTv.setVisibility(View.GONE);
//			//�������������߳�
//			MainWifiService.myHandler.sendEmptyMessage(Util.RESTART);
//			break;
//		default:
//			break;
		}
	}
	
}
