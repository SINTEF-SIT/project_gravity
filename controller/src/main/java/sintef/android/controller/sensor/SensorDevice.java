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

    public static SensorDevice fromString(String lookup) {
        if (lookup != null) {
            for (SensorDevice device : SensorDevice.values()) {
                if (lookup.equalsIgnoreCase(device.data)) return device;
            }
        }
            throw new IllegalArgumentException("No such device: " + lookup);
    }
}
