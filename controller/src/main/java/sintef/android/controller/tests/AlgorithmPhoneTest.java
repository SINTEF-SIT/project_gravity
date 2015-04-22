/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

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
