package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.SensorData;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private double totAccThreshold, verticalAccThreshold, accComparisonThreshold, angleThreshold;

    public boolean calculateAccelerations(double x, double tetaX, double y, double tetaY, double z, double tetaZ, double priorAngle, double postAngle)
    {
        double totalAcceleration = accelerationTotal(x, y, z);
        double verticalAcceleration = verticalAcceleration(x, tetaX, y, tetaY, z, tetaZ);

        if (totalAcceleration >= totAccThreshold && verticalAcceleration >= verticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= accComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }


    public boolean angleOfPhone(double priorAngle, double postAngle)
    {
        if (postAngle - priorAngle >= angleThreshold){
            return true;
        }
        return false;
    }

    private double verticalComparedToTotal(double vertical, double total)
    {
        return 1;
    }

    //Calculates total acceleration at one point
    private double accelerationTotal(double x, double y, double z)
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    //calculates vertical acceleration at one point
    private double verticalAcceleration(double x, double tetaX, double y, double tetaY, double z, double tetaZ)
    {
        return x*Math.sin(tetaX) + z*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ);
    }
}
