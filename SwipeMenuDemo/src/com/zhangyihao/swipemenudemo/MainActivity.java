package com.zhangyihao.swipemenudemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private SwipeListView mSwipeListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSwipeListView = (SwipeListView) findViewById(R.id.activity_main_listview);
		List<SwipeBean> datas = new ArrayList<SwipeBean>();
		SwipeBean bean = new SwipeBean();
		bean.setCanDelete(true);
		bean.setCanUpload(true);
		bean.setTitle("AAAAAAA");
		datas.add(bean);
		
		bean = new SwipeBean();
		bean.setCanDelete(false);
		bean.setCanUpload(true);
		bean.setTitle("不可删除");
		datas.add(bean);
		
		bean = new SwipeBean();
		bean.setCanDelete(true);
		bean.setCanUpload(false);
		bean.setTitle("可删除,不可上传");
		datas.add(bean);
		
		bean = new SwipeBean();
		bean.setCanDelete(true);
		bean.setCanUpload(false);
		bean.setTitle("不可上传");
		datas.add(bean);
		
		bean = new SwipeBean();
		bean.setCanDelete(false);
		bean.setCanUpload(false);
		bean.setTitle("不可删除,不可上传");
		datas.add(bean);
		
		SwipeListViewAdapter adapter = new SwipeListViewAdapter(MainActivity.this, datas);
		this.mSwipeListView.setAdapter(adapter);
	}
}
