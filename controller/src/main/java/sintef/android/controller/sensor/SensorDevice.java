package sintef.android.controller.sensor;

/**
 * Created by samyboy89 on 03/02/15.
 */
public enum SensorDevice {
    WATCH("watch"),
    PHONE("phone"),
    OTHER("other");

    private String data;

    private SensorDevice(String data) {
        this.data = data;
    }

    public String getValue() {
        return data;
    }
}
