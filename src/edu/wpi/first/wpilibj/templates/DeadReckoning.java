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
 * 34
 */
public class DeadReckoning {
    RobotDrive driveTrain;
    Jaguar shoot;
    Jaguar load;
    Jaguar collect;
    Jaguar bridge;
    AnalogChannel potentiometer;
    double speed = .778443;
    Messager msg = new Messager();
    
    boolean Autonomousflag;
    
    public DeadReckoning(RobotDrive drive, Jaguar shootMotor, Jaguar loadMotor, 
            Jaguar collectMotor, Jaguar bridgeMotor)
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
        shoot.set(.32);
        Timer.delay(5);
        collect.set(1);
        load.set(-1);
        Timer.delay(7);
        
        shoot.set(0);        
        collect.set(0);
        load.set(0);
    }
    public void driveToBridge()
    {
        System.out.println("Val: " + potentiometer.getVoltage());
        
        if(Autonomousflag) {
            shoot();
            driveTrain.tankDrive(-speed, speed);
            Timer.delay(1);
            
            if(potentiometer.getVoltage() < 1.5) {//line
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(1.2);               
               
                driveTrain.tankDrive(0, 0);
                Timer.delay(0.9);
                bridge.set(-1);
                
                
            } else if (potentiometer.getVoltage() < 3 && potentiometer.getVoltage() > 1.5) {//left
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(1);
                driveTrain.tankDrive(0, -speed);
                Timer.delay(1);
                driveTrain.tankDrive(-speed, 0);
                Timer.delay(1);
                driveTrain.tankDrive(speed, speed);
                Timer.delay(2);
                bridge.set(-1);
                
                
            } else if (potentiometer.getVoltage() < 4.5 && potentiometer.getVoltage() > 3) {//right
                
                driveTrain.tankDrive(speed,speed);
                Timer.delay(1);
                driveTrain.tankDrive(0, speed);
                Timer.delay(1);
                driveTrain.tankDrive(speed, 0);
                Timer.delay(1);
                driveTrain.tankDrive(speed, speed);
                Timer.delay(2);
                bridge.set(-1);
                
                
            }
        Autonomousflag = false;
      } else {
            driveTrain.tankDrive(0, 0);
     }
  }
}
