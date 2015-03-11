package sintef.android.controller.tests;

import android.hardware.Sensor;
import android.test.InstrumentationTestCase;

import junit.framework.TestCase;

import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by Ole on 19.02.2015.
 */
public class SensorSessionTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testSession (){
        SensorSession session1 = new SensorSession("id1", Sensor.TYPE_ACCELEROMETER, SensorDevice.WATCH , SensorLocation.LEFT_PANT_POCKET);
        assertEquals(session1.getId(),"id1");
        assertEquals(session1.getSensorDevice(),SensorDevice.WATCH);
        assertEquals(session1.getSensorType(),1);
        assertEquals(session1.getSensorLocation(),SensorLocation.LEFT_PANT_POCKET);
        assertEquals(session1,session1.getSessionFromString(session1.getStringFromSession()));



    }



    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
