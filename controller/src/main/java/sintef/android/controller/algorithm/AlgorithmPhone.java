package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.SensorData;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private static double totAccThreshold, verticalAccThreshold, accComparisonThreshold, angleThreshold;

    public static boolean calculateAccelerations(double x, double y, double tetaY, double z, double tetaZ)
    {
        double totalAcceleration = accelerationTotal(x, y, z);
        double verticalAcceleration = verticalAcceleration(x, y, tetaY, z, tetaZ);

        if (totalAcceleration >= totAccThreshold && verticalAcceleration >= verticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= accComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }



    public static boolean angleOfPhone(double priorAngle, double postAngle)
    {
        if (postAngle - priorAngle >= angleThreshold){
            return true;
        }
        return false;
    }

    private static double verticalComparedToTotal(double vertical, double total)
    {
        return total/vertical;
    }

    //Calculates total acceleration at one point
    private static double accelerationTotal(double x, double y, double z)
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    //calculates vertical acceleration at one point
    private static double verticalAcceleration(double x, double y, double tetaY, double z, double tetaZ)
    {
        return x*Math.sin(tetaZ) + z*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ);
    }
}
