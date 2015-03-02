package sintef.android.controller.algorithm;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private static double totAccThreshold = 5;
    private static double verticalAccThreshold = 5;
    private static double accComparisonThreshold = 0.5;
    private static double angleThreshold = 30;
    private static double gravity = 9.81;

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
        return Math.abs(x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ));
    }
}
