package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Nick, Alex, Michael, Dale, Chris
 */
public class GyroX implements PIDSource, PIDOutput {

    double startAngle = 0; //Change depending on decided starting angle when beginning competition
    RobotDrive driveTrain;
    
    Gyro gyro;
    double modulatedAngle = 0;//Change depending on decided starting angle when beginning competition AKA start angle
    double targetAngle = 0;

    public GyroX(final int gyroChannel) {
        this.gyro = new Gyro(gyroChannel);
    }

    public GyroX(final int gyroChannel, RobotDrive robotDrive) {
        this.gyro = new Gyro(gyroChannel);
        this.driveTrain = robotDrive;
    }   

    private double modAngle(double angle) {
        double returnAngle = angle % 360 + (angle > 180 ? -360 : 0);
        return returnAngle;
    }
    

    public double refreshGyro() {        
        modulatedAngle = modAngle(gyro.getAngle() * 4.14015366);               
        return modulatedAngle;
    }

    public void turnRobotToAngle(double targetAngle) {
        refreshGyro();
        double nowAngle = targetAngle - modulatedAngle;
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
    
    public void turnRobot(double turnAmount) {
        double newAngle = modulatedAngle + turnAmount;
        while (MathX.abs(newAngle - modulatedAngle) > 2) {
            turnRobotToAngle(newAngle);
        }
    }

    public double getDriveConstant() {
        double driveConstant = (Math.abs(targetAngle
                - modulatedAngle) > 10 ? .75 : .45) * ((targetAngle - modulatedAngle) > 0
                ? 1 : -1);
        return driveConstant;
    }
    
    public double pidGet() {
        System.out.println(refreshGyro());
        System.out.println("in:" + refreshGyro());
        
        return refreshGyro();
    }

    public void pidWrite(double output) {
        //lazySusan.setRaw((int)output);
        System.out.println("out:" + output);
        driveTrain.arcadeDrive(0, -output);
    }
    
}
