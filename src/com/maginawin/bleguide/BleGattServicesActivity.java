package com.maginawin.bleguide;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BleGattServicesActivity extends Activity {

	private ListView mListView;
	private List<BluetoothGattService> mServices;
	private ServiceAdapter mAdapter;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble_gatt_services);
		mListView = (ListView) findViewById(R.id.ble_services_list_view);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BleApplication app = (BleApplication) getApplication();
		mServices = app.getBleService().getBluetoothGatt().getServices();
		mAdapter = new ServiceAdapter();
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		registerReceiver(mReceiver, BleService.getIntentFilter());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		unregisterReceiver(mReceiver);
	}

	private class ServiceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mServices.size();
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
			if (mServices.size() > 0) {
				BluetoothGattService service = mServices.get(position);
				ViewHolder holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_ble_devices, parent, false);
				holder.serviceName = (TextView) convertView
						.findViewById(R.id.device_name);
				holder.serviceUuid = (TextView) convertView
						.findViewById(R.id.device_address);
				holder.serviceName.setText("Service : "
						+ service.getCharacteristics().size()
						+ "Characteristics.");
				holder.serviceUuid.setText(service.getUuid().toString());
				return convertView;
			} else {
				return null;
			}
		}

	}

	private class ViewHolder {
		private TextView serviceName;
		private TextView serviceUuid;
	}
}
