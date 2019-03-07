
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class DianJiActivity extends Activity implements OnClickListener{

	private Button dj_jl_forward_bt; 
	private Button dj_jl_reverse_bt;
	private Button dj_jl_stop_bt;

	private Button dj_zl_forward_bt;
	private Button dj_zl_reverse_bt;
	private Button dj_zl_stop_bt;
	private Button dj_zl_jiasu_bt;
	private Button dj_zl_jiansu_bt;
	
	private ImageView dj_jl_iv;
	private ImageView dj_zl_iv;
	//private TextView dj_zl_zkb;
	
	//����������� ���ڵ�ַ
	private int cl1Addr1 = 0;
	private int cl1Addr2 = 0;
	//ֱ����� ���ڵ�ַ
	private int zldjAddr1=0;
	private int zldjAddr2=0;
	
	private Animation animation_RotateZheng;
	private Animation animation_RotateFan;
	private boolean isFirstZheng=true;
	private boolean isFirstFan=true;
	private boolean isFirstJlZheng=true;
	private boolean isFirstJlFan=true;
	
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
			case Util.FDDATA:
				 
				 String res = ((String)msg.obj).trim();
				 
				 if(res.contains("\r\n")){
					 String fds[] = res.split("\r\n");
					 for(int i = 0;i<fds.length;i++){
						 parseData(fds[i]);
					 }
				 }else{
					 parseData(res);
				 }
				 
				break;
				
			default :
					break;
			}
		}
	};
	
	private void parseData(String msg){
		
		 String msgs[] = msg.trim().split(" ");
		 byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);//�������ڵ�ַΪ00 00
         Util.addr1 = bb[0];//ʮ����
         Util.addr2 = bb[1];
         //������� A3-lx=cl1
         if("A3".equals(msgs[4])){
        	 
        	 cl1Addr1 = bb[0];
        	 cl1Addr2 = bb[1];
        	 String mode=msgs[5]; 
        	 if(mode.equals("4E")){
        		 
        		 if (isFirstJlZheng) {
        		 	 dj_jl_iv.startAnimation(animation_RotateZheng);
					 isFirstJlZheng=false;
					 isFirstJlFan=true;
				 }
				 
			 }
			 if(mode.equals("46")){ 
				  
				 if (isFirstJlFan) {
					 dj_jl_iv.startAnimation(animation_RotateFan);
					 isFirstJlFan=false;
					 isFirstJlZheng=true;
				 }
				 
			 }
			 if(mode.equals("53")){
				 dj_jl_iv.clearAnimation();
				 isFirstJlFan=true;
				 isFirstJlZheng=true;
			 }
        	  
        	 
         }
         //ֱ����� ���� �ڵ� ��ַ��A4��-lx=cl2
         if("A4".equals(msgs[4])){ 
        	 zldjAddr1 = bb[0];
        	 zldjAddr2 = bb[1]; 
        	 String mode=msgs[5]; 
        	 
			 if(mode.equals("4E")){
				 if (isFirstZheng) {
					 dj_zl_iv.startAnimation(animation_RotateZheng);
					 isFirstZheng=false;
					 isFirstFan=true;
				 }
				 
			 }
			 if(mode.equals("46")){ 
				 if (isFirstFan) {
					 dj_zl_iv.startAnimation(animation_RotateFan);
					 isFirstFan=false;
					 isFirstZheng=true;
				 }
				 
			 }
			 if(mode.equals("53")){
				 dj_zl_iv.clearAnimation();
				 isFirstZheng=true;
				 isFirstFan=true;
			 }
			 
         }
         
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiaozhiliu);
        initAnimation();
        initView();
 	    
    }
    
    private void initAnimation() {
		// TODO Auto-generated method stub
    	
    	animation_RotateZheng=new RotateAnimation(0.0f, +359.9f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation_RotateZheng.setRepeatCount(-1);
		animation_RotateZheng.setDuration(3000);
		
		animation_RotateFan=new RotateAnimation(0.0f, -359.9f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation_RotateFan.setRepeatCount(-1);
		animation_RotateFan.setDuration(3000);

		LinearInterpolator lir = new LinearInterpolator();    
		animation_RotateZheng.setInterpolator(lir); 
		animation_RotateFan.setInterpolator(lir); 
		
	}
	private void initView(){
    	
    	dj_jl_iv = (ImageView)this.findViewById(R.id.dj_jl_iv);
    	dj_zl_iv = (ImageView)this.findViewById(R.id.dj_zl_iv);
    	
    	dj_jl_forward_bt = (Button)this.findViewById(R.id.dj_jl_forward_bt);
    	dj_zl_forward_bt = (Button)this.findViewById(R.id.dj_zl_forward_bt);
    	dj_jl_reverse_bt = (Button)this.findViewById(R.id.dj_jl_reverse_bt);
    	dj_zl_reverse_bt = (Button)this.findViewById(R.id.dj_zl_reverse_bt);
    	dj_jl_stop_bt = (Button)this.findViewById(R.id.dj_jl_stop_bt);
    	dj_zl_stop_bt = (Button)this.findViewById(R.id.dj_zl_stop_bt);
    	dj_zl_jiasu_bt = (Button)this.findViewById(R.id.dj_zl_jiasu_bt);
    	dj_zl_jiansu_bt = (Button)this.findViewById(R.id.dj_zl_jiansu_bt);
    	
    	dj_jl_reverse_bt.setOnClickListener(this);
    	dj_jl_forward_bt.setOnClickListener(this);
    	dj_jl_stop_bt.setOnClickListener(this);
    	
    	dj_zl_reverse_bt.setOnClickListener(this);
    	dj_zl_forward_bt.setOnClickListener(this);
    	dj_zl_stop_bt.setOnClickListener(this);
    	dj_zl_jiasu_bt.setOnClickListener(this);
    	dj_zl_jiansu_bt.setOnClickListener(this);
    	
	}
    
    @Override
    protected void onResume() {
    	//����Ϊ��activity��handler
    	Util.uiHandler = myHandler;
    	Util.whichBlock="showdata";//Ϊ����һ��ҳ��ʵ�ֶ�� ���ӿ�����ʾ����
    	
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
		
		int jieidna = 0xA4;
		//int sourceNo=0x01;//��Դ��� ������� 01 ֱ����� 03
		int data = 'S' & 0xff;//���״̬ 
		int addr1 = 0;
		int addr2 = 0;
		
		switch (v.getId()) {
		    //����������� ����
			case R.id.dj_jl_forward_bt:
				
				jieidna = 0xA3;
				data = 0x4E;
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				break;
			case R.id.dj_jl_reverse_bt:
				 
				jieidna = 0xA3;
				data= 0x46;
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				break;
			case R.id.dj_jl_stop_bt:
				 
				jieidna = 0xA3;
				data = 'S' & 0xff; 
				addr1 = cl1Addr1;
				addr2 = cl1Addr2;
				break;
				
			//ֱ���������	
			case R.id.dj_zl_forward_bt:
				 
				jieidna = 0xA4;
				data = 'N' & 0xff; 
				addr1 = zldjAddr1;
				addr2 = zldjAddr2;
				 
				break;
			case R.id.dj_zl_reverse_bt:
				 
				jieidna = 0xA4;
				data = 'F' & 0xff;
				addr1 = zldjAddr1;
				addr2 = zldjAddr2;
				
				break;
			case R.id.dj_zl_stop_bt:
				 
				jieidna = 0xA4;
				data= 'S' & 0xff;
				addr1 = zldjAddr1;
				addr2 = zldjAddr2;
				
				break;
			case R.id.dj_zl_jiasu_bt:
				
				//����  +ռ�ձ�
				jieidna = 0xA4;
				data= '+' & 0xff; 
				addr1 = zldjAddr1;
				addr2 = zldjAddr2;
				
				break;
			case R.id.dj_zl_jiansu_bt:
				//����  -ռ�ձ�
				jieidna = 0xA4; 
				data= '-' & 0xff; 
				addr1 = zldjAddr1;
				addr2 = zldjAddr2;
				
				break;
			 
			default:
				break;
			
		}
		 
		int[] datasrl =new int[]{addr1,addr2,jieidna,data,0xAA,0xAA,0xAA};
		sendMsgToService(datasrl,Util.CHUANGLIAN);
		
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
