/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;


/**
 *
 * @author Rajath
 * 34
 */
public class DeadReckoning {
    RobotDrive driveTrain;
    Jaguar shoot;
    Jaguar load;
    Jaguar collect;
    Jaguar bridge;
    AnalogChannel potentiometer;
    
    boolean Autonomousflag;
    
    public DeadReckoning(RobotDrive drive, Jaguar shootMotor, Jaguar loadMotor, 
            Jaguar collectMotor, Jaguar bridgeMotor)
    {
        driveTrain = drive;
        shoot = shootMotor;
        load = loadMotor;
        collect = collectMotor;
        bridge = bridgeMotor;
        potentiometer = new AnalogChannel(1);
        Autonomousflag = true;
    }
    public void shoot()
    {
        shoot.set(.75);
        Timer.delay(3);
        collect.set(1);
        load.set(1);
    }
    public void driveToBridge()
    {
        if(Autonomousflag) {
            if(potentiometer.getValue() < 1.5) {
                driveTrain.tankDrive(-1,-1);
                Timer.delay(1);
                driveTrain.tankDrive(-1, 0);
                Timer.delay(.25);
                driveTrain.tankDrive(0, -1);
                Timer.delay(.25);
                driveTrain.tankDrive(-1, -1);
                Timer.delay(2);
                bridge.set(-1);
                shoot();
                Autonomousflag = false;
            } else if (potentiometer.getValue() < 3 && potentiometer.getValue() > 1.5) {
                driveTrain.tankDrive(-1,-1);
                Timer.delay(3);
                bridge.set(-1);
                shoot();
                Autonomousflag = false;
            } else if (potentiometer.getValue() < 4.5 && potentiometer.getValue() > 3) {
                driveTrain.tankDrive(-1,-1);
                Timer.delay(1);
                driveTrain.tankDrive(0, -1);
                Timer.delay(.25);
                driveTrain.tankDrive(-1, 0);
                Timer.delay(.25);
                driveTrain.tankDrive(-1, -1);
                Timer.delay(2);
                bridge.set(-1);
                shoot();
                Autonomousflag = false;
            } else {
                shoot();
                Autonomousflag = false;
            }
      } else {
            driveTrain.tankDrive(0, 0);
     }
  }
}
