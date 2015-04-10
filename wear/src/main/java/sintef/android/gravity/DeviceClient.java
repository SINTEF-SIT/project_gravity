package sintef.android.gravity;

/*
 * Much based on https://github.com/pocmo/SensorDashboard
 * Such copy. Very paste.
 */

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;
import android.util.SparseLongArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.data.SensorDataObject;

public class DeviceClient {
    private static final String TAG = Constants.TAG_WEAR;
    private static final int CLIENT_CONNECTION_TIMEOUT = Constants.CLIENT_CONNECTION_TIMEOUT;
    private String mode = ClientPaths.MODE_PUSH;
    private SensorEventBuffer mSensorEventBuffer;

    public static DeviceClient instance;

    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }
        return instance;
    }

    private Context context;
    private GoogleApiClient googleApiClient;
    private ExecutorService executorService;
    private int filterId;

    private SparseLongArray lastSensorData;

    private DeviceClient(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();

        executorService = Executors.newCachedThreadPool();
        lastSensorData = new SparseLongArray();

        mSensorEventBuffer = SensorEventBuffer.getInstance();
    }

    public void setSensorFilter(int filterId) {
        Log.d(TAG, "Now filtering by sensor: " + filterId);

        this.filterId = filterId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void pushData() {
        if (mode.equals(ClientPaths.MODE_PULL)) {
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
        switch(mode) {
            case ClientPaths.MODE_PULL:
                mSensorEventBuffer.addSensorData(session, sensorType, accuracy, timestamp, values);
                break;
            case ClientPaths.MODE_PUSH:
                sendSensorData(session, sensorType, accuracy, timestamp, values);
                Log.w(TAG, "Pushing sensor data");
                break;
            default:
                break;
        }
    }

    public void sendSensorData(final String session, final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        long t = System.currentTimeMillis();

        long lastTimestamp = lastSensorData.get(sensorType);
        long timeAgo = t - lastTimestamp;

        lastSensorData.put(sensorType, t);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.w(TAG, "executing thread to send sensor data");
                    sendSensorDataInBackground(session, sensorType, accuracy, timestamp, values);
                } catch (Exception e) {
                    Log.w("DC", e);
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
        send(putDataRequest);
        Log.w(TAG, "Starting to send sensor data");
    }

    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    private synchronized void send(PutDataRequest putDataRequest) {
        if (validateConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.v(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }
}
