/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Rajath
 */
public class DeadReckoning {

    RobotDrive driveTrain;
    Jaguar shoot;
    Jaguar collect;
    Jaguar bridge;
    AnalogChannel potentiometer;
    double speed = .778443;
    Messager msg = new Messager();
    boolean Autonomousflag;

    public DeadReckoning(RobotDrive drive, Jaguar shootMotor,
            Jaguar collectMotor, Jaguar bridgeMotor) {
        driveTrain = drive;
        shoot = shootMotor;

        collect = collectMotor;
        bridge = bridgeMotor;
        potentiometer = new AnalogChannel(7);
        Autonomousflag = true;
    }    
    

    public void shoot() {
        shoot.set(.72);
        Timer.delay(7);
        collect.set(-1);
    }

    public void driveToBridge() {
        System.out.println("Val: " + potentiometer.getVoltage());

        if (Autonomousflag) {
            if (potentiometer.getVoltage() < 5) {//line
                shoot();
                Timer.delay(7);
                shoot.set(0);
                collect.set(0);

                driveTrain.tankDrive(-speed, speed);
                Timer.delay(1);
                driveTrain.tankDrive(speed, speed);
                Timer.delay(1.2);

                driveTrain.tankDrive(0, 0);
                Timer.delay(0.9);
                bridge.set(-1);

            } else if (potentiometer.getVoltage() > 5) {//left
                shoot();
            }
            Autonomousflag = false;
        } else {
            driveTrain.tankDrive(0, 0);
        }
    }
}
