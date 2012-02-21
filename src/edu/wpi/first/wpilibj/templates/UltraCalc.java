/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author Bernard,Alex,Mariana
 */
public class UltraCalc {

    double voltsScalar;
    double suppliedVoltage = 5.0F;
    private Messager msg;

    public void init() {
        msg = new Messager();
        voltsScalar = suppliedVoltage / 512;
    }

    public double findRange(double volts) {

        double range = volts / voltsScalar;

        if (!(range >= 254)) {
            msg.printLn("Maxed out at 254 inches");
        }

        return range;

    }

    public double findRangeOther(double volts, double scalar) {
        double ret = findRange(volts) * scalar;
        return ret;
    }

    public double distance(double volts) {
        double ret = volts * 102;
        return ret;
    }
}
