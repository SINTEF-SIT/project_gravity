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

package sintef.android.controller.tests;

import android.hardware.Sensor;
import android.util.Log;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.algorithm.AlgorithmsToChoose;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.MagneticFieldData;
import sintef.android.controller.sensor.data.RotationVectorData;
import sintef.android.controller.utils.PreferencesHelper;

/**
 * Created by samyboy89 on 14/04/15.
 */
public class IntegrationTest {

    private static boolean mReceiveAnswer;
    private static int mPreviousAlgorithmId;

    public static final String TEST_ANSWER_TAG = "test_answer";

    public IntegrationTest() {
        EventBus.getDefault().register(this);
    }

    /**
    |-------------------------------------------------------------------------|
    |  Test ID                 |  1: Threshold algorithm, fall registered     |
    |-------------------------------------------------------------------------|
    |  Modules involved        |  Controller, Algorithm                       |
    |-------------------------------------------------------------------------|
    |  Case description        |  Sending sensor data from controller that    |
    |                          |  causes a fall detection in the threshold    |
    |                          |  based algorithm.                            |
    |-------------------------------------------------------------------------|
    |  Expected result         |  isFall returning true, and FALL_DETECTED    |
    |                          |  posted to Event bus                         |
    |-------------------------------------------------------------------------|
     */
    public static final String TEST_TAG_1 = "test_1";
    public static final String TEST_SENSOR_1 = "test_sensor_1";

    public void runTestId1() {
        // Store previous algorithm used, to be restored at the end of the test
        mPreviousAlgorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        // Force the application to use PHONE_THRESHOLD
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, AlgorithmsToChoose.ID_PHONE_THRESHOLD);

        // Create a new mock sensor session
        SensorSession sensorSessionAcc = new SensorSession(TEST_SENSOR_1 + ":ACC", Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionRot = new SensorSession(TEST_SENSOR_1 + ":ROT", Sensor.TYPE_ROTATION_VECTOR, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionMag = new SensorSession(TEST_SENSOR_1 + ":MAG", Sensor.TYPE_MAGNETIC_FIELD, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create mock sensor data objects
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(100, 100, 100);
        RotationVectorData rotationVectorData = new RotationVectorData(new float[] {0.27839798f, 0.16639067f, -0.11554366f, 0.9388601f, 0.0f});
        MagneticFieldData magneticFieldData = new MagneticFieldData(new float[] {0.59999996f, 2.3999999f, -39.18f});

        // Allow to receive data
        mReceiveAnswer = true;

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSessionAcc, linearAccelerationData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionRot, rotationVectorData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionMag, magneticFieldData, System.currentTimeMillis()));

        // Write log to console
        Log.i(TEST_TAG_1, "Sent data");
    }

    /**
     |-------------------------------------------------------------------------|
     |  Test ID                 |  2: Threshold algorithm, fall not registered |
     |-------------------------------------------------------------------------|
     |  Modules involved        |  Controller, Algorithm                       |
     |-------------------------------------------------------------------------|
     |  Case description        |  Sending sensor data from controller that    |
     |                          |  doesn't cause a fall detection in the       |
     |                          |  threshold based algorithm.                  |
     |-------------------------------------------------------------------------|
     |  Expected result         |  isFall returning false, and FALL_DETECTED   |
     |                          |  not posted to Event bus                     |
     |-------------------------------------------------------------------------|
     */

    public static final String TEST_TAG_2 = "test_2";
    public static final String TEST_SENSOR_2 = "test_sensor_2";

    public void runTestId2() {
        // Store previous algorithm used, to be restored at the end of the test
        mPreviousAlgorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        // Force the application to use PHONE_THRESHOLD
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, AlgorithmsToChoose.ID_PHONE_THRESHOLD);

        // Create a new mock sensor session
        SensorSession sensorSessionAcc = new SensorSession(TEST_SENSOR_2 + ":ACC", Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionRot = new SensorSession(TEST_SENSOR_2 + ":ROT", Sensor.TYPE_ROTATION_VECTOR, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionMag = new SensorSession(TEST_SENSOR_2 + ":MAG", Sensor.TYPE_MAGNETIC_FIELD, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create mock sensor data objects
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(0, 0, 0);
        RotationVectorData rotationVectorData = new RotationVectorData(new float[] {0.27839798f, 0.16639067f, -0.11554366f, 0.9388601f, 0.0f});
        MagneticFieldData magneticFieldData = new MagneticFieldData(new float[] {0.59999996f, 2.3999999f, -39.18f});

        // Allow to receive data
        mReceiveAnswer = true;

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSessionAcc, linearAccelerationData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionRot, rotationVectorData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionMag, magneticFieldData, System.currentTimeMillis()));

        // Write log to console
        Log.i(TEST_TAG_2, "Sent data");
    }

    /**
     |-------------------------------------------------------------------------|
     |  Test ID                 |  3: Pattern recognition algorithm,           |
     |                          |  fall registered                             |
     |-------------------------------------------------------------------------|
     |  Modules involved        |  Controller, Algorithm                       |
     |-------------------------------------------------------------------------|
     |  Case description        |  Sending sensor data from controller that    |
     |                          |  causes a fall detection in the pattern      |
     |                          |  recognition algorithm.                      |
     |-------------------------------------------------------------------------|
     |  Expected result         |  isFall returning true, and FALL_DETECTED    |
     |                          |  posted to Event bus                         |
     |-------------------------------------------------------------------------|
     */

