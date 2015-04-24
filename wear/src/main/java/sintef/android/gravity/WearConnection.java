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

import android.app.Notification;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import de.greenrobot.event.EventBus;
import sintef.android.controller.Controller;
import sintef.android.controller.common.ClientPaths;
import sintef.android.controller.common.Constants;

public class WearConnection extends WearableListenerService {

    private static final String TAG = "G:WEAR:MRS";
    private WearDeviceClient mWearDeviceClient;
    private SensorManagerWear mSensorManagerWear;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Controller.DBG) Log.w(TAG, "started mrs");
        mWearDeviceClient = WearDeviceClient.getInstance(this);
        mWearDeviceClient.setMode(ClientPaths.MODE_DEFAULT);
        mSensorManagerWear = SensorManagerWear.getInstance(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.watch_notification_title));
        builder.setContentText(getString(R.string.watch_notification_text));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.wear_bak));
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManagerWear.stopMeasurement();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Controller.DBG) Log.d(TAG, "Received message: " + messageEvent.getPath());

        String[] message = messageEvent.getPath().split("/");

        switch("/" + message[1]) {
            case ClientPaths.MODE_PULL:
                mWearDeviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.MODE_PUSH:
                mWearDeviceClient.setMode(messageEvent.getPath());
                break;
            case ClientPaths.START_PUSH:
                mWearDeviceClient.pushData();
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
        EventBus.getDefault().post(progress);
    }
}
