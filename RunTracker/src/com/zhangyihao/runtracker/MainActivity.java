package com.zhangyihao.runtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
	}


	@Override
	protected Fragment createFragment() {
		return new RunFragment();
	}
}