    public static final String TEST_TAG_3 = "test_3";
    public static final String TEST_SENSOR_3 = "test_sensor_3";

    public void runTestId3() {
        // Store previous algorithm used, to be restored at the end of the test
        mPreviousAlgorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        // Force the application to use PHONE_THRESHOLD
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, AlgorithmsToChoose.ID_PHONE_PATTERN_RECOGNITION);

        // Create a new mock sensor session
        SensorSession sensorSessionAcc = new SensorSession(TEST_SENSOR_3 + ":ACC", Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionRot = new SensorSession(TEST_SENSOR_3 + ":ROT", Sensor.TYPE_ROTATION_VECTOR, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionMag = new SensorSession(TEST_SENSOR_3 + ":MAG", Sensor.TYPE_MAGNETIC_FIELD, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create mock sensor data objects
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(100, 100, 100);
        RotationVectorData rotationVectorData = new RotationVectorData(new float[] {0.27839798f, 0.16639067f, -0.11554366f, 0.9388601f, 0.0f});
        MagneticFieldData magneticFieldData = new MagneticFieldData(new float[] {0.59999996f, 2.3999999f, -39.18f});

        // Allow to receive data
        mReceiveAnswer = true;

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSessionAcc, linearAccelerationData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionRot, rotationVectorData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionMag, magneticFieldData, System.currentTimeMillis()));

        // Write log to console
        Log.i(TEST_TAG_3, "Sent data");
    }

    /**
     |-------------------------------------------------------------------------|
     |  Test ID                 |  4: Pattern recognition algorithm,           |
     |                          |  fall not registered                         |
     |-------------------------------------------------------------------------|
     |  Modules involved        |  Controller, Algorithm                       |
     |-------------------------------------------------------------------------|
     |  Case description        |  Sending sensor data from controller to the  |
     |                          |  bus that doesn't cause a fall detection     |
     |                          |  in the pattern recognition algorithm        |
     |-------------------------------------------------------------------------|
     |  Expected result         |  isFall returning false, and FALL_DETECTED   |
     |                          |  not posted to Event bus                     |
     |-------------------------------------------------------------------------|
     */

    public static final String TEST_TAG_4 = "test_4";
    public static final String TEST_SENSOR_4 = "test_sensor_4";

    public void runTestId4() {
        // Store previous algorithm used, to be restored at the end of the test
        mPreviousAlgorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        // Force the application to use PHONE_THRESHOLD
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, AlgorithmsToChoose.ID_PHONE_PATTERN_RECOGNITION);

        // Create a new mock sensor session
        SensorSession sensorSessionAcc = new SensorSession(TEST_SENSOR_4 + ":ACC", Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionRot = new SensorSession(TEST_SENSOR_4 + ":ROT", Sensor.TYPE_ROTATION_VECTOR, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);
        SensorSession sensorSessionMag = new SensorSession(TEST_SENSOR_4 + ":MAG", Sensor.TYPE_MAGNETIC_FIELD, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create mock sensor data objects
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(0, 0, 0);
        RotationVectorData rotationVectorData = new RotationVectorData(new float[] {0.27839798f, 0.16639067f, -0.11554366f, 0.9388601f, 0.0f});
        MagneticFieldData magneticFieldData = new MagneticFieldData(new float[] {0.59999996f, 2.3999999f, -39.18f});

        // Allow to receive data
        mReceiveAnswer = true;

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSessionAcc, linearAccelerationData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionRot, rotationVectorData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionMag, magneticFieldData, System.currentTimeMillis()));

        // Write log to console
        Log.i(TEST_TAG_4, "Sent data");
    }

    /**
     |-------------------------------------------------------------------------|
     |  Test ID                 |  5: Fall detection triggered,                |
     |                          |  alarm activated                             |
     |-------------------------------------------------------------------------|
     |  Modules involved        |  Algorithm, MainService, Alarm               |
     |-------------------------------------------------------------------------|
     |  Case description        |  Triggering a fall detection to see if the   |
     |                          |  alarm activates.                            |
     |-------------------------------------------------------------------------|
     |  Expected result         |  Alarm                                       |
     |-------------------------------------------------------------------------|
     */

    public static final String TEST_TAG_5 = "test_5";
    public static final String TEST_SENSOR_5 = "test_sensor_5";

    public void runTestId5() {
    }

    public void onEvent(EventTypes types) {
        if (mReceiveAnswer) {
            switch (types) {
                case TEST_FALL: // Using this because it is independent of the "Alarm" switch
                    Log.i(TEST_ANSWER_TAG, "Is fall");
                    break;
                case TEST_NO_FALL: // Using this because it is independent of the "Alarm" switch
                    Log.i(TEST_ANSWER_TAG, "Is not a fall");
                    break;
            }

            // Only allow one answer
            mReceiveAnswer = false;

            // Restores the previous used algorithm
            PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, mPreviousAlgorithmId);
        }
    }


}
