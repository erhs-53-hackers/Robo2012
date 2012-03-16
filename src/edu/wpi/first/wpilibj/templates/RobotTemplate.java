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
    Messager msg;
    Joystick leftStick, rightStick, launchControlStick;
    Controls launchControls;
    AxisCamera camera;
    ImageProcessing imageProc;
    ParticleAnalysisReport target;
    Physics physics;
    Launcher launcher;
    Jaguar bridgeArm, collector;
    GyroX gyro;
    boolean isManual = true;
    boolean isShooting = false;
    int shots = 0;
    double distanceFromTarget;
    double hoopHeight = Physics.HOOP1;

    public void robotInit() {
        msg = new Messager();
        msg.printLn("Loading Please Wait...");
        Timer.delay(10);
        drive = new RobotDrive(
                RoboMap.MOTOR1, RoboMap.MOTOR2, RoboMap.MOTOR3, RoboMap.MOTOR4);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        drive.setSafetyEnabled(false);
        getWatchdog().setEnabled(false);
        leftStick = new Joystick(RoboMap.JOYSTICK1);
        rightStick = new Joystick(RoboMap.JOYSTICK2);
        launchControlStick = new Joystick(RoboMap.JOYSTICK3);
        launchControls = new Controls(launchControlStick);
        //imageProc = new ImageProcessing();
        physics = new Physics();
        bridgeArm = new Jaguar(RoboMap.BRIDGE_MOTOR);
        collector = new Jaguar(RoboMap.COLLECT_MOTOR);
        launcher = new Launcher();
        //gyro = new GyroX(RoboMap.GYRO, RoboMap.LAUNCH_TURN, drive);
        msg.printLn("Done: FRC 2012");
    }

    public void autonomousInit() {
        isShooting = false;//change me!!!!!
    }

    public void autonomousPeriodic() {
        launcher.launchMotor.set(.75);
        Timer.delay(3);
        collector.set(-1);
        launcher.loadMotor.set(-1);

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
        launcher.launchMotor.set(0);
        collector.set(0);
        launcher.loadMotor.set(0);
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

    public void teleopPeriodic() {
        System.out.println("Teleop Looping");

        // switch to control assisted teleop
        if (launchControls.button11()) {
            isManual = true;
        } else if (launchControls.button12()) {
            isManual = false;
        }

        // drive system, independent of teleop assistance
        if (leftStick.getRawButton(2) || rightStick.getRawButton(2)) {
            drive.tankDrive(leftStick.getAxis(Joystick.AxisType.kY) * .5,
                    rightStick.getAxis(Joystick.AxisType.kY) * .5);
        } else {
            drive.tankDrive(leftStick, rightStick);
        }

        // motor to lower bridge arm, currently independent of teleop assitance
        if (leftStick.getRawButton(3)) {
            bridgeArm.set(.5);
        } else if (leftStick.getRawButton(2)) {
            bridgeArm.set(-1);
        } else {
            bridgeArm.set(0);
        }

        if (isManual) {
            msg.printOnLn("Mode: Manual", DriverStationLCD.Line.kMain6);
            if (launchControls.button7()) {
                collector.set(-1);
            } else if (launchControls.button8()) {
                collector.set(0);
            }
            double power = (launchControlStick.getThrottle() + 1) / 2;
            launcher.launchMotor.set(power);
            msg.printOnLn("Launch Power = " + power, DriverStationLCD.Line.kUser2);
            // control the firing mechanism
            if (launchControls.button1()) {
                launcher.manualShoot();
            } else {
                launcher.loadMotor.set(0);
            }
        } else if (!isManual) {
            msg.printOnLn("Mode: Auto", DriverStationLCD.Line.kMain6);
            collector.set((launchControlStick.getThrottle() + 1) / 2);
            if (launchControls.FOV_Left()) {
                target = imageProc.middleTargetLeft;
                hoopHeight = Physics.HOOP2;
            } else if (launchControls.FOV_Right()) {
                target = imageProc.middleTargetRight;
                hoopHeight = Physics.HOOP2;
            } else if (launchControls.FOV_Top()) {
                target = imageProc.topTarget;
                hoopHeight = Physics.HOOP3;
            } else if (launchControls.FOV_Bottom()) {
                target = imageProc.bottomTarget;
                hoopHeight = Physics.HOOP1;
            }
            if (launchControls.button2()) {
                isShooting = true;
            }
        }

        /*
         * if (launchControls.button3()) { gyro.turnRobotToAngle(0);
         *
         * } else if (launchControls.button4()) { gyro.turnRobotToAngle(180);
         *
         * } else if (launchControls.button5()) { gyro.turnRobotToAngle(-90);
         *
         * } else if (launchControls.button6()) { gyro.turnRobotToAngle(90);
         *
         * }
         *
         */

        /*
         * //motor to control lazy susan for launcher if
         * (launchControls.button9()) { gyro.turnAngle(5); } else if
         * (launchControls.button10()) { gyro.turnAngle(-5); }
         *
         */

    }
}
