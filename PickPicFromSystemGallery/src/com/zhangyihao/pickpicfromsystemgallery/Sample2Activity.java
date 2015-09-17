package com.zhangyihao.pickpicfromsystemgallery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zhangyihao.pickpicfromsystemgallery.sample2.ImageFolder;
import com.zhangyihao.pickpicfromsystemgallery.sample2.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.NoCopySpan.Concrete;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Sample2Activity extends Activity {

	private GridView mGridView;
	private RelativeLayout mBottomRelativeLayout;
	private TextView mDirNameTextView;
	private TextView mDirCountTextView;
	
	private ProgressDialog mProgressDialog;
	
	private int mScreenHeight;
	private List<String> mImages;
	private File mCurrnetDir;
	private int mMaxCount;
	private List<ImageFolder> mImageFolder;
	
	private Handler mHandler;
	private static final int LOAD_IMAGE = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample2);
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		
		mScreenHeight = outMetrics.heightPixels;
		
		initView();
		getImages();
		initEvent();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == LOAD_IMAGE) {
					if(mProgressDialog!=null) {
						mProgressDialog.dismiss();
						fillImg2GridView();
					}
				}
			}
		};
	}
	
	protected void fillImg2GridView() {
		if(mCurrnetDir==null) {
			Toast.makeText(Sample2Activity.this, "未扫描到任何图片！", Toast.LENGTH_SHORT).show();
			return;
		}
		mImages = Arrays.asList(mCurrnetDir.list());
		ImageAdapter adapter = new ImageAdapter(this, mImages, mCurrnetDir.getAbsolutePath());
		mGridView.setAdapter(adapter);
		
		mDirCountTextView.setText(""+mMaxCount);
		mDirNameTextView.setText(mCurrnetDir.getName());
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.sample2_id_gridView);
		mDirNameTextView = (TextView) findViewById(R.id.sample2_id_choose_dir);
		mDirCountTextView = (TextView) findViewById(R.id.sample2_id_total_count);
		mBottomRelativeLayout = (RelativeLayout) findViewById(R.id.sample2_id_bottom_ly);
	}
	
	private void getImages() {
		boolean mounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(!mounted) {
			Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		mProgressDialog = ProgressDialog.show(this,null, "正在扫描图片");
		new Thread() {
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Toast.makeText(Sample2Activity.this, "URI:"+mImgUri.toString(), Toast.LENGTH_LONG).show();
				ContentResolver cr = Sample2Activity.this.getContentResolver();
				Cursor cursor = cr.query(mImgUri, null, 
						MediaStore.Images.Media.MIME_TYPE+"=? or "+ MediaStore.Images.Media.MIME_TYPE+"=? ",
						new String[]{"image/jpeg", "image/png"},
						MediaStore.Images.Media.DATE_MODIFIED);
				Set<String> mDirPath = new HashSet<String>();
				while(cursor.moveToNext()) {
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					File parentFile = new File(path).getParentFile();
					if(parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();
					if(!mDirPath.contains(dirPath)) {
						mDirPath.add(dirPath);
						ImageFolder imageFolder = new ImageFolder();
						imageFolder.setDir(dirPath);
						imageFolder.setFirstImagePath(path);
						if(parentFile.list() != null) {
							int imageCount = parentFile.list(new FilenameFilter() {
								
								@Override
								public boolean accept(File dir, String filename) {
									if(filename.endsWith(".jpg") 
											|| filename.endsWith(".jpeg") || filename.endsWith(".png")) {
										return true;
									}
									return false;
								}
							}).length;
							imageFolder.setCount(imageCount);
							mImageFolder.add(imageFolder);
							if(imageCount > mMaxCount) {
								mMaxCount = imageCount;
								mCurrnetDir = parentFile;
							}
						}
					}
				}
				cursor.close();
				mDirPath = null;
				mHandler.sendEmptyMessage(LOAD_IMAGE);
			};
		}.start();
	}
	
	private void initEvent() {
		
	}
	
	private class ImageAdapter extends BaseAdapter {

		private Context mContent;
		private List<String> mImage;
		private String dirPath;
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapter(Context context, List<String> image, String dirPath) {
			this.mContent = context;
			this.mImage = image;
			this.dirPath = dirPath;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
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
			
			ImageLoader.getInstance().loadImage(dirPath+File.separator+mImage.get(position), viewHolder.imageView);
			
			return convertView;
		}
		
		private class ViewHolder {
			ImageView imageView;
			ImageButton imageButton;
		}
		
	}
	
}
