
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class KongTiaoActivity extends Activity implements OnClickListener{
	private ImageButton kaiguanIb;
	private Button moshiBt,zidongFengBt,wenduAddBt,wenduMinBt,fengxiangBt,fengliangBt,dingshiKaiBt,dingshiGuanBt,kuailengBt,dingshiBt,dingshi_add_bt,dingshi_min_bt,shangxiafeng_bt,zhileng_bt,zhire_bt;
    private TextView kaiguanTv,relengTv,fengxiangTv,fengliangTv,wenduTv,dingshiTv,dingshiText,shangxiafengTv;
    int kongtiao_moshi=0,dingshi_add,dingshi_min,shangxiafeng_num=0;
    
    double time=0;
    int num1=0,num2=0,num3=0,addr1=0,addr2=0;
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
				
				break;
			case Util.FDDATA:
				parseData((String)msg.obj);
				break;
			}
		}
	};
	
	private void parseData(String msg){
		String datas[] = msg.split(" ");
		System.out.println("msg = "+msg);
		if(datas!=null&&datas.length>=10&&datas[4].equals("A2")){
			int kaiguan = HexDump.hexStringToByteArray(datas[5])[0];
			int model = HexDump.hexStringToByteArray(datas[6])[0];
			int wendu = HexDump.hexStringToByteArray(datas[7])[0];
			int fengliang = HexDump.hexStringToByteArray(datas[8])[0];
			int dingshikai = HexDump.hexStringToByteArray(datas[9])[0];
			int dingshiguan = HexDump.hexStringToByteArray(datas[10])[0];
			int h = HexDump.hexStringToByteArray(datas[11])[0];//小时
			int m = HexDump.hexStringToByteArray(datas[12])[0];//分钟
			int shangxiafeng = HexDump.hexStringToByteArray(datas[13])[0];//上下风
			addr1 = HexDump.hexStringToByteArray(datas[17])[0];
			addr2 = HexDump.hexStringToByteArray(datas[18])[0];
			if(kaiguan == 0){
				kaiguanTv.setText("状态：开");
			}else{
				kaiguanTv.setText("状态：关");
			}
			if(model == 0){
				relengTv.setText("模式：自动");
			}else if(model == 1){
				relengTv.setText("模式：制冷");
			}else if(model == 2){
				relengTv.setText("模式：除湿");
			}else if(model == 3){
				relengTv.setText("模式：制热");
			}else if(model == 4){
				relengTv.setText("模式：通风");
			}
			wenduTv.setText("温度："+wendu);			
			if(fengliang == 0){
				fengliangTv.setText("风速：");
			}else if(fengliang == 1){
				fengliangTv.setText("风速：低");
			}else if(fengliang == 2){
				fengliangTv.setText("风速：中");
			}else if(fengliang == 3){
				fengliangTv.setText("风速：高");
			}
			if(dingshikai == 1){
				if(m == 1){
					dingshiTv.setText("定时开："+h+".5h");
				}else{
					dingshiTv.setText("定时开："+h+"h");
				}

			}else{
				 
			}
			if(dingshiguan ==1){
				if(m == 1){
					dingshiTv.setText("定时关："+h+".5h");
				}else{
					dingshiTv.setText("定时关："+h+"h");
				}

			}
			if(dingshikai==0 && dingshiguan==0 ){
				   dingshiTv.setText("定时：");
			}
			
			if(shangxiafeng==0){
				shangxiafengTv.setText("上下风");
			}else if(shangxiafeng==7){
				shangxiafengTv.setText(" ");
			}
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kongtiao);
        
        kaiguanIb = (ImageButton)this.findViewById(R.id.kongtiao_switch_ib);
        moshiBt = (Button)this.findViewById(R.id.kongtiao_moshi_bt);
        kaiguanIb.setOnClickListener(this);
        moshiBt.setOnClickListener(this);
        wenduAddBt = (Button)this.findViewById(R.id.kongtiao_wendu_add_bt);
        wenduMinBt = (Button)this.findViewById(R.id.kongtiao_wendu_min_bt);
        wenduAddBt.setOnClickListener(this);
        wenduMinBt.setOnClickListener(this);
        fengliangBt = (Button)this.findViewById(R.id.kongtiao_fengliang_bt);
        fengliangBt.setOnClickListener(this);
        dingshi_add_bt = (Button) findViewById(id.kongtiao_dingshi_add_bt);
        dingshi_add_bt.setOnClickListener(this);
        dingshi_min_bt = (Button) findViewById(id.kongtiao_dingshi_min_bt);
        dingshi_min_bt.setOnClickListener(this);
  
        kaiguanTv = (TextView)this.findViewById(R.id.kongtiao_kaiguan_tv);
        relengTv = (TextView)this.findViewById(R.id.kongtiao_releng_tv);
        fengliangTv = (TextView)this.findViewById(R.id.kongtiao_fengliang_tv);
        wenduTv = (TextView)this.findViewById(R.id.kongtiao_wendu_tv);
        dingshiTv = (TextView)this.findViewById(R.id.kongtiao_dingshi_tv);
        dingshiText = (TextView) findViewById(id.kongtiao_dingshi_text);
        
        shangxiafengTv = (TextView) findViewById(id.kongtiao_shangxiafeng_tv);
        shangxiafeng_bt = (Button) findViewById(id.kongtiao_shangxiafeng_bt);
        shangxiafeng_bt.setOnClickListener(this);
        
        zhileng_bt = (Button) findViewById(id.kongtiao_zhileng_bt);
        zhileng_bt.setOnClickListener(this);
        zhire_bt = (Button) findViewById(id.kongtiao_zhire_bt);
        zhire_bt.setOnClickListener(this);
        
        shangxiafengTv = (TextView) findViewById(id.kongtiao_shangxiafeng_tv);
    }
    
    @Override
    protected void onResume() {
    	//设置为本activity的handler
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
		int data = 0x01;
		int h = 0xAA;
		int m = 0xAA;
		switch (v.getId()) {
		case R.id.kongtiao_switch_ib:
			data = 0x01;
			break;
		case R.id.kongtiao_moshi_bt:
			data = 0x02;
			break;
		case R.id.kongtiao_wendu_add_bt:
			data = 0x03;
			break;
		case R.id.kongtiao_wendu_min_bt:
			data = 0x04;
			break;
		case R.id.kongtiao_fengliang_bt:
			data = 0x05;
			break;
		case R.id.kongtiao_dingshi_bt:
			if(kongtiao_moshi==3){
				if(!dingshiText.getText().equals("定时")){
					if(num3==0){					
						String ss[]=(time+"").split("\\.");
						if(ss[1].equals("5")){
							m = 0x01;
						}else{
							m=0x00;
						}
						data=0x06;
						h=(int) time;
					}else if(num3==1){
						data=0x06;
						h=0x00;
						m=0x00;
					}else if(num3==2){
						data=0x06;
						h=0x0F;
						m=0x0F;
					}
				}else{
					showMsg(getResources().getString(R.string.please_confirm_time));
				}
			}else if(kongtiao_moshi==1 || kongtiao_moshi==2){
				if(!dingshiText.getText().equals("定时")){
					if(num1==0){					
						String ss[]=(time+"").split(".");
						if(ss[1].equals("5")){
							m = 0x01;
						}else{
							m=0x00;
						}
						data=0x06;
						h=(int) time;
					}else if(num1==1){
						data=0x06;
						h=0x00;
						m=0x00;
					}
				}else{
					showMsg(getResources().getString(R.string.please_confirm_time));
				}
			}			
			break;
			
		case R.id.kongtiao_dingshi_add_bt:
			if(time<24){
				if(time<10){
					time=time+0.5;
				}else{
					time++;
				}
			}else{
				time=0;
			}
			dingshiText.setText("定时"+time+"h");
			break;
		case R.id.kongtiao_dingshi_min_bt:
			if(time>0){
				if(time<10){
					time=time-0.5;
				}else{
					time=time--;
				}
			}else{
				time=0;
			}
			dingshiText.setText("定时"+time+"h");
			break;
		case R.id.kongtiao_shangxiafeng_bt:
            data=0x07;
			break;
		case R.id.kongtiao_zhileng_bt:
			data=0x08;
			break;
		case R.id.kongtiao_zhire_bt:
			data=0x09;
			break;
		}
		int datasrl[] = {addr1,addr2,0xA2,data,h,m,0xAA};
		sendMsgToService(datasrl,Util.JIAJU);
	}
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
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
}
