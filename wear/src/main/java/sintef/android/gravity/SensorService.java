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

import java.util.HashMap;

import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = Constants.TAG_WEAR;

    private HashMap<Integer, SensorSession> mSensorGroup = new HashMap<>();
    private SensorManager mSensorManager;
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

        for (int key : Constants.SENSORS_WEAR.keySet()) {
            addSensorToSystem("watch:" + Constants.SENSORS_WEAR.get(key), key, SensorDevice.WATCH, SensorLocation.RIGHT_ARM);
        }
        /*
        addSensorToSystem("watch:accelerometer", Sensor.TYPE_ACCELEROMETER, SensorDevice.WATCH, SensorLocation.RIGHT_ARM);
        addSensorToSystem("watch:gyroscope", Sensor.TYPE_GYROSCOPE, SensorDevice.WATCH, SensorLocation.RIGHT_ARM);
        addSensorToSystem("watch:rotation_vector", Sensor.TYPE_ROTATION_VECTOR, SensorDevice.WATCH, SensorLocation.RIGHT_ARM);
        */
    }

    private void addSensorToSystem(String id, int type, SensorDevice device, SensorLocation location) {
        SensorSession sensorSession = new SensorSession(id, type, device, location);
        mSensorGroup.put(type, sensorSession);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(type), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopMeasurement() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.addSensorData(mSensorGroup.get(event.sensor.getType()).getStringFromSession(), event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
