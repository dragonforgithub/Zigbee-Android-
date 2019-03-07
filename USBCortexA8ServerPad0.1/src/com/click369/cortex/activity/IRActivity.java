
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;



@SuppressLint("HandlerLeak")
public class IRActivity extends Activity implements OnClickListener{
	//��N����ʾ���ֹͣ��������F����ʾ��ƿ�ʼ������
	private Button btn_ir1, btn_ir2,btn_ir3;
	
	int IRAddr1 = 0x0A,IRAddr2 = 0xA5;//���� �ڵ� ��ַ 
 
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			 
				case Util.FDDATA:
					 String res = ((String)msg.obj).trim();
					 
					 if(res.contains("\r\n")){
						 String fds[] = res.split("\r\n");
						 for(int i = 0;i<fds.length;i++){
							 paresData(fds[i]);
						 }
					 }else{
						 paresData(res);
					 }
					break;
				default :
						break;
			}
		}
	};
	
	private void paresData(String msg){
		 String msgs[] = msg.trim().split(" ");
		 byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
         Util.addr1 = bb[0];//ʮ����
         Util.addr2 = bb[1];
          
         if("A5".equals(msgs[4])){
        	 //���� ״̬�ɼ� 46 53 4E
        	 IRAddr1 = bb[0];
        	 IRAddr2 = bb[1];
        	 /*
        	 if(msgs[5].equals("4E")){
        		 iv_dianyuan1.setImageResource(R.drawable.design_icon_on);
        		 btn_dianyuan1.setText("��Դ��");
        		 isDYOn=true;
        	 }else if(msgs[5].equals("46")){
        		 iv_dianyuan1.setImageResource(R.drawable.design_icon_off);
        		 btn_dianyuan1.setText("��Դ��");
        		 isDYOn=false;
        	 }
        	  */
         }
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircontrol);
        initView();

    }
  
    private void initView(){
    	 
    	btn_ir1 = (Button)this.findViewById(R.id.ir_btn_1);
    	btn_ir2 = (Button)this.findViewById(R.id.ir_btn_2);
    	btn_ir3 = (Button)this.findViewById(R.id.ir_btn_3);
    	 
    	btn_ir1.setOnClickListener(this);
    	btn_ir2.setOnClickListener(this);
    	btn_ir3.setOnClickListener(this);
    	  
    }
    
    @Override
    protected void onResume() { 
    	Util.uiHandler = myHandler;
    	Util.whichBlock = "showdata";
    	super.onResume();
    }
    
    @Override
	protected void onStop() { 
		super.onStop();
	}
    
    @Override
    protected void onDestroy() { 
    	super.onDestroy();
    }
    
	@Override
	public void onClick(View v) {
		 
		int msg=0xAA;
		switch (v.getId()) {
		 	
			case R.id.ir_btn_1:
				 
			    msg=0x4E;
				break;
				
			case R.id.ir_btn_2:
				msg=0x46;
				break;
			case R.id.ir_btn_3:
				msg=0x53;
				break;
			 
			default:
				break;
		}
		
		int[] datas = {IRAddr1,IRAddr2,0xA5,msg,0xAA,0xAA,0xAA};
		sendMsgToService(datas,Util.JIAJU);
		
	} 
	//datas��˳�� 1�����ڵ�ַ1 2�����ڵ�ַ2  3�����ӽڵ�  4����Դ�ڵ�  5��������Ϣ1  6��������Ϣ2 7��������Ϣ3
    private void sendMsgToService(int datas[],int what){
			if(MainZigBeeService.myHandler!=null){
					Message msg = Message.obtain();
					msg.what = what;
					msg.obj = datas;
					MainZigBeeService.myHandler.sendMessage(msg);
			}else{
				showMsg(getResources().getString(R.string.service_not_start));
			}
	}
    public void showMsg(String text){
 		Toast.makeText(this, text,200).show();
 	}
}
