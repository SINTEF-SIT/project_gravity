package sintef.android.controller.sensor.data;

/**
 * Created by samyboy89 on 05/02/15.
 */
public abstract class SensorDataObject {

    protected final float mValues[];

    public SensorDataObject(float values[]) {
        mValues = values;
    }

    /*
     * SensorType.ACCELEROMETER:
     * (All values are in SI units (m/s^2))
     * values[0]: Acceleration minus Gx on the x-axis
     * values[1]: Acceleration minus Gy on the y-axis
     * values[2]: Acceleration minus Gz on the z-axis
     *
     * SensorType.MAGNETIC_FIELD:
     * (All values are in micro-Tesla (uT))
     * values[0]: Ambient magnetic field on the x-axis
     * values[1]: Ambient magnetic field on the y-axis
     * values[2]: Ambient magnetic field on the z-axis
     *
     * SensorType.GYROSCOPE:
     * values[0]: Angular speed around the x-axis
     * values[1]: Angular speed around the y-axis
     * values[2]: Angular speed around the z-axis
     *
     * SensorType.LIGHT:
     * values[0]: Ambient light level in SI lux units
     *
     * SensorType.PRESSURE:
     * values[0]: Atmospheric pressure in hPa (millibar)
     *
     * SensorType.PROXIMITY:
     * values[0]: Proximity sensor distance measured in centimeters
     *
     * SensorType.GRAVITY:
     * (All values have units of m/s^2)
     * values[0]: Direction and magnitude of gravity on the x-axis
     * values[1]: Direction and magnitude of gravity on the y-axis
     * values[2]: Direction and magnitude of gravity on the z-axis
     *
     * SensorType.LINEAR_ACCELERATION:
     * (All values have units of m/s^2)
     * values[0]: Acceleration along the x-axis
     * values[1]: Acceleration along the y-axis
     * values[2]: Acceleration along the z-axis
     *
     * SensorType.ROTATION_VECTOR:
     * values[0]: x*sin(θ/2)
     * values[1]: y*sin(θ/2)
     * values[2]: z*sin(θ/2)
     * values[3]: cos(θ/2)
     * values[4]: estimated heading Accuracy (in radians) (-1 if unavailable)
     *
     * SensorType.ORIENTATION:
     * values[0]: Azimuth, angle between the magnetic north direction and the y-axis, around the z-axis (0 to 359). 0=North, 90=East, 180=South, 270=West
     * values[1]: Pitch, rotation around x-axis (-180 to 180), with positive values when the z-axis moves toward the y-axis.
     * values[2]: Roll, rotation around the x-axis (-90 to 90) increasing as the device moves clockwise.
     *
     * SensorType.RELATIVE_HUMIDITY:
     * values[0]: Relative ambient air humidity in percent
     *
     * SensorType.AMBIENT_TEMPERATURE:
     * values[0]: ambient (room) temperature in degree Celsius.
     *
     * SensorType.MAGNETIC_FIELD_UNCALIBRATED:
     * values[0] = x_uncalib
     * values[1] = y_uncalib
     * values[2] = z_uncalib
     * values[3] = x_bias
     * values[4] = y_bias
     * values[5] = z_bias
     *
     * SensorType.GYROSCOPE_UNCALIBRATED:
     * values[0] : angular speed (w/o drift compensation) around the X axis in rad/s
     * values[1] : angular speed (w/o drift compensation) around the Y axis in rad/s
     * values[2] : angular speed (w/o drift compensation) around the Z axis in rad/s
     * values[3] : estimated drift around X axis in rad/s
     * values[4] : estimated drift around Y axis in rad/s
     * values[5] : estimated drift around Z axis in rad/s
     */

    public float[] getValues() {
        return mValues;
    }
}
