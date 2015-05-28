package com.mymcu.bleapi;

import java.util.*;

import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WMBleService extends Service {

	private static final String TAG = "WMBleService";

	// ble var
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothDevice mBluetoothDevice;
	private int mBleState;
	// private List<BluetoothDevice> mConnectedBleDevices = new
	// ArrayList<BluetoothDevice>();
	// private HashMap<String, BluetoothGatt> mConnectedGatts = new
	// HashMap<String, BluetoothGatt>();
	private final LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			if (mBluetoothDevice != null) {
				if (mBluetoothDevice.equals(device)) {
					scanBleDevices(false, false);
					connectBleDevice(device.getAddress());
				}
			}
			Intent aIntent = new Intent();
			aIntent.setAction(WMBleAttributes.BLE_ON_LE_SCAN);
			aIntent.putExtra(WMBleAttributes.EXTRA_BLE_DEVICE, device);
			aIntent.putExtra(WMBleAttributes.EXTRA_BLE_DEVICE_RSSI, rssi);
			sendBroadcast(aIntent);
			Log.d(TAG, "on le scan device address : " + device.getAddress()
					+ ", rssi : " + rssi + ", scan record : " + scanRecord);
		}
	};
	private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(gatt, status, newState);

			mBleState = newState;

			if (newState == BluetoothGatt.STATE_DISCONNECTED) {
				reconnectGatt();
			} else if (newState == BluetoothGatt.STATE_CONNECTED) {
				if (status == 133) {
					disconnectGatt();
					scanBleDevices(true, true);
				} else {
					scanBleDevices(false, false);
				}
			}

			Log.d(TAG, "on connection state change, status : " + status
					+ ", new state : " + newState);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			super.onServicesDiscovered(gatt, status);

			Log.d(TAG, "on services discovered");
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicRead(gatt, characteristic, status);

			Log.d(TAG, "on characteristic read");
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicWrite(gatt, characteristic, status);

			Log.d(TAG, "on characteristic write");
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			// TODO Auto-generated method stub
			super.onCharacteristicChanged(gatt, characteristic);

			Log.d(TAG, "on characteristic changed");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			super.onReadRemoteRssi(gatt, rssi, status);

			Log.d(TAG, "on read remote rssi : " + rssi);
		}

	};

	private IBinder mBinder;
	private Handler mHandler;
	private boolean isScanning = false; // 是否在查找蓝牙

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mHandler = new Handler();
		mBinder = new LocalBinder();
		mBleState = BluetoothGatt.STATE_DISCONNECTED;
		Log.d(TAG, "on create");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "on start command");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "on destroy");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "on unbind");
		return super.onUnbind(intent);
	}

	/** 判断是否支持蓝牙 4.0 */
	private boolean isBleSupport() {
		boolean support = false;
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Log.d(TAG, "ble status abnormal");
			updateBroadcast(WMBleAttributes.BLE_STATUS_ABNORMAL);
		} else {
			if (getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				if (mBluetoothAdapter != null) {
					support = true;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Does not support BLE",
						Toast.LENGTH_SHORT).show();
			}
		}
		Log.d(TAG, "is ble support : " + support);
		return support;
	}

	/** 在一个周期查找 ble 设备 */
	public void scanBleDevices(boolean enable, final boolean repeat) {
		if (isBleSupport()) {
			if (enable) {
				mHandler.postDelayed(new Runnable() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (isScanning) {
							isScanning = false;
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
							Log.d(TAG, "auto stop le scan with period : "
									+ WMBleAttributes.BLE_SCAN_PERIOD);
							if (repeat) {
								scanBleDevices(true, repeat);
							}
						}
					}
				}, WMBleAttributes.BLE_SCAN_PERIOD);
				isScanning = true;
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			} else {
				isScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				Log.d(TAG, "man made stop le scan");
			}
		}
	}

	public void connectBleDevice(final String bleAddress) {
		if (mBluetoothGatt != null) {
			if (bleAddress.equals(mBluetoothDevice.getAddress())) {
				if (mBleState == BluetoothGatt.STATE_DISCONNECTED) {
					if (mBluetoothGatt.connect()) {
						return;
					}
				}
			}
		}
		// 如果以前连接过 Gatt, 应先将 Gatt 清空
		int connectDelayed = WMBleAttributes.BLE_CONNECT_DELAYED;
		if (mBluetoothDevice != null) {
			disconnectGatt();
			connectDelayed *= 6;
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mBluetoothAdapter != null) {
					// if
					// (!bleAddress.equals(mBluetoothDevice.getAddress())) {
					BluetoothDevice aDevice = mBluetoothAdapter
							.getRemoteDevice(bleAddress);
					mBluetoothGatt = aDevice.connectGatt(
							getApplicationContext(), true, mBleGattCallback);
					mBluetoothDevice = aDevice;
					Log.d(TAG, "man made connect ble");
					// }
				}
			}
		}, connectDelayed);
	}

	/** 断开连接, 清空 Gatt */
	public void disconnectBleDevice() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
//		mBluetoothGatt.close();
		if (mBluetoothDevice != null) {
			mBluetoothDevice = null;
		}
		Log.d(TAG, "man made disconnect ble");
	}

	/** 断开 Gatt */
	private void disconnectGatt() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
//		mBluetoothGatt.close();
//		mBluetoothGatt = null;
	}

	/** 断线或者连接失败, 重连之前的 device */
	private void reconnectGatt() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mBluetoothGatt.connect()) {
					disconnectGatt();
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							scanBleDevices(true, true);
						}
					}, WMBleAttributes.BLE_SCAN_PERIOD);
				}
			}
		}, 100);
	}

	/** 　通用广播, 传入 action */
	private void updateBroadcast(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public WMBleService getBleService() {
			return WMBleService.this;
		}
	}

	// Getter and Setter

	public BluetoothGatt getmBluetoothGatt() {
		return mBluetoothGatt;
	}

	public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
		this.mBluetoothGatt = mBluetoothGatt;
	}

	public int getmBleState() {
		return mBleState;
	}
}
