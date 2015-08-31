package com.zhangyihao.testgalleryimageshow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	int[] images = new int[]{R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5 };
	private HorizontalScrollView scrollView;
	private LinearLayout linear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		scrollView = (HorizontalScrollView) this.findViewById(R.id.pic_scrollView);
		scrollView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(MainActivity.this, "show..", Toast.LENGTH_SHORT).show();
				v.findViewById(R.id.scheme_detial_gallery_item_delete).setVisibility(View.VISIBLE);
				return true;
			}
			
		});
		linear = (LinearLayout) this.findViewById(R.id.pic_container);
		createChildLinearLayout();
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_activity_scheme_detail_gallery, null);
				ImageView mImg = (ImageView) convertView.findViewById(R.id.scheme_detial_gallery_item_image);
				ImageView delete = (ImageView) convertView.findViewById(R.id.scheme_detial_gallery_item_delete);
				delete.setVisibility(View.GONE);
				mImg.setImageResource(images[images.length-1]);
				convertView.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						v.findViewById(R.id.scheme_detial_gallery_item_delete).setVisibility(View.VISIBLE);
						return true;
					}
					
				});
				
				delete.setOnClickListener(new DeleteOnLongClickListener(MainActivity.this, ""+(images.length-1), convertView, linear));
				linear.addView(convertView);
			}
		});
	}
	
	private void createChildLinearLayout() {
		for (int i = 0; i < images.length-2; i++) {
			View convertView = LayoutInflater.from(this).inflate(R.layout.item_activity_scheme_detail_gallery, null);
			ImageView mImg = (ImageView) convertView.findViewById(R.id.scheme_detial_gallery_item_image);
			ImageView delete = (ImageView) convertView.findViewById(R.id.scheme_detial_gallery_item_delete);
			delete.setVisibility(View.GONE);
			mImg.setImageResource(images[i]);
			convertView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					v.findViewById(R.id.scheme_detial_gallery_item_delete).setVisibility(View.VISIBLE);
					return true;
				}
				
			});
			
			delete.setOnClickListener(new DeleteOnLongClickListener(MainActivity.this, ""+i, convertView, linear));
			linear.addView(convertView);
		}
	}
	

	public class DeleteOnLongClickListener implements View.OnClickListener {
		
		private Context context;
		private View parent;
		private String path;
		private LinearLayout linear;
		
		public DeleteOnLongClickListener(Context context, String path, View parent, LinearLayout linear) {
			this.path = path;
			this.parent = parent;
			this.linear = linear;
			this.context = context;
		}
		
		@Override
		public void onClick(final View v) {
			Toast.makeText(MainActivity.this, "show.."+path, Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("确定删除？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					linear.removeView(parent);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					v.setVisibility(View.GONE);
				}
			});
			builder.create().show();
		}
		
	}
}
