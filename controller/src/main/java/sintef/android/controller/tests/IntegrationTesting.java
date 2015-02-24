package sintef.android.controller.tests;

import android.hardware.Sensor;
import android.test.InstrumentationTestCase;

import de.greenrobot.event.EventBus;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GravityData;

/**
 * Created by Ole on 24.02.2015.
 */
public class IntegrationTesting extends InstrumentationTestCase {

    SensorData data11;
    SensorData data2;

    public void testMain(){
        EventBus.getDefault().registerSticky(this);
        SensorSession session1 = new SensorSession("id1", Sensor.TYPE_ACCELEROMETER, SensorDevice.WATCH , SensorLocation.LEFT_PANT_POCKET);
        float[] liste = new float[1];
        liste[0] = 2;
        GravityData data1 = new GravityData(liste);
        data2 = new SensorData(session1, data1, System.currentTimeMillis());
        EventBus.getDefault().post(data2);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(data11,data2);
    }

    public void onEvent(SensorData data){
        data11 = data;

    }

}
