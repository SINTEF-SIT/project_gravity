package sintef.android.gravity;

/*
 * Much based on https://github.com/pocmo/SensorDashboard
 * Such copy. Very paste.
 */

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;

import de.greenrobot.event.EventBus;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;

public class MessageReceiverService extends WearableListenerService {

    private static final String TAG = "MessageReceiverService";
    private DeviceClient deviceClient;
    private EventBus mEventBus;
    private SensorRecorder mSensorRecorder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("MRS", "started mrs");
        mEventBus = EventBus.getDefault();
        deviceClient = DeviceClient.getInstance(this);
        deviceClient.setMode(ClientPaths.MODE_DEFAULT);
        mSensorRecorder = SensorRecorder.getInstance(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.watch_notification_title));
        builder.setContentText(getString(R.string.watch_notification_text));
        builder.setSmallIcon(R.drawable.ic_stat_on_blue);
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorRecorder.stopMeasurement();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/filter")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    int filterById = dataMap.getInt(Constants.FILTER);
                    deviceClient.setSensorFilter(filterById);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message: " + messageEvent.getPath());

        String[] message = messageEvent.getPath().split("/");

        Log.w("MSG", Arrays.toString(message));

        switch("/" + message[1]) {
            case ClientPaths.START_MEASUREMENT:
                break;
            case ClientPaths.STOP_MEASUREMENT:
                break;
            case ClientPaths.MODE_PULL:
                deviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.MODE_PUSH:
                deviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.START_PUSH:
                deviceClient.pushData();
                break;
            case ClientPaths.START_ALARM:
                startAlarm(true);
                break;
            case ClientPaths.STOP_ALARM:
                startAlarm(false);
                break;
            case ClientPaths.ALARM_PROGRESS:
                updateAlarmProgress(Integer.valueOf(message[2]));
                break;
        }
    }

    /**
     * Starts the AlarmActivity.class.
     * If true, the alarm will start. If false, the alarm will stop
     *
     * @param runAlarm
     */
    private synchronized void startAlarm(boolean runAlarm) {
        Intent alarm = new Intent(this, AlarmActivity.class);
        alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        alarm.putExtra(Constants.WATCH_ALARM_ACTIVITY_RUN_ALARM, runAlarm);
        Log.w("MRS", "starting activity alarm");
        startActivity(alarm);
    }

    private void updateAlarmProgress(int progress) {
        mEventBus.post(progress);
    }
}
