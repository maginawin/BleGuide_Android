package com.mymcu.bleapi;

public class WMBleAttributes {
	
	/** ble 状态异常常量 */
	public static final String BLE_STATUS_ABNORMAL = "com.mymcu.bleapi.bleStatusAbnormal";
	
	/** 查找蓝牙的周期 (ms) */
	public static final int BLE_SCAN_PERIOD = 8000;
	
	/** 连接蓝牙的延时, 为了满足大部分的安卓手机 (ms) */
	public static final int BLE_CONNECT_DELAYED = 500;

	/** 查找到蓝牙的广播 */
	public static final String BLE_ON_LE_SCAN = "com.mymcu.bleapi.bleOnLeScan";
	
	/** 蓝牙设备的 Extra, RSSI */
	public static final String EXTRA_BLE_DEVICE = "com.mymcu.bleapi.extraBleDevice";
	public static final String EXTRA_BLE_DEVICE_RSSI = "com.mymcu.bleapi.extraBleDeviceRssi";
}
