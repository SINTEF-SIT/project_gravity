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

package sintef.android.controller.sensor.data;

public class GravityData extends SensorDataObject {
    /**
     *  <h4>{@link android.hardware.Sensor#TYPE_GRAVITY Sensor.TYPE_GRAVITY}:</h4>
     *
     *  <p>A three dimensional vector indicating the direction and magnitude of gravity.  Units
     *  are m/s^2. The coordinate system is the same as is used by the acceleration sensor.</p>
     *  <p><b>Note:</b> When the device is at rest, the output of the gravity sensor should be identical
     *  to that of the accelerometer.</p>
     */

    public GravityData(float[] values) {
        super(values);
    }

    public GravityData(float x, float y, float z) {
        super(new float[] {x, y, z});
    }

    public float getX() {
        return getValues()[0];
    }

    public float getY() {
        return getValues()[1];
    }

    public float getZ() {
        return getValues()[2];
    }
}
