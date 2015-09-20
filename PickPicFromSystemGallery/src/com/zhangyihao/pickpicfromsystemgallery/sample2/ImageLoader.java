package com.zhangyihao.pickpicfromsystemgallery.sample2;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
	private Semaphore mPoolThreadHandlerSemaphore = new Semaphore(0);
	private Semaphore mPoolThreadSemaphore;
	
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
	
	public static ImageLoader getInstance() {
		if(mInstance == null) {
			synchronized (ImageLoader.class) {
				if(mInstance == null) {
					mInstance = new ImageLoader(DEFAULT_THERAD_COUNT, myType);
				}
			}
		}
		return mInstance;
	}
	
	public static ImageLoader getInstance(int threadCount, Type type) {
		if(mInstance == null) {
			synchronized (mInstance) {
				if(mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}
	
	private void init(int threadCount, Type type) {
		//后台轮询线程
		mPoolThread = new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						//线程池中取任务处理
						mThreadPool.execute(getTask());
						try {
							mPoolThreadSemaphore.acquire();
						} catch (InterruptedException e) {
						}
					}
				};
				mPoolThreadHandlerSemaphore.release();
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
		mPoolThreadSemaphore = new Semaphore(threadCount);
	}
	
	
	/**
	 * 根据Path设置ImageView图片
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
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
			addTask(new Runnable() {
				@Override
				public void run() {
					// 图片压缩
					// 1. 获取图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					Bitmap bitmap = decodeBitmapFromPath(path, imageSize.width, imageSize.height);
					// 2. 添加到LruCache
					addBitmapToLruCache(path, bitmap);
					// 3. 刷新UI
					refreshUI(path, imageView, bitmap);
					mPoolThreadSemaphore.release();
				}

				
			});
		}
		
		Bitmap bm = getBitmapFromLruCache(path);
		if(bm!=null) {
			refreshUI(path, imageView, bm);
		}
	}

	private void refreshUI(final String path, final ImageView imageView, Bitmap bm) {
		BitmapHolder holder = new BitmapHolder();
		holder.bitmap = bm;
		holder.imageView = imageView;
		holder.path = path;
		
		Message msg = Message.obtain();
		msg.obj = holder;
		mUIHandler.sendMessage(msg);
	}
	
	
	protected void addBitmapToLruCache(String path, Bitmap bitmap) {
		if(getBitmapFromLruCache(path)==null && bitmap!=null) {
			mLruCache.put(path, bitmap);
		}
	}

	protected Bitmap decodeBitmapFromPath(String path, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		options.inSampleSize = caculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		
		if(width>reqWidth || height>reqHeight) {
			int widthRadio = Math.round(width*1.0f/reqWidth);
			int heightRadio = Math.round(height*1.0f/reqHeight);
			
			inSampleSize = Math.max(widthRadio, heightRadio);
		}
		
		return inSampleSize;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		
		try {
			if(mPoolThreadHandler == null) {
				mPoolThreadHandlerSemaphore.acquire();
			}
		} catch (InterruptedException e) {
		}
		
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
			width = getImageViewFieldValue(imageView, "mMaxWidth");
//			width = imageView.getMaxWidth();
		}
		
		if(width<=0) {
			width = metrics.widthPixels;
		}
		
		int height = imageView.getHeight();
		if(height<=0) {
			height = layoutParams.height;
		}
		if(height<=0) {
//			height = imageView.getMaxHeight();
			height = getImageViewFieldValue(imageView, "mMaxHeight");
		}
		
		if(height<=0) {
			height = metrics.heightPixels;
		}
		
		size.width = width;
		size.height = height;
		return size;
	}
	
	/**
	 * 通过反射获取ImageView的属性值（宽度、高度）
	 * 使用反射，兼容低版本
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if(fieldValue>0 && fieldValue<=Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
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
