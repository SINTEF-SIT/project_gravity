package sintef.android.controller.algorithm;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import sintef.android.controller.EventTypes;
import sintef.android.controller.RecordAlgorithmData;
import sintef.android.controller.RecordEventData;
import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;
import sintef.android.controller.utils.PreferencesHelper;

/**
 * Created by araneae on 09.02.15.
 */
public class ThresholdWatch implements AlgorithmInterface {

    public static final String FALLINDEX_IMPACT = "imp_thr";

    //TODO: get data to make the thresholds better.
    public static final double default_thresholdFall = 30; //20

    //Calculate the acceleration.
    private static double fallIndex(List<LinearAccelerationData> sensors, int startList){

        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();
        int startValue = startList;

        for (LinearAccelerationData xyz : sensors){
            x.add((double) xyz.getX());
            y.add((double) xyz.getY());
            z.add((double) xyz.getZ());
        }

        List <List> sensorData = new ArrayList<List>();
        sensorData.add(x);
        sensorData.add(y);
        sensorData.add(z);

        double directionAcceleration = 0;
        double totAcceleration = 0;

        for (int i = 0; i < sensorData.size(); i++){
            for (int j = startValue; j < sensorData.get(i).size(); j++){
                directionAcceleration += Math.pow((Double) sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        return Math.sqrt(totAcceleration);
    }
    //Recognize fall pattern, and decide if there is a fall or not
    public static boolean thresholdAlgorithmWatch(List<LinearAccelerationData> sensors){
        if (sensors.size() == 0) {return true;}

        double accelerationData;
        int startList = 1;

        accelerationData = fallIndex(sensors, startList);

        /** RECORDING - fallIndex */
        if (PreferencesHelper.isRecording()) EventBus.getDefault().post(new RecordEventData(EventTypes.RECORDING_WATCH_FALL_INDEX, accelerationData));


        return accelerationData >= getThresholdFall();
    }

    @Override
    public boolean isFall(long id, SensorAlgorithmPack pack) {
        List<LinearAccelerationData> accDataWatch = new ArrayList<>();

        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            switch (entry.getKey().getSensorDevice()) {
                case WATCH:
                    switch (entry.getKey().getSensorType()){
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                accDataWatch.add((LinearAccelerationData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                    }
                    break;
            }

        }

        boolean isFall = thresholdAlgorithmWatch(accDataWatch);

        /** RECORDING - isFall */
        if (PreferencesHelper.isRecording()) EventBus.getDefault().post(new RecordAlgorithmData(id, "watch_threshold", isFall));

        return isFall;
    }

    public static double getThresholdFall() {
        return PreferencesHelper.getFloat(FALLINDEX_IMPACT, (float) default_thresholdFall);
    }
}
