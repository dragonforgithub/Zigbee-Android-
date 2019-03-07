package com.click369.cortex.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.click369.cortex.R;
import com.click369.cortex.util.DownLoadRunnable;
import com.click369.cortex.util.Util;

//登录界面控制器
@SuppressLint({ "NewApi", "HandlerLeak" })
public class ChoiceBoxActivity extends Activity implements OnItemClickListener{
	GridView gview;
	MyAdapter mAdapter;
	public static Activity choiceBoxActivity;
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

				case Util.FAIL:

					break;

				case Util.NOBOX:

					break;

				case Util.CHOICESECESS:
					Intent intent = new Intent(ChoiceBoxActivity.this,EnterActivity.class);
					intent.putExtra("name", Util.boxNum);
					ChoiceBoxActivity.this.startActivity(intent);
					ChoiceBoxActivity.this.finish();
					break;

				case Util.SECESS://存在 实验平台可被选择
					String boxStr = ((String)msg.obj).trim();
					String boxs[] = boxStr.split(" ");
					mAdapter.setData(boxs);
					break;
				default:
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.keepScreenWake(this);
		setContentView(R.layout.activity_choicebox);
		gview = (GridView)this.findViewById(R.id.choicebox_gridView1);
		gview.setOnItemClickListener(this);
		mAdapter= new MyAdapter();
		gview.setAdapter(mAdapter);

		String urlPath ="sdf?action=readOnline&username="+Util.userName;
		new Thread(new DownLoadRunnable(myHandler,urlPath,0)).start();

	}

	class MyAdapter extends BaseAdapter{
		ArrayList<String> alist = new ArrayList<String>();
		public MyAdapter(){
		}

		public void setData(String strs[]){
			if(strs!=null&&strs.length>0){
				for(String s:strs){
					alist.add(s);
				}
				this.notifyDataSetChanged();
			}
		}
		@Override
		public int getCount() {
			return alist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return alist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			view = LayoutInflater.from(ChoiceBoxActivity.this).inflate(R.layout.boxlist_item, null);
			TextView nameTv = (TextView)view.findViewById(R.id.choice_item_name_tv);
			String name = alist.get(arg0);
			nameTv.setText(name);
			view.setTag(name);//设置 标签为 箱子标号
			return view;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void showMsg(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View paramView, int arg2, long arg3) {

		String name = (String)paramView.getTag();
		try {
			Util.boxNum = URLEncoder.encode(name, "UTF-8");//设置 箱子标号
			String urlPath ="sdf?action=choise&bn="+Util.boxNum+"&username="+Util.userName;
			new Thread(new DownLoadRunnable(myHandler,urlPath,1)).start();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}

