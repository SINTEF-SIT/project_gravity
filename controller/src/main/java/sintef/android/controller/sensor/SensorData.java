package sintef.android.controller.sensor;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class SensorData {

    private final SensorSession mSensorSession;
    private final Object mSensorData;
    private final long mTimeCaptured;

    public SensorData(SensorSession sensorSession, Object sensorData, long timeCaptured) {
        mSensorSession = sensorSession;
        mSensorData = sensorData;
        mTimeCaptured = timeCaptured;
    }

    public SensorSession getSensorSession() {
        return mSensorSession;
    }

    public Object getSensorData() {
        return mSensorData;
    }

    public long getTimeCaptured() {
        return mTimeCaptured;
    }

}
