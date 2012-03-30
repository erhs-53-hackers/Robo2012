package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author Bernard, Alex, Mariana, Nick
 */
public class UltraCalc {

    private static double maxVoltage = 5.0;
    private static double maxDistance = 512; //inches
    private static double voltsScalar = maxVoltage / maxDistance;

    /**
     * Find the raw distance to the target
     *
     * @param volts average voltage of the ultrasonic sensor
     * @return raw distance to target, in inches
     */
    public static double getRawDistance(double volts) {
        double rawDistance = volts * 102;
        return rawDistance;
    }

    /**
     * get the distance to the target, scaled on a quartic polynomial of the
     * form ax^4 + bx^3 + cx^2 + dx + e
     *
     * @param volts average voltage of the ultrasonic sensor
     * @return scaled distance to target, in inches
     */
    public static double getScaledDistance(double volts) {
        double a = 3.92e-10;
        double b = -2.46e-7;
        double c = 5.68e-5;
        double d = -5.96e-3;
        double e = 1.29505;
        double rawDistance = getRawDistance(volts);
        if (rawDistance > 25) {
            double scale =
                    a * MathX.pow(rawDistance, 4)
                    + b * MathX.pow(rawDistance, 3)
                    + c * MathX.pow(rawDistance, 2)
                    + d * rawDistance
                    + e;
            return rawDistance * scale;
        } else {
            return rawDistance;
        }

    }
}
