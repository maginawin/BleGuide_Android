package com.maginawin.bleguide;

import com.mymcu.bleapi.WMBleService;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BleApplication extends Application {

	private final static String TAG = "BleApplication";

	// private BleService mService;
	private BleService mService;
	private IBle mBle;
	private String mAddress;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((BleService.LocalBinder) rawBinder).getService();
			mBle = mService.getBle();
			if (mBle != null && !mBle.adapterEnabled()) {
				// TODO: enalbe adapter
				Log.d(TAG, "enable adapter");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		Intent bindIntent = new Intent(this, BleService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		unbindService(mServiceConnection);
	}

	public IBle getIBle() {
		return mBle;
	}

	public String getmAddress() {
		return mAddress;
	}

	public void setmAddress(String mAddress) {
		this.mAddress = mAddress;
	}

}
