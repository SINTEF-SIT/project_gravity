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
public class GyroscopeData extends SensorDataObject {

    /**
     * <h4>{@link android.hardware.Sensor#TYPE_GYROSCOPE Sensor.TYPE_GYROSCOPE}:
     * </h4> All values are in radians/second and measure the rate of rotation
     * around the device's local X, Y and Z axis. The coordinate system is the
     * same as is used for the acceleration sensor. Rotation is positive in the
     * counter-clockwise direction. That is, an observer looking from some
     * positive location on the x, y or z axis at a device positioned on the
     * origin would report positive rotation if the device appeared to be
     * rotating counter clockwise. Note that this is the standard mathematical
     * definition of positive rotation and does not agree with the definition of
     * roll given earlier.
     * <ul>
     * <li> values[0]: Angular speed around the x-axis </li>
     * <li> values[1]: Angular speed around the y-axis </li>
     * <li> values[2]: Angular speed around the z-axis </li>
     * </ul>
     * <p>
     * Typically the output of the gyroscope is integrated over time to
     * calculate a rotation describing the change of angles over the timestep,
     * for example:
     * </p>
     *
     * <pre class="prettyprint">
     *     private static final float NS2S = 1.0f / 1000000000.0f;
     *     private final float[] deltaRotationVector = new float[4]();
     *     private float timestamp;
     *
     *     public void onSensorChanged(SensorEvent event) {
     *          // This timestep's delta rotation to be multiplied by the current rotation
     *          // after computing it from the gyro sample data.
     *          if (timestamp != 0) {
     *              final float dT = (event.timestamp - timestamp) * NS2S;
     *              // Axis of the rotation sample, not normalized yet.
     *              float axisX = event.values[0];
     *              float axisY = event.values[1];
     *              float axisZ = event.values[2];
     *
     *              // Calculate the angular speed of the sample
     *              float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
     *
     *              // Normalize the rotation vector if it's big enough to get the axis
     *              if (omegaMagnitude > EPSILON) {
     *                  axisX /= omegaMagnitude;
     *                  axisY /= omegaMagnitude;
     *                  axisZ /= omegaMagnitude;
     *              }
     *
     *              // Integrate around this axis with the angular speed by the timestep
     *              // in order to get a delta rotation from this sample over the timestep
     *              // We will convert this axis-angle representation of the delta rotation
     *              // into a quaternion before turning it into the rotation matrix.
     *              float thetaOverTwo = omegaMagnitude * dT / 2.0f;
     *              float sinThetaOverTwo = sin(thetaOverTwo);
     *              float cosThetaOverTwo = cos(thetaOverTwo);
     *              deltaRotationVector[0] = sinThetaOverTwo * axisX;
     *              deltaRotationVector[1] = sinThetaOverTwo * axisY;
     *              deltaRotationVector[2] = sinThetaOverTwo * axisZ;
     *              deltaRotationVector[3] = cosThetaOverTwo;
     *          }
     *          timestamp = event.timestamp;
     *          float[] deltaRotationMatrix = new float[9];
     *          SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
     *          // User code should concatenate the delta rotation we computed with the current rotation
     *          // in order to get the updated rotation.
     *          // rotationCurrent = rotationCurrent * deltaRotationMatrix;
     *     }
     * </pre>
     * <p>
     * In practice, the gyroscope noise and offset will introduce some errors
     * which need to be compensated for. This is usually done using the
     * information from other sensors, but is beyond the scope of this document.
     * </p>
     */

    public GyroscopeData(float[] values) {
        super(values);
    }

    public GyroscopeData(float x, float y, float z) {
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
