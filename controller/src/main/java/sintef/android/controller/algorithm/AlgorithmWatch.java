package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.List;

import sintef.android.controller.sensor.SensorData;

/**
 * Created by araneae on 09.02.15.
 */
public class AlgorithmWatch
{
    //TODO: find out how the sensor data should be read, so that it can be used in the Fall Index method.

    //call it something better when I find something better to name it
    //the main method of the algorithm. Will allway run (or sleep), and does the fetching of sensordata, and runs the other methods when needed.
    public void fallAlgorithm ()
    {
        //TODO: make this.
    }

    //Calculate the acceleration.
    private double fallIndex (List<SensorData> sensors)
    {
        List <Double> x = (List<Double>) sensors.get(0);
        List <Double> y = (List<Double>) sensors.get(0);
        List <Double> z = (List<Double>) sensors.get(0);

        List <List> sensorData = new ArrayList<List>();
        sensorData.add(x);
        sensorData.add(y);
        sensorData.add(z);

        double directionAcceleration = 0;
        double totAcceleration = 0;
        //double result;

        for (int i = 0; i < sensorData.size(); i++)
        {
            for (int j = 0; j < sensorData.get(i).size(); j++)
            {
                if (j > 0)
                {
                    directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
                }
                //not sure if this one is necessary/destroys the algorithm
                else
                {
                    directionAcceleration = Math.pow((Double) sensorData.get(i).get(j), 2);
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
    public boolean patternRecognition (double accelerationData)
    {
        //TODO: make this
        double threshold = 0;
        return accelerationData >= threshold;
    }
}
