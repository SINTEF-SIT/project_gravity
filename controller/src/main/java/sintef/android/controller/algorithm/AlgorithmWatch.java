package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.List;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.data.AccelerometerData;
import sintef.android.controller.sensor.data.LinearAccelerationData;

/**
 * Created by araneae on 09.02.15.
 */
public class AlgorithmWatch
{
    //TODO: get data to make the thresholds better.
    private static final double thresholdFall = 20;
    private static final double thresholdStill = 5;
    private static final double gravity = 9.81;
    private static final double atleastReadings = 10;

    //Calculate the acceleration.
    //Switch back to List <SensorData> after testing
    private static FallIndexValues fallIndex(List<LinearAccelerationData> sensors, int startList)
    {

        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();
        int startValue = startList;

        double fall = 0;

        for (LinearAccelerationData xyz : sensors)
        {
            x.add((double) xyz.getX());
            y.add((double) xyz.getY()-gravity);
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
                if (Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2) > 50 && startList < j)
                {
                    startList = j;
                }
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        return new FallIndexValues (Math.sqrt(totAcceleration), startList);
    }

    /*private static double fallIndex(List<AccelerometerData> sensors, int numberOfCalculations)
    {
        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();


        for (AccelerometerData xyz : sensors)
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
            for (int j = sensorData.get(i).size()-numberOfCalculations; j < sensorData.get(i).size(); j++) {
                if (j > 0) directionAcceleration += Math.pow((Double) sensorData.get(i).get(j) - (Double) sensorData.get(i).get(j - 1), 2);
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        //result = Math.sqrt(totAcceleration);

        return Math.sqrt(totAcceleration);
    }*/

    private static double stillPattern(List<LinearAccelerationData> sensors, int startList)
    {
        return fallIndex(sensors, startList).getFallData();
    }


    //might change the name/remove this if necessary, might want to change the parameter
    //Recognize fall pattern, and decide if there is a fall or not
    public static boolean patternRecognition(List<LinearAccelerationData> sensors)
    {
        FallIndexValues accelerationData;
        //double impactFallData;
        double afterFallData;
        int startList = 1;

        //if (sensors.size() >= 20) accelerationData = fallIndex(sensors, 20);
        accelerationData = fallIndex(sensors, startList);

        if (accelerationData.getFallData() >= thresholdFall && sensors.size()-accelerationData.getStartIndex() > atleastReadings)
        {
            afterFallData = stillPattern(sensors, accelerationData.getStartIndex());
            return afterFallData <= thresholdStill;
        }
        return false;
    }
}
class FallIndexValues
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
