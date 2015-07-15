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
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import sintef.android.controller.AlarmEvent;
import sintef.android.controller.AlarmView;
import sintef.android.controller.EventTypes;
import sintef.android.controller.algorithm.AlgorithmsToChoose;
import sintef.android.controller.common.Constants;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.controller.utils.SoundHelper;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class AlarmFragment extends Fragment {

    @InjectView(R.id.alarm_view)            AlarmView mAlarmView;
    @InjectView(R.id.alarm_advanced)        LinearLayout mAdvancedView;
    @InjectView(R.id.alarm_algorithm)       TextView mAlarmAlgorithm;
    @InjectView(R.id.alarm_start)           Button mAlarmButton;

    private static Vibrator sVibrator;
    private static TelephonyManager sManager;
        private static StatePhoneReceiver sPhoneStateListener;
    private static boolean sCallFromApp = false; // To control the call has been made from the application
    private static boolean sCallFromOffHook = false; // To control the change to idle state is from the app call

    public AlarmFragment() { }

    public static AlarmFragment newInstance(boolean start_alarm) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AlarmService.ALARM_STARTED, start_alarm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sVibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        sPhoneStateListener = new StatePhoneReceiver();
        sManager = ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        ButterKnife.inject(this, getView());

        mAlarmView.setOnStopListener(new AlarmView.OnStopListener() {
            @Override
            public void onStop() {
                if (sVibrator != null) sVibrator.cancel();
                EventBus.getDefault().post(EventTypes.ALARM_STOPPED);
            }
        });
        mAlarmView.setOnAlarmListener(new AlarmView.OnAlarmListener() {
            @Override
            public void onAlarm() {

                EventBus.getDefault().post(EventTypes.STOP_ALARM);
                Log.w("Alarm", "notifying EMHT");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            String data = "";
                            data += "type=fall&";
                            data += "callee.phoneNumber=" + getResources().getString(R.string.callee_phone_number) + "&";
                            data += "callee.name=" + getResources().getString(R.string.callee_name) + "&";
                            data += "callee.address=" + getResources().getString(R.string.callee_address);
                            Log.w("Alarm", "postdata is: " + data);
                            URL url = new URL(getResources().getString(R.string.server_address) + "/alarm");
                            Log.w("Alarm", "emht server url is: " + url.toString());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setInstanceFollowRedirects(false);
                            connection.setRequestMethod("POST");
                            BufferedWriter bw = null;
                            try {
                                bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                                bw.write(data);
                                bw.flush();
                            } finally {
                                if (bw != null) bw.close();
                            }
                            Log.w("Alarm", "connection response code is: " + connection.getResponseCode());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();

                Log.w("Alarm", "calling nextofkin");
                sManager.listen(sPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                sCallFromApp = true;

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + PreferencesHelper.getString(Constants.PREFS_NEXT_OF_KIN_TELEPHONE)));
                getActivity().startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);

        boolean start_alarm = getArguments().getBoolean(AlarmService.ALARM_STARTED);

        if (start_alarm) mAlarmView.startAlarm();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdvancedModeElements();
    }

    private void updateAdvancedModeElements() {
        boolean advancedMenuAvailable = PreferencesHelper.getBoolean(MainActivity.ADVANCED_MENU_AVAILABLE, false);
        if (advancedMenuAvailable) {
            mAdvancedView.setVisibility(View.VISIBLE);
            mAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(EventTypes.START_ALARM);
                }
            });
            int algorithm = PreferencesHelper.getInt(Constants.PREFS_ALGORITHM, Constants.PREFS_DEFAULT_ALGORITHM);
            mAlarmAlgorithm.setText(String.format(getString(R.string.fragment_alarm_algorithm), AlgorithmsToChoose.getAlgorithm(algorithm).getCorrectString()));
        } else {
            mAdvancedView.setVisibility(View.GONE);
        }
    }

    public void onEvent(EventTypes type) {
        switch (type) {
            case START_ALARM:
                sVibrator.vibrate(Constants.ALARM_VIBRATION_PATTERN_ON_WATCH, 0);
                mAlarmView.startAlarm();
                break;
            case STOP_ALARM:
                if (sVibrator != null) sVibrator.cancel();
                mAlarmView.stopAlarmWithoutNotify();
                break;
            case ADVANCED_MODE_CHANGED:
                updateAdvancedModeElements();
                break;
        }
    }

    public void onEvent(AlarmEvent event) {
        SoundHelper.playAlarmSound();
        mAlarmView.setAlarmProgress(event.progress);
    }

    public class StatePhoneReceiver extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {

                case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
                    if (sCallFromApp) {
                        sCallFromApp = false;
                        sCallFromOffHook = true;

                        try {
                            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //Activate loudspeaker
                        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(true);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE: //Call is finished
                    if (sCallFromOffHook) {
                        sCallFromOffHook = false;
                        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
                        sManager.listen(sPhoneStateListener, PhoneStateListener.LISTEN_NONE);
                    }
                    break;
            }
        }
    }
}
