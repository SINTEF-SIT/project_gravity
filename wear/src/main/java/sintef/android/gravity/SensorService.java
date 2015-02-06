package sintef.android.gravity;

/*
 * Much based on https://github.com/pocmo/SensorDashboard
 * Such copy. Very paste.
 */

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import sintef.android.controller.common.Constants;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = Constants.TAG_WEAR;

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    SensorManager mSensorManager;
    private DeviceClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Project Gravity");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.drawable.ic_launcher);

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);

        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }

            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w(TAG, "No Gyroscope Sensor found");
            }

            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }

        }
    }

    private void stopMeasurement() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
