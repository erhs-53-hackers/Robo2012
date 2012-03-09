/*
 * FRC Team 53:  The Alien Cow Abductors
 * 2012 FRC Competition "Rebound Rumble"
 * Released under GNU GPL v. 3 or later
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Team53
 */
public class RobotTemplate extends IterativeRobot {
    
    RobotDrive drive;
    Joystick stick1;
    Joystick stick2;
    Joystick stick3;
    AxisCamera camera;
    ImageProcessing imageProc;
    ParticleAnalysisReport target;
    Physics physics;
    Launcher launcher;
    Jaguar bridgeArm;
    Jaguar collectMotor;
    GyroX gyro;
    Messager msg;
    Controls controls;
    boolean isManual = true;
    boolean isShooting = false;
    int shots = 0;
    double distanceFromTarget;
    double hoopHeight = Physics.HOOP1;
    boolean collecting = false;
    
    public void robotInit() {
        msg = new Messager();
        
        msg.printLn("Loading Please Wait...");
        Timer.delay(10);
        msg.printLn("1");
        //left front, left back, right front, right back
        drive = new RobotDrive(
                RoboMap.MOTOR1, RoboMap.MOTOR2, RoboMap.MOTOR3, RoboMap.MOTOR4);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        drive.setSafetyEnabled(false);
        getWatchdog().setEnabled(false);
        msg.printLn("2");
        
        stick1 = new Joystick(RoboMap.JOYSTICK1);
        stick2 = new Joystick(RoboMap.JOYSTICK2);
        stick3 = new Joystick(RoboMap.JOYSTICK3);
        controls = new Controls(stick3);
        msg.printLn("3");
        
        
        
        
        
        msg.printLn("4");
        //imageProc = new ImageProcessing();
        msg.printLn("5");
        physics = new Physics();
        msg.printLn("6");
        bridgeArm = new Jaguar(RoboMap.BRIDGE_MOTOR);
        msg.printLn("7");
        collectMotor = new Jaguar(RoboMap.COLLECT_MOTOR);
        msg.printLn("8");
        launcher = new Launcher();
        msg.printLn("9");
        //gyro = new GyroX(RoboMap.GYRO, RoboMap.LAUNCH_TURN, drive);
        
        
        
        msg.printLn("Done: FRC 2012");
    }
    
    public void autonomousInit() {
        isShooting = false;//change me!!!!!
    }
    
    public void autonomousPeriodic() {
        /*
         * if (camera.freshImage() && false) { try {
         * imageProc.getTheParticles(camera); target =
         * ImageProcessing.getTopMost(imageProc.particles);
         *
         *
         * double angle = ImageProcessing.getHorizontalAngle(target);
         * //msg.printLn("" + angle); /* while (MathX.abs(angle -
         * gyro.modulatedAngle) > 2) { gyro.turnToAngle(angle);
         * getWatchdog().feed(); }
         *
         *
         *
         *
         *
         *
         *
         * if (isShooting) { Timer.delay(3);
         *
         * launcher.shoot(target.boundingRectHeight, Physics.HOOP3);
         *
         * shots++; if (shots == 2) { isShooting = false; } } } catch (Exception
         * e) { e.printStackTrace(); System.out.println("ERROR!!! Cannot Fetch
         * Image"); } } getWatchdog().feed();
         */
    }
    
    public void teleopInit() {
        //camera = AxisCamera.getInstance();
        //camera.writeBrightness(30);
        //camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        msg.clearConsole();
    }
    
    public void teleopContinuous() {
        // Have the camera scan for targets
/*
         * if (camera.freshImage()) { try { imageProc.getTheParticles(camera);
         * imageProc.organizeTheParticles(imageProc.particles);
         *
         * if (isShooting) { double angle =
         * ImageProcessing.getHorizontalAngle(target); //msg.printLn("" +
         * angle);
         *
         * while (MathX.abs(angle - gyro.modulatedAngle) > 2) {
         * gyro.turnToAngle(angle); getWatchdog().feed(); }
         * launcher.shoot(target.boundingRectHeight, hoopHeight); isShooting =
         * false; } } catch (Exception e) { e.printStackTrace();
         * //msg.printLn("ERROR!!! Cannot Fetch Image"); } }
         *
         */
    }
    
    //called continuously
    public void teleopPeriodic() {
        System.out.println("Hello");
        
        if (controls.button7()) {            
            collecting = true;
            
        } else if (controls.button8()) {
            collecting = false;
        }
        
        if (collecting) {
            collectMotor.set(-1);
        } else {
            collectMotor.set(0);
        }
        
        if (controls.button8()) {
            isManual = true;
            
        } else if (controls.button7()) {
            //isManual = false; REMOVE ME!!!!!
        }
        
        if (stick1.getRawButton(2) || stick2.getRawButton(2)) {
            drive.tankDrive(stick1.getAxis(Joystick.AxisType.kY) * .5,
                    stick2.getAxis(Joystick.AxisType.kY) * .5);            
        } else {
            drive.tankDrive(stick1, stick2);
        }
        
        if (!isManual) {
            //motor to collect the balls off the ground
            msg.printOnLn("Mode: Auto", DriverStationLCD.Line.kMain6);
            collectMotor.set((stick3.getThrottle() + 1) / 2);
            if (controls.FOV_Left()) {
                target = imageProc.middleTargetLeft;
                hoopHeight = Physics.HOOP2;
                
            } else if (controls.FOV_Right()) {
                target = imageProc.middleTargetRight;
                hoopHeight = Physics.HOOP2;
                
            } else if (controls.FOV_Top()) {
                target = imageProc.topTarget;
                hoopHeight = Physics.HOOP3;
                
            } else if (controls.FOV_Bottom()) {
                target = imageProc.bottomTarget;
                hoopHeight = Physics.HOOP1;
                
            }
            if (controls.button2()) {
                isShooting = true;
            }
        } else {
            msg.printOnLn("Mode: Manual", DriverStationLCD.Line.kMain6);
            
            
            double power = (stick3.getThrottle() + 1) / 2;
            launcher.launchMotor.set(power);
            
            if (controls.button1()) {
                launcher.manualShoot();
            } else {
                launcher.loadMotor.set(0);
            }
        }

        /*
         * if (controls.button3()) { gyro.turnRobotToAngle(0);
         *
         * } else if (controls.button4()) { gyro.turnRobotToAngle(180);
         *
         * } else if (controls.button5()) { gyro.turnRobotToAngle(-90);
         *
         * } else if (controls.button6()) { gyro.turnRobotToAngle(90);
         *
         * }
         *
         */

        /*
         * //motor to control lazy susan for launcher if (controls.button9()) {
         * gyro.turnAngle(5); } else if (controls.button10()) {
         * gyro.turnAngle(-5); }
         *
         */



        // motor to lower bridge arm
        if (stick1.getRawButton(3)) {
            bridgeArm.set(.25);
        } else if (stick1.getRawButton(2)) {
            bridgeArm.set(-1);
        } else {
            bridgeArm.set(0);
        }
        
        
    }
}
