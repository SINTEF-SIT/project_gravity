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

public enum SensorLocation {

    LEFT_ARM("left_arm"),
    RIGHT_ARM("right_arm"),
    LEFT_PANT_POCKET("left_pant_pocket"),
    RIGHT_PANT_POCKET("right_pant_pocket"),
    LEFT_PANT_BACK_POCKET("left_pant_back_pocket"),
    RIGHT_PANT_BACK_POCKET("right_pant_back_pocket"),
    LEFT_JACKET_POCKET("left_jacket_pocket"),
    RIGHT_JACKET_POCKET("right_jacket_pocket"),
    HEAD("head"),
    NECK("neck"),
    STOMACH("stomach"),
    PURSE("purse"),
    BACK("back"),
    LEFT_FOOT("left_foot"),
    RIGHT_FOOT("right_foot"),
    LEFT_SHOULDER("left_shoulder"),
    RIGHT_SHOULDER("right_shoulder"),
    OTHER("other");

    private String data;

    SensorLocation(String data) {
        this.data = data;
    }

    public String getValue() {
        return data;
    }

    public static SensorLocation fromString(String lookup) {
        if (lookup != null) {
            for (SensorLocation location : SensorLocation.values()) {
                if (lookup.equalsIgnoreCase(location.data)) return location;
            }
        }
        throw new IllegalArgumentException("No such location: " + lookup);
    }
}
