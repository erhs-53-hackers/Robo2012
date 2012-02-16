/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Nick, Alex
 */

public class GyroX {
    
    RobotDrive drive;
    Gyro gyro;
    double modulatedAngle;
    double targetAngle = 0;
    
    public GyroX(final int gyroInit, RobotDrive drive) {
        this.gyro = gyro;
        this.drive = drive;
    }

    public void turnToAngle(double newAngle, double currentAngle) {
        double nowAngle = newAngle-currentAngle;
        if(nowAngle>180) nowAngle -= 360;
        if(nowAngle<-180) nowAngle += 360;
        double driverate = nowAngle / 180;
        int multi = (nowAngle > 0 ? 1: -1);
        if(Math.abs(nowAngle) > 1.5){
            if(Math.abs(nowAngle) < 10) drive.tankDrive(.45 * multi, -.45 * multi);
            else drive.tankDrive(.75 * multi,-.75 * multi);
        }
    }
    
    private double modAngle(double inpangle){
        double retangle = inpangle%360 + (inpangle>180 ? -360:0);
        return retangle;
    }
    
    public double refreshGyro() {
        modulatedAngle = modAngle(gyro.getAngle() * 4.14015366);
        return modulatedAngle;
    }
    
    public void goStraight(boolean newTarget){
        if(newTarget) targetAngle = modulatedAngle;
        double driveConstant = (Math.abs(targetAngle-modulatedAngle)>10 ? .75:.45) * ((targetAngle-modulatedAngle)>0 ? 1:-1);
        drive.tankDrive(.25-driveConstant,.25+driveConstant);
    }
    
}
