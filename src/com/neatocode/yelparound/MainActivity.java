package com.neatocode.yelparound;

import java.util.ArrayList;
import java.util.List;

import com.firebase.client.Firebase;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	
	private static final String LOG_TAG = "SensorTest";

	private TextView acc_x;
	private TextView acc_y;
	private TextView acc_z;
	private TextView gyro_x;
	private TextView gyro_y;
	private TextView gyro_z;
	
	private SensorManager mSensorManager;

	private Sensor mOrientation;

	private double previousReading = System.nanoTime();
	private final long START_TIME = System.nanoTime();
	//Double gpsLat;
	
	//Double gpsLon;
	private int stage = 0;

	Firebase accData = new Firebase("https://labapp.firebaseio.com/accData");
	
    Firebase accXFB = new Firebase("https://labapp.firebaseio.com/accData/accX");
    Firebase accYFB = new Firebase("https://labapp.firebaseio.com/accData/accY");
    Firebase accZFB = new Firebase("https://labapp.firebaseio.com/accData/accZ");
	
    private List<String> dataList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		accData.removeValue();
		Firebase accData = new Firebase("https://labapp.firebaseio.com/accData");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		acc_x = (TextView) findViewById(R.id.acc_x);
		acc_y = (TextView) findViewById(R.id.acc_y);
		acc_z = (TextView) findViewById(R.id.acc_z);
		gyro_x = (TextView) findViewById(R.id.gyro_x);
		gyro_y = (TextView) findViewById(R.id.gyro_y);
		gyro_z = (TextView) findViewById(R.id.gyro_z);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		Log.i(LOG_TAG, "STARTING NEW LOG");
		
		//mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		/*
		Intent cameraI = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(cameraI, 1); 
		stage = 2;
		*/
	}
	/*
	public void onActivityResult(int req, int res, Intent data) {
		if (req == 1 ) { // must be video request 
			Log.i(LOG_TAG, "video recorded");
			stage = 3;
		}
	}
	*/

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
		// You must implement this callback in your code.
	}

	@Override
	protected void onResume() {
		super.onResume();
		//mSensorManager.registerListener(this, mOrientation,
		//		SensorManager.SENSOR_DELAY_NORMAL);
		
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), mSensorManager.SENSOR_DELAY_FASTEST);
	   // mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), mSensorManager.SENSOR_DELAY_NORMAL);
	 
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onDestroy() {
		accData.setValue(dataList);
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	public double secondsSincePreviousReading() {
		return (System.nanoTime() - previousReading)*(Math.pow(10,-9));
		 
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		 synchronized (this) {
		        switch (event.sensor.getType()){
		            case Sensor.TYPE_LINEAR_ACCELERATION:
		            	float x = event.values[0];
		            	float y = event.values[1];
		            	float z = event.values[2];
		            	
		                acc_x.setText("x: "+Float.toString(event.values[0]));
		                acc_y.setText("y: "+Float.toString(event.values[1]));
		                acc_z.setText("z: "+Float.toString(event.values[2]));
		                
		                //accXFB.setValue(x);
		                //accYFB.setValue(y);
		                //accZFB.setValue(z);
		                //double elapsedSeconds = secondsSincePreviousReading();
		                double elapsedSecondsTotal = (System.nanoTime() - START_TIME)*(Math.pow(10,-9));
		       		 	accData.child(dataList.size()+"").setValue(elapsedSecondsTotal + " , " + x + " , " + y + " , " + z);
		                dataList.add(elapsedSecondsTotal + " , " + x + " , " + y + " , " + z);
		                
		                /*
		                if (elapsedSeconds >= .02) {
		                	previousReading = System.nanoTime();
			                double elapsedSecondsTotal = (System.nanoTime() - START_TIME)*(Math.pow(10,-9));
			       		 	
			                accData.setValue(elapsedSeconds);
			                Log.i(LOG_TAG, " " +  elapsedSeconds );
		                }
		                else {
		                	Log.i(LOG_TAG, "skipped");
		                }
						*/
		            break;
		        case Sensor.TYPE_GYROSCOPE:
		                gyro_x.setText("x: "+Float.toString(event.values[0]));
		                gyro_y.setText("y: "+Float.toString(event.values[1]));
		                double numY = event.values[1];
		                if (numY > 0.8) {
		                	Log.i("TAG", "LEFT");
		                	
		                }
		                if (numY < -0.8) {
		                	Log.i("TAG", "RIGHT");
		                	
		                }
		                gyro_z.setText("z: "+Float.toString(event.values[2]));
		        break;
		 
		        }
		    }
	}


}
