package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.data.AccelerometerData;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private static double totAccThreshold = 9;
    private static double verticalAccThreshold = 8;
    private static double accComparisonThreshold = 0.75;
    private static double angleThreshold = 20;

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ)
    {
        double totalAcceleration = accelerationTotal(x, y, z);
        double verticalAcceleration = verticalAcceleration(x, y, z, tetaY, tetaZ);

        if (totalAcceleration >= totAccThreshold && verticalAcceleration >= verticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) <= accComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ, double testtotAccThreshold, double testverticalAccThreshold, double testaccComparisonThreshold)
    {
        double totalAcceleration = accelerationTotal(x, y, z);
        double verticalAcceleration = verticalAcceleration(x, y, z, tetaY, tetaZ);

        if (totalAcceleration >= testtotAccThreshold && verticalAcceleration >= testverticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) <= testaccComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    /*
    impact pattern recognition:
    if there is a massive deaccelreation close after the max acceleration impact indicates that it is a fall.
     */
    public boolean impactPattern(List<AccelerometerData> accelerometerData, int indeks){
        double maxAcceleration = 0;
        int counter = 0;
        double currentAcceleration;
        double threshold1 = 5;

        for (int i = indeks; indeks < accelerometerData.size(); i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration > maxAcceleration){
                maxAcceleration = currentAcceleration;
                counter = 10;
            }
            else {
                if(counter != 0 && currentAcceleration*threshold1 < maxAcceleration){return true;}
                counter --;
            }
        }
        return false;
    }

    public static boolean isPhoneVertical(double priorAngle, double postAngle, double angleThreshold)
    {
        if (postAngle - priorAngle >= angleThreshold){ return true; }
        return false;
    }

    private static double verticalComparedToTotal(double vertical, double total)
    {
        return vertical/total;
    }

    //Calculates total acceleration at one point
    private static double accelerationTotal(double x, double y, double z)
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    //calculates vertical acceleration at one point
    private static double verticalAcceleration(double x, double y, double z, double tetaY, double tetaZ)
    {
        return x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ);
    }
}
