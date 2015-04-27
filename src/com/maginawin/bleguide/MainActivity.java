package com.maginawin.bleguide;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	// private static final String TAG = "MainActivity";

	private ActionBar actionBar;
	private ListView bleDevicesListView;
	private List<BluetoothDevice> devicesArray;
	private BleDevicesAdapter bleDevicesAdapter;
	private boolean isScanning;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BleService.BLE_STATUS_ABNORMAL.equals(action)) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(enableIntent);
			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras
						.getParcelable(BleService.EXTRA_DEVICE);
				if (!devicesArray.contains(device)) {
					devicesArray.add(device);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							bleDevicesAdapter.notifyDataSetChanged();
						}
					});
				}
			} else if (BleService.BLE_DEVICE_SCANING.equals(action)) {
				isScanning = true;
				invalidateOptionsMenu();
			} else if (BleService.BLE_DEVICE_STOP_SCAN.equals(action)) {
				isScanning = false;
				invalidateOptionsMenu();
			} else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				Intent servicesIntent = new Intent(MainActivity.this,
						BleGattServicesActivity.class);
				startActivity(servicesIntent);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		devicesArray = new ArrayList<BluetoothDevice>();
		bleDevicesAdapter = new BleDevicesAdapter();
		isScanning = false;

		actionBar = getActionBar();
		// Set is show app icon in action bar.
		actionBar.setDisplayShowHomeEnabled(true);
		// Make app icon as a button.
		// actionBar.setHomeButtonEnabled(true);

		bleDevicesListView = (ListView) findViewById(R.id.ble_devices_list_view);
		bleDevicesListView.setAdapter(bleDevicesAdapter);

		bleDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				BluetoothDevice device = devicesArray.get(position);
				ConnectBleDevice(device);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater mInflater = new MenuInflater(this);
		mInflater.inflate(R.menu.menu_main, menu);
		MenuItem scan = menu.findItem(R.id.scan_ble_item);
		MenuItem stop = menu.findItem(R.id.stop_scan_item);
		if (isScanning) {
			scan.setTitle("Scanning");
			scan.setEnabled(false);
			stop.setEnabled(true);
		} else {
			scan.setTitle("Scan");
			scan.setEnabled(true);
			stop.setEnabled(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setCheckable(true);
		}
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {

		} else if (itemId == R.id.scan_ble_item) {
			scanBleDevices(true);
		} else if (itemId == R.id.stop_scan_item) {
			scanBleDevices(false);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		bleDevicesAdapter.notifyDataSetChanged();
		registerReceiver(mReceiver, BleService.getIntentFilter());
		invalidateOptionsMenu();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		unregisterReceiver(mReceiver);
		scanBleDevices(false);
		devicesArray.clear();
	}

	private void scanBleDevices(final boolean enable) {
		devicesArray.clear();
		bleDevicesAdapter.notifyDataSetChanged();
		BleApplication app = (BleApplication) getApplication();
		app.getBleService().scanBleDevices(enable);
	}

	private void connectBleDevice(String address) {
		BleApplication app = (BleApplication) getApplication();
		app.getBleService().connectBleDevice(address);
	}

	private void ConnectBleDevice(BluetoothDevice device) {
		BleApplication app = (BleApplication) getApplication();
		app.getBleService().connectBleDevice(device);
	}

	private class BleDevicesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devicesArray.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (devicesArray.size() > 0) {
				ViewHolder holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_ble_devices, parent, false);
				holder.deviceName = (TextView) convertView
						.findViewById(R.id.device_name);
				holder.deviceAddress = (TextView) convertView
						.findViewById(R.id.device_address);
				BluetoothDevice device = (BluetoothDevice) devicesArray
						.get(position);
				holder.deviceName.setText(device.getName());
				;
				holder.deviceAddress.setText(device.getAddress());
				;
				return convertView;
			} else {
				return null;
			}
		}

	}

	class ViewHolder {
		private TextView deviceName;
		private TextView deviceAddress;
	}
}
