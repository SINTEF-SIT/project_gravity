package sintef.android.controller.common;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iver on 06.02.15.
 */
public class Constants {
    public static final String SESSION = "session";
    public static final String ACCURACY = "accuracy";
    public static final String TIMESTAMP = "timestamp";
    public static final String VALUES = "values";
    public static final String FILTER = "filter";
    public static final String TAG_WEAR = "GRAVITY/WEAR";
    public static final String TAG_MOBILE = "GRAVITY/MOBILE";

    public static final String DATA_MAP_PATH = "/sensor/";
    public static final int CLIENT_CONNECTION_TIMEOUT = 15000;
    public static final int ALL_SENSORS_FILTER = 99;
    public static final int WEAR_BUFFER_SIZE = 10; // in seconds

    public static final int SENSOR_PULL_FREQ = 50; // in Hz
    public static final int SENSOR_BATCHING_DELAY = 10; // in seconds
    public static final Map<Integer, String> SENSORS_WEAR;
    static {
        SENSORS_WEAR = new HashMap<Integer, String>();
        SENSORS_WEAR.put(Sensor.TYPE_ACCELEROMETER, "accelerometer");
        SENSORS_WEAR.put(Sensor.TYPE_GYROSCOPE, "gyroscope");
        SENSORS_WEAR.put(Sensor.TYPE_ROTATION_VECTOR, "rotation_vector");
    }

    public static final String SENSOR_SESSION_SPLIT_KEY = ";";

    public static final int ALGORITHM_SEND_FREQUENCY = 1000;
    public static final int ALGORITHM_SEND_OVERLAPPING = 1000;
    public static final int ALGORITHM_SEND_AMOUNT = ALGORITHM_SEND_FREQUENCY + ALGORITHM_SEND_OVERLAPPING;

    public static final String PREFS_FIRST_START = "pref_first_start";
    public static final String PREFS_NEXT_OF_KIN_NAME = "pref_next_of_kin_name";
    public static final String PREFS_NEXT_OF_KIN_TELEPHONE = "pref_next_of_kin_telephone";
}
