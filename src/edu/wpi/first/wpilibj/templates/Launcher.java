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
        this.encoder = new Encoder(1, 2, false);
        this.encoder.setDistancePerPulse(1);
    }
    
    /**
     * Constructor used for custom ports on the motors and encoder
     * @param motor the split motor used in the launcher
     * @param loadMotor the motor used to load the launcher
     * @param encoder the shaft encoder, attached to either motor
     * 
     */
    public Launcher(Jaguar launchMotor, Jaguar loadMotor, Encoder encoder) {
        this.launchMotor = launchMotor;
        this.loadMotor = loadMotor;
        this.encoder = encoder;
        this.encoder.setDistancePerPulse(1);
    }
    
    public void shoot(int pixel) {
        double speed = 0.0;
        int launchSpeed = pixel * /* this can change to tested values later */1000;
        encoder.start();
        while (MathX.abs(encoder.getRate() - launchSpeed) >= 5) {
            launchMotor.set(speed);
            speed += .025;
            Timer.delay(.1);
        }
        encoder.stop();
        loadMotor.set(.25);
        Timer.delay(5);
        loadMotor.set(0);
    }
   
}
