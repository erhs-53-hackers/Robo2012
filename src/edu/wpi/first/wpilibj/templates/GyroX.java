/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;

/**
 *
 * @author Nick, Alex
 */
public class GyroX {

    Jaguar drive;
    Gyro gyro;
    double modulatedAngle;
    double targetAngle = 0;

    public GyroX(final int gyroInit, Jaguar drive) {
        this.gyro = new Gyro(gyroInit);
        this.drive = drive;
    }

    public void turnToAngle(double newAngle) {
        refreshGyro();
        double nowAngle = newAngle - modulatedAngle;

        if (nowAngle > 180) {
            nowAngle -= 360;
        }
        if (nowAngle < -180) {
            nowAngle += 360;
        }
        int multi = (nowAngle > 0 ? 1 : -1);
        if (Math.abs(nowAngle) > 1.5) {
            if (Math.abs(nowAngle) < 10) {
                drive.set(.45 * multi);
            } else {
                drive.set(.75 * multi);
            }

        }

    }

    private double modAngle(double angle) {
        double retangle = angle % 360 + (angle > 180 ? -360 : 0);
        return retangle;
    }

    public double refreshGyro() {
        this.gyro.reset();
        modulatedAngle = modAngle(gyro.getAngle() * 4.14015366);
        return modulatedAngle;
    }
    /*
     * public void goStraight(boolean newTarget) { if (newTarget) { targetAngle
     * = modulatedAngle; } double driveConstant = (Math.abs(targetAngle -
     * modulatedAngle) > 10 ? .75 : .45) * ((targetAngle - modulatedAngle) > 0 ?
     * 1 : -1); drive.tankDrive(.25 - driveConstant, .25 + driveConstant); }
     *
     */

    public void turnAngle(double turnAmount) {
        double newAngle = modulatedAngle + turnAmount;
        turnToAngle(newAngle);
    }
}
