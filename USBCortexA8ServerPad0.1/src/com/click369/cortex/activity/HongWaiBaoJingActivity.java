
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.click369.cortex.R;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class HongWaiBaoJingActivity extends Activity implements OnClickListener{
	private ImageView oneIv,twoIv,threeIv;
	//���ڵ�ַ  �����  �ŵ�  �͹���ǿ����ʾ
	private TextView addressTv,netNumTv,singleTv,failShowTv;
	private Button bj_btn;
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
//			case Util.SERVICEDOWNTHREADSTOP://��̨�߳�ֹͣ  ֪ͨ�û����������߳�
//				failShowTv.setText(Util.NOSERVERRESTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
//			case Util.NONETWORK://��̨�߳�ֹͣ  ֪ͨ�û����������߳�
//				failShowTv.setText(Util.NONETWORKSTR);
//				failShowTv.setVisibility(View.VISIBLE);
//				break;
			case Util.FDDATA:
				 String res = ((String)msg.obj).trim();
				 String msgs[] = res.trim().split(" ");
				if(msgs[4].equals("B1") && msgs[0].equals("FD")){
					parseData((String)msg.obj);
					System.out.println((String)msg.obj);
				}
				
				break;
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
	//���ⱨ��һ�δ�����������
	private void parseData(String dataStr){
		dataStr = dataStr.trim();
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				
				setBJState(datas);
				 
			}
		}else{
			String datas[] = dataStr.split(" ");
			
			setBJState(datas);
			
		}
		
	}
	
	private void setBJState(String[] datas) {
		// TODO Auto-generated method stub
		int zhuangtai = HexDump.hexStringToByteArray(datas[5])[0];
		if("B1".equals(datas[4])){
			changeLight(oneIv,zhuangtai);
			
		}
//		else if("B2".equals(datas[4])){
//			changeLight(twoIv,zhuangtai);
//		}else if("B3".equals(datas[4])){
//			changeLight(threeIv,zhuangtai);
//		}
		
	}
	
	private void changeLight(ImageView iv,int zhuangtai){
	   // 4E-78-N  46-70-F
		if(zhuangtai == 78){
			iv.setImageResource(R.drawable.design_icon_on);
			bj_btn.setText("   ����   ");
		}else {
			iv.setImageResource(R.drawable.design_icon_off);
			bj_btn.setText("   ����   ");
		}
		
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hongwaibaojing);
        oneIv = (ImageView)this.findViewById(R.id.bj_iv1);
        bj_btn=(Button)this.findViewById(R.id.bj_btn);
        
    }
    
    @Override
    protected void onResume() {
    	//����Ϊ��activity��handler
    	Util.uiHandler = myHandler;
    	Util.whichBlock = "showdata";
    	super.onResume();
    }
    @Override
	protected void onStop() {
//		Util.unBindMyService(this);
		super.onStop();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

}
