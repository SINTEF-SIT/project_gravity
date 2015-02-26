package sintef.android.controller.tests;

import android.hardware.Sensor;
import android.test.InstrumentationTestCase;

import de.greenrobot.event.EventBus;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.GravityData;

/**
 * Created by Ole on 24.02.2015.
 */
public class IntegrationTesting extends InstrumentationTestCase {

    SensorData retrieveData;
    SensorData sendData;

    public void testMain(){
        EventBus.getDefault().registerSticky(this);
        SensorSession session = new SensorSession("id1", Sensor.TYPE_ACCELEROMETER, SensorDevice.WATCH , SensorLocation.LEFT_PANT_POCKET);
        float[] list = new float[1];
        list[0] = 2;
        GravityData origData;
        origData = new GravityData(list);
        sendData = new SensorData(session, origData, System.currentTimeMillis());
        EventBus.getDefault().post(sendData);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(retrieveData, sendData);
    }
    // Should listen to isFall instead of directly from eventbus, kan da vente en bestemt tid

    public void onEvent(SensorData data){
        retrieveData = data;

    }

}
