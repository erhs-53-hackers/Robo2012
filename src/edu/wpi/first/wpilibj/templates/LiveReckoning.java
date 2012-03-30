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
    private AxisCamera camera;    
    public ImageProcessing imageProc;
    private Launcher launcher;
    private PIDController pid;
    private double savedDist = 0;
    private boolean stepFlag = false;
    private boolean isDone = false;
    private boolean start = true;

    public LiveReckoning(RobotDrive drive, Launcher launcher,
            Jaguar collectMotor, Jaguar bridgeMotor, GyroX gyro1) {

        gyro = gyro1;        
        msg = new Messager();
        this.drive = drive;
        this.launcher = launcher;
        collect = collectMotor;
        bridge = bridgeMotor;
        //potentiometer = new AnalogChannel(1);        
        
        camera = AxisCamera.getInstance();
        camera.writeBrightness(30);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        camera.writeMaxFPS(10);

        imageProc = new ImageProcessing();
        
        reset();
    }

    public final void reset() {
        pid = new PIDController(0.08, 0, 0, gyro, gyro);
        pid.setOutputRange(-1, 1);
    }

    public void free() {
        if (pid.isEnable()) {
            pid.disable();
            pid.free();
        }
    }

    public void turnToTarget(ParticleAnalysisReport part) {
        while (!camera.freshImage()) {
            msg.printLn("Waiting for Camera...");
        }

        try {
            imageProc.getTheParticles(camera);

            gyro.gyro.reset();
            gyro.refreshGyro();
            double angle = ImageProcessing.getHorizontalAngle(part);
            pid.setSetpoint(angle);
            System.out.println("Setpoint: " + angle);


            pid.enable();

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void doAuto() {
        try {
            if (start) {
                imageProc.getTheParticles(camera);
                ParticleAnalysisReport top = ImageProcessing.getTopMost(imageProc.particles);
                turnToTarget(top);
                launcher.launchMotor.set(.75);
                Timer.delay(7);
                collect.set(1);
                start = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }



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
}
