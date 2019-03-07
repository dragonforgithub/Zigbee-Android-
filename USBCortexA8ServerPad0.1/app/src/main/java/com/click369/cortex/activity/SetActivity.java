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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.activity.StartActivity.DeviceEntry;
import com.click369.cortex.service.ChuangLianService;
import com.click369.cortex.service.DengGuangService;
import com.click369.cortex.service.MainWifiService;
import com.click369.cortex.service.MainZigBeeService;
import com.click369.cortex.service.ShengGuangService;
import com.click369.cortex.util.Util;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

@SuppressLint("NewApi")
public class SetActivity extends Activity implements OnClickListener{
	Button zigbeeBt,blueBt,jinru,back;
	String tag = "SetActivity";
	public static final String CGQ="cgq";//传感器
	public static final String GPS="gps";//GPS
	public static final String ONEDESIGN="one";//工程设计1
	public static final String TWODESIGN="two";//工程设计2

	static UsbSerialDriver driver1;//用来启动服务时传递

	State wifi;
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
		setContentView(R.layout.activity_setpage);
		jinru = (Button)findViewById(R.id.setpage_jinru_btn);
		back = (Button)findViewById(R.id.setpage_back_btn);
		zigbeeBt = (Button)findViewById(R.id.set_xx_zigbee_btn);
		blueBt = (Button)findViewById(R.id.set_xx_bluetooth_btn);
		blueBt.setOnClickListener(this);
		zigbeeBt.setOnClickListener(this);
		jinru.setOnClickListener(this);
		back.setOnClickListener(this);
		//初始化 usbmanager
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		//在此判断有没有wifi
		ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

		final Intent service = new Intent(this, MainZigBeeService.class);
		stopService(service);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.set_xx_zigbee_btn:
				new DialogSet(this, "ZigBee设置");
				break;
			case R.id.set_xx_bluetooth_btn:
				new DialogSet(this, "蓝牙设置");
				break;
			case R.id.setpage_jinru_btn:

				changeActivity();
				break;
			case R.id.setpage_back_btn:
				this.finish();
				break;
			default :
				break;
		}
	}


	private void clickBtn() {
		// TODO Auto-generated method stub

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
			showMsg(getResources().getString(R.string.no_driver));
			return;
		}
		//启动SetActivity界面并传入 driver 和tag
		driver1=driver;
	}


	private void changeActivity(){
		Intent activityIntent = new Intent(this, EnterActivity.class);
		if(activityIntent!=null){
			this.startActivity(activityIntent);

			//同时启动服务
			if(driver1!=null){
				MainZigBeeService.sDriver = driver1;
				final Intent service = new Intent(this, MainZigBeeService.class);
				startService(service);

			} else{
//				//在此判断有没有wifi
//				ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//				//获取状态
//				State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//				//判断wifi已连接的条件
//				if(wifi == State.CONNECTED||wifi==State.CONNECTING){
//					final Intent service = new Intent(this, MainWifiService.class);
//					startService(service);
//				}else{
				showMsg(getResources().getString(R.string.ZigbeeOrWifi_not_connect));
//				}
			}

			this.finish();
		}
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		refreshDeviceList();

		Util.whichBlock = "";//协调器
		super.onResume();

	}
	/**
	 * 监测 USB设备
	 * */
	private void refreshDeviceList() {
		// TODO Auto-generated method stub
		final List<DeviceEntry> result = new ArrayList<DeviceEntry>();
		for (final UsbDevice device : mUsbManager.getDeviceList().values()) {
			final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mUsbManager, device);
			if (drivers.isEmpty()) {

			} else {
				for (UsbSerialDriver driver : drivers) {
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

			//clickBtn();

			final UsbSerialDriver driver = entry.driver;

			if (driver == null) {
				showMsg(getResources().getString(R.string.no_driver));
				return;
			}
			//启动SetActivity界面并传入 driver 和tag
			driver1=driver;

			showMsg(getResources().getString(R.string.Zigbee_connected)+","+title);

		}else{
				/*//判断wifi已连接的条件
				if(wifi == State.CONNECTED||wifi==State.CONNECTING){
					showMsg("wifi已连接...");
				}else{
				}*/

			showMsg(getResources().getString(R.string.device_not_found));
			return;
		}
		//showMsg("刷新完成, " + mEntries.size() + " 个设备");
	}

}
