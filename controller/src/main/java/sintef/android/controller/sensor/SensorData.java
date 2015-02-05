package sintef.android.controller.sensor;

import sintef.android.controller.sensor.data.SensorDataObject;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class SensorData implements Comparable<SensorData> {

    private final SensorSession mSensorSession;
    private final SensorDataObject mSensorData;
    private final long mTimeCaptured;

    public SensorData(SensorSession sensorSession, SensorDataObject sensorData, long timeCaptured) {
        mSensorSession = sensorSession;
        mSensorData = sensorData;
        mTimeCaptured = timeCaptured;
    }

    public SensorSession getSensorSession() {
        return mSensorSession;
    }

    public SensorDataObject getSensorData() {
        return mSensorData;
    }

    public long getTimeCaptured() {
        return mTimeCaptured;
    }

    @Override
    public int compareTo(SensorData sensorData) {
        return (int) (mTimeCaptured - sensorData.getTimeCaptured());
    }
}
