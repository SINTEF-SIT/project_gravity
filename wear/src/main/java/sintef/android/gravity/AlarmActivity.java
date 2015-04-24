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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmView;
import sintef.android.controller.Controller;
import sintef.android.controller.common.Constants;
import sintef.android.controller.WearDeviceClientMobile;

public class AlarmActivity extends Activity {

    private static final String TAG = "G:WEAR:AA";

    private AlarmView mAlarmView;

    private static Vibrator mVibrator;
    private WearDeviceClientMobile mWearDeviceClientMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() == null) return;

        boolean runAlarm = getIntent().getExtras().containsKey(Constants.WATCH_ALARM_ACTIVITY_RUN_ALARM) && getIntent().getExtras().getBoolean(Constants.WATCH_ALARM_ACTIVITY_RUN_ALARM);
        if (Controller.DBG) Log.d(TAG, "runAlarm = " + runAlarm);

        if (!runAlarm) {
            if (Controller.DBG) Log.d(TAG, "Finishing");
            finish();
            return;
        }

        if (Controller.DBG) Log.d(TAG, "Initialising and staring alarm");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);

        EventBus.getDefault().register(this);
        mWearDeviceClientMobile = WearDeviceClientMobile.getInstance(this);

        mAlarmView = new AlarmView(this, R.layout.show_alarm);
        mAlarmView.setOnStopListener(new AlarmView.OnStopListener() {
            @Override
            public void onStop() {
                if (Controller.DBG) Log.d(TAG, "Stop alarm clicked");
                mWearDeviceClientMobile.stopAlarm();
                finish();
            }
        });
        mAlarmView.setStrokeWidth(14);
        mAlarmView.setOnAlarmListener(new AlarmView.OnAlarmListener() {
            @Override
            public void onAlarm() {
                if (Controller.DBG) Log.d(TAG, "Alarm has gone off");
                if (mVibrator != null) mVibrator.cancel();

                finish();
            }
        });

        setContentView(mAlarmView);

        mAlarmView.startAlarm();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVibrator != null) mVibrator.cancel();
        EventBus.getDefault().unregister(this);
        mAlarmView = null;

        if (Controller.DBG) Log.d(TAG, "Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVibrator != null) mVibrator.cancel();
        EventBus.getDefault().unregister(this);
        mAlarmView = null;

        if (Controller.DBG) Log.d(TAG, "Destroy");
    }

    public void onEvent(int progress) {
        mAlarmView.setAlarmProgress(progress);

        if (Controller.DBG) Log.d(TAG, "Progress: " + progress + " received");
    }

}
