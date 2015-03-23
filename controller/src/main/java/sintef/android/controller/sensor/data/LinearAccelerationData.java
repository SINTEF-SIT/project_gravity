package sintef.android.controller.sensor.data;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class LinearAccelerationData extends SensorDataObject {
    /**
     * <h4>{@link android.hardware.Sensor#TYPE_LINEAR_ACCELERATION Sensor.TYPE_LINEAR_ACCELERATION}:</h4>
     *
     *
     * The linear acceleration sensor provides you with a three-dimensional vector representing acceleration along each device axis, excluding gravity. The following code shows you how to get an instance of the default linear acceleration sensor:
     *
     * private SensorManager mSensorManager;
     * private Sensor mSensor;
     * ...
     * mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
     * mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
     * Conceptually, this sensor provides you with acceleration data according to the following relationship:
     *
     * linear acceleration = acceleration - acceleration due to gravity
     * You typically use this sensor when you want to obtain acceleration data without the influence of gravity. For example, you could use this sensor to see how fast your car is going. The linear acceleration sensor always has an offset, which you need to remove. The simplest way to do this is to build a calibration step into your application. During calibration you can ask the user to set the device on a table, and then read the offsets for all three axes. You can then subtract that offset from the acceleration sensor's direct readings to get the actual linear acceleration.
     *
     * The sensor coordinate system is the same as the one used by the acceleration sensor, as are the units of measure (m/s2).
     *
     */

    public LinearAccelerationData(float[] values) {
        super(values);
    }

    public LinearAccelerationData(float x, float y, float z) {
        super(new float[] {x, y, z});
    }

    public float getX() {
        return getValues()[0];
    }

    public float getY() {
        return getValues()[1];
    }

    public float getZ() {
        return getValues()[2];
    }
}
