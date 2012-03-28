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

/**
 *
 * @author Alex
 */
public class LiveReckoning {

    RobotDrive driveTrain;
    Jaguar shoot;
    Jaguar load;
    Jaguar collect;
    Jaguar bridge;
    AnalogChannel potentiometer;
    GyroX gyro;
    AnalogChannel ultrasonic;
    double savedDist = 0;
    boolean stepFlag = false;
    boolean isDone = false;

    public LiveReckoning(RobotDrive drive, Jaguar shootMotor, Jaguar loadMotor,
            Jaguar collectMotor, Jaguar bridgeMotor, GyroX gyro1, AnalogChannel ultrasonic1) {
        driveTrain = drive;
        shoot = shootMotor;
        load = loadMotor;
        collect = collectMotor;
        bridge = bridgeMotor;
        potentiometer = new AnalogChannel(1);
        gyro = gyro1;
        ultrasonic = ultrasonic1;
    }

    public void shoot(double horAngleToTarget, double numberOfShots) {
            if (gyro.modulatedAngle == 0 && stepFlag == false) {
                savedDist = UltraCalc.getScaledDistance(ultrasonic.getAverageVoltage());
                stepFlag = true;
            } else {
                gyro.turnRobotToAngle(0);
            }
            if (stepFlag) {
                if (horAngleToTarget != 0) {
                    gyro.turnTurretToAngle(horAngleToTarget);
                } else {
                    for (int i = 0; i < numberOfShots; i++) { //modulate power according to distance and height
                        shoot.set(.75);
                        Timer.delay(3);
                        collect.set(1);
                        load.set(1);
                    }
                    isDone = true;
                }
            }
            if(isDone){ //resets robot
                stepFlag = false;
                gyro.turnTurretToAngle(0);
                isDone = false;
            }
        }
}
