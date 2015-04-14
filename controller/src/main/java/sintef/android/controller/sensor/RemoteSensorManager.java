package sintef.android.controller.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.Controller;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.SensorDataObject;


/**
 * Created by iver on 09.02.15.
 */

public class RemoteSensorManager {

    private static final String TAG = "G:CONTROLLER:RSM";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;

    private static RemoteSensorManager instance;

    private ExecutorService mExecutor;
    private GoogleApiClient mWearableClient;
    private EventBus mEventBus;

    public static synchronized RemoteSensorManager getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteSensorManager(context.getApplicationContext());
        }

        return instance;
    }

    private RemoteSensorManager(Context context) {
        mWearableClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        mExecutor = Executors.newCachedThreadPool();
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
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

    public synchronized void addSensorData(SensorSession sensorSession, int accuracy, long timestamp, float[] values) {
        SensorDataObject sensorDataObject = null;
        switch(sensorSession.getSensorType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorDataObject = new LinearAccelerationData(values.clone());
                break;
        }

        if (sensorDataObject != null) {
            mEventBus.post(new SensorData(sensorSession, sensorDataObject, TimeUnit.NANOSECONDS.toMillis(timestamp)));
        }
    }

    public boolean validateConnection() {
        if (mWearableClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mWearableClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    public void startMeasurement() {
        sendMessage(ClientPaths.START_MEASUREMENT);
    }

    public void stopMeasurement() {
        sendMessage(ClientPaths.STOP_MEASUREMENT);
    }

    public void setMode(final String mode) {
        sendMessage(mode);
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

    private void sendMessage(final String message) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(message);
            }
        });
    }

    private void controlMeasurementInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mWearableClient).await().getNodes();

            if (Controller.DBG) Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Wearable.MessageApi.sendMessage(mWearableClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (Controller.DBG) Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            if (Controller.DBG) Log.w(TAG, "No connection possible");
        }
    }
}