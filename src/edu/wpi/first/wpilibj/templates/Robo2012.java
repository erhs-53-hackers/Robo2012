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
    Jaguar launcher1;
    Jaguar launcher2;
    Encoder launcherEncoder;
    Jaguar bridgeArm;
    Messager msg;
    Controls controls;
    
    boolean isShooting = false;

    public void robotInit() {
        Timer.delay(10);
        drive = new RobotDrive(1, 2);
        //drive = new RobotDrive(1, 2, 3, 4);
        stick = new Joystick(1);
        controls = new Controls(stick);
        msg = new Messager();
        camera = AxisCamera.getInstance();
        camera.writeBrightness(30);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        imageProc = new ImageProcessing();
        physics = new Physics();
        bridgeArm = new Jaguar(1);
        launcher = new Launcher();
        msg.printLn("FRC 2012");
    }

    public void autonomousPeriodic() {
    }

    public void teleopPeriodic() {
        
        drive.arcadeDrive(stick);
        //drive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), 0, 0);
        
        // motor to lower bridge
        if (stick.getRawButton(1)) bridgeArm.set(.2);
        else bridgeArm.set(0);
        
        /*
         * Select the target to aim at 
         */
        if (stick.getRawButton(3)) {
            target = imageProc.leftT;
            isShooting = true;
        } else if (stick.getRawButton(4)) {
            target = imageProc.rightT;
            isShooting = true;
        } else if (stick.getRawButton(5)) {
            target = imageProc.topT;
            isShooting = true;
        } else if (stick.getRawButton(6)) {
            target = imageProc.bottomT;
            isShooting = true;
        }

        /*
         * Have the camera scan for targets
         */
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
