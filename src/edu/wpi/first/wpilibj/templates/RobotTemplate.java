/*
 * FRC Team 53:  The Alien Cow Abductors
 * 2012 FRC Competition "Rebound Rumble"
 * Released under GNU GPL v. 3 or later
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Team53
 */
public class RobotTemplate extends IterativeRobot {

    RobotDrive drive;
    Messager msg;
    Joystick leftStick, rightStick, launchControlStick, testStick;
    Controls launchControls;
    Launcher launcher;
    Jaguar bridgeArm, collector;
    GyroX gyro;
    boolean isManual = true;
    boolean isShooting = false;
    DeadReckoning dead;
    LiveReckoning live;
    boolean first = true;

    public void robotInit() {
        msg = new Messager();
        msg.printLn("Loading Please Wait...");
        Timer.delay(10);

        drive = new RobotDrive(RoboMap.MOTOR1, RoboMap.MOTOR2, RoboMap.MOTOR3, RoboMap.MOTOR4);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

        drive.setSafetyEnabled(false);
        getWatchdog().setEnabled(false);
        leftStick = new Joystick(RoboMap.JOYSTICK1);
        rightStick = new Joystick(RoboMap.JOYSTICK2);
        launchControlStick = new Joystick(RoboMap.JOYSTICK3);
        launchControls = new Controls(launchControlStick);

        bridgeArm = new Jaguar(RoboMap.BRIDGE_MOTOR);
        collector = new Jaguar(RoboMap.COLLECT_MOTOR);
        launcher = new Launcher(collector);

        gyro = new GyroX(RoboMap.GYRO, drive);
        dead = new DeadReckoning(drive, launcher.launchMotor, collector, bridgeArm);
        live = new LiveReckoning(drive, launcher, collector, bridgeArm, gyro);

        msg.printLn("Done: FRC 2012");
        //while(true) {
        //System.out.println("Gyro:" + gyro.refreshGyro());
        //}
    }

    public void autonomousInit() {
        isShooting = false;//change me!!!!!       
    }

    public void autonomousPeriodic() {

        if (first) {
            while (!live.camera.freshImage()) {
            }
            try {
                live.imageProc.getTheParticles(live.camera);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ParticleAnalysisReport[] parts = live.imageProc.particles;
            if (parts != null) {
                if (parts.length > 0) {
                    ParticleAnalysisReport top = ImageProcessing.getTopMost(parts);
                    live.turnToTarget(top);
                } else {
                    msg.printLn("Can't find target");
                }
            } else {
                msg.printLn("Can't find target");
            }
            
            dead.shoot();
            first = false;
        }
        
        //live.doAuto();
    }

    public void disabledInit() {
        live.free();
    }

    public void teleopInit() {
        launcher.launchMotor.set(0);
        collector.set(0);
        //live.free();
        //live.reset();
        msg.clearConsole();
    }

    public void teleopPeriodic() {



        // drive system, independent of teleop assistance
        if (leftStick.getRawButton(2) || rightStick.getRawButton(2)) {
            drive.tankDrive(leftStick.getAxis(Joystick.AxisType.kY) * .5,
                    rightStick.getAxis(Joystick.AxisType.kY) * .5);
        } else {
            drive.tankDrive(leftStick, rightStick);
        }

        // motor to lower bridge arm, currently independent of teleop assitance
        if (leftStick.getRawButton(3)) {
            bridgeArm.set(-1);
        } else if (leftStick.getRawButton(2)) {
            bridgeArm.set(1);
        } else {
            bridgeArm.set(0);
        }

        if (isManual) {
            msg.printOnLn("Mode: Manual", DriverStationLCD.Line.kMain6);

            if (launchControlStick.getTrigger()) {
                try {
                    live.imageProc.getTheParticles(live.camera);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ParticleAnalysisReport[] parts = live.imageProc.particles;
                if (parts != null) {
                    if (parts.length > 0) {
                        live.start = true;
                        ParticleAnalysisReport top = ImageProcessing.getTopMost(parts);
                        live.turnToTarget(top);
                    } else {
                        msg.printLn("Can't find target");
                    }
                } else {
                    msg.printLn("Can't find target");
                }
            } else {
                //live.free();                
                live.reset();
            }


            collector.set(launchControlStick.getY());

            double power = (launchControlStick.getThrottle() + 1) / 2;

            if (launchControls.button5()) {
                power = .65;
            } else if (launchControls.button3()) {
                power = .7;
            } else if (launchControls.button4()) {
                power = .75;
            } else if (launchControls.button6()) {
                power = .8;
            }
            launcher.launchMotor.set(power);
            msg.printOnLn("Launch Power = " + power, DriverStationLCD.Line.kUser2);

        }

        live.doTele();

    }
}
