
package com.click369.cortex.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.util.HexDump;


@SuppressLint("HandlerLeak")
public class DataShowActivity extends Activity {
    private Button clean,stop,send,back;
    private CheckBox cbSL,cbAutoClean;
    private EditText sendEt;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    boolean looping = true,autoClean = false,isSL = true;//isSL是否是十六进制
    int lineNum= 0;// 显示的行数
    //接收后台传入的数据
    Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Util.ALLDATA:
			case Util.FCDATA:
			case Util.FDDATA:
				if(looping){
					String msgStr = (String)msg.obj;
					if(isSL){
						mDumpTextView.append("接收："+msgStr+"\r\n");
					}else{
						/*
						String datas[] = msgStr.split(" ");
						if(datas.length>20){
							//数据从十六进制转为十进制
							byte dataByte [] = HexDump.hexStringToByteArray(datas[8]+datas[9]+datas[10]+datas[11]+datas[12]);
							msgStr = new String(dataByte);
							mDumpTextView.append("接收："+msgStr+"\r\n");
						}
						*/
					}
					lineNum ++;
					if(autoClean){
						if(lineNum>=18){
							mDumpTextView.setText("");
							lineNum = 0;
						}
					}else{
						if(lineNum>=180){
							mDumpTextView.setText("");
							lineNum = 0;
						}
					}
					//滚动到最底
					mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
				}
				break;
			}
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datashow);
        cbSL = (CheckBox) findViewById(R.id.data_shiliu_checkBox);
        cbAutoClean = (CheckBox) findViewById(R.id.data_autoclean_checkBox);
        cbSL.setChecked(true);
        MyCheckedListener mcl = new MyCheckedListener();
        cbSL.setOnCheckedChangeListener(mcl);
        cbAutoClean.setOnCheckedChangeListener(mcl);
        
        sendEt = (EditText) findViewById(R.id.data_et);
        clean = (Button) findViewById(R.id.data_clean_button);
        stop = (Button) findViewById(R.id.data_stop_button);
        send = (Button) findViewById(R.id.data_send_button);
        back = (Button) findViewById(R.id.data_back_button);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	DataShowActivity.this.finish();
            }
        });
        clean.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mDumpTextView.setText("");
        		lineNum = 0;
        	}
        });
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(looping){
            		looping = false;
            		stop.setText("开始");
            	}else{
            		looping = true;
            		stop.setText("停止");
            	}
            }
        });
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if((sendEt.getText().toString().trim()+"").length()>0){
            		if(MainZigBeeService.myHandler!=null){
		            	Message msg = Message.obtain();
		            	msg.what = Util.WRITEDATA;
		            	msg.obj = sendEt.getText().toString();
		            	MainZigBeeService.myHandler.sendMessage(msg);
		            	
		            	mDumpTextView.append("发送："+sendEt.getText().toString()+"\r\n");
						mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
						sendEt.setText("");
            		}else{
            			Toast.makeText(DataShowActivity.this, "服务未启动，可能由于没有设备插入", Toast.LENGTH_LONG).show();
            		}
            	}else{
            		Toast.makeText(DataShowActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
            	}
            }
        });
    }
    
    //多选框选中事件
    class MyCheckedListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.data_shiliu_checkBox:
				if(isChecked){
					cbSL.setChecked(true);
					isSL = true;
				}
				break;
			case R.id.data_autoclean_checkBox:
				if(isChecked&&lineNum>=18){
					mDumpTextView.setText("");
					lineNum = 0;
				}
				autoClean = isChecked;
				break;
			}
		}
    	
    	
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.uiHandler = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.uiHandler = myHandler;
        Util.whichBlock = "showdata";
    }

}
