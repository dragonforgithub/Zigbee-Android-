package com.click369.cortex.activity;

import com.click369.cortex.R;
import com.click369.cortex.R.id;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.service.ShengGuangService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ShengGuangActivity extends Activity implements OnClickListener{
	Button baojing;
	ImageView baojing_img;
	int addr1=0,addr2=0;
	int renti=0,tianranqi=0;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Util.ALLDATA:

					break;
				case Util.FDDATA:
					String datas[] = (msg.obj.toString()).split(" ");
					if(datas[4].equals("B1")){
						parseData(msg.obj.toString());
					}
					break;
			}
		}
	};

	private void parseData(String dataStr){
		dataStr = dataStr.trim();
		int zhuangtai=0;
		if(dataStr.contains("\r\n")){
			String rootdatas[] = dataStr.split("\r\n");
			int length = rootdatas.length;
			for(int i = 0;i<length;i++){
				String datas[] = rootdatas[i].split(" ");
				int gao = HexDump.hexStringToByteArray(datas[5])[0];
				int di = HexDump.hexStringToByteArray(datas[7])[0];
				int a1 = HexDump.hexStringToByteArray(datas[17])[0];
				int a2 = HexDump.hexStringToByteArray(datas[18])[0];
				if(a1<0){
					addr1=a1+256;
				}else{
					addr1=a1;
				}
				if(a2<0){
					addr2=a2+256;
				}else{
					addr2=a2 ;
				}
				renti =gao;
//					tianranqi = di; 

				if(renti==78 && ShengGuangService.isstart ){
					baojing_img.setBackgroundResource(R.drawable.design_icon_on);
				}else if(renti==70){
					baojing_img.setBackgroundResource(R.drawable.design_icon_off);
				}

			}
		}else{
			String datas[] = dataStr.split(" ");
			int gao = HexDump.hexStringToByteArray(datas[5])[0];
			int di = HexDump.hexStringToByteArray(datas[7])[0];
			int a1 = HexDump.hexStringToByteArray(datas[17])[0];
			int a2 = HexDump.hexStringToByteArray(datas[18])[0];
			if(a1<0){
				addr1=a1+256;
			}else{
				addr1=a1;
			}
			if(a2<0){
				addr2=a2+256;
			}else{
				addr2=a2 ;
			}
			renti =gao;
//				tianranqi = di; 
			System.out.println("ShengGuangService.isstart="+ShengGuangService.isstart);
			System.out.println("人体红外。。。。="+renti);
			if(renti==78 && ShengGuangService.isstart ){
				baojing_img.setBackgroundResource(R.drawable.design_icon_on);
			}else if(renti==70){
				baojing_img.setBackgroundResource(R.drawable.design_icon_off);
			}
		}

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shengguang);
		baojing_img = (ImageView) findViewById(id.baojing_img);
		baojing = (Button) findViewById(id.baojing);
		baojing.setOnClickListener(this);
		if(ShengGuangService.isstart){
			baojing.setText("已打开声光报警");
		}else{
			baojing.setText("已关闭声光报警");
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.baojing:
				if(!ShengGuangService.isstart){
					Intent shengguangService  = new Intent(this,ShengGuangService.class);
					this.startService(shengguangService);
					baojing.setText("已打开声光报警");
					System.out.println("已打开声光报警");

				}else{
					Intent shengguangService  = new Intent(this,ShengGuangService.class);
					this.stopService(shengguangService);
					baojing.setText("已关闭开声光报警");
					Util.sgHandler = myHandler;
					Util.sgwhichBlock = "";
					int data[] = {addr1,addr2,0xB1,0xAA,0xAA,0xAA,0x46};
					sendMsgToService(data,Util.JIAJU);
				}

				break;
		}
	}
	@Override
	protected void onResume() {
		//设置为本activity的handler
		Util.uiHandler = myHandler;
		Util.whichBlock = "showdata";
		super.onResume();
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
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

}
