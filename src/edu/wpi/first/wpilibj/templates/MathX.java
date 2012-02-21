package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.util.MathUtils;

/**
 * Implements java.lang.Math and com.sun.squak.util.MathUtils to trig functions 
 * to use degrees instead of Radians.
 * For use in Physics class, specifically
 * @author Nick
 */
public class MathX {

    public static double round(double input) {
        double result = MathUtils.round(input);
        return result;
    }

    public static double abs(double input) {
        double result = java.lang.Math.abs(input);
        return result;
    }

    public static double tan(double input) {
        double result = java.lang.Math.tan(
                java.lang.Math.toRadians(input));
        return result;
    }

    public static double atan(double input) {
        double result = java.lang.Math.toDegrees(
                MathUtils.atan(input));
        return result;
    }

    public static double cos(double input) {
        double result = java.lang.Math.cos(
                java.lang.Math.toRadians(input));
        return result;
    }

    public static double acos(double input) {
        double result = java.lang.Math.toDegrees(
                MathUtils.acos(input));
        return result;
    }

    public static double sin(double input) {
        double result = java.lang.Math.sin(
                java.lang.Math.toRadians(input));
        return result;
    }

    public static double asin(double input) {
        double result = java.lang.Math.toDegrees(
                MathUtils.asin(input));
        return result;
    }

    public static double pow(double a, double b) {
        return MathUtils.pow(a, b);
    }

    public static double sqrt(double d) {
        return java.lang.Math.sqrt(d);
    }
}
