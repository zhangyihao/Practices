package com.zhangyihao.swipemenudemo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SwipeListViewAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<SwipeBean> mDatas;
	
	public SwipeListViewAdapter(Context context, List<SwipeBean> datas) {
		this.mContext = context;
		this.mDatas = datas;
	}
	
	@Override
	public int getCount() {
		return this.mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.item_swipe_listview, null);
			View menuView = LayoutInflater.from(this.mContext).inflate(R.layout.swipe_listview_menu, null);
			
			holder.mDeleteTextView = (TextView) menuView.findViewById(R.id.swipe_listview_menu_delete);
			holder.mUploadTextView = (TextView) menuView.findViewById(R.id.swipe_listview_menu_upload);
			holder.mTitleTextView = (TextView) contentView.findViewById(R.id.item_swipe_listview_title);
			
			convertView = new SwipeListViewItemLayout(contentView, menuView, null, null);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		SwipeBean bean = (SwipeBean)this.getItem(position);
		holder.mDeleteTextView.setVisibility(View.VISIBLE);
		holder.mUploadTextView.setVisibility(View.VISIBLE);
		if(!bean.isCanDelete()) {
			holder.mDeleteTextView.setVisibility(View.GONE);
		}
		
		if(!bean.isCanUpload()) {
			holder.mUploadTextView.setVisibility(View.GONE);
		}
		
		holder.mTitleTextView.setText(bean.getTitle());
		
		return convertView;
	}
	
	class ViewHolder {
		TextView mTitleTextView;
		TextView mDeleteTextView;
		TextView mUploadTextView;
	}

}
