package sintef.android.controller.algorithm;

import java.util.ArrayList;
import java.util.List;

import sintef.android.controller.sensor.SensorData;

/**
 * Created by araneae on 09.02.15.
 */
public class AlgorithmWatch
{

    private double fallIndex (List<SensorData> sensors)
    {
        List <Double> x = sensors.get(0);
        List <Double> y = sensors.get(0);
        List <Double> z = sensors.get(0);

        List <List> sensorData = new ArrayList<List>();
        sensorData.add(x);
        sensorData.add(y);
        sensorData.add(z);

        double directionAcceleration = 0;
        double totAcceleration = 0;
        double result = 0;

        for (int i = 0; i < sensorData.size(); i++)
        {
            for (int j = 0; j < sensorData.get(i).size(); j++)
            {
                if (j > 0)
                {
                    directionAcceleration += Math.pow(sensorData.get(i).get(j) - sensorData.get(i).get(j - 1), 2);
                }
                //not sure if this one is necessary
                else
                {
                    directionAcceleration = Math.pow((Double) sensorData.get(i).get(j), 2);
                }
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        result = Math.sqrt(totAcceleration);
        
        return result;
    }
}
