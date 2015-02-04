package sintef.android.controller.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import de.greenrobot.event.EventBus;
import sintef.android.algorithm.utils.EventTypes;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class SensorManager implements SensorEventListener {

    private EventBus mEventBus;

    private android.hardware.SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;

    private SensorSession mSensorAccelerometerSession;
    private SensorSession mSensorGravitySession;

    public SensorManager(Context context) {
        mEventBus = EventBus.getDefault();
        mEventBus.registerSticky(this);

        mSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (accelerometer != null){
            mEventBus.post("Success! There's an accelerometer.");
            mAccelerometer = accelerometer;
            mGravity = gravity;
        } else {
            mEventBus.post("Failure! No accelerometer.");
        }

        SensorHandshake gravityHandshake = SensorHandshake.createConnectHandshake("test:id:gravity", SensorType.GRAVITY, SensorLocation.RIGHT_ARM);
        mSensorGravitySession = gravityHandshake.getSensorSession();
        mEventBus.post(gravityHandshake);

        SensorHandshake accelerometerHandshake = SensorHandshake.createConnectHandshake("test:id:accelerometer", SensorType.ACCELEROMETER, SensorLocation.LEFT_ARM);
        mSensorAccelerometerSession = accelerometerHandshake.getSensorSession();
        mEventBus.post(accelerometerHandshake);
    }

    public void onEvent(EventTypes eventType) {
        switch (eventType) {
            case ONRESUME:
                if (mAccelerometer != null) mSensorManager.registerListener(this, mAccelerometer, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
                if (mGravity != null) mSensorManager.registerListener(this, mGravity, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case ONPAUSE:
                mSensorManager.unregisterListener(this);
                break;
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        SensorSession sensorSession = null;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorSession = mSensorAccelerometerSession;
                break;
            case Sensor.TYPE_GRAVITY:
                sensorSession = mSensorGravitySession;
                break;
        }
        if (sensorSession != null) mEventBus.post(new SensorData(sensorSession, event.values[0], System.currentTimeMillis()));
    }

}
