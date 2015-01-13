package net.gerosyab.magic8ball.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class Shaker {
	private final double thresholdValue = 1.9d;
	private SensorManager mSensorManager;
	private long mTimeCheckpoint;
	private double mThreshold = Math.pow(thresholdValue, 2) * Math.pow(SensorManager.GRAVITY_EARTH, 2);
	private long mInterval = 500;
	private double resultantForce;
	private float sx;
	private float sy;
	private float sz;
	private Shaker.Callback mCallBack;

	public Shaker(Context context, Shaker.Callback callBack) {
		this.mCallBack = callBack;
		this.mTimeCheckpoint = 0;

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	private SensorEventListener listener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent e) {
			MyLog.d("Shaker", "Shaker onSensorChanged()");
			if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				
				sx = e.values[0];
				sy = e.values[1];
				sz = e.values[2];
				
				resultantForce = (sx * sx) + (sy * sy) + (sz * sz);
				
				if (mThreshold < resultantForce) {
					isShaking();
				} else {
					isNotShaking();
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public void open(){
		MyLog.d("Shaker", "Shaker open()");
		mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
	}
	
	public void close() {
		MyLog.d("Shaker", "Shaker close()");
		mSensorManager.unregisterListener(listener);
	}

	private void isShaking() {
		mTimeCheckpoint = SystemClock.elapsedRealtime();
	}

	private void isNotShaking() {
		long curTime = SystemClock.elapsedRealtime();

		if (mTimeCheckpoint > 0) {
			if (curTime - mTimeCheckpoint > mInterval) {
				mTimeCheckpoint = 0;
				if (mCallBack != null) {
					mCallBack.shakingDetected();
				}
			}
		}
	}
	
	public interface Callback {
		void shakingDetected();
	}
}