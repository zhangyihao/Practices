package com.zhangyihao.pickpicfromsystemgallery.sample2;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zhangyihao.pickpicfromsystemgallery.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private static Set<String> mSelectImage = new HashSet<String>();

	private Context mContent;
	private List<String> mImage;
	private String dirPath;
	private LayoutInflater mLayoutInflater;
	
	public ImageAdapter(Context context, List<String> image, String dirPath) {
		this.mContent = context;
		this.mImage = image;
		this.dirPath = dirPath;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return this.mImage.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mImage.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.sample2_gridview_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.sample2_gridview_item_image);
			viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.sample2_gridview_item_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imageView.setImageResource(R.drawable.pictures_no);
		viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
		viewHolder.imageView.setColorFilter(null);
		
		final String filePath = dirPath + File.separator + mImage.get(position);
		ImageLoader.getInstance().loadImage(filePath, viewHolder.imageView);
		
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mSelectImage.contains(filePath)) {
					mSelectImage.remove(filePath);
					viewHolder.imageView.setColorFilter(null);
					viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
				} else {
					mSelectImage.add(filePath);
					viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
					viewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
				}
//				notifyDataSetChanged();
				
			}
		});
		
		if(mSelectImage.contains(filePath)) {
			viewHolder.imageView.setBackgroundColor(Color.parseColor("#77000000"));
			viewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
		}
		
		return convertView;
	}
	
	private class ViewHolder {
		ImageView imageView;
		ImageButton imageButton;
	}
}
