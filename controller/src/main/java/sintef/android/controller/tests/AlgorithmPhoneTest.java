package sintef.android.controller.tests;

import android.test.InstrumentationTestCase;
import java.lang.Math;

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
        assertTrue(AlgorithmPhone.angleOfPhone(90, 15, 45));
        assertFalse(AlgorithmPhone.angleOfPhone(90,45,45));
    }

    public void testIsFallTest(){
        double pi = Math.PI;
        double piHalf = pi/2;
        assertTrue(isFallTest(8,5,20,piHalf,piHalf,4,4,0.9));
        assertFalse(isFallTest(8,5,1,piHalf,piHalf,4,4,0.9));


    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
