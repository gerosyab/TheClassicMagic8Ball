package net.gerosyab.magic8ball.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class Mover {
	private SensorManager mSensorManager;
	private float sx;
	private float sy;
	private float sz;

	public Mover(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	private SensorEventListener listener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent e) {
			MyLog.d("Shaker", "Mover onSensorChanged()");
			if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				
				sx = e.values[0];
				sy = e.values[1];
				sz = e.values[2];
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public void open(){
		MyLog.d("Shaker", "Mover open()");
		mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
	}
	
	public void close() {
		MyLog.d("Shaker", "Mover close()");
		mSensorManager.unregisterListener(listener);
	}

	public float getSx(){
		return sx;
	}
	
	public float getSy(){
		return sy;
	}
	
	public float getSz(){
		return sz;
	}
}