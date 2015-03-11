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
import sintef.android.controller.sensor.data.MagneticFieldData;
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
        if (mIsRecording && type == EventTypes.FALL_DETECTED) {
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
                JsonObject recordings = new JsonObject();

                recordings.addProperty("test_id", mTestIdInput.getText().toString());

                JsonObject sensorData = new JsonObject();

                JsonArray accelerometerArray = new JsonArray();
                JsonArray rotationVectorArray = new JsonArray();
                JsonArray magneticFieldArray = new JsonArray();
                JsonArray gyroscopeArray = new JsonArray();
                JsonArray gravityArray = new JsonArray();

                for (SensorAlgorithmPack pack : mSensorAlgorithmPackList) {
                    for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
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
                                    accelerometerArray.add(accelerometerObject);
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
                                    rotationVectorArray.add(rotationVectorObject);
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
                                    magneticFieldArray.add(magneticFieldObject);
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
                                    gyroscopeArray.add(gyroscopeObject);
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
                                    gravityArray.add(gravityObject);
                                }
                                break;
                        }
                    }
                }

                sensorData.add("accelerometer_data", accelerometerArray);
                sensorData.add("rotation_vector_data", rotationVectorArray);
                sensorData.add("magnetic_field_data", magneticFieldArray);
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
