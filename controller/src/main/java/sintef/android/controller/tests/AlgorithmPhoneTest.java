package sintef.android.controller.tests;

import android.test.InstrumentationTestCase;
import java.lang.Math;

import sintef.android.controller.algorithm.ThresholdPhone;

/**
 * Created by Ole on 23.02.2015.
 */
public class AlgorithmPhoneTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testAngleOfPhoneTrue (){
        assertTrue(ThresholdPhone.isPhoneVertical(15, 90, 45));
    }

    public void testAngleOfPhoneFalse (){
        assertFalse(ThresholdPhone.isPhoneVertical(15, 45, 45));
    }

    public void testIsFallTestTrue(){
        double pi = Math.PI;
        double piHalf = pi/2;
        //assertTrue(AlgorithmPhone.isFall(8,20,5,piHalf,piHalf,4,4,0.9));

        /*
        System.out.println("TEST");
        System.out.println("TEST " + AlgorithmPhone.isFallTest(8,20,5,piHalf,piHalf,4,4,0.9));
        System.out.println("/TEST");*/
    }

    public void testIsFallTestFalse(){
        double pi = Math.PI;
        double piHalf = pi/2;
        //assertFalse(AlgorithmPhone.isFall(8,1,5,piHalf,piHalf,4,4,0.9));
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
