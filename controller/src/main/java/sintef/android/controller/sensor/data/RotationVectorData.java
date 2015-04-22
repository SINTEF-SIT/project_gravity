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

/**
 * Created by samyboy89 on 05/02/15.
 */
public class RotationVectorData extends SensorDataObject {

    /**
     *  <h4>{@link android.hardware.Sensor#TYPE_ROTATION_VECTOR Sensor.TYPE_ROTATION_VECTOR}:</h4>
     *  <p>The rotation vector represents the orientation of the device as a combination of an <i>angle</i>
     *  and an <i>axis</i>, in which the device has rotated through an angle &#952 around an axis
     *  &lt;x, y, z>.</p>
     *  <p>The three elements of the rotation vector are
     *  &lt;x*sin(&#952/2), y*sin(&#952/2), z*sin(&#952/2)>, such that the magnitude of the rotation
     *  vector is equal to sin(&#952/2), and the direction of the rotation vector is equal to the
     *  direction of the axis of rotation.</p>
     *  </p>The three elements of the rotation vector are equal to
     *  the last three components of a <b>unit</b> quaternion
     *  &lt;cos(&#952/2), x*sin(&#952/2), y*sin(&#952/2), z*sin(&#952/2)>.</p>
     *  <p>Elements of the rotation vector are unitless.
     *  The x,y, and z axis are defined in the same way as the acceleration
     *  sensor.</p>
     *  The reference coordinate system is defined as a direct orthonormal basis,
     *  where:
     * </p>
     *
     * <ul>
     * <li>X is defined as the vector product <b>Y.Z</b> (It is tangential to
     * the ground at the device's current location and roughly points East).</li>
     * <li>Y is tangential to the ground at the device's current location and
     * points towards magnetic north.</li>
     * <li>Z points towards the sky and is perpendicular to the ground.</li>
     * </ul>
     *
     * <p>
     * <center><img src="../../../images/axis_globe.png"
     * alt="World coordinate-system diagram." border="0" /></center>
     * </p>
     *
     * <ul>
     * <li> values[0]: x*sin(&#952/2) </li>
     * <li> values[1]: y*sin(&#952/2) </li>
     * <li> values[2]: z*sin(&#952/2) </li>
     * <li> values[3]: cos(&#952/2) </li>
     * <li> values[4]: estimated heading Accuracy (in radians) (-1 if unavailable)</li>
     * </ul>
     * <p> values[3], originally optional, will always be present from SDK Level 18 onwards.
     * values[4] is a new value that has been added in SDK Level 18.
     * </p>
     */

    public RotationVectorData(float[] values) {
        super(values);
    }

    /**
     * @return x*sin(θ/2)
     */
    public float getX() {
        return getValues()[0];
    }

    /**
     * @return y*sin(θ/2)
     */
    public float getY() {
        return getValues()[1];
    }

    /**
     * @return z*sin(θ/2)
     */
    public float getZ() {
        return getValues()[2];
    }

    /**
     * @return 1*cos(θ/2)
     */
    public float getCos() {
        return getValues()[3];
    }

    /**
     * @return estimated heading Accuracy (in radians) (-1 if unavailable)
     */
    public float getEstimatedHeadingAccuracy() {
        return getValues()[4];
    }
}
