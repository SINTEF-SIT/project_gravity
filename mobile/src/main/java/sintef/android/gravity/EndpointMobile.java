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

package sintef.android.gravity;

import android.hardware.Sensor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.DeviceClient;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.SensorDataObject;

public class EndpointMobile extends WearableListenerService {

    private static final String TAG = "G:PHONE:WCM";

    @Override
    public void onCreate() {
        super.onCreate();
        Wearable.MessageApi.addListener(DeviceClient.getInstance(this).getWearableClient(), this);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        if (Controller.DBG) Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        if (Controller.DBG) Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Controller.DBG) Log.d(TAG, "Received message: " + messageEvent.getPath());

        switch(messageEvent.getPath()) {
            case ClientPaths.STOP_ALARM:
                EventBus.getDefault().post(EventTypes.STOP_ALARM);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                if (path.startsWith(Constants.DATA_MAP_PATH)) {
                    unpackAndSendSensorData(
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackAndSendSensorData(DataMap dataMap) {
        SensorSession session = SensorSession.getSessionFromString(dataMap.getString(Constants.SESSION));
        int accuracy = dataMap.getInt(Constants.ACCURACY);
        long timestamp = dataMap.getLong(Constants.TIMESTAMP);
        float[] values = dataMap.getFloatArray(Constants.VALUES);

        if (Controller.DBG) Log.d(TAG, "Received sensor data " + session.getSensorType() + " = " + Arrays.toString(values));

        SensorDataObject sensorDataObject = null;
        switch(session.getSensorType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorDataObject = new LinearAccelerationData(values.clone());
                break;
        }

        if (sensorDataObject != null) {
            EventBus.getDefault().post(new SensorData(session, sensorDataObject, TimeUnit.NANOSECONDS.toMillis(timestamp)));
        }
    }
}