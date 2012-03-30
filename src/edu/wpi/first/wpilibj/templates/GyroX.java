package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Nick, Alex, Michael, Dale, Chris
 */
public class GyroX implements PIDSource, PIDOutput {

    double startAngle = 0; //Change depending on decided starting angle when beginning competition
    RobotDrive driveTrain;
    PWM lazySusan;
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

    public GyroX(final int gyroChannel, final int lazySusanTurnPort, RobotDrive robotDrive) {
        this.gyro = new Gyro(gyroChannel);
        this.lazySusan = new PWM(lazySusanTurnPort);
        this.driveTrain = robotDrive;
    }

    private double modAngle(double angle) {
        double returnAngle = angle % 360 + (angle > 180 ? -360 : 0);
        return returnAngle;
    }
    double last = 0;

    public double refreshGyro() {        
        modulatedAngle = modAngle(gyro.getAngle() * 4.14015366);
        //System.out.println("" + gyro.getAngle());
        double rejetheth = gyro.pidGet()- last;
        last = rejetheth;
        //System.out.println("PID:" + rejetheth);
        
        return modulatedAngle;
    }

    public void turnTurretToAngle(double targetAngle) {
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
                lazySusan.setRaw((int) (.45 * multi * 255));
            } else {
                lazySusan.setRaw((int) (.75 * multi * 255));
            }
        }
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

    public void turnTurret(double turnAmount) {
        double newAngle = modulatedAngle + turnAmount;
        while (MathX.abs(newAngle - modulatedAngle) > 2) {
            turnTurretToAngle(newAngle);
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
        System.out.println(modAngle(gyro.getAngle() * 4.14015366));
        return modAngle(gyro.getAngle() * 4.14015366);
    }

    public void pidWrite(double output) {
        //lazySusan.setRaw((int)output);
        driveTrain.arcadeDrive(0, -output);
    }
    
}
