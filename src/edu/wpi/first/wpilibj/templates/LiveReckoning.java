/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Michael, Alex
 */
public class LiveReckoning {

    private Messager msg;
    private RobotDrive drive;
    private Jaguar collect;
    private Jaguar bridge;    
    private GyroX gyro;
    public AxisCamera camera;
    public ImageProcessing imageProc;
    private Launcher launcher;
    public PIDController pid;
    
    

    public LiveReckoning(RobotDrive drive, Launcher launcher,
            Jaguar collectMotor, Jaguar bridgeMotor, GyroX gyro1) {

        gyro = gyro1;
        msg = new Messager();
        this.drive = drive;
        this.launcher = launcher;
        collect = collectMotor;
        bridge = bridgeMotor;

        camera = AxisCamera.getInstance();
        camera.writeBrightness(30);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        camera.writeMaxFPS(10);

        imageProc = new ImageProcessing();

        pid = new PIDController(0.08, 0, 0, gyro, gyro);
        pid.setOutputRange(-1, 1);
    }

    public final void reset() {
        if (pid.isEnable()) {
            pid.reset();            
        }
    }

    public void disable() {
        if (pid.isEnable()) {
            pid.disable();            
        }
    }

    public void turnToTarget(ParticleAnalysisReport part) {
        if (camera.freshImage()) {
            if (!pid.isEnable()) {
                try {
                    imageProc.getTheParticles(camera);
                    gyro.gyro.reset();
                    gyro.refreshGyro();
                    double angle = ImageProcessing.getHorizontalAngle(part);
                    pid.setSetpoint(angle);
                    System.out.println("Setpoint: " + angle);

                    pid.enable();
                    System.out.println("PID Enabled");

                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
                
            }
        } else {
            System.out.println("Waiting for fresh image...");
        }
        System.out.println("set:" + pid.getSetpoint());
    }

    public void turnToTopTarget() {
        try {
            imageProc.getTheParticles(camera);
            ParticleAnalysisReport[] parts = imageProc.particles;
            if (parts != null && parts.length > 0) {

                ParticleAnalysisReport top = ImageProcessing.getTopMost(parts);
                turnToTarget(top);

            } else {
                msg.printOnLn("Can't find target", DriverStationLCD.Line.kUser6);
            }
        } catch (Exception ex) {
            msg.printOnLn("Can't find target", DriverStationLCD.Line.kUser6);
        }
    }

    public void doTele() {

        if (camera.freshImage()) {
            try {
                imageProc.getTheParticles(camera);

                ParticleAnalysisReport[] parts = imageProc.particles;
                if (parts != null) {
                    if (parts.length > 0) {
                        ParticleAnalysisReport topTarget = ImageProcessing.getTopMost(imageProc.particles);
                        double dist = imageProc.getDistance(topTarget, ImageProcessing.topTargetHeight);
                        msg.printOnLn("Dist(Top):" + dist, DriverStationLCD.Line.kUser6);

                    } else {
                        msg.printOnLn("Can't find target", DriverStationLCD.Line.kUser6);
                    }
                } else {
                    msg.printOnLn("Can't find target", DriverStationLCD.Line.kUser6);
                }

            } catch (Exception e) {
                msg.printLn(e.getMessage());

            }
        }
    }
}
