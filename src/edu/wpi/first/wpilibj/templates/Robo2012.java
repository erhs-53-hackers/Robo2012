/*
 * FRC Team 53:  The Alien Cow Abductors
 * 2012 FRC Competition "Rebound Rumble"
 * Released under GNU GPL v. 3 or later
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Team53
 */
public class Robo2012 extends IterativeRobot {

    RobotDrive drive;
    Joystick stick;
    AxisCamera camera;
    ImageProcessing imageProc;
    ParticleAnalysisReport target;
    Physics physics;
    Launcher launcher;
    Jaguar bridgeArm;
    Jaguar launchTurn;
    Jaguar collectMotor;
    AnalogChannel autoPot;
    AnalogChannel telePot;
    GyroX gyro;
    Messager msg;
    Controls controls;
    
    boolean isShooting = false;
    int shots = 0;
    double distanceFromTarget;

    public void robotInit() {
        Timer.delay(10);
        //left front, left back, right front, right back
        drive = new RobotDrive(
                RoboMap.MOTOR1, RoboMap.MOTOR3, RoboMap.MOTOR2, RoboMap.MOTOR4);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

        stick = new Joystick(RoboMap.JOYSTICK1);
        controls = new Controls(stick);
        msg = new Messager();
        camera = AxisCamera.getInstance();
        camera.writeBrightness(30);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        imageProc = new ImageProcessing();
        physics = new Physics(6574);
        bridgeArm = new Jaguar(RoboMap.BRIDGE_MOTOR);
        launchTurn = new Jaguar(RoboMap.LAUNCH_TURN);
        collectMotor = new Jaguar(RoboMap.COLLECT_MOTOR);
        launcher = new Launcher();
        gyro = new GyroX(RoboMap.GYRO, launchTurn);
        autoPot = new AnalogChannel(RoboMap.AUTO_POT);
        telePot = new AnalogChannel(RoboMap.TELO_POT);
        msg.printLn("FRC 2012");
    }

    public void autonomousInit() {
        isShooting = true;
    }

    public void autonomousPeriodic() {
        if (camera.freshImage()) {
            try {
                imageProc.getTheParticles(camera);
                target = ImageProcessing.getTopMost(imageProc.particles);
                int img = 640;
                double p = (640/2) - target.center_mass_x;
                double angle = p/physics.LAMBDA;
                gyro.turnToAngle(angle);
                if(isShooting){
                    Timer.delay(3);
                    
                    launcher.shoot(target.boundingRectHeight, Physics.HOOP3);

                    //load and shoot again
                    shots++;
                    if (shots == 2) {
                        isShooting = false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                msg.printLn("ERROR!!! Cannot Fetch Image");
            }
        }
    }

    public void teleopPeriodic() {

        drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), MathX.pow(stick.getZ(), 3), 0);
        
        gyro.refreshGyro();
        
        if (controls.button2()) {
            gyro.turnToAngle(0);
        }
        
        //motor to collect the balls off the ground
        collectMotor.set(.5);
        
        //motor to control lazy susan for launcher
        if (controls.button10()) {
            launchTurn.set(.25);
        } else if (controls.button11()) {
            launchTurn.set(-.25);
        } else {
            launchTurn.set(0);
        }               

        // motor to lower bridge arm
        if (controls.button6()) {
            bridgeArm.set(.75);
            bridgeArm.set(-.5);
        } else {
            bridgeArm.set(0);
        }

        // Have the camera scan for targets
        if (camera.freshImage()) {
            try {
                imageProc.getTheParticles(camera);
                if (isShooting) {
                    isShooting = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.printLn("ERROR!!! Cannot Fetch Image");
            }
        }
               //Select the target to aim at 
        if (controls.FOV_Left()) {
            target = imageProc.middleTarget;
            isShooting = true;
        } else if (controls.FOV_Right()) {
            target = imageProc.middleTarget;
            isShooting = true;
        } else if (controls.FOV_Top()) {
            target = imageProc.topTarget;
            isShooting = true;
        } else if (controls.FOV_Bottom()) {
            target = imageProc.bottomTarget;
            isShooting = true;
        }
    }
}
