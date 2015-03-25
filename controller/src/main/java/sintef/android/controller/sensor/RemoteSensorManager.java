package sintef.android.controller.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GyroscopeData;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.RotationVectorData;
import sintef.android.controller.sensor.data.SensorDataObject;


/**
 * Created by iver on 09.02.15.
 */

public class RemoteSensorManager {
    private static final String TAG = "GRAVITY/RSM";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;

    private static RemoteSensorManager instance;

    private Context context;
    private ExecutorService executorService;
    private SparseArray<Sensor> sensorMapping;
    private ArrayList<Sensor> sensors;
//    private SensorNames sensorNames;
    private GoogleApiClient googleApiClient;
    private EventBus mEventBus;

    public static synchronized RemoteSensorManager getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteSensorManager(context.getApplicationContext());
        }

        return instance;
    }

    private RemoteSensorManager(Context context) {
        this.context = context;
        this.sensorMapping = new SparseArray<Sensor>();
        this.sensors = new ArrayList<Sensor>();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        this.executorService = Executors.newCachedThreadPool();
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void onEvent(EventTypes event) {
        switch (event) {
            case START_ALARM:
                startAlarm();
                break;
            case ALARM_STOPPED:
                stopAlarm();
                break;
            default:
                break;
        }
    }

    public void onEvent(AlarmEvent event) {
        setAlarmProgress(event.progress);
    }

    public synchronized void addSensorData(SensorSession sensorSession, int accuracy, long timestamp, float[] values) {
        SensorDataObject sensorDataObject;
        switch(sensorSession.getSensorType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorDataObject = new AccelerometerData(values.clone());
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorDataObject = new LinearAccelerationData(values.clone());
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorDataObject = new GyroscopeData(values.clone());
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorDataObject = new RotationVectorData(values.clone());
                break;
            default:
                sensorDataObject = null;
                break;
        }

        if (sensorDataObject != null) {
            mEventBus.post(new SensorData(sensorSession, sensorDataObject, TimeUnit.NANOSECONDS.toMillis(timestamp)));

        }
    }

    public boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    public void filterBySensorId(final int sensorId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                filterBySensorIdInBackground(sensorId);
            }
        });
    };

    private void filterBySensorIdInBackground(final int sensorId) {
        Log.d(TAG, "filterBySensorId(" + sensorId + ")");

        if (validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create("/filter");

            dataMap.getDataMap().putInt(Constants.FILTER, sensorId);
            dataMap.getDataMap().putLong(Constants.TIMESTAMP, System.currentTimeMillis());

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Filter by sensor " + sensorId + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
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
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    controlMeasurementInBackground(message);
                }
            });
    }

    private void controlMeasurementInBackground(final String path) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());

            for (Node node : nodes) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), path, null
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            Log.w(TAG, "No connection possible");
        }
    }
}