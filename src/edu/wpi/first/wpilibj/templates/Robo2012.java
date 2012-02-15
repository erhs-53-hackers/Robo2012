/*
 * FRC Team 53:  The Alien Cow Abductors
 * 2012 FRC Competition "Rebound Rumble"
 * Released under GNU GPL v. 3 or later
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

public class Robo2012 extends IterativeRobot {

    RobotDrive drive;
    Joystick stick;
    AxisCamera camera;
    ImageProcessing imageProc;
    ParticleAnalysisReport target;
    Physics physics;
    Launcher launcher;
    Jaguar bridgeArm;
    AnalogChannel autoPot;
    AnalogChannel telePot;
    Messager msg;
    Controls controls;
    
    boolean isShooting = false;

    public void robotInit() {
        Timer.delay(10);
        drive = new RobotDrive(RoboMap.MOTOR1, RoboMap.MOTOR2);
        //drive = new RobotDrive(RoboMap.MOTOR1, RoboMap.MOTOR2, RoboMap.MOTOR3, RoboMap.MOTOR4);
        stick = new Joystick(RoboMap.JOYSTICK1);
        controls = new Controls(stick);
        msg = new Messager();
        camera = AxisCamera.getInstance();
        camera.writeBrightness(30);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        imageProc = new ImageProcessing();
        physics = new Physics();
        bridgeArm = new Jaguar(RoboMap.BRIDGE_MOTOR);
        launcher = new Launcher();
        autoPot = new AnalogChannel(RoboMap.AUTO_POT);
        telePot = new AnalogChannel(RoboMap.TELO_POT);
        msg.printLn("FRC 2012");
    }

    public void autonomousPeriodic() {
        int x = (int) MathX.round(autoPot.getAverageVoltage());
        int shootDelay = 0;
        boolean shootFlag = true;
        
        if (x <= 4) {
            shootDelay = x;
        } else if (x == 5) {
            shootFlag = false;
        }

        if (shootFlag != false) {
            Timer.delay(shootDelay);
            //image processing here
            //physics code here
            //shooting algorithm here
        }
    }

    public void teleopPeriodic() {
        
        drive.arcadeDrive(stick);
        //drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), 0, 0);
        
        // motor to lower bridge
        if (controls.button1()) bridgeArm.set(.2);
        else bridgeArm.set(0);
        
        //Select the target to aim at 
        if (controls.button3()) {
            target = imageProc.leftT;
            isShooting = true;
        } else if (controls.button4()) {
            target = imageProc.rightT;
            isShooting = true;
        } else if (controls.button5()) {
            target = imageProc.topT;
            isShooting = true;
        } else if (controls.button6()) {
            target = imageProc.bottomT;
            isShooting = true;
        }

        // Have the camera scan for targets
        if (camera.freshImage()) {
            try {
                imageProc.getTheParticles(camera);
                int pixelHeight = imageProc.getImgHeight(imageProc.party);
                msg.printLn("Pixels = " + pixelHeight);
                msg.printLn(imageProc.orginizeParticles(imageProc.party, imageProc.getTotalXCenter(imageProc.party), imageProc.getTotalXCenter(imageProc.party)));
                
                if (isShooting) {
                    launcher.shoot(pixelHeight);
                    /*
                        physics.setP(imageProc.getImgHeight(imageProc.party));
                        physics.calculateInfo();
                        double velocity = physics.calculateLaunchVelocity();
                        msg.printLn("" + velocity);
                        physics.pushInfoToDashboard();
                        msg.printLn("" + imageProc.getTotalXCenter(imageProc.party));
                        msg.printLn("" + imageProc.getTotalYCenter(imageProc.party));
                    */
                    isShooting = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.printLn("ERROR!!! Cannot Fetch Image");
            }
        }
    }
}
