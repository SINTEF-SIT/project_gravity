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
import sintef.android.controller.sensor.data.RotationVectorData;
import sintef.android.controller.utils.PreferencesHelper;

/**
 * Created by samyboy89 on 14/04/15.
 */
public class IntegrationTest {

    private static boolean mReceiveAnswer;
    private static int mPreviousAlgorithmId;

    public IntegrationTest() {
        EventBus.getDefault().register(this);
    }

    /**
    |-------------------------------------------------------------------------|
    |  Test ID 1:              |  Threshold algorithm, fall registered        |
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
        SensorSession sensorSessionRot = new SensorSession(TEST_SENSOR_1 + ":ROT", Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create mock sensor data objects
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(100, 100, 100);
        RotationVectorData rotationVectorData = new RotationVectorData(new float[] {1, 1, 1, 1, 1});

        // Allow to receive data
        mReceiveAnswer = true;

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSessionAcc, linearAccelerationData, System.currentTimeMillis()));
        EventBus.getDefault().post(new SensorData(sensorSessionRot, rotationVectorData, System.currentTimeMillis()));

        // Write log to console
        Log.i(TEST_TAG_1, "Sent data");
    }

    public void onEvent(EventTypes types) {
        if (mReceiveAnswer) {
            switch (types) {
                case TEST_FALL: // Using this because it is independent of the "Alarm" switch
                    Log.i(TEST_TAG_1, "Is fall");
                    break;
                case TEST_NO_FALL: // Using this because it is independent of the "Alarm" switch
                    Log.i(TEST_TAG_1, "Is not a fall");
                    break;
            }

            // Only allow one answer
            mReceiveAnswer = false;

            // Restores the previous used algorithm
            PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, mPreviousAlgorithmId);
        }
    }


}
