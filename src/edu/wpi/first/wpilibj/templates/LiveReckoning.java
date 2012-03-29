/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Alex
 */
public class LiveReckoning {

    private Messager msg;
    private RobotDrive drive;
    private Jaguar collect;
    private Jaguar bridge;
    private AnalogChannel potentiometer;
    private GyroX gyro;
    private AnalogChannel ultrasonic;
    private ImageProcessing imageProc;
    private Launcher launcher;
    private PIDController pid;
    private double savedDist = 0;
    private boolean stepFlag = false;
    private boolean isDone = false;
    private boolean start = true;

    public LiveReckoning(RobotDrive drive, Launcher launcher,
            Jaguar collectMotor, Jaguar bridgeMotor, GyroX gyro1,
            AnalogChannel ultrasonic1) {

        pid = new PIDController(0.1, 0, 0, gyro1, gyro1);
        pid.setSetpoint(0);
        pid.setOutputRange(-255, 255);
        msg = new Messager();
        this.drive = drive;
        this.launcher = launcher;
        collect = collectMotor;
        bridge = bridgeMotor;
        potentiometer = new AnalogChannel(1);
        gyro = gyro1;
        ultrasonic = ultrasonic1;
        imageProc = new ImageProcessing();
    }
    
    public void free() {
        pid.disable();
        pid.free();        
    }

    public void doAuto(AxisCamera camera) {
        
        if (camera.freshImage()) {
            try {
                imageProc.getTheParticles(camera);
                if(start) {
                    gyro.refreshGyro();                    
                    ParticleAnalysisReport top = ImageProcessing.getTopMost(imageProc.particles);
                    double angle = ImageProcessing.getHorizontalAngle(top);
                    pid.setSetpoint(gyro.modulatedAngle - angle);
                    System.out.println("Setpoint: " + (gyro.modulatedAngle - angle));
                    start = false;
                }
                
                if(!pid.isEnable()) pid.enable();
                
                


                //msg.printOnLn("Top:" + imageProc.isTopTarget(target), DriverStationLCD.Line.kMain6);
                //msg.printOnLn("Bottom:" + imageProc.isBottomTarget(target), DriverStationLCD.Line.kUser2);
                //msg.printOnLn("dist(midtop):" + imageProc.getDistance(imageProc.particles[0], ImageProcessing.topTargetHeight), DriverStationLCD.Line.kUser3);
                //msg.printOnLn("Tilt:" + imageProc.getCameraTilt(), DriverStationLCD.Line.kUser4);

                // start gyro debug
                //double angle = imageproc.getHorizontalAngle();

                //gyro.turnTurret(angle);
                //end gyro debug


            } catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
            }


        } else {
            msg.printLn("No Camera Image");
        }

        /*
         * if(imageProc.isTopTarget(target)) { msg.printLn("Top"); }
         * if(imageProc.isBottomTarget(target)) { msg.printLn("Botton"); }
         * if(!imageProc.isBottomTarget(target) &&
         * !imageProc.isTopTarget(target)) { msg.printLn("No target found"); }
         *
         */
    }

    public void doTele(AxisCamera camera, boolean isShooting) {

        if (camera.freshImage() && isShooting) {
            try {

                imageProc.getTheParticles(camera);
                //ParticleAnalysisReport topTarget = imageProc.getTopTarget();
                //double angle = ImageProcessing.getHorizontalAngle(topTarget);
                //gyro.turnTurret(angle);
                //launcher.shootTopTarget();

                isShooting = false;
            } catch (Exception e) {
                msg.printLn(e.getMessage());
                isShooting = false;
            }
        }
    }

    public void shoot(double horAngleToTarget, double numberOfShots) {
        if (gyro.modulatedAngle == 0 && stepFlag == false) {
            savedDist = UltraCalc.getScaledDistance(ultrasonic.getAverageVoltage());
            stepFlag = true;
        } else {
            gyro.turnRobotToAngle(0);
        }
        if (stepFlag) {
            if (horAngleToTarget != 0) {
                gyro.turnTurretToAngle(horAngleToTarget);
            } else {
                for (int i = 0; i < numberOfShots; i++) { //modulate power according to distance and height
                    launcher.launchMotor.set(.75);
                    Timer.delay(3);
                    collect.set(1);
                    launcher.loadMotor.set(1);
                }
                isDone = true;
            }
        }

        if (isDone) { //resets robot
            stepFlag = false;
            gyro.turnTurretToAngle(0);
            isDone = false;
        }
    }
}
