package sintef.android.gravity;

import android.net.Uri;
import android.util.Log;

import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.RemoteSensorManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;

public class RemoteSensorService extends WearableListenerService {
    private static final String TAG = "GRAVITY/SensorReceiverService";

    private RemoteSensorManager sensorManager;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = RemoteSensorManager.getInstance(this);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.d(TAG, "onDataChanged()");

        for (DataEvent dataEvent : dataEvents) {
//            Log.d(TAG, "dataevent: " + dataEvent);
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
//                Log.d(TAG, "path: " + path);
                if (path.startsWith(Constants.DATA_MAP_PATH)) {
//                    Log.d(TAG, "unpacking data");
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        int accuracy = dataMap.getInt(Constants.ACCURACY);
        long timestamp = dataMap.getLong(Constants.TIMESTAMP);
        float[] values = dataMap.getFloatArray(Constants.VALUES);

        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values));

        sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }
}