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

    public void testAngleOfPhoneTrue (){
        assertTrue(AlgorithmPhone.isPhoneVertical(15,90,45));
    }

    public void testAngleOfPhoneFalse (){
        assertFalse(AlgorithmPhone.isPhoneVertical(15,45,45));
    }

    public void testIsFallTestTrue(){
        double pi = Math.PI;
        double piHalf = pi/2;
        assertTrue(AlgorithmPhone.isFall(8,20,5,piHalf,piHalf,4,4,0.9));

        /*
        System.out.println("TEST");
        System.out.println("TEST " + AlgorithmPhone.isFallTest(8,20,5,piHalf,piHalf,4,4,0.9));
        System.out.println("/TEST");*/
    }

    public void testIsFallTestFalse(){
        double pi = Math.PI;
        double piHalf = pi/2;
        assertFalse(AlgorithmPhone.isFall(8,1,5,piHalf,piHalf,4,4,0.9));
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
