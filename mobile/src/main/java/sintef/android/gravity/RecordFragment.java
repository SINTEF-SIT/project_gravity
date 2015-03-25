package sintef.android.gravity;

import android.hardware.Sensor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GravityData;
import sintef.android.controller.sensor.data.GyroscopeData;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.sensor.data.MagneticFieldData;
import sintef.android.controller.sensor.data.RotationVectorData;
import sintef.android.controller.utils.PreferencesHelper;
import sintef.android.gravity.wizard.FloatingHintEditText;

/**
 * Created by samyboy89 on 23/02/15.
 */
public class RecordFragment extends Fragment {

    // TEST_ID, FALL_NR, DATA, TID, FALL
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
                if (!mIsRecording) {
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

    private static Map<SensorSession, List<SensorData>> mRecordedData = new HashMap<>();
    public List<Long> mFallDetectedAtTimes = new ArrayList<>();
    private Timer mTimer;

    private boolean mIsRecording = false;

    private boolean gotMagLast = false;

    public void onEvent(SensorData data) {
        if (!mIsRecording) return;

        if (!mRecordedData.containsKey(data.getSensorSession())) {
            mRecordedData.put(data.getSensorSession(), new ArrayList<SensorData>());
        }

        if (data.getSensorSession().getSensorType() == Sensor.TYPE_MAGNETIC_FIELD && !gotMagLast) {
            mRecordedData.get(data.getSensorSession()).add(data);
            gotMagLast = true;
        } else if (data.getSensorSession().getSensorType() == Sensor.TYPE_LINEAR_ACCELERATION && gotMagLast) {
            mRecordedData.get(data.getSensorSession()).add(data);
            gotMagLast = false;
        }
    }

    public void onEvent(EventTypes type) {
        if (mIsRecording && type == EventTypes.FALL_DETECTED_FOR_RECORDING) {
            mFallDetectedAtTimes.add(System.currentTimeMillis());
        }
    }

    private void startRecording() {
        mFallDetectedAtTimes.clear();
        mRecordedData.clear();
        mIsRecording = true;
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
        mIsRecording = false;
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

                JsonObject sensorData = new JsonObject();

                for (Map.Entry<SensorSession, List<SensorData>> entry : mRecordedData.entrySet()) {
                    JsonArray sensorDataArray = new JsonArray();
                    switch (entry.getKey().getSensorType()) {
                        case Sensor.TYPE_ACCELEROMETER:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                AccelerometerData accData = (AccelerometerData) data.getSensorData();
                                JsonObject accelerometerObject = new JsonObject();
                                accelerometerObject.addProperty("time", data.getTimeCaptured());
                                accelerometerObject.addProperty("x", accData.getX());
                                accelerometerObject.addProperty("y", accData.getY());
                                accelerometerObject.addProperty("z", accData.getZ());
                                sensorDataArray.add(accelerometerObject);
                            }
                            break;
                        case Sensor.TYPE_ROTATION_VECTOR:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                RotationVectorData rotData = (RotationVectorData) data.getSensorData();
                                JsonObject rotationVectorObject = new JsonObject();
                                rotationVectorObject.addProperty("time", data.getTimeCaptured());
                                rotationVectorObject.addProperty("x", rotData.getX());
                                rotationVectorObject.addProperty("y", rotData.getY());
                                rotationVectorObject.addProperty("z", rotData.getZ());
                                rotationVectorObject.addProperty("cos", rotData.getCos());
                                rotationVectorObject.addProperty("eha", rotData.getEstimatedHeadingAccuracy());
                                sensorDataArray.add(rotationVectorObject);
                            }
                            break;
                        case Sensor.TYPE_MAGNETIC_FIELD:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                MagneticFieldData magData = (MagneticFieldData) data.getSensorData();
                                JsonObject magneticFieldObject = new JsonObject();
                                magneticFieldObject.addProperty("time", data.getTimeCaptured());
                                magneticFieldObject.addProperty("x", magData.getX());
                                magneticFieldObject.addProperty("y", magData.getY());
                                magneticFieldObject.addProperty("z", magData.getZ());
                                sensorDataArray.add(magneticFieldObject);
                            }
                            break;
                        case Sensor.TYPE_GYROSCOPE:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                GyroscopeData gyrData = (GyroscopeData) data.getSensorData();
                                JsonObject gyroscopeObject = new JsonObject();
                                gyroscopeObject.addProperty("time", data.getTimeCaptured());
                                gyroscopeObject.addProperty("x", gyrData.getX());
                                gyroscopeObject.addProperty("y", gyrData.getY());
                                gyroscopeObject.addProperty("z", gyrData.getZ());
                                sensorDataArray.add(gyroscopeObject);
                            }
                            break;
                        case Sensor.TYPE_GRAVITY:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                GravityData graData = (GravityData) data.getSensorData();
                                JsonObject gravityObject = new JsonObject();
                                gravityObject.addProperty("time", data.getTimeCaptured());
                                gravityObject.addProperty("x", graData.getX());
                                gravityObject.addProperty("y", graData.getY());
                                gravityObject.addProperty("z", graData.getZ());
                                sensorDataArray.add(gravityObject);
                            }
                            break;
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                SensorData data = entry.getValue().get(i);
                                LinearAccelerationData linearAccData = (LinearAccelerationData) data.getSensorData();
                                JsonObject linearAccObject = new JsonObject();
                                linearAccObject.addProperty("time", data.getTimeCaptured());
                                linearAccObject.addProperty("x", linearAccData.getX());
                                linearAccObject.addProperty("y", linearAccData.getY());
                                linearAccObject.addProperty("z", linearAccData.getZ());
                                sensorDataArray.add(linearAccObject);
                            }
                            break;
                    }
                    sensorData.add(entry.getKey().getId(), sensorDataArray);
                }

                recordings.add("sensor_data", sensorData);

                JsonArray fallDetectedArray = new JsonArray();
                for (Long time : mFallDetectedAtTimes) {
                    JsonObject fallDetectedObject = new JsonObject();
                    fallDetectedObject.addProperty("time", time);
                    fallDetectedArray.add(fallDetectedObject);
                }

                recordings.add("fall_detected_at_times", fallDetectedArray);


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

                /** gson.toJson(recordings) */


        /* prints
        {
              "test_id": "001",
              "fall_nr": "1",
              "sensor_data":
                {
                  "accelerometer_data": [
                    {
                      "time": 1639543,
                      "x": 0,
                      "y": 0,
                      "z": 0
                    },
                    ...
                  ],
                  "rotation_vector_data": [
                    {
                      "time": 1639543,
                      "x": 0,
                      "y": 0,
                      "z": 0,
                      "cos": 0,
                      "eha": 0
                    },
                    ...
                  ],
                  "magnetic_field_data": [
                    {
                      "time": 1639543,
                      "x": 0,
                      "y": 0,
                      "z": 0
                    },
                    ...
                  ],
                  "gyroscope_data": [
                    {
                      "time": 1639543,
                      "x": 0,
                      "y": 0,
                      "z": 0
                    },
                    ...
                  ],
                  "gravity_data": [
                    {
                      "time": 1639543,
                      "x": 0,
                      "y": 0,
                      "z": 0
                    },
                    ...
                  ],
                },
              "fall_detected_at_times": [
                  {
                    "time": 1639543
                  },
                  ...
                ],
        }
        */
                return null;
            }
        }.execute();
    }

    private void cancelRecording() {
        mFallDetectedAtTimes.clear();
        mRecordedData.clear();
        mIsRecording = false;

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
