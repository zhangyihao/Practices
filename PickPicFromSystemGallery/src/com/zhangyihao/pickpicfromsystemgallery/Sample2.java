package com.zhangyihao.pickpicfromsystemgallery;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Sample2 extends Activity {
	
	private int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample2);
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		
		mScreenHeight = outMetrics.heightPixels;
		
	}
	
}
