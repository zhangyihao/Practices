package com.zhangyihao.pickpicfromsystemgallery.sample2;

import java.util.List;

import com.zhangyihao.pickpicfromsystemgallery.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ListImageDirPopWindow extends PopupWindow {
	private int width;
	private int height;
	private View mContentView;
	private ListView mListView;
	private List<ImageFolder> mDatas;
	
	public interface OnDirSelectListener {
		void onSelect(ImageFolder imageFolder);
	}
	
	private OnDirSelectListener mOnDirSelectListener;
	
	public ListImageDirPopWindow(Context context, List<ImageFolder> datas) {
		calWidthAndHeight(context);
		
		this.mDatas = datas;
		this.mContentView = LayoutInflater.from(context).inflate(R.layout.popwindow_main, null);
		setContentView(this.mContentView);
		setWidth(this.width);
		setHeight(this.height);
		
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		
		setTouchInterceptor(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
		
		initViews(context);
		initEvent();
	}

	public void setOnDirSelectListener(OnDirSelectListener onDirSelectListener) {
		this.mOnDirSelectListener = onDirSelectListener;
	}
	
	private void initViews(Context context) {
		this.mListView = (ListView) this.mContentView.findViewById(R.id.popwindow_main_listview);
		this.mListView.setAdapter(new ListDirAdapter(context, this.mDatas));
	}

	private void initEvent() {
		this.mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(ListImageDirPopWindow.this.mOnDirSelectListener!=null) {
					ListImageDirPopWindow.this.mOnDirSelectListener.onSelect(mDatas.get(position));
				}
			}
		});
	}

	private void calWidthAndHeight(Context context) {
		WindowManager wm =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		this.width = outMetrics.widthPixels;
		this.height = (int) (outMetrics.heightPixels * 0.7);
	}
	
	private class ListDirAdapter extends ArrayAdapter<ImageFolder> {
		
		private LayoutInflater mLayoutInflater;
		private List<ImageFolder> mDatas;

		public ListDirAdapter(Context context, List<ImageFolder> objects) {
			super(context, 0, objects);
			
			this.mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = this.mLayoutInflater.inflate(R.layout.popwindow_main_listview_item, null);
				
				holder.mImageView = (ImageView) convertView.findViewById(R.id.popwinow_main_list_item_imageview);
				holder.mDirName = (TextView) convertView.findViewById(R.id.popwindow_main_list_item_dir_name);
				holder.mCount = (TextView) convertView.findViewById(R.id.popwindow_main_list_item_count);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			ImageFolder imageFolder = getItem(position);
			holder.mImageView.setImageResource(R.drawable.pictures_no);
			ImageLoader.getInstance().loadImage(imageFolder.getFirstImagePath(), holder.mImageView);
			
			holder.mDirName.setText(imageFolder.getName());
			holder.mCount.setText(imageFolder.getCount()+"");
			
//			return super.getView(position, convertView, parent);
			return convertView;
		}
		
		private class ViewHolder {
			ImageView mImageView;
			TextView mDirName;
			TextView mCount;
		}
		
	}
	
}
