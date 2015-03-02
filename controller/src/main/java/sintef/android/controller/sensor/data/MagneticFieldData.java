package sintef.android.controller.sensor.data;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class MagneticFieldData extends SensorDataObject {

    /**
     * All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis.
     */

    public MagneticFieldData(float[] values) {
        super(values);
    }

    public MagneticFieldData(float x, float y, float z) {
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
