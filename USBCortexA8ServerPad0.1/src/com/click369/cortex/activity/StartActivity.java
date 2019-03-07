package com.click369.cortex.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.service.ShengGuangService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("NewApi")
public class StartActivity extends Activity implements OnClickListener{
	Button chuanGanBt;
	TextView msgTv;
	State wifi;
	private final String TAG = StartActivity.class.getSimpleName();
	private UsbManager mUsbManager;
    private List<DeviceEntry> mEntries = new ArrayList<DeviceEntry>();
    
    class DeviceEntry {
        public UsbDevice device;
        public UsbSerialDriver driver;
        public DeviceEntry(UsbDevice device, UsbSerialDriver driver) {
            this.device = device;
            this.driver = driver;
        }
        
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		msgTv = (TextView)findViewById(R.id.first_textView);
		chuanGanBt = (Button)findViewById(R.id.first_cg);
		chuanGanBt.setTag(SetActivity.CGQ);
		chuanGanBt.setOnClickListener(this);
     	//�ڴ��ж���û��wifi
		ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		//��ȡ״̬
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		
	}
	@Override
	public void onClick(View v) {
		
		clickButton(SetActivity.class,v.getTag().toString());
		Intent intent = new Intent(this,SetActivity.class);
		intent.putExtra("tag", v.getTag().toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
		
	}
	
	private void refreshDeviceList() {
        final List<DeviceEntry> result = new ArrayList<DeviceEntry>();
        for (final UsbDevice device : mUsbManager.getDeviceList().values()) {
            final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mUsbManager, device);
            Log.d(TAG, "����usb�豸: " + device);
            if (drivers.isEmpty()) {
                Log.d(TAG, "  - û�пɼ���usb����");
            } else {
                for (UsbSerialDriver driver : drivers) {
                    Log.d(TAG, "  + " + driver);
                    result.add(new DeviceEntry(device, driver));
                }
            }
        }
        mEntries.clear();
        mEntries.addAll(result);
        if(mEntries.size()>0){
        	 DeviceEntry entry = mEntries.get(0);
        	 final String title = String.format("Vendor %s Product %s",
                     HexDump.toHexString((short) entry.device.getVendorId()),
                     HexDump.toHexString((short) entry.device.getProductId()));
        	 msgTv.setText("zigBee�豸�Ѿ�����,"+title);
        }else{
			//�ж�wifi�����ӵ�����
			if(wifi == State.CONNECTED||wifi==State.CONNECTING){
				msgTv.setText("wifi�Ѿ�����...");
			}else{
				msgTv.setText("zigBee�豸��wifi�豸����δ���ӣ�������豸...");
			}
        	
        }
        Log.d(TAG, "ˢ�����, " + mEntries.size() + " ���豸");
    }
	
	private void clickButton(Class<?> cls,String tag){

			 if(mEntries==null||mEntries.size()==0){
				 showMsg(getResources().getString(R.string.device_not_found));
				 return;
			 }
			 final DeviceEntry entry = mEntries.get(0);
			 if(entry==null){
				 showMsg(getResources().getString(R.string.device_not_found));
				 return;
			 }
	         final UsbSerialDriver driver = entry.driver;
	         if (driver == null) {
	             Log.d(TAG, "û������");
	             showMsg(getResources().getString(R.string.no_driver));
	             return;
	         }
	        //����set���沢���� driver ��tag
	        //SetActivity.show(this, driver, tag);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onResume() {
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){   
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
		} 
		refreshDeviceList();
		super.onResume();
	}
	
	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public void onBackPressed() {
		showDialog();
		return;
	}
	
	private void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.exit_confirm));
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Intent servicea = new Intent(StartActivity.this, MainZigBeeService.class);
				StartActivity.this.stopService(servicea);
				final Intent serviceb = new Intent(StartActivity.this, MainWifiService.class);
				StartActivity.this.stopService(serviceb);
				final Intent servicec = new Intent(StartActivity.this, ChuangLianService.class);
				StartActivity.this.stopService(servicec);
				final Intent serviced = new Intent(StartActivity.this, DengGuangService.class);
				StartActivity.this.stopService(serviced);
				final Intent servicee = new Intent(StartActivity.this, ShengGuangService.class);
				StartActivity.this.stopService(servicee);
				StartActivity.this.finish();
				if(LoginActivity.loginActivity!=null){
					LoginActivity.loginActivity.finish();
				}
				Process.killProcess(Process.myPid());
			}
		});
		builder.setNeutralButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
		});
		builder.create().show();
	}

}
