/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Nick, Alex, Michael
 */
public class GyroX {

    PWM drive;
    
    RobotDrive driveTrain;
    Gyro gyro;
    double modulatedAngle;
    double targetAngle = 0;

    public GyroX(final int gyroInit, final int port, RobotDrive robo) {
        this.gyro = new Gyro(gyroInit);
        this.drive = new PWM(port);
        this.driveTrain = robo;
        
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
                drive.setRaw((int)(.45 * multi * 255));
            } else {
                drive.setRaw((int)(.75 * multi * 255));
            }

        }

    }
    public void turnRobotToAngle(double newAngle) {
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
                driveTrain.mecanumDrive_Polar(0, 0, .45 * multi);
            } else {
                driveTrain.mecanumDrive_Polar(0, 0, .75 * multi);
            }

        }

    }

    private double modAngle(double angle) {
        double retangle = angle % 360 + (angle > 180 ? -360 : 0);
        return retangle;
    }

    public double refreshGyro() {
        //this.gyro.reset();
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
        while (MathX.abs(newAngle - modulatedAngle) > 2) {
            turnToAngle(newAngle);
        }
    }
}
