package sintef.android.controller.tests;

import android.test.InstrumentationTestCase;

import junit.framework.TestCase;

import sintef.android.controller.algorithm.AlgorithmPhone;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorDevice;
import sintef.android.controller.sensor.SensorLocation;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.GravityData;
import sintef.android.controller.sensor.data.SensorDataObject;

/**
 * Created by Ole on 23.02.2015.
 */
public class AlgorithmPhoneTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testAngleOfPhone (){
        assertTrue(AlgorithmPhone.angleOfPhone(3.4,6.7));
    }

    public void testVerticalComparedToTotal(){

    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
