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

package sintef.android.controller.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;

/**
 * Created by samyboy89 on 05/02/15.
 */
public class SensorAlgorithmPack {

    private HashMap<SensorSession, List<SensorData>> mSensorData = new HashMap<>();

    public static SensorAlgorithmPack processNewSensorData(Map<SensorSession, List<SensorData>> first, Map<SensorSession, List<SensorData>> last) {
        SensorAlgorithmPack pack = new SensorAlgorithmPack();
        pack.getSensorData().putAll(first);

        if (last == null) return pack;

        for (Map.Entry<SensorSession, List<SensorData>> entry : last.entrySet()) {
            if (pack.getSensorData().containsKey(entry.getKey())) {
                pack.getSensorData().get(entry.getKey()).addAll(entry.getValue());
            }
        }

        return pack;
    }

    public HashMap<SensorSession, List<SensorData>> getSensorData() {
        return mSensorData;
    }
}
