package sintef.android.gravity;

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

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.EventTypes;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;
import sintef.android.controller.sensor.RemoteSensorManager;
import sintef.android.controller.sensor.SensorSession;

public class RemoteSensorService extends WearableListenerService {

    private static final String TAG = "G:PHONE:RSS";

    private RemoteSensorManager mRemoteSensorManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mRemoteSensorManager = RemoteSensorManager.getInstance(this);
        Wearable.MessageApi.addListener(mRemoteSensorManager.getWearableClient(), this);

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
                    unpackSensorData(
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackSensorData(DataMap dataMap) {

        SensorSession session = SensorSession.getSessionFromString(dataMap.getString(Constants.SESSION));
        int accuracy = dataMap.getInt(Constants.ACCURACY);
        long timestamp = dataMap.getLong(Constants.TIMESTAMP);
        float[] values = dataMap.getFloatArray(Constants.VALUES);

        if (Controller.DBG) Log.d(TAG, "Received sensor data " + session.getSensorType() + " = " + Arrays.toString(values));

        mRemoteSensorManager.addSensorData(session, accuracy, timestamp, values);
    }
}