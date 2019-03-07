package com.click369.cortex.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.click369.cortex.R;
import com.click369.cortex.util.SavePerfrence;
import com.click369.cortex.util.Util;

public class DialogSet extends Dialog implements android.view.View.OnClickListener{
	private View diaView;
	Spinner spinnerRate,spinnerChuanKou,spinnerShuJuWei,spinnerJiOu,spinnerTingZhi;
	Button cancel,confirm,title;
	SavePerfrence spf;
	Context context;
	
	//zxf 5-31 解决修改COM参数 设置问题。再次打开 显示最后修改的 参数
	int rateI = 5, stopBitI=0,jiouI=0,dataBitsI=8,chuankouI=0;
		
	
	public DialogSet(Context context,String text) {
		super(context, R.style.setdialog);
		this.context = context;
		spf = new SavePerfrence(context);
		diaView = View.inflate(context, R.layout.dialog_setting, null);
		this.setContentView(diaView);
		initSpinner(diaView);
		title = (Button)this.findViewById(R.id.dialog_set_title);
		title.setText(text);
		cancel = (Button)this.findViewById(R.id.dialog_cancel_btn);
		cancel.setOnClickListener(this);
		confirm = (Button)this.findViewById(R.id.dialog_queding_btn);
		confirm.setOnClickListener(this);
		
		this.show();
	}
	//1200、2400、4800、9600、19200、28400、57600、  115200
	String rate[] = {"1200","2400","4800","9600","19200","38400","57600","115200"};
	String chuanKou[] = {"ttyUsb0","ttyUsb1","ttyUsb2"};
	String shuJuWei[] = {"0","1","2","3","4","5","6","7","8"};
	
	String jiOu[] = {"NONE","ODD","EVEN","MARK","SPACE"};
	int jiOuData[] = {0,1,2,3,4};
	
	String tingZhi[] = {"1","1.5","2"};
	int tingZhiData[] = {1,3,2};
	
	private void initSpinner(View v){
		spinnerRate = (Spinner)v.findViewById(R.id.dialog_spinner_rate);
		spinnerRate.setTag("rate");
		spinnerChuanKou = (Spinner)v.findViewById(R.id.dialog_spinner_chuankou);
		spinnerChuanKou.setTag("ck");
		spinnerShuJuWei = (Spinner)v.findViewById(R.id.dialog_spinner_shujuwei);
		spinnerShuJuWei.setTag("sjw");
		spinnerJiOu = (Spinner)v.findViewById(R.id.dialog_spinner_jiou);
		spinnerJiOu.setTag("jo");
		spinnerTingZhi = (Spinner)v.findViewById(R.id.dialog_spinner_tingzhiwei);
		spinnerTingZhi.setTag("tz");
//		MyItemClick mc = new MyItemClick();
//		spinnerRate.setOnItemSelectedListener(mc);
//		spinnerChuanKou.setOnItemSelectedListener(mc);
//		spinnerShuJuWei.setOnItemSelectedListener(mc);
//		spinnerJiOu.setOnItemSelectedListener(mc);
//		spinnerTingZhi.setOnItemSelectedListener(mc);
		//波特率数据
		ArrayAdapter<String> saRate = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, rate);  
		saRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRate.setAdapter(saRate);
		//串口数据
		ArrayAdapter<String> saChuanKou = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, chuanKou);  
		saChuanKou.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChuanKou.setAdapter(saChuanKou);
		//数据位数据
		ArrayAdapter<String> saShuJuWei = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, shuJuWei);  
		saShuJuWei.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerShuJuWei.setAdapter(saShuJuWei);
		//奇偶数据
		ArrayAdapter<String> saJiOu = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, jiOu);  
		saJiOu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerJiOu.setAdapter(saJiOu);
		//停止位数据
        ArrayAdapter<String> saTingZhi = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tingZhi);  
        saTingZhi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTingZhi.setAdapter(saTingZhi);
         
        GetCommSetData();
       
        spinnerRate.setSelection(rateI);
        spinnerChuanKou.setSelection(chuankouI);
        spinnerShuJuWei.setSelection(dataBitsI);
        spinnerJiOu.setSelection(jiouI);
        spinnerTingZhi.setSelection(stopBitI);
        
        setData();
        
	}
    /**
     * 获取spf中保存的COM数据 zxf
     * */
	private void GetCommSetData() {
		// TODO Auto-generated method stub
		chuankouI=Integer.parseInt(spf.getPerfrence(SavePerfrence.CHUANKOUI, "0"));
        rateI=Integer.parseInt(spf.getPerfrence(SavePerfrence.RATEI, "5"));
  		dataBitsI=Integer.parseInt(spf.getPerfrence(SavePerfrence.SHUJUWEII, "8"));
  		jiouI=Integer.parseInt(spf.getPerfrence(SavePerfrence.JIOUI, "0"));
  		stopBitI=Integer.parseInt(spf.getPerfrence(SavePerfrence.TINGZHIWEII, "0"));
  		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_cancel_btn:
			this.cancel();
			break;
			
		case R.id.dialog_queding_btn:
			setData();
			this.cancel();
			break;
		}
	}
	
	private void setData(){
		Util.rate = Integer.parseInt(rate[spinnerRate.getSelectedItemPosition()]);
		Util.dataBits = Integer.parseInt(shuJuWei[spinnerShuJuWei.getSelectedItemPosition()]);
		Util.jiou = jiOuData[spinnerJiOu.getSelectedItemPosition()];
		Util.stopBit= tingZhiData[spinnerTingZhi.getSelectedItemPosition()];
		
		//保存到spf
		spf.savePerfrence(SavePerfrence.CHUANKOUI, spinnerChuanKou.getSelectedItemPosition()+"");
		spf.savePerfrence(SavePerfrence.RATEI, spinnerRate.getSelectedItemPosition()+"");
		spf.savePerfrence(SavePerfrence.SHUJUWEII, spinnerShuJuWei.getSelectedItemPosition()+"");
		spf.savePerfrence(SavePerfrence.JIOUI, spinnerJiOu.getSelectedItemPosition()+"");
		spf.savePerfrence(SavePerfrence.TINGZHIWEII, spinnerTingZhi.getSelectedItemPosition()+"");
		  
    }
	
	
}
