package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Nick, Michael
 */
public class Launcher {

    Jaguar launchMotor;
    Jaguar loadMotor;
    Encoder encoder;

    /**
     * Default constructor for the launcher
     */
    public Launcher() {
        this.launchMotor = new Jaguar(RoboMap.LAUNCH_MOTOR);
        this.loadMotor = new Jaguar(RoboMap.LOAD_MOTOR);
        this.encoder = new Encoder(
                RoboMap.LAUNCH_ENCODER1,
                RoboMap.LAUNCH_ENCODER2,
                false);
        this.encoder.setDistancePerPulse(1);
    }

        private double getRPM() {
        double rate;
        int sampleNumber = 500;
        double deltaTime = .5;
        double deltaEncod;
        double[] encod1Array = new double[(sampleNumber + 1)];
        double[] encod2Array = new double[(sampleNumber + 1)];
        double encod1=0, encod2=0;
        for(int i = 0; i < sampleNumber; i++) {
            encod1Array[i] = encoder.getRaw();
        }

        Timer.delay(deltaTime);
        for(int i = 0; i < sampleNumber; i++) {
            encod2Array[i] = encoder.getRaw();
        }
        for(int i = 0; i < sampleNumber; i++) {
            encod1 += encod1Array[i];
        }
        encod1 /= (sampleNumber - 1);
        for(int i = 0; i < sampleNumber; i++) {
            encod2 += encod2Array[i];
        }
        encod2 /= (sampleNumber - 1);
        deltaEncod = encod2 - encod1;
        rate = deltaEncod / (deltaTime * 6) * 4.67;
        return rate;
    }

    public void shoot(int pixel, double height) {
        double speed = 0.0;
        /* 1000 can change to tested values later */
        int launchSpeed = pixel * 1000;
        encoder.start();
        while (MathX.abs(getRPM() - launchSpeed) >= 100) {
            launchMotor.set(speed);
            speed += .025;
            Timer.delay(.025);
        }
        encoder.stop();
        loadMotor.set(1);
        Timer.delay(5);
        loadMotor.set(0);
        launchMotor.set(0);
    }
    
    public void manualShoot() {
        loadMotor.set(1);
        Timer.delay(5);
        loadMotor.set(0);
        launchMotor.set(0);
    }
}
