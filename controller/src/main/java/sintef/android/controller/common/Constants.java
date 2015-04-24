/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package sintef.android.controller.common;

import android.hardware.Sensor;

import java.util.HashMap;
import java.util.Map;

import sintef.android.controller.algorithm.AlgorithmsToChoose;
import sintef.android.controller.sensor.SensorLocation;

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

    public static final int SENSOR_PULL_FREQ = 40000; // in Hz
    public static final int SENSOR_BATCHING_DELAY = 10; // in seconds

    public static final Map<Integer, String> SENSORS_WEAR;
//    static {
//        SENSORS_WEAR = new HashMap<>();
//        SENSORS_WEAR.put(Sensor.TYPE_ACCELEROMETER, "accelerometer");
//        SENSORS_WEAR.put(Sensor.TYPE_GYROSCOPE, "gyroscope");
//        SENSORS_WEAR.put(Sensor.TYPE_ROTATION_VECTOR, "rotation_vector");
//    }
    static {
        SENSORS_WEAR = new HashMap<>();
        SENSORS_WEAR.put(Sensor.TYPE_LINEAR_ACCELERATION, "linear_acceleration");
    }
    public static final SensorLocation WEAR_SENSOR_LOCATION = SensorLocation.RIGHT_ARM;

    public static final long[] ALARM_VIBRATION_PATTERN_ON_WATCH = {0, 100, 1000};

    public static final String SENSOR_SESSION_SPLIT_KEY = ";";

    public static final int ALGORITHM_SEND_FREQUENCY = 1000;
    public static final int ALGORITHM_SEND_OVERLAPPING = 1000;
    public static final int ALGORITHM_SEND_AMOUNT = ALGORITHM_SEND_FREQUENCY + ALGORITHM_SEND_OVERLAPPING;

    public static final String WATCH_ALARM_ACTIVITY_RUN_ALARM = "run_alarm";

    public static final String PREFS_FIRST_START = "pref_first_start";
    public static final String PREFS_NEXT_OF_KIN_NAME = "pref_next_of_kin_name";
    public static final String PREFS_NEXT_OF_KIN_TELEPHONE = "pref_next_of_kin_telephone";

    public static final String PREFS_ALGORITHM = "pref_algorithm";
    public static final int PREFS_DEFAULT_ALGORITHM = AlgorithmsToChoose.ALL.getId();

}
