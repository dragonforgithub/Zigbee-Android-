
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;



@SuppressLint("HandlerLeak")
public class KaiGuanLiangKongZhiActivity extends Activity implements OnClickListener{
	//‘N’表示电灯停止工作；‘F’表示电灯开始工作。
	private Button btn_dianyuan1, deng1,deng2,deng,chuanglian,luodideng,hongwai,dianfanbao,weibolu,jiashiqi,autowendu,autoshidu,autoguangzhao;
	private ImageView iv_dianyuan1,jiashiqiIv, deng1Iv,deng2Iv,dengIv,chuanglianIv,luodidengIv,dianfanbaoIv,weiboluIv,hongwaiIv;
	private EditText autowenduEt,autoshiduEt,autoguangzhaoEt;

	int dengAddr1 = 0,dengAddr2 = 0;//照明 节点 地址
	int jdqAddr1=0,jdqAddr2=0;//继电器（电源） 节点地址
	/*
	int dwjAddr1 = 0,dwjAddr2 = 0;//电饭煲 微波炉 加湿器
	int cllddAddr1 = 0,cllddAddr2 = 0;//窗帘 落地灯
	*/
	int a = 0,b=0,c = 0,d = 0;
	private boolean isLight1On,isLight2On,isLightAllOn,isDYOn;

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
						 paresData(fds[i]);
						 //System.out.println("1111111111111111fds[i]"+fds[i]);
					 }
				 }else{
					 paresData(res);
				 }
				break;
			}
		}
	};

	private void paresData(String msg){
		 String msgs[] = msg.trim().split(" ");
		 byte []bb = HexDump.hexStringToByteArray(msgs[1]+msgs[4]);
         Util.addr1 = bb[0];//十进制
         Util.addr2 = bb[1];

         /*
         if("A6".equals(msgs[4])){
        	 cllddAddr1 = bb[0];
        	 cllddAddr2 = bb[1];

        	 String clStr = msgs[5];
        	 String lddStr = msgs[6];
        	 String hongwaiStr = msgs[7];
        	 if(HexDump.hexStringToByteArray(clStr)[0] == 'F'){
        		 chuanglianIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 chuanglianIv.setImageResource(R.drawable.design_icon_on);
        	 }
        	 if(HexDump.hexStringToByteArray(lddStr)[0] == 'F'){
        		 luodidengIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 luodidengIv.setImageResource(R.drawable.design_icon_on);
        	 }
        	 if(HexDump.hexStringToByteArray(hongwaiStr)[0] == 'F'){
        		 hongwaiIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 hongwaiIv.setImageResource(R.drawable.design_icon_on);
        	 }
         }else if("A7".equals(msgs[4])){
        	 dwjAddr1 = bb[0];
        	 dwjAddr2 = bb[1];
//        	 String dfbStr = msgs[5];
//        	 String wblStr = msgs[6];
//        	 String jsqStr = msgs[7];
        	 String clStr = msgs[5];
        	 String lddStr = msgs[6];
        	 String hongwaiStr = msgs[7];
        	 if(HexDump.hexStringToByteArray(clStr)[0] == 'F'){
        		 dianfanbaoIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 dianfanbaoIv.setImageResource(R.drawable.design_icon_on);
        	 }
        	 if(HexDump.hexStringToByteArray(lddStr)[0] == 'F'){
        		 weiboluIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 weiboluIv.setImageResource(R.drawable.design_icon_on);
        	 }
        	 if(HexDump.hexStringToByteArray(hongwaiStr)[0] == 'F'){
        		 jiashiqiIv.setImageResource(R.drawable.design_icon_off);
        	 }else{
        		 jiashiqiIv.setImageResource(R.drawable.design_icon_on);
        	 }
         }else
          */
         if("A9".equals(msgs[4])){
        	 //2路 照明灯 状态采集
        	 dengAddr1 = bb[0];
        	 dengAddr2 = bb[1];

        	 String clStr = msgs[5];
        	 String lddStr = msgs[6];

        	 if(HexDump.hexStringToByteArray(clStr)[0] == 'F'){
        		 deng1Iv.setImageResource(R.drawable.design_icon_off);
        		 dengIv.setImageResource(R.drawable.design_icon_off);
        		 isLight1On=false;
        		 deng1.setText("灯1开");
        	 }else{
        		 deng1Iv.setImageResource(R.drawable.design_icon_on);
        		 isLight1On=true;
        		 deng1.setText("灯1关");
        	 }
        	 if(HexDump.hexStringToByteArray(lddStr)[0] == 'F'){
        		 deng2Iv.setImageResource(R.drawable.design_icon_off);
        		 dengIv.setImageResource(R.drawable.design_icon_off);
        		 isLight2On=false;
        		 deng2.setText("灯2开");
        	 }else{
        		 deng2Iv.setImageResource(R.drawable.design_icon_on);

        		 isLight2On=true;
        		 deng2.setText("灯2关");
        	 }

        	 if (clStr.equals("46") && lddStr.equals("46")) {
        		 isLightAllOn=false;
        		 dengIv.setImageResource(R.drawable.design_icon_off);
        		 deng.setText("全开");
			 }
        	 if (clStr.equals("4E") && lddStr.equals("4E")) {
        		 isLightAllOn=true;
        		 dengIv.setImageResource(R.drawable.design_icon_on);
        		 deng.setText("全关");
			 }


         }else if("A8".equals(msgs[4])){
        	 //继电器 采集数据
        	 jdqAddr1 = bb[0];
        	 jdqAddr2 = bb[1];

        	 if(msgs[5].equals("4E")){
        		 iv_dianyuan1.setImageResource(R.drawable.design_icon_on);
        		 btn_dianyuan1.setText("电源关");
        		 isDYOn=true;
        	 }else if(msgs[5].equals("46")){
        		 iv_dianyuan1.setImageResource(R.drawable.design_icon_off);
        		 btn_dianyuan1.setText("电源开");
        		 isDYOn=false;
        	 }

         }

	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaiguanliangkongzhi);
        initView();
