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
