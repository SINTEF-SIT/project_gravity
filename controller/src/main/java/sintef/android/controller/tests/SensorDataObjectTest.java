package sintef.android.controller.tests;

import android.test.InstrumentationTestCase;

import junit.framework.TestCase;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.GravityData;
import sintef.android.controller.sensor.data.SensorDataObject;

/**
 * Created by Ole on 19.02.2015.
 */
public class SensorDataObjectTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testSensorDataObject (){
        float[] liste = new float[1];
        liste[0] = 2;
        GravityData data1 = new GravityData(liste);
        float[] values = data1.getValues();
        assertEquals(liste[0],values[0]);
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
