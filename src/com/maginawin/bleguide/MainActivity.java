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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private ActionBar actionBar;
	private ListView bleDevicesListView;
	private List<BluetoothDevice> devicesArray;
	private BleDevicesAdapter bleDevicesAdapter;

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
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		devicesArray = new ArrayList<BluetoothDevice>();
		bleDevicesAdapter = new BleDevicesAdapter();

		actionBar = getActionBar();
		// Set is show app icon in action bar.
		actionBar.setDisplayShowHomeEnabled(true);
		// Make app icon as a button.
		// actionBar.setHomeButtonEnabled(true);

		bleDevicesListView = (ListView) findViewById(R.id.ble_devices_list_view);
		bleDevicesListView.setAdapter(bleDevicesAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu_main, menu);
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
			devicesArray.clear();
			bleDevicesAdapter.notifyDataSetChanged();
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
		// scanBleDevices(true);
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
		BleApplication app = (BleApplication) getApplication();
		app.getBleService().scanBleDevices(enable);
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
						.inflate(R.layout.item_ble_devices, null);
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
