package sintef.android.gravity;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;

public class SensorRecorder implements SensorEventListener {
    private static final String TAG = Constants.TAG_WEAR;

    private HashMap<Integer, SensorSession> mSensorGroup = new HashMap<>();
    private SensorManager mSensorManager;
    private DeviceClient client;
    private static SensorRecorder instance;

    public static synchronized SensorRecorder getInstance(Context context) {
        if (instance == null) {
            instance = new SensorRecorder(context.getApplicationContext());
        }

        return instance;
    }

    private SensorRecorder(Context context) {
        //android.os.Debug.waitForDebugger();
        Log.w("SS", "started sensor service");

        client = DeviceClient.getInstance(context);

        mSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        for (int key : Constants.SENSORS_WEAR.keySet()) {
            addSensorToSystem("watch:" + Constants.SENSORS_WEAR.get(key), key, SensorDevice.WATCH, Constants.WEAR_SENSOR_LOCATION);
        }

        /*
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(getString(R.string.watch_notification_title));
        builder.setContentText(getString(R.string.watch_notification_text));
        builder.setSmallIcon(R.drawable.ic_launcher);
        startForeground(1, builder.build());
        */
    }

    private void addSensorToSystem(String id, int type, SensorDevice device, SensorLocation location) {
        SensorSession sensorSession = new SensorSession(id, type, device, location);
        mSensorGroup.put(type, sensorSession);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(type), Constants.SENSOR_PULL_FREQ);
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
