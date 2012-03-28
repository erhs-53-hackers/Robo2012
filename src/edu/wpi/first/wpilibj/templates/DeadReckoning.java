/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;


/**
 *
 * @author Rajath
 * 34
 */
public class DeadReckoning {
    RobotDrive driveTrain;
    Victor shoot;
    Victor load;
    Victor collect;
    Victor bridge;
    AnalogChannel potentiometer;
    double speed = .678443;
    Messager msg = new Messager();
    
    boolean Autonomousflag;
    
    public DeadReckoning(RobotDrive drive, Victor shootMotor, Victor loadMotor, 
            Victor collectMotor, Victor bridgeMotor)
    {
        driveTrain = drive;
        shoot = shootMotor;
        load = loadMotor;
        collect = collectMotor;
        bridge = bridgeMotor;
        potentiometer = new AnalogChannel(7);
        Autonomousflag = true;
    }
    public void shoot()
    {
        shoot.set(.35);
        Timer.delay(3);
        collect.set(-1);
        load.set(-1);
        Timer.delay(5);
    }
    public void driveToBridge()
    {
        System.out.println("Val: " + potentiometer.getVoltage());
        
        if(Autonomousflag) {
            shoot();
            driveTrain.tankDrive(-speed, speed);
            Timer.delay(2);
            
            if(potentiometer.getVoltage() < 1.5) {//left
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(1.4);               
               
                driveTrain.tankDrive(0, 0);
                Timer.delay(0.9);
                bridge.set(-1);
                
                Autonomousflag = false;
            } else if (potentiometer.getVoltage() < 3 && potentiometer.getVoltage() > 1.5) {//line
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(3);
                bridge.set(-1);
                
                Autonomousflag = false;
            } else if (potentiometer.getVoltage() < 4.5 && potentiometer.getVoltage() > 3) {//right
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(1);
                driveTrain.tankDrive(0, speed);
                Timer.delay(.25);
                driveTrain.tankDrive(speed, 0);
                Timer.delay(.25);
                driveTrain.tankDrive(speed, speed);
                Timer.delay(2);
                bridge.set(-1);
                
                Autonomousflag = false;
            } else {//nothing
                
                Autonomousflag = false;
            }
      } else {
            driveTrain.tankDrive(0, 0);
     }
  }
}
