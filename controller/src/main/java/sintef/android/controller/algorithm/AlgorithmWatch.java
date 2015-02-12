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
    //Calculate the acceleration.
    //Switch back to List <SensorData> after testing
    private static double fallIndex(List<AccelerometerData> sensors)
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
            for (int j = 1; j < sensorData.get(i).size(); j++)
            {
                directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
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
                if (j > 0)
                {

                    directionAcceleration += Math.pow((Double) sensorData.get(i).get(j) - (Double) sensorData.get(i).get(j - 1), 2);

                }
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        //result = Math.sqrt(totAcceleration);

        return Math.sqrt(totAcceleration);
    }


    //might change the name/remove this if necessary, might want to change the parameter
    //Recognize fall pattern, and decide if there is a fall or not
    //
    public static boolean patternRecognition(List<AccelerometerData> sensors)
    {
        //TODO: make this even better
        //might not need the ", 20" here, depends on how we set up the use of this algorithm
        double accelerationData = fallIndex(sensors, 20);
        double afterFallData;
        //for the fall
        //just a guess for the moment, update it when we see what it should be
        double thresholdMa = 4;
        //for not moving after the fall
        //just a guess for the moment, update it when we see what it should be
        double thresholdMi = 0.5;
        if (accelerationData > thresholdMa)
        {
            return true;
            //TODO: fix pattern recognition
            //might be unnecessary, have to test a little bit before we can be sure.
            //afterFallData = fallIndex(sensors, 5);
            //if (afterFallData < thresholdMi)
            //{
            //}
            //else
            //{
            //    return false;
            //}
        }
        else
        {
            return false;
        }
    }
}
