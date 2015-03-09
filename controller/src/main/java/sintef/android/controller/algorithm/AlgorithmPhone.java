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
    private static double impactThreshold = 3;

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

    //main pattern recognition method
    public static boolean Patternrecognition(List<AccelerometerData> accelerometerData, int index){
        if (impactPattern(accelerometerData, index)){
            return true;
        }
        return false;
    }
    /*
    impact pattern recognition:
    if there is a massive deaccelreation close after the max acceleration impact indicates that it is a fall.

        Test values == TRUE:
    impactThreshold = 3
    x = [3, 4, 2, 6, 4, 4, 2]
    y = [3, 4, 2, 6, 4, 3, 1]
    z = [3, 4, 2, 6, 4, 4, 2]
        Test values == FALSE:
    impactThreshold = 3
    x = [6, 5, 5, 4, 4, 3, 0]
    y = [6, 5, 5, 4, 3, 3, 0]
    z = [6, 5, 5, 4, 3, 3, 0]
     */
    private static boolean impactPattern(List<AccelerometerData> accelerometerData, int index){
        double maxAcceleration = 0;
        int counter = 0;
        final int iterationsAfterMaxAcc = 5;
        double currentAcceleration;

        for (int i = index; i < accelerometerData.size(); i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration > maxAcceleration){
                maxAcceleration = currentAcceleration;
                counter = iterationsAfterMaxAcc;
            }
            else {
                if(counter == 0){return false;}
                if(currentAcceleration*impactThreshold < maxAcceleration){return true;}
                counter --;
            }
        }
        return false;
    }
    //FOR TESTING PURPOSES
    public static boolean impactPattern(List<AccelerometerData> accelerometerData, int index, double impactThreshold){
        double maxAcceleration = 0;
        int counter = 0;
        final int iterationsAfterMaxAcc = 5;
        double currentAcceleration;

        for (int i = index; i < accelerometerData.size(); i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration > maxAcceleration){
                maxAcceleration = currentAcceleration;
                counter = iterationsAfterMaxAcc;
            }
            else {
                if(counter == 0){return false;}
                if(currentAcceleration*impactThreshold < maxAcceleration){return true;}
                counter --;
            }
        }
        return false;
    }
    /*
    pattern for pre impact:
    increase in acceleration,
     */
    public static boolean preImpactpattern(List<AccelerometerData> accelerometerData, int index){
        int something = 15;

        if (index < something){something = index;}
        for (int i = index-something; i < index; i++){
            //TODO: fill in
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
