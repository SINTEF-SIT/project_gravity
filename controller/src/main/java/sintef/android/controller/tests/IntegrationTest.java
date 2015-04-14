package sintef.android.controller.tests;

import android.hardware.Sensor;

import de.greenrobot.event.EventBus;
import sintef.android.controller.algorithm.AlgorithmsToChoose;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.utils.PreferencesHelper;

/**
 * Created by samyboy89 on 14/04/15.
 */
public class IntegrationTest {

    


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
    public static final String TEST_SENSOR_ID = "test_sensor_1";

    public void testId1() {
        // Store previous algorithm used, to be restored at the end of the test
        int previousAlgorithmId = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);

        // Force the application to use PHONE_THRESHOLD
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, AlgorithmsToChoose.ID_PHONE_THRESHOLD);

        // Create a new mock sensor session
        SensorSession sensorSession = new SensorSession(TEST_SENSOR_ID, Sensor.TYPE_LINEAR_ACCELERATION, SensorDevice.PHONE, SensorLocation.RIGHT_ARM);

        // Create a new mock sensor data object
        LinearAccelerationData linearAccelerationData = new LinearAccelerationData(100, 100, 100);

        // Posts the mock session and data at current time
        EventBus.getDefault().post(new SensorData(sensorSession, linearAccelerationData, System.currentTimeMillis()));


        // Restores the previous used algorithm
        PreferencesHelper.putInt(Constants.PREFS_ALGORITHM, previousAlgorithmId);
    }


}
