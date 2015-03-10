package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.List;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.data.AccelerometerData;

/**
 * Created by araneae on 09.02.15.
 */
public class AlgorithmWatch
{
    private static final double thresholdFall = 2;
    //private static final double thresholdImpact = 8;
    private static final double thresholdStill = 3;
    private static  final double gravity = 9.81;

    //Calculate the acceleration.
    //Switch back to List <SensorData> after testing
    private static double fallIndex(List<AccelerometerData> sensors, int startXYZ, int startList)
    {

        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();

        double fall = 0;

        for (AccelerometerData xyz : sensors)
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
            for (int j = 1; j < sensorData.get(i).size(); j++)
            {
                directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
                if (Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2) > fall)
                {
                    fall = Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
                    startXYZ = i;
                    startList = j;
                }
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        //result = Math.sqrt(totAcceleration);

        return Math.sqrt(totAcceleration);
    }

    private static double fallIndex(List<AccelerometerData> sensors, int numberOfCalculations)
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
    }

    private static double stillPattern(List<AccelerometerData> sensors, int startXYZ, int startList)
    {
        return fallIndex(sensors, startXYZ, startList);
    }


    //might change the name/remove this if necessary, might want to change the parameter
    //Recognize fall pattern, and decide if there is a fall or not
    public static boolean patternRecognition(List<AccelerometerData> sensors)
    {
        double accelerationData;
        //double impactFallData;
        double afterFallData;
        int startXYZ = 0;
        int startList = 0;

        //if (sensors.size() >= 20) accelerationData = fallIndex(sensors, 20);
        accelerationData = fallIndex(sensors, startXYZ, startList);

        if (accelerationData > thresholdFall)
        {
            afterFallData = stillPattern(sensors, startXYZ, startList);
            return afterFallData < thresholdStill;
        }
        return false;
    }
}
