package com.zhangyihao.pickpicfromsystemgallery.sample2;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ImageLoader {
	
	private static ImageLoader mInstance;
	private static final int DEFAULT_THERAD_COUNT = 1;
	
	/**
	 * 存放图片缓冲
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static Type myType = Type.FILO;
	private LinkedList<Runnable> mTaskQueue;
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	private Handler mUIHandler;
	
	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}
	
	private void init(int threadCount, Type type) {
		//后台轮询线程
		mPoolThread = new Thread() {

			@Override
			public void run() {
				Looper.loop();
				mPoolThreadHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						//线程池中取任务处理
						mThreadPool.execute(getTask());
					}
				};
				Looper.loop();
			}
			
		};
		mPoolThread.start();
		
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int cacheMenory = maxMemory/8;
		mLruCache = new LruCache<String, Bitmap>(cacheMenory) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
		
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		myType = type;
	}
	
	public static ImageLoader getInstance() {
		if(mInstance == null) {
			synchronized (mInstance) {
				if(mInstance == null) {
					mInstance = new ImageLoader(DEFAULT_THERAD_COUNT, myType);
				}
			}
		}
		return mInstance;
	}
	
	/**
	 * 根据Path设置ImageView图片
	 * @param path
	 * @param imageView
	 */
	private void loadImage(String path, final ImageView imageView) {
		imageView.setTag(path);
		if(mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO 获取图片，并展示
					BitmapHolder holder = (BitmapHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bitmap = holder.bitmap;
					String path = holder.path;
					
					if(imageView.getTag().toString().equals(path)) {
						// ImageView是复用的，需要比对ImageView的Tag和path是否一致，
						imageView.setImageBitmap(bitmap);
					}
				}
			};
		} else {
			addTasks(new Runnable() {
				@Override
				public void run() {
					// TODO 加载图片
					// 图片压缩
					// 1. 获取图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					
				}

				
			});
		}
		
		Bitmap bm = getBitmapFromLruCache(path);
		if(bm!=null) {
			BitmapHolder holder = new BitmapHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			
			Message msg = Message.obtain();
			msg.obj = holder;
			mUIHandler.sendMessage(msg);
		}
	}
	
	
	private void addTasks(Runnable runnable) {
		mTaskQueue.add(runnable);
		mPoolThreadHandler.sendEmptyMessage(0x010);
	}
	
	private Runnable getTask() {
		if(myType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else {
			return mTaskQueue.removeLast();
		}
	}

	private Bitmap getBitmapFromLruCache(String path) {
		return mLruCache.get(path);
	}
	
	private ImageSize getImageViewSize(ImageView imageView) {
		ImageSize size = new ImageSize();
		
		DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
		
		LayoutParams layoutParams = imageView.getLayoutParams();
		int width = imageView.getWidth();
		if(width<=0) {
			width = layoutParams.width;
		}
		if(width<=0) {
			width = imageView.getMaxWidth();
		}
		
		if(width<=0) {
			width = metrics.widthPixels;
		}
		
		int height = imageView.getHeight();
		if(height<=0) {
			height = layoutParams.height;
		}
		if(height<=0) {
			height = imageView.getMaxHeight();
		}
		
		if(height<=0) {
			height = metrics.heightPixels;
		}
		
		size.width = width;
		size.height = height;
		return size;
	}
	
	private class ImageSize  {
		int width;
		int height;
	}
	
	private class BitmapHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}


	public enum Type {
		FIFO, FILO;
	}

}
