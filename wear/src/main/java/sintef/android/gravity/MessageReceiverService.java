package sintef.android.gravity;

/*
 * Much based on https://github.com/pocmo/SensorDashboard
 * Such copy. Very paste.
 */

import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;

public class MessageReceiverService extends WearableListenerService {

    private static final String TAG = "G:WEAR:MRS";
    private DeviceClient mDeviceClient;
    private EventBus mEventBus;
    private SensorRecorder mSensorRecorder;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Controller.DBG) Log.w(TAG, "started mrs");
        mEventBus = EventBus.getDefault();
        mDeviceClient = DeviceClient.getInstance(this);
        mDeviceClient.setMode(ClientPaths.MODE_DEFAULT);
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Controller.DBG) Log.d(TAG, "Received message: " + messageEvent.getPath());

        String[] message = messageEvent.getPath().split("/");

        switch("/" + message[1]) {
            case ClientPaths.START_MEASUREMENT:
                break;
            case ClientPaths.STOP_MEASUREMENT:
                break;
            case ClientPaths.MODE_PULL:
                mDeviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.MODE_PUSH:
                mDeviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.START_PUSH:
                mDeviceClient.pushData();
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

        if (Controller.DBG) Log.w(TAG, "starting activity alarm");
        startActivity(alarm);
    }

    private void updateAlarmProgress(int progress) {
        mEventBus.post(progress);
    }
}