//        String urlPath = "lx=jsq&bn="+Util.boxNum;
//        Util.isNeedConn = false;
//		Util.bindMyService(this,urlPath);
//		startGetFD();
    }

//    boolean isGetFD = true;
//    private void startGetFD(){
//    	new Thread(){
//    		public void run(){
//    			while(isGetFD){
//	    			try {
//						String urlPath = "rdf?action=read&lx=jsq&bn="+Util.boxNum;
//						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
//						urlPath = "rdf?action=read&lx=fs&bn="+Util.boxNum;
//						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
//						urlPath = "rdf?action=read&lx=wbl&bn="+Util.boxNum;
//						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
////						urlPath = "rdf?action=read&lx=dd&bn="+Util.boxNum;
////						new Thread(new DownLoadRunnable(myHandler,urlPath,3)).start();
//						Thread.sleep(1000*2);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//    			}
//    		}
//    	}.start();
//    }

    private void initView(){

    	deng1Iv = (ImageView)this.findViewById(R.id.kg_deng_one_iv);
    	deng2Iv = (ImageView)this.findViewById(R.id.kg_deng_two_iv);
    	dengIv = (ImageView)this.findViewById(R.id.kg_deng_all_iv);//所有照明灯
        iv_dianyuan1=(ImageView)this.findViewById(R.id.kg_dianyuan_one_iv);

    	deng1Iv.setTag(0);
    	deng2Iv.setTag(0);
    	iv_dianyuan1.setTag(0);

    	deng1 = (Button)this.findViewById(R.id.kg_deng_one_bt);
    	deng2 = (Button)this.findViewById(R.id.kg_deng_two_bt);
    	deng = (Button)this.findViewById(R.id.kg_deng_all_bt);
    	btn_dianyuan1=(Button)this.findViewById(R.id.kg_dianyuan_one_bt);;

    	deng1.setOnClickListener(this);
    	deng2.setOnClickListener(this);
    	deng.setOnClickListener(this);
    	btn_dianyuan1.setOnClickListener(this);

    	/*

    	deng3Iv = (ImageView)this.findViewById(R.id.kg_deng_three_iv);deng3Iv.setTag(0);

    	chuanglianIv = (ImageView)this.findViewById(R.id.kg_chuanglian_iv);chuanglianIv.setTag(0);
    	luodidengIv = (ImageView)this.findViewById(R.id.kg_luodideng_iv);luodidengIv.setTag(0);
    	hongwaiIv = (ImageView)this.findViewById(R.id.kg_rentihongwai_iv);hongwaiIv.setTag(0);

    	dianfanbaoIv = (ImageView)this.findViewById(R.id.kg_dianfanbao_iv);dianfanbaoIv.setTag(0);
    	weiboluIv = (ImageView)this.findViewById(R.id.kg_wbl_iv);weiboluIv.setTag(0);
    	jiashiqiIv = (ImageView)this.findViewById(R.id.kg_jsq_iv);jiashiqiIv.setTag(0);

    	autowenduEt = (EditText)this.findViewById(R.id.kg_wenduauto_et);//autowenduIv.setTag(0);
    	autoshiduEt = (EditText)this.findViewById(R.id.kg_shiduauto_et);//autoshiduIv.setTag(0);
    	autoguangzhaoEt = (EditText)this.findViewById(R.id.kg_guangzhaoauto_et);//autoguangzhaoIv.setTag(0);

    	jiashiqiIv = (ImageView)this.findViewById(R.id.kg_jsq_iv);jiashiqiIv.setTag(0);
    	deng3 = (Button)this.findViewById(R.id.kg_deng_three_bt);

    	chuanglian = (Button)this.findViewById(R.id.kg_chuanglian_bt);
    	luodideng = (Button)this.findViewById(R.id.kg_luodideng_bt);
    	hongwai = (Button)this.findViewById(R.id.kg_rentihongwai_bt);

    	dianfanbao = (Button)this.findViewById(R.id.kg_dianfanbao_bt);
    	weibolu = (Button)this.findViewById(R.id.kg_wbl_bt);
    	jiashiqi = (Button)this.findViewById(R.id.kg_jsq_bt);

    	autowendu = (Button)this.findViewById(R.id.kg_wenduauto_bt);
    	autoshidu = (Button)this.findViewById(R.id.kg_shiduauto_bt);
    	autoguangzhao = (Button)this.findViewById(R.id.kg_guangzhaoauto_bt);

    	deng3.setOnClickListener(this);
    	chuanglian.setOnClickListener(this);
    	luodideng.setOnClickListener(this);
    	hongwai.setOnClickListener(this);
    	dianfanbao.setOnClickListener(this);
    	weibolu.setOnClickListener(this);
    	jiashiqi.setOnClickListener(this);
    	autowendu.setOnClickListener(this);
    	autoshidu.setOnClickListener(this);
    	autoguangzhao.setOnClickListener(this);
    	jiashiqiIv.setOnClickListener(this);


    	*/

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

		super.onStop();
	}

    @Override
    protected void onDestroy() {
//    	isGetFD = false;
//    	Util.unBindMyService(this);
    	super.onDestroy();
    }

    int addr1 = 0;
	int addr2 = 0;
	//int jidianqi_num1=0;// 继电器 的
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.kg_deng_one_bt:
				addr1 = dengAddr1;
				addr2 = dengAddr2;
				clickBt(0xA9,1,isLight1On);
				break;

			case R.id.kg_deng_two_bt:
				addr1 = dengAddr1;
				addr2 = dengAddr2;
				clickBt(0xA9,2,isLight2On);
				break;
			case R.id.kg_deng_all_bt:
				addr1 = dengAddr1;
				addr2 = dengAddr2;
				clickBt(0xA9,0,isLightAllOn);
				break;

			case R.id.kg_dianyuan_one_bt:
				// 电源控制按钮 1路 继电器控制
				addr1 = jdqAddr1;
				addr2 = jdqAddr2;
				clickBt(0xA8,4,isDYOn);

			  break;

 /*
		 case R.id.kg_chuanglian_bt:
			addr1 = cllddAddr1;
			addr2 = cllddAddr2;
			clickBt(0xA6,0,chuanglianIv);
			break;
		case R.id.kg_luodideng_bt:
			addr1 = cllddAddr1;
			addr2 = cllddAddr2;
			clickBt(0xA6,1,luodidengIv);
			break;
		case R.id.kg_rentihongwai_bt:
			addr1 = cllddAddr1;
			addr2 = cllddAddr2;
			clickBt(0xA6,2,hongwaiIv);
			break;
		case R.id.kg_dianfanbao_bt:
			addr1 = dwjAddr1;
			addr2 = dwjAddr2;
			clickBt(0xA7,0,dianfanbaoIv);
			break;
		case R.id.kg_wbl_bt:
			addr1 = dwjAddr1;
			addr2 = dwjAddr2;
			clickBt(0xA7,1,weiboluIv);
			break;
		case R.id.kg_jsq_bt:
			addr1 = dwjAddr1;
			addr2 = dwjAddr2;
			clickBt(0xA7,2,jiashiqiIv);
			break;

		case R.id.kg_deng_three_bt:
			addr1 = dengAddr1;
			addr2 = dengAddr2;
			clickBt(0xA8,2,deng3Iv);
			break;

		case R.id.kg_wenduauto_bt:
			clickBt(9,autowenduIv);
			clickAutoBt(1);
			break;
		case R.id.kg_shiduauto_bt:
			clickBt(10,autoshiduIv);
			clickAutoBt(2);
			break;
		case R.id.kg_guangzhaoauto_bt:
			clickBt(11,autoguangzhaoIv);
			clickAutoBt(3);
			break;
		case R.id.kg_jiashiqi_iv:
			int data = 0;
			addr1 = jsqAddr1;
			addr2 = jsqAddr2;
			if((Integer)jiashiqiIv.getTag()==0){
				jiashiqiIv.setTag(1);
				jiashiqiIv.setImageResource(R.drawable.design_toggle_on);
				//0xA4
				data = 'F' & 0xff;
			}else{
				jiashiqiIv.setTag(0);
				jiashiqiIv.setImageResource(R.drawable.design_toggle_off);
				data = 'N' & 0xff;
			}
			int datas[] = {addr1,addr2,0xA6,data,0xAA,0xAA,0xAA};
			Util.sendMsgToService(datas,Util.SENDDATATOSERVICE,KaiGuanLiangKongZhiActivity.this);
			break;

			*/

		default:
			break;
		}
	}

	private void clickAutoBt(int num){//自动调节模式的发送方法
		int data = 0;
		if(num==1){
			String s = autowenduEt.getText().toString().trim()+"";
			if(s.length()>0){data = Integer.parseInt(s);}else{data = 20;}
		}else if(num==1){
			String s = autoshiduEt.getText().toString().trim()+"";
			if(s.length()>0){data = Integer.parseInt(s);}else{data = 20;}
		}else if(num==3){
			String s = autoguangzhaoEt.getText().toString().trim()+"";
			if(s.length()>0){data = Integer.parseInt(s);}else{data = 200;}
		}
		int subDatas[] = {0xAA,0xAA,0xAA,0XAA};
		subDatas[0] = num;
		subDatas[1] = data;
		int datas[] = {0xFC,subDatas[0],subDatas[1],subDatas[2],0xAA,0xFC};
//		Util.sendMsgToService(datas,Util.SENDAUTODATATOSERVICE,KaiGuanLiangKongZhiActivity.this);
	}

	private void clickBt(int jiedian,int num,boolean isLightOn){
		int[] datas=new int[7];
		if (num==1) {
			if (isLightOn) {
				datas =new int[] {addr1,addr2,jiedian,0x46,0xAA,0xAA,0xAA};
			}else{
				datas =new int[] {addr1,addr2,jiedian,0x4E,0xAA,0xAA,0xAA};
			}

		}else if(num==2){
			if (isLightOn) {
				datas =new int[] {addr1,addr2,jiedian,0xAA,0x46,0xAA,0xAA};
			}else{
				datas =new int[] {addr1,addr2,jiedian,0xAA,0x4E,0xAA,0xAA};
			}
		}else if(num==0){
			if (isLightOn) {
				datas =new int[] {addr1,addr2,jiedian,0x46,0x46,0xAA,0xAA};
			}else{
				datas =new int[] {addr1,addr2,jiedian,0x4E,0x4E,0xAA,0xAA};
			}
		}else if(num==4){
			if (isDYOn) {
				datas =new int[] {addr1,addr2,jiedian,0x46,0xAA,0xAA,0xAA};
			}else{
				datas =new int[] {addr1,addr2,jiedian,0x4E,0xAA,0xAA,0xAA};
			}
		}

		sendMsgToService(datas,Util.JIAJU);

		/*
		   if((Integer)showiV.getTag()==1){
				showiV.setTag(0);
				showiV.setImageResource(R.drawable.design_icon_off);
				int subDatas[] = {0x02,0x00,0XAA};
				//subDatas[num] = 'F' & 0xff;
				int datas[] = {addr1,addr2,jiedian,subDatas[0],subDatas[1],subDatas[2],0xAA};
				sendMsgToService(datas,Util.JIAJU);
			}else{
				showiV.setTag(1);
				showiV.setImageResource(R.drawable.design_icon_on);
				int subDatas[] = {0x06,0x4E,0x46};
				//subDatas[num] = 'N' & 0xff;
				int datas[] = {addr1,addr2,jiedian,subDatas[0],subDatas[1],subDatas[2],0xAA};
				sendMsgToService(datas,Util.JIAJU);
			}
		    	*/

	}
	//datas的顺序 1、网内地址1 2、网内地址2  3、板子节点  4、资源节点  5、控制信息1  6、控制信息2 7、控制信息3
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
