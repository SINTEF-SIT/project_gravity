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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.SensorEventBuffer;

public class DeviceClient {

    private static final String TAG = "G:WEAR:DC";

    public static DeviceClient instance;

    private String mMode = ClientPaths.MODE_PUSH;
    private SensorEventBuffer mSensorEventBuffer;
    private GoogleApiClient mWearableClient;
    private ExecutorService mExecutor;

    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }
        return instance;
    }

    private DeviceClient(Context context) {
        mWearableClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        mExecutor = Executors.newCachedThreadPool();

        mSensorEventBuffer = SensorEventBuffer.getInstance();

        EventBus.getDefault().register(this);
    }

    public GoogleApiClient getWearableClient() {
        return mWearableClient;
    }

    public void onEvent(EventTypes event) {
        switch (event) {
            case START_ALARM:
                startAlarm();
                break;
            case ALARM_STOPPED:
                stopAlarm();
                break;
        }
    }

    public void onEvent(AlarmEvent event) {
        setAlarmProgress(event.progress);
    }

    public void setMode(String mode) {
        this.mMode = mode;
    }

    public void getBuffer() {
        sendMessage(ClientPaths.START_PUSH);
    }

    public void startAlarm() {
        sendMessage(ClientPaths.START_ALARM);
    }

    public void stopAlarm() {
        sendMessage(ClientPaths.STOP_ALARM);
    }

    public void setAlarmProgress(int progress) {
        sendMessage(ClientPaths.ALARM_PROGRESS + progress);
    }

    private void sendMessage(final String path) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendMessageInBackground(path);
            }
        });
    }

    private void sendMessageInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mWearableClient).await().getNodes();

            if (Controller.DBG) Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Wearable.MessageApi.sendMessage(mWearableClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (Controller.DBG) Log.d(TAG, "sendMessageInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            if (Controller.DBG) Log.w(TAG, "No connection possible");
        }
    }

    public void pushData() {
        if (mMode.equals(ClientPaths.MODE_PULL)) {
            for (Object data : mSensorEventBuffer.getBufferAsArray()) {
                SensorEventBuffer.SensorEventData event = (SensorEventBuffer.SensorEventData) data;
                sendSensorData(
                        event.getSession(),
                        event.getSensorType(),
                        event.getAccuracy(),
                        event.getTimestamp(),
                        event.getValues()
                );
            }
        }
    }

    public void addSensorData(final String session, final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        switch(mMode) {
            case ClientPaths.MODE_PULL:
                mSensorEventBuffer.addSensorData(session, sensorType, accuracy, timestamp, values);
                break;
            case ClientPaths.MODE_PUSH:
                sendSensorData(session, sensorType, accuracy, timestamp, values);
                if (Controller.DBG) Log.w(TAG, "Pushing sensor data");
                break;
            default:
                break;
        }
    }

    public void sendSensorData(final String session, final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Controller.DBG) Log.w(TAG, "executing thread to send sensor data");
                    sendSensorDataInBackground(session, sensorType, accuracy, timestamp, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private synchronized void sendSensorDataInBackground(String session, int sensorType, int accuracy, long timestamp, float[] values) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(Constants.DATA_MAP_PATH + sensorType);

        dataMap.getDataMap().putString(Constants.SESSION, session);
        dataMap.getDataMap().putInt(Constants.ACCURACY, accuracy);
        dataMap.getDataMap().putLong(Constants.TIMESTAMP, timestamp);
        dataMap.getDataMap().putFloatArray(Constants.VALUES, values);

        PutDataRequest putDataRequest = dataMap.asPutDataRequest();

        if (Controller.DBG) Log.w(TAG, "Starting to send sensor data");

        if (validateConnection()) {
            Wearable.DataApi.putDataItem(mWearableClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    if (Controller.DBG) Log.v(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    private boolean validateConnection() {
        if (mWearableClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mWearableClient.blockingConnect(Constants.CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }
}
