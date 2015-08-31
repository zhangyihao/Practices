package com.zhangyihao.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class RunManager {
	private static final String TAG = "RunManager";
	
	public static final String ACTION_LOCATION = "com.zhangyihao.runtracker.ACTION_LOCATION";
	
	private static RunManager sRunManager;
	private Context mAppContext;
	private LocationManager mLocationManager;
	
	private RunManager(Context appContext) {
		this.mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static RunManager getInstance(Context context) {
		if(sRunManager==null) {
			sRunManager = new RunManager(context.getApplicationContext());
		}
		return sRunManager;
	}
	
	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate?0:PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}
	
	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER; 
		PendingIntent intent =  getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 0, 0, intent);
	}
	
	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if(pi!=null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
 	}
	
	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}
}
