package com.xjq.music.shaker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * 晃动监听类，用于监听系统传感器。
 */
public class ShakeListener implements SensorEventListener {
	// 定义传感器晃动速度
	private static final int SPEED_SHRESHOLD = 3000;
	// 坐标更新时间
	private static final int UPTATE_INTERVAL_TIME = 70;
	private static final boolean DEBUG = true;
	private static final String TAG = "xjq";
	// 传感器管理实例变量
	private SensorManager sensorManager;
	// 传感器实例变量
	private Sensor sensor;
	// 传感器监听器
	private OnShakeListener onShakeListener;

	private Context mContext;
	// 历史保存坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 历史更新时间
	private long lastUpdateTime;


	public ShakeListener(Context c) {
		if (DEBUG)
			Log.i(TAG, "	***ShakeListener");
		mContext = c;
		start();
	}

	// 注册传感器
	public void start() {
		// 1.通过getSystemService获取sensor服务,其实就是初始化一个SensorManager实例。
		if (DEBUG)
			Log.i(TAG, "	--->ShakeListener--->start");
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			// 2.通过SensorManager 的getDefaultSensor方法获取指定类型的传感器的Sensor对象。
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}

		if (sensor != null) {
			//4. 通过 SensorManager 的registerListener方法注册监听，获取传感器变化值。
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}

	}

	//注销监听
	public void stop() {
		if (DEBUG)
			Log.i(TAG, "	--->ShakeListener--->stop");
		sensorManager.unregisterListener(this);
	}

	// 给监听器赋值
	public void setOnShakeListener(OnShakeListener listener) {
		onShakeListener = listener;
	}

	// 3.实现SensorEventListener接口的onSensorChanged
	//当传感器的值发生变化时，会走这里
	//当传感器的值发生变化时，会调用onSensorChanged方法。当传感器的精度变化时会调用onAccuracyChanged方法。
	public void onSensorChanged(SensorEvent event) {
/*		if (DEBUG)
			Log.i(TAG, "	--->ShakeListener--->onSensorChanged");*/
		// 获取当前系统时间
		long currentUpdateTime = System.currentTimeMillis();
		// 距离上一次更新的时间
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 限制更新时间
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 更新时间
		lastUpdateTime = currentUpdateTime;

		// 获取坐标值
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 坐标变更
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// 更新历史坐标
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ timeInterval * 10000;
/*		if (DEBUG)
			Log.i(TAG, "	--->ShakeListener--->onSensorChanged*********");*/
		// 大于指定速度则判断为晃动
		if (speed >= SPEED_SHRESHOLD) {
			onShakeListener.onShake();
		}
	}
	
	//当传感器的精度变化时，会走这里
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	// 晃动监听接口
	public interface OnShakeListener {
		public void onShake();
	}

}