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

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import sintef.android.controller.algorithm.AlgorithmMain;
import sintef.android.controller.algorithm.SensorAlgorithmPack;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorManager;
import sintef.android.controller.sensor.SensorSession;

public class Controller {

    private static String TAG = "G:CONTROLLER:C";

    public static boolean DBG = false;
    private static boolean DBG_RATE = false;

    private static Controller sController;
    private static List<Map<SensorSession, List<SensorData>>> sDataStore = new ArrayList<>();

    private static long sCurrentTime = System.currentTimeMillis();
    private static int sFrequency = 0;

    public static void initializeController(Context context) {
        if (sController == null) sController = new Controller(context);
    }

    private Controller(final Context context) {
        EventBus.getDefault().register(this);

        AlgorithmMain.initializeAlgorithmMaster();
        SensorManager.getInstance(context);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                // RemoteSensorManager.getInstance(context).setMode(ClientPaths.MODE_DEFAULT);

                SensorAlgorithmPack pack;
                sDataStore.add(0, new HashMap<SensorSession, List<SensorData>>());
                if (sDataStore.size() > 2) {
                    pack = SensorAlgorithmPack.processNewSensorData(sDataStore.get(2), sDataStore.get(1));
                } else if (sDataStore.size() > 1) {
                    pack = SensorAlgorithmPack.processNewSensorData(sDataStore.get(1), null);
                } else {
                    return;
                }
                EventBus.getDefault().post(pack);

                if (sDataStore.size() > 2) sDataStore.remove(2);

            }
        }, 0, Constants.ALGORITHM_SEND_FREQUENCY);


    }

    public synchronized void onEvent(SensorData data) {
        if (data == null || data.getSensorSession() == null) return;

        if (sDataStore.isEmpty()) sDataStore.add(0, new HashMap<SensorSession, List<SensorData>>());
        Map<SensorSession, List<SensorData>> sensorData = sDataStore.get(0);

        if (data.getSensorSession().getSensorType() == Sensor.TYPE_LINEAR_ACCELERATION) sFrequency += 1;
        if (sCurrentTime + 1000 <= System.currentTimeMillis() ) {
            if (DBG_RATE) Log.d(TAG, String.format("%d @ %d", sFrequency, sCurrentTime));

            if (sFrequency < 10) {
                EventBus.getDefault().post(EventTypes.RESET_SENSOR_LISTENERS);
            }

            sCurrentTime = System.currentTimeMillis();
            sFrequency = 0;
        }

        if (!sensorData.containsKey(data.getSensorSession())) {
            sensorData.put(data.getSensorSession(), new ArrayList<SensorData>());
        }

        sensorData.get(data.getSensorSession()).add(data);
    }
}
