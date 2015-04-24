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

package sintef.android.controller.sensor;

import sintef.android.controller.common.Constants;

public class SensorSession {

    private final String mId;
    private final int mSensorType;
    private final SensorDevice mSensorDevice;
    private final SensorLocation mSensorLocation;

    public SensorSession(String id, int sensorType, SensorDevice sensorDevice, SensorLocation sensorLocation) {
        mId = id;
        mSensorType = sensorType;
        mSensorDevice = sensorDevice;
        mSensorLocation = sensorLocation;
    }

    protected SensorSession(String id) {
        mId = id;
        mSensorType = -1;
        mSensorDevice = null;
        mSensorLocation = null;
    }

    public String getId() {
        return mId;
    }

    public int getSensorType() {
        return mSensorType;
    }

    public SensorDevice getSensorDevice() {
        return mSensorDevice;
    }

    public SensorLocation getSensorLocation() {
        return mSensorLocation;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SensorSession))
            return false;
        if (obj == this)
            return true;

        SensorSession rhs = (SensorSession) obj;
        return mId.equals(rhs.mId);
    }

    public String getStringFromSession() {
        return    mId + Constants.SENSOR_SESSION_SPLIT_KEY
                + mSensorType + Constants.SENSOR_SESSION_SPLIT_KEY
                + mSensorDevice.getValue() + Constants.SENSOR_SESSION_SPLIT_KEY
                + mSensorLocation.getValue();
    }

    public static SensorSession getSessionFromString(String parsedSession) {
        String splitSession[] = parsedSession.split(Constants.SENSOR_SESSION_SPLIT_KEY);
        return new SensorSession(splitSession[0],
                Integer.parseInt(splitSession[1]),
                SensorDevice.fromString(splitSession[2]),
                SensorLocation.fromString(splitSession[3]));
    }
}
