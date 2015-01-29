package sintef.android.algorithm.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.greenrobot.event.EventBus;

/**
 * Created by samyboy89 on 29/01/15.
 */
public class SensorHelper implements SensorEventListener {

    private EventBus mEventBus;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    public SensorHelper(Context context) {
        mEventBus = EventBus.getDefault();
        mEventBus.registerSticky(this);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null){
            mEventBus.post("Success! There's an accelerometer.");
            mAccelerometer = accelerometer;
        } else {
            mEventBus.post("Failure! No accelerometer.");
        }
    }

    public void onEvent(EventTypes eventType) {
        switch (eventType) {
            case ONRESUME:
                if (mAccelerometer != null) mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float value = event.values[0];
        mEventBus.post(new SensorData(value));
        // Do something with this sensor value.
    }

}
