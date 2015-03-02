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
import sintef.android.controller.algorithm.SensorAlgorithmPack;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.GravityData;
import sintef.android.controller.sensor.data.GyroscopeData;
import sintef.android.controller.sensor.data.RotationVectorData;
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
    @InjectView(R.id.record_fall_nr)            FloatingHintEditText mFallNrInput;
    @InjectView(R.id.record)                    View mRecordIcon;
    @InjectView(R.id.stop)                      View mStopIcon;
    @InjectView(R.id.record_button)             FrameLayout mRecordButton;
    @InjectView(R.id.record_time)               TextView mRecordTime;

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

    public List<SensorAlgorithmPack> mSensorAlgorithmPackList = new ArrayList<>();
    public List<Long> mFallDetectedAtTimes = new ArrayList<>();
    private Timer mTimer;

    private boolean mIsRecording = false;
    private boolean mEveryOtherPack = true;

    public void onEvent(SensorAlgorithmPack pack) {
        if (mIsRecording && mEveryOtherPack) {
            mSensorAlgorithmPackList.add(pack);
            mEveryOtherPack = false;
        } else {
            mEveryOtherPack = true;
        }
    }

    public void onEvent(EventTypes type) {
        if (mIsRecording && type == EventTypes.ALARM_DETECTED) {
            mFallDetectedAtTimes.add(System.currentTimeMillis());
        }
    }

    private void startRecording() {
        mFallDetectedAtTimes.clear();
        mSensorAlgorithmPackList.clear();
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
                HashMap<Long, AccelerometerData> accelerometerData = new HashMap<>();
                HashMap<Long, RotationVectorData> rotationVectorData = new HashMap<>();
                HashMap<Long, GyroscopeData> gyroscopeData = new HashMap<>();
                HashMap<Long, GravityData> gravityData = new HashMap<>();
                for (SensorAlgorithmPack pack : mSensorAlgorithmPackList) {
                    for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
                        switch (entry.getKey().getSensorType()) {
                            case Sensor.TYPE_ACCELEROMETER:
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    accelerometerData.put(entry.getValue().get(i).getTimeCaptured(), (AccelerometerData) entry.getValue().get(i).getSensorData());
                                }
                                break;
                            case Sensor.TYPE_ROTATION_VECTOR:
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    rotationVectorData.put(entry.getValue().get(i).getTimeCaptured(), (RotationVectorData) entry.getValue().get(i).getSensorData());
                                }
                                break;
                            case Sensor.TYPE_GYROSCOPE:
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    gyroscopeData.put(entry.getValue().get(i).getTimeCaptured(), (GyroscopeData) entry.getValue().get(i).getSensorData());
                                }
                                break;
                            case Sensor.TYPE_GRAVITY:
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    gravityData.put(entry.getValue().get(i).getTimeCaptured(), (GravityData) entry.getValue().get(i).getSensorData());
                                }
                                break;
                        }
                    }
                }

                JsonObject recordings = new JsonObject();

                recordings.addProperty("test_id", mTestIdInput.getText().toString());
                recordings.addProperty("fall_nr", mFallNrInput.getText().toString());

                JsonObject sensorData = new JsonObject();

                JsonArray accelerometerArray = new JsonArray();
                for (Map.Entry<Long, AccelerometerData> accData : accelerometerData.entrySet()) {
                    JsonObject accelerometerObject = new JsonObject();
                    accelerometerObject.addProperty("time", accData.getKey());
                    accelerometerObject.addProperty("x", accData.getValue().getX());
                    accelerometerObject.addProperty("y", accData.getValue().getY());
                    accelerometerObject.addProperty("z", accData.getValue().getZ());
                    accelerometerArray.add(accelerometerObject);
                }

                JsonArray rotationVectorArray = new JsonArray();
                for (Map.Entry<Long, RotationVectorData> rotData : rotationVectorData.entrySet()) {
                    JsonObject rotationVectorObject = new JsonObject();
                    rotationVectorObject.addProperty("time", rotData.getKey());
                    rotationVectorObject.addProperty("x", rotData.getValue().getX());
                    rotationVectorObject.addProperty("y", rotData.getValue().getY());
                    rotationVectorObject.addProperty("z", rotData.getValue().getZ());
                    rotationVectorObject.addProperty("cos", rotData.getValue().getCos());
                    rotationVectorObject.addProperty("eha", rotData.getValue().getEstimatedHeadingAccuracy());
                    rotationVectorArray.add(rotationVectorObject);
                }

                JsonArray gyroscopeArray = new JsonArray();
                for (Map.Entry<Long, GyroscopeData> gyrData : gyroscopeData.entrySet()) {
                    JsonObject gyroscopeObject = new JsonObject();
                    gyroscopeObject.addProperty("time", gyrData.getKey());
                    gyroscopeObject.addProperty("x", gyrData.getValue().getX());
                    gyroscopeObject.addProperty("y", gyrData.getValue().getY());
                    gyroscopeObject.addProperty("z", gyrData.getValue().getZ());
                    gyroscopeArray.add(gyroscopeObject);
                }

                JsonArray gravityArray = new JsonArray();
                for (Map.Entry<Long, GravityData> graData : gravityData.entrySet()) {
                    JsonObject gravityObject = new JsonObject();
                    gravityObject.addProperty("time", graData.getKey());
                    gravityObject.addProperty("x", graData.getValue().getX());
                    gravityObject.addProperty("y", graData.getValue().getY());
                    gravityObject.addProperty("z", graData.getValue().getZ());
                    gravityArray.add(gravityObject);
                }

                sensorData.add("accelerometer_data", accelerometerArray);
                sensorData.add("rotation_vector_data", rotationVectorArray);
                sensorData.add("gyroscope_data", gyroscopeArray);
                sensorData.add("gravity_data", gravityArray);

                recordings.add("sensor_data", sensorData);

                JsonArray fallDetectedArray = new JsonArray();
                for (Long time : mFallDetectedAtTimes) {
                    JsonObject fallDetectedObject = new JsonObject();
                    fallDetectedObject.addProperty("time", time);
                    fallDetectedArray.add(fallDetectedObject);
                }

                recordings.add("fall_detected_at_times", fallDetectedArray);


                Gson gson = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

                try {
                    Socket socket = new Socket("projectgravity.no-ip.org", 8765);
                    DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                    DOS.writeBytes(gson.toJson(recordings));
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
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
        mSensorAlgorithmPackList.clear();
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
