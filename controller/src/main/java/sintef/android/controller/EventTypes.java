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

package sintef.android.controller;

/**
 * Created by samyboy89 on 29/01/15.
 */
public enum EventTypes {
    ONRESUME,
    ONPAUSE,
    ONDESTROY,
    ONSTOP,
    FINISH,
    FALL_DETECTED,
    RESET_SENSOR_LISTENERS,
    START_ALARM,
    STOP_ALARM,
    ALARM_STOPPED,
    ADVANCED_MODE_CHANGED,

    TEST_FALL,
    TEST_NO_FALL,

    RECORDING_PHONE_TOTAL_ACCELERATION,
    RECORDING_PHONE_VERTICAL_ACCELERATION,
    RECORDING_WATCH_FALL_INDEX,
    RECORDING_WATCH_DIRECTION_ACCELERATION,
    RECORDING_WATCH_AFTER_FALL,
}
