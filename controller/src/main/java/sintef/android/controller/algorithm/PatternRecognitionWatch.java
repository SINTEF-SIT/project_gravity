package sintef.android.controller.algorithm;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;

/**
 * Created by araneae on 09.04.15.
 */
public class PatternRecognitionWatch implements AlgorithmInterface
{
    //TODO: get data to make the thresholds better.
    private static final double thresholdFall = 1; //20
    private static final double thresholdStill = 500; //5
    private static final double atleastReadings = 10;
    private static int movementThreshold = 50;

    //Calculate the acceleration.
    private static FallIndexValues fallIndex(List<LinearAccelerationData> sensors, int startList)
    {

        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();
        int startValue = startList;

        for (LinearAccelerationData xyz : sensors)
        {
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
        //double result;

        for (int i = 0; i < sensorData.size(); i++)
        {
            for (int j = startValue; j < sensorData.get(i).size(); j++)
            {
                directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
                if (Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2) > movementThreshold && startList < j)
                {
                    startList = j;
                }
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        return new FallIndexValues (Math.sqrt(totAcceleration), startList);
    }

    private static double stillPattern(List<LinearAccelerationData> sensors, int startList)
    {
        return fallIndex(sensors, startList).getFallData();
    }


    //Recognize fall pattern, and decide if there is a fall or not
    public static boolean patternRecognition(List<LinearAccelerationData> sensors)
    {
        FallIndexValues accelerationData;
        double afterFallData;
        int startList = 1;
        accelerationData = fallIndex(sensors, startList);

        if (accelerationData.getFallData() >= thresholdFall && sensors.size()-accelerationData.getStartIndex() > atleastReadings)
        {
            afterFallData = stillPattern(sensors, accelerationData.getStartIndex());
            return afterFallData <= thresholdStill;
        }
        return false;
    }

    @Override
    public boolean isFall(SensorAlgorithmPack pack) {
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
        return patternRecognition(accDataWatch);
    }

    private static class FallIndexValues
    {
        private double fallData;
        private int startIndex;
        private final int layingDownCount = 10; //number of readings to skip when checking if the person is laying still, so that it will not check the fall again and therefor say that the person is not laying still

        FallIndexValues(double fallData, int startIndex)
        {
            this.fallData = fallData;
            this.startIndex = startIndex+layingDownCount;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public double getFallData() {
            return fallData;
        }
    }
}
