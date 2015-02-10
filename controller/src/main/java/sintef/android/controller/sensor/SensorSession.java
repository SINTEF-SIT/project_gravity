package sintef.android.controller.sensor;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class SensorSession {

    private final String mId;
    private final int mSensorType;
    private final SensorDevice mSensorDevice;
    private final SensorLocation mSensorLocation;

    protected SensorSession(String id, int sensorType, SensorDevice sensorDevice, SensorLocation sensorLocation) {
        mId = id;
        mSensorType = sensorType;
        mSensorDevice = sensorDevice;
        mSensorLocation = sensorLocation;
    }

    protected SensorSession(String id) {
        mId = id;
        mSensorType = -1;
        mSensorDevice = null;
        mSensorLocation = null;
    }

    public String getId() {
        return mId;
    }

    public int getSensorType() {
        return mSensorType;
    }

    public SensorDevice getSensorDevice() {
        return mSensorDevice;
    }

    public SensorLocation getSensorLocation() {
        return mSensorLocation;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SensorSession))
            return false;
        if (obj == this)
            return true;

        SensorSession rhs = (SensorSession) obj;
        return mId.equals(rhs.mId);
    }
}
