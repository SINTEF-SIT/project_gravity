package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.data.AccelerometerData;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private static double totAccThreshold = 6;
    private static double verticalAccThreshold = 5;
    private static double accComparisonThreshold = 0.5;
    private static double angleThreshold = 30;
    private static double gravity = 9.81;
    private static double impactThreshold = 3;
    private static double preimpactThreshold = 3;

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ)
    {
        double totalAcceleration = Math.abs(gravity-accelerationTotal(x, y, z));
        double verticalAcceleration = Math.abs(gravity-verticalAcceleration(x, y, z, tetaY, tetaZ));

        if (totalAcceleration >= totAccThreshold && verticalAcceleration >= verticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= accComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ, double testtotAccThreshold, double testverticalAccThreshold, double testaccComparisonThreshold)
    {
        double totalAcceleration = Math.abs(gravity-accelerationTotal(x, y, z));    
        double verticalAcceleration = Math.abs(gravity-verticalAcceleration(x, y, z, tetaY, tetaZ));

        if (totalAcceleration >= testtotAccThreshold && verticalAcceleration >= testverticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= testaccComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    //main pattern recognition method
    public static boolean Patternrecognition(List<AccelerometerData> accelerometerData){
        double maxAcceleration = 0;
        double currentAcceleration;
        int index = 0;

        //iterating over the data and finds the point with the highest acceleration
        for (int i = 0; i < accelerometerData.size(); i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration > maxAcceleration){
                index = i;
                maxAcceleration = currentAcceleration;
            }
        }

        if (impactPattern(accelerometerData, index, maxAcceleration)){
            return true;
        }
        return false;
    }
    /*
    impact pattern recognition:
    if there is a massive deaccelreation close after the max acceleration impact indicates that it is a fall.

        Test values == TRUE:
    x = [3, 4, 2, 6, 4, 4, 2]
    y = [3, 4, 2, 6, 4, 3, 1]
    z = [3, 4, 2, 6, 4, 4, 2]
    index = 3
    maxAcceleration = 10.39
    TESTimpactThreshold = 3

        Test values == FALSE:
    x = [6, 5, 5, 4, 4, 3, 0]
    y = [6, 5, 5, 4, 3, 3, 0]
    z = [6, 5, 5, 4, 3, 3, 0]
    index = 0
    maxAcceleration = 10.39
    TESTimpactThreshold = 3
     */
    private static boolean impactPattern(List<AccelerometerData> accelerometerData, int index, double maxAcceleration){
        final int iterationsAfterMaxAcc = 5;
        double currentAcceleration;

        //iterating from toppoint to see if there is a big deacceleration after it.
        for (int i = index+1; i <= index+iterationsAfterMaxAcc; i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration*impactThreshold <= maxAcceleration){
                return true;
            }
            return false;
        }
        return false;
    }
    //FOR TESTING PURPOSES
    public static boolean impactPattern(List<AccelerometerData> accelerometerData, int index, double maxAcceleration, double TESTimpactThreshold){
        final int iterationsAfterMaxAcc = 5;
        double currentAcceleration;

        //iterating from toppoint to see if there is a big deacceleration after it.
        for (int i = index+1; i <= index+iterationsAfterMaxAcc; i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration*TESTimpactThreshold <= maxAcceleration){
                return true;
            }
            return false;
        }
        return false;
    }

    /*
    pattern for pre impact:
    increase in acceleration, to the max
    hvis det ved et punkt før var mye lavere akslerasjon så indikerer det et fall i preimpact stadiet.

        Test values == TRUE:
    x = [1, 3, 4, 6]
    y = [1, 3, 4, 6]
    z = [1, 3, 4, 6]
    index = 3
    maxAcceleration = 10,39
    TESTpreimpactThreshold = 3

        Test values == FALSE
    x = [1, 3, 4, 4, 4, 4, 6]
    y = [1, 3, 4, 4, 4, 4, 6]
    z = [1, 3, 4, 4, 4, 4, 6]
    index = 6
    maxAcceleration = 10,39
    TESTpreimpactThreshold = 3
    */
    private static boolean preImpactpattern(List<AccelerometerData> accelerometerData, int index, double maxAcceleration){
        double currentAcceleration;
        int n = 5;
        int endLoop = index-n;

        if (endLoop < 0){endLoop = 0;}

        for (int i = index-1; i >= endLoop; i--){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
            if (currentAcceleration*preimpactThreshold < maxAcceleration){return true;}
        }
        return false;
    }
    //for testing purposes
    public static boolean preImpactpattern(List<AccelerometerData> accelerometerData, int index, double maxAcceleration, double TESTpreimpactThreshold){
        double currentAcceleration;
        int n = 5;

        for (int i = index-1; i >= index-n; i--){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
            if (currentAcceleration*TESTpreimpactThreshold < maxAcceleration){return true;}
        }
        return false;
    }


    public static boolean isPhoneVertical(double priorAngle, double postAngle, double angleThreshold)
    {
        return postAngle - priorAngle >= angleThreshold;
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
    private static double verticalAcceleration(double x, double y, double z, double tetaY, double tetaZ)
    {
        return Math.abs(x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ));
    }
}
