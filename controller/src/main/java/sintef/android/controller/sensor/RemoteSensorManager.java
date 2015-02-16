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
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GyroscopeData;
import sintef.android.controller.sensor.data.RotationVectorData;
import sintef.android.controller.sensor.data.SensorDataObject;

/*
import sintef.android.gravity.data.Sensor;
import sintef.android.gravity.data.SensorDataPoint;
import sintef.android.gravity.data.SensorNames;
import sintef.android.gravity.events.NewSensorEvent;
import sintef.android.gravity.events.SensorUpdatedEvent;
*/

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
//        this.sensorNames = new SensorNames();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        this.executorService = Executors.newCachedThreadPool();
        mEventBus = EventBus.getDefault();
    }

    /*
    public List<Sensor> getSensors() {
        return (List<Sensor>) sensors.clone();
    }

    public Sensor getSensor(long id) {
        return sensorMapping.get((int) id);
    }
    */

    /*
    private Sensor createSensor(int id) {
        Sensor sensor = new Sensor(id, sensorNames.getName(id));

        sensors.add(sensor);
        sensorMapping.append(id, sensor);

        mEventBus.post(new NewSensorEvent(sensor));

        return sensor;
    }

    private Sensor getOrCreateSensor(int id) {
        Sensor sensor = sensorMapping.get(id);

        if (sensor == null) {
            sensor = createSensor(id);
        }

        return sensor;
    }
    */

    public synchronized void addSensorData(SensorSession sensorSession, int accuracy, long timestamp, float[] values) {
        SensorDataObject sensorDataObject;
        switch(sensorSession.getSensorType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorDataObject = new AccelerometerData(values);
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorDataObject = new GyroscopeData(values);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorDataObject = new RotationVectorData(values);
                break;
            default:
                sensorDataObject = null;
                break;
        }

        if (sensorDataObject != null) {
//            Log.d(TAG, "should appear in graph now");
            mEventBus.post(new SensorData(sensorSession, sensorDataObject, TimeUnit.NANOSECONDS.toMillis(timestamp)));

        }

        /*
        Sensor sensor = getOrCreateSensor(sensorType);

        // TODO: We probably want to pull sensor data point objects from a pool here
        SensorDataPoint dataPoint = new SensorDataPoint(timestamp, accuracy, values);

        sensor.addDataPoint(dataPoint);

        Log.d(TAG, "added: " + dataPoint.toString());
        mEventBus.post(new SensorUpdatedEvent(sensor, dataPoint));
        */
    }

    private boolean validateConnection() {
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
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.START_MEASUREMENT);
            }
        });
    }

    public void stopMeasurement() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.STOP_MEASUREMENT);
            }
        });
    }

    public void setMode(final String mode) {
        executorService.submit((new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(mode);
            }
        }));
    }

    public void getBuffer() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.START_PUSH);
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