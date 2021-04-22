package com.example.wifinavigation_find;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
public class MapActivity extends Activity implements SensorEventListener {
	public WifiManager wifiManager;
	private float currentDegree = 0f;
	float degree = 0f;
	private long lastUpdate = 0;
	private float last_x, last_y, last_z;
	private static final float SHAKE_THRESHOLD = 600;
	private float[] gravity = new float[3];
	public Handler scanHandler = new Handler();
	static int step = 0;
	// device sensor manager
	private SensorManager mSensorManager;
	OurView view;
	Canvas canvas;
	Bitmap arrow, miniArrow, backgroundMap, tempMap;
	float x_axis, y_axis, xS, yS;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new OurView(this);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		tempMap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
		arrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.the_arrow);
		x_axis = GetterSetter.x;
		y_axis = GetterSetter.y;
		xS = 20;
		yS = 20;
		setContentView(view);
		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
		view.pause();	
	}
	@Override
	protected void onResume() {
		super.onResume();
		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);				
		view.resume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		x_axis = e.getX();
		y_axis = e.getY() - 100f;
		return true;
	}
	public class OurView extends SurfaceView implements Runnable {
		Thread thread = null;
		SurfaceHolder holder;
		boolean run = false;
		public OurView(Context context) {
			super(context);
			holder = getHolder();
		}
		@Override
		public void run() {
			while (run == true) {
				// perform canvas drawing
				if (!holder.getSurface().isValid()) {
					continue;
				}
				canvas = holder.lockCanvas();
				Paint paint = new Paint();
				paint.setColor(Color.RED);
				paint.setTextSize(20);
				// canvas.drawARGB(0, 0, 0, 0);

				backgroundMap = Bitmap.createScaledBitmap(tempMap,
						canvas.getWidth(), canvas.getHeight(), true);
				canvas.drawBitmap(backgroundMap, 0, 0, null);
				miniArrow = Bitmap.createScaledBitmap(arrow, 25, 25, true);
				Matrix matrix = new Matrix();
				matrix.setTranslate(x_axis - (miniArrow.getWidth() / 2), y_axis
						- (miniArrow.getHeight() / 2));
				matrix.preRotate(currentDegree, miniArrow.getWidth() / 2,
						miniArrow.getHeight() / 2);
				// canvas.drawText("x is " + x_axis + ": y is " + y_axis +"D: "
				// + currentDegree,
				// x_axis + 20, y_axis + 20, paint);
				canvas.drawText("x is " + x_axis, x_axis + 20, y_axis + 20,
						paint);
				canvas.drawText("y is " + y_axis, x_axis + 20, y_axis + 40,
						paint);
				canvas.drawText("" + currentDegree, x_axis + 20, y_axis + 60,
						paint);
				canvas.drawText("Steps: " + step, x_axis + 20, y_axis + 80,
						paint);
				// canvas.drawCircle(x_axis, y_axis, 10f, paint);
				canvas.drawBitmap(miniArrow, matrix, paint);
				holder.unlockCanvasAndPost(canvas);
			}
		}
		public void pause() {
			run = false;
			while (true) {
				try {
					thread.join();
					backgroundMap.recycle();
					miniArrow.recycle();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			thread = null;
		}
		public void resume() {
			run = true;
			thread = new Thread(this);
			thread.start();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;
		currentDegree = getDirection(event);
		// Log.d("Angle", String.valueOf(direction));
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			final float alpha = 0.8f;
			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
			float x = event.values[0] - gravity[0];
			float y = event.values[1] - gravity[1];
			float z = event.values[2] - gravity[2];
			long currTime = System.currentTimeMillis();
			if ((currTime - lastUpdate) > 100) {
				long diffTime = (currTime - lastUpdate);
				lastUpdate = currTime;
				float speed = Math.abs(x + y + z - last_x - last_y - last_z)
						/ diffTime * 40000;				
				if (speed > SHAKE_THRESHOLD) {

					if (x_axis > view.getWidth() - miniArrow.getWidth()) {
						x_axis = view.getWidth() - miniArrow.getWidth();
					}
					if (y_axis > view.getHeight()) {
						y_axis = view.getHeight();
					}
					if (x_axis + xS <= 0) {
						x_axis = 0;
						xS = 0;
					}

					if (y_axis + yS <= 0) {
						y_axis = 0;
						yS = 0;
					}
					
					// for north
					if (currentDegree >= 0 && currentDegree < 90) {

						if (currentDegree >= 0 && currentDegree < 45) {

							x_axis -= 2.5;
							y_axis += 5;

						} else {
							x_axis -= 5;
							y_axis += 2.5;
						}
						// for East
					} else if (currentDegree > 90 && currentDegree < 180) {

						if (currentDegree >= 90 && currentDegree < 135) {

							x_axis -= 5;
							y_axis -= 2.5;

						} else {
							x_axis -= 2.5;
							y_axis -= 5;
						}
						// for South
					} else if (currentDegree > 180 && currentDegree < 70) {

						if (currentDegree >= 180 && currentDegree < 225) {
							x_axis += 2.5;
							y_axis -= 5;
						} else {
							x_axis += 5;
							y_axis -= 2.5;
						}
						// for West
					} else if (currentDegree > 270 && currentDegree <= 360) {

						if (currentDegree >= 270 && currentDegree < 315) {
							x_axis += 5;
							y_axis += 2.5;
						} else {
							x_axis += 2.5;
							y_axis += 5;
						}
					} else {
					}step++;
				}
				last_x = x;
				last_y = y;
				last_z = z;

			}}
	}

	private float getDirection(SensorEvent event) {
		Sensor compassSensor = event.sensor;
		if (compassSensor.getType() == Sensor.TYPE_ORIENTATION) {
			// get the angle around the z-axis rotated
			degree = Math.round(event.values[0]);
		}
		return degree;
	}
}
