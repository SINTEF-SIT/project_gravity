package sintef.android.controller.sensor.data;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class GravityData extends SensorDataObject {
    /**
     *  <h4>{@link android.hardware.Sensor#TYPE_GRAVITY Sensor.TYPE_GRAVITY}:</h4>
     *
     *  <p>A three dimensional vector indicating the direction and magnitude of gravity.  Units
     *  are m/s^2. The coordinate system is the same as is used by the acceleration sensor.</p>
     *  <p><b>Note:</b> When the device is at rest, the output of the gravity sensor should be identical
     *  to that of the accelerometer.</p>
     */

    public GravityData(float[] values) {
        super(values);
    }

    public GravityData(float x, float y, float z) {
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
