package com.zhangyihao.pickpicfromsystemgallery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zhangyihao.pickpicfromsystemgallery.sample2.ImageAdapter;
import com.zhangyihao.pickpicfromsystemgallery.sample2.ImageFolder;
import com.zhangyihao.pickpicfromsystemgallery.sample2.ListImageDirPopWindow;
import com.zhangyihao.pickpicfromsystemgallery.sample2.ListImageDirPopWindow.OnDirSelectListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Sample2Activity extends Activity {

	private GridView mGridView;
	private RelativeLayout mBottomRelativeLayout;
	private TextView mDirNameTextView;
	private TextView mDirCountTextView;
	
	private ProgressDialog mProgressDialog;
	private ListImageDirPopWindow mListImagePopwindow;
	
	private ImageAdapter mImageAdapter;
	
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
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == LOAD_IMAGE) {
					if(mProgressDialog!=null) {
						mProgressDialog.dismiss();
						fillImg2GridView();
						initDirPopWindow();
					}
				}
			}
		};
		mImageFolder = new ArrayList<ImageFolder>();
		mImages = new ArrayList<String>();
		initView();
		getImages();
		initEvent();
	}
	
	protected void initDirPopWindow() {
		mListImagePopwindow = new ListImageDirPopWindow(Sample2Activity.this, this.mImageFolder);
		mListImagePopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			
			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		
		mListImagePopwindow.setOnDirSelectListener(new OnDirSelectListener() {
			
			@Override
			public void onSelect(ImageFolder imageFolder) {
				mCurrnetDir = new File(imageFolder.getDir());
				mImages = Arrays.asList( mCurrnetDir.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String filename) {
						if(filename.endsWith(".jpg") 
								|| filename.endsWith(".jpeg") || filename.endsWith(".png")) {
							return true;
						}
						return false;
					}
				}));
				
				mImageAdapter = new ImageAdapter(Sample2Activity.this, mImages, mCurrnetDir.getAbsolutePath());
				mGridView.setAdapter(mImageAdapter);
				
				mDirNameTextView.setText(imageFolder.getName());
				mDirCountTextView.setText(mImages.size()+"");
				mListImagePopwindow.dismiss();
				
			}
		});
	}

	/**
	 * 内容区域变亮
	 */
	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}
	
	/**
	 * 内容区域变暗
	 */
	protected void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	protected void fillImg2GridView() {
		if(mCurrnetDir==null) {
			Toast.makeText(Sample2Activity.this, "未扫描到任何图片！", Toast.LENGTH_SHORT).show();
			return;
		}
		mImages = Arrays.asList(mCurrnetDir.list());
		mImageAdapter = new ImageAdapter(this, mImages, mCurrnetDir.getAbsolutePath());
		mGridView.setAdapter(mImageAdapter);
		
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
//				Toast.makeText(Sample2Activity.this, "URI:"+mImgUri.toString(), Toast.LENGTH_LONG).show();
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
		this.mBottomRelativeLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Sample2Activity.this.mListImagePopwindow.showAsDropDown(Sample2Activity.this.mBottomRelativeLayout, 0, 0);
				lightOff();
			}
		});
	}
}
