package sintef.android.controller.sensor;

/**
 * Created by samyboy89 on 03/02/15.
 */
public class SensorHandshake {

    private final SensorSession mSensorSession;
    private final Type mType;

    public SensorHandshake(SensorSession sensorSession, Type type) {
        mSensorSession = sensorSession;
        mType = type;
    }

    public static SensorHandshake createConnectHandshake(String constantUniqueIdForDeviceSessionLocationAndType, SensorType sensorType, SensorLocation sensorLocation) {
        SensorSession sensorSession = new SensorSession(constantUniqueIdForDeviceSessionLocationAndType, sensorType, sensorLocation);
        return new SensorHandshake(sensorSession, Type.CONNECT);
    }

    public static SensorHandshake createDisconnectHandshake(String constantUniqueIdForDeviceSessionLocationAndType) {
        SensorSession sensorSession = new SensorSession(constantUniqueIdForDeviceSessionLocationAndType);
        return new SensorHandshake(sensorSession, Type.DISCONNECT);
    }


    public SensorSession getSensorSession() {
        return mSensorSession;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        CONNECT, DISCONNECT
    }

}
