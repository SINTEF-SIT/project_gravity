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

package sintef.android.gravity.advanced;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import sintef.android.controller.RecordAlgorithmData;
import sintef.android.controller.RecordEventData;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.R;
import sintef.android.gravity.wizard.FloatingHintEditText;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class RecordFragment extends Fragment {

    @InjectView(R.id.button_bar)                LinearLayout mButtonBar;
    @InjectView(R.id.send_button)               Button mSendButton;
    @InjectView(R.id.cancel_button)             Button mCancelButton;
    @InjectView(R.id.input_fields)              LinearLayout mInputLayout;
    @InjectView(R.id.record_test_id)            FloatingHintEditText mTestIdInput;
    @InjectView(R.id.server_ip)                 FloatingHintEditText mServerIp;
    @InjectView(R.id.server_port)               FloatingHintEditText mServerPort;
    @InjectView(R.id.record)                    View mRecordIcon;
    @InjectView(R.id.stop)                      View mStopIcon;
    @InjectView(R.id.record_button)             FrameLayout mRecordButton;
    @InjectView(R.id.record_time)               TextView mRecordTime;

    private static final String SERVER_IP = "server_ip";
    private static final String SERVER_PORT = "server_port";

    private static final String DEFAULT_SERVER_IP = "projectgravity.no-ip.org";
    private static final String DEFAULT_SERVER_PORT = "8765";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() == null) return;

        ButterKnife.inject(this, getView());
        EventBus.getDefault().register(this);

        mServerIp.setText(PreferencesHelper.getString(SERVER_IP, DEFAULT_SERVER_IP));
        mServerPort.setText(PreferencesHelper.getString(SERVER_PORT, DEFAULT_SERVER_PORT));

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PreferencesHelper.isRecording()) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRecording();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRecording();
            }
        });
    }

    private Timer mTimer;

    private List<RecordEventData> mRecordEvents = new ArrayList<>();
    public List<RecordAlgorithmData> mRecordAlgorithm = new ArrayList<>();


    public void onEvent(RecordEventData recording) {
        if (!PreferencesHelper.isRecording()) return;
        mRecordEvents.add(recording);

    }

    public void onEvent(RecordAlgorithmData recording) {
        if (!PreferencesHelper.isRecording()) return;
        mRecordAlgorithm.add(recording);
    }

    private void startRecording() {
        PreferencesHelper.putBoolean(PreferencesHelper.FALL_DETECTION_ENABLED, false);
        getActivity().invalidateOptionsMenu();

        PreferencesHelper.putBoolean(PreferencesHelper.RECORDING_ENABLED, true);

        mRecordEvents.clear();
        mRecordAlgorithm.clear();

        startRecordingSetViewParams();

        if (mTimer != null) mTimer.cancel();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            int time = 0;

            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime(time++);
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopRecording() {
        PreferencesHelper.putBoolean(PreferencesHelper.RECORDING_ENABLED, false);
        stopRecordingSetViewParams();
        mTimer.cancel();
    }


    private void sendRecording() {
        sendRecordingSetViewParams();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JsonObject recordings = new JsonObject();

                String id = mTestIdInput.getText().toString();
                recordings.addProperty("test_id", id);

                JsonObject calculations = new JsonObject();

                JsonArray phoneVerticalAcceleration = new JsonArray();
                JsonArray phoneTotalAcceleration = new JsonArray();
                JsonArray watchFallIndex = new JsonArray();
                JsonArray watchDirectionAcceleration = new JsonArray();
                JsonArray watchAfterFall = new JsonArray();

                for (RecordEventData entry : mRecordEvents) {
                    JsonObject object = new JsonObject();
                    object.addProperty("time", entry.time);
                    object.addProperty("value", entry.value);

                    switch (entry.type) {
                        case RECORDING_PHONE_VERTICAL_ACCELERATION:
                            phoneVerticalAcceleration.add(object);
                            break;
                        case RECORDING_PHONE_TOTAL_ACCELERATION:
                            phoneTotalAcceleration.add(object);
                            break;
                        case RECORDING_WATCH_FALL_INDEX:
                            watchFallIndex.add(object);
                            break;
                        case RECORDING_WATCH_DIRECTION_ACCELERATION:
                            watchDirectionAcceleration.add(object);
                            break;
                        case RECORDING_WATCH_AFTER_FALL:
                            watchAfterFall.add(object);
                            break;
                    }
                }

                calculations.add("phone_vertical_acceleration", phoneVerticalAcceleration);
                calculations.add("phone_total_acceleration", phoneTotalAcceleration);
                calculations.add("watch_fall_index", watchFallIndex);
                calculations.add("watch_direction_acceleration", watchDirectionAcceleration);
                calculations.add("watch_after_fall", watchAfterFall);

                recordings.add("calculations", calculations);

                /*
                JsonArray recordEvents = new JsonArray();

                for (RecordEvent event : mRecordEvents) {
                    JsonObject accelerometerObject = new JsonObject();
                    accelerometerObject.addProperty("vertical_acceleration", event.mVerAcc);
                    accelerometerObject.addProperty("total_acceleration", event.mTotAcc);
                    recordEvents.add(accelerometerObject);
                }

                recordings.add("record_data", recordEvents);
                */

                Collections.sort(mRecordAlgorithm);
                JsonArray fallDetectedArray = new JsonArray();
                for (RecordAlgorithmData algorithmData : mRecordAlgorithm) {
                    JsonObject fallDetectedObject = new JsonObject();
                    fallDetectedObject.addProperty("id", algorithmData.id);
                    fallDetectedObject.addProperty("time", algorithmData.time);
                    fallDetectedObject.addProperty("name", algorithmData.name);
                    fallDetectedObject.addProperty("isFall", algorithmData.isFall);
                    fallDetectedArray.add(fallDetectedObject);
                }
                recordings.add("fall_detection", fallDetectedArray);

                Gson gson = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

                final String ip = mServerIp.getText().toString();
                final String port = mServerPort.getText().toString();
                PreferencesHelper.putString(SERVER_IP, ip);
                PreferencesHelper.putString(SERVER_PORT, port);

                final String jsonData = gson.toJson(recordings);
                RecordHistoryFragment.saveJSONDataToDisk(id, jsonData);

                try {
                    Socket socket = new Socket(ip, Integer.valueOf(port));
                    socket.setSoTimeout(10000);
                    DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                    DOS.writeBytes(jsonData);
                    socket.close();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Sensor data sent to server " + ip + ":" + port, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Sensor data failed to send to " + ip + ":" + port , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    private void cancelRecording() {
        PreferencesHelper.putBoolean(PreferencesHelper.RECORDING_ENABLED, false);

        mRecordEvents.clear();
        mRecordAlgorithm.clear();

        sendRecordingSetViewParams();
    }

    private void updateTime(int sec) {
        int minutes;
        int seconds;
        if (sec > 60) {
            seconds = sec % 60;
            minutes = (sec - seconds) / 60;
        } else {
            minutes = 0;
            seconds = sec;
        }
        mRecordTime.setText(getTwoDigitNumber(minutes)+":"+getTwoDigitNumber(seconds));
    }

    private String getTwoDigitNumber(int number) {
        return number < 10 ? "0"+number : String.valueOf(number);
    }

    private void startRecordingSetViewParams() {
        mRecordTime.setVisibility(View.VISIBLE);
        mRecordTime.setText("00:00");
        mRecordButton.setVisibility(View.VISIBLE);
        mRecordIcon.setVisibility(View.GONE);
        mStopIcon.setVisibility(View.VISIBLE);
        mInputLayout.setVisibility(View.GONE);
        mButtonBar.setVisibility(View.GONE);
    }

    private void stopRecordingSetViewParams() {
        mRecordTime.setVisibility(View.GONE);
        mRecordButton.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mButtonBar.setVisibility(View.VISIBLE);
    }

    private void sendRecordingSetViewParams() {
        mRecordTime.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.GONE);
        mButtonBar.setVisibility(View.GONE);
        mRecordButton.setVisibility(View.VISIBLE);
        mRecordIcon.setVisibility(View.VISIBLE);
        mStopIcon.setVisibility(View.GONE);
    }
}
