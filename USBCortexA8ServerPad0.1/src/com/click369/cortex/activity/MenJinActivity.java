
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class MenJinActivity extends Activity implements OnClickListener{
	//���ڵ�ַ  �����  �ŵ�  �͹���ǿ����ʾ
	TextView addressTv,netNumTv,singleTv,failShowTv;
	private ImageView lockIv;
	private TextView lockTv;
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
			case Util.FDDATA:
				parseData((String)msg.obj);
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
	
	private void parseData(String dataStr){
		String datas[] = dataStr.split(" ");
//		int jiedian = HexDump.hexStringToByteArray(datas[4])[0];
//		System.out.println("jiedian = "+jiedian+"  0xB2"+0xB2 +"    "+(jiedian == 0xB2));
		if("B4".equals(datas[4])){
			int zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
			if(zhuangtai == 0){
				lockIv.setImageResource(R.drawable.lock_off);
				lockTv.setText("��");
			}else if(zhuangtai == 1){
				lockIv.setImageResource(R.drawable.lock_on);
				lockTv.setText("��");
			}
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menjin);
        lockIv = (ImageView)this.findViewById(R.id.menjin_zhuangtai_iv);
        lockTv = (TextView)this.findViewById(R.id.menjin_zhuangtai_tv);
        lockIv.setImageResource(R.drawable.lock_off);
		lockTv.setText("��");
    }
    
    @Override
    protected void onResume() {
    	//����Ϊ��activity��handler
    	Util.uiHandler = myHandler;
    	Util.whichBlock ="showdata";
    	super.onResume();
    }
    @Override
	protected void onStop() {
//		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {

	}
}
