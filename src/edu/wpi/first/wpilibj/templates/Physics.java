package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author nick, michael
 */
public class Physics {
    
    public static final double HOOP1 = 2 + (1 / 3.0);//ft
    public static final double HOOP2 = 5 + (1 / 12.0);//ft
    public static final double HOOP3 = 8 + (1 / 6.0);//ft
    private double currentHoop = 94/12;//HOOP3;//the current hoop
    
    private static final int FOV = 33;//Field of View
    private static final int MAXPIXEL = 480;//height
    private static final double LAMBDA = MAXPIXEL / FOV;
    
    private static final float LAUNCH_HEIGHT = 4f;

    private static final int CAMERA_OFFSET = 11/12;//height the camera is off the ground
    private static final double RADIUS = 1.5;//half the width of the target

    /** the acceleration due to gravity in ft/sec*/
    private static final int g = 32;
    
    /** the observed pixel value from the camera*/
    private double p;
    /** the first approximation at the distance from the camera to the target*/
    private double R;  
    /** the pixel value, adjusted for fore-shortening*/
    private double pprime;
    /** the R value, calculated with the new pixel value*/
    private double rprime;
    /** the final horizontal distance to the target*/
    private double dprime;
    /** the adjustment for the angle the ball must enter the hoop at*/
    private double n;
    
    /** Include capabilities to push the information to the FRC Dashboard */
    Messager msg = new Messager();
    
    /**
     * set the current height of the hoop
     * @param num the height of the hoop in inches
     */
    public void setCurrentHoop(final double num) {
        currentHoop = num;
    }
    
    /**
     * sets the vertical height of the target from the center, in pixels
     * @param p height of the target, in pixels
     */
    public void setP(int p) {
        this.p = p;
    }
    
    /**
     * calculate distance from camera to center of hoop
     * @return distance in feet
     */
    protected double calculateR() {
        double rr = RADIUS / MathX.tan(p / LAMBDA);
        double distance = rr;
        this.R = distance;
        return distance;
    }
    
    /**
     * calculate the vertical height of the target in pixels
     * @return height of target in pixels
     */
    protected double calculateCorrection() {
        double theta = MathX.asin((currentHoop - CAMERA_OFFSET) / R);
        double c = MathX.cos(theta);
        double pp = p / c;
        this.pprime = pp;
        return pp;
    }
    
    /**
     * calculate the final distance from the camera to the hoop, adjusted for
     * foreshortening
     * @return the distance in feet
     */
    protected double calculateFinalR() {
        double rp = RADIUS / (MathX.tan(pprime / LAMBDA));
        this.rprime = rp;
        return rp;
    }
    
    /**
     * calculate the final horizontal distance from the targets
     * @return the distance in feet
     */
    protected double calculateFinalD() {
        double dp = MathX.sqrt(
                MathX.pow(rprime, 2) - MathX.pow((currentHoop - CAMERA_OFFSET), 2));
        this.dprime = dp;
        return dp;
    }
    
    /**
     * @return the possible error of R
     */
    protected double calculateError() {
        double error = rprime / (pprime);
        return error;
    }

    /**
     * calculate the adjustment for the angle the ball must enter in
     * @return the adjustment value, as a double
     */
    protected double calculateN() {
        double tanT = MathX.tan(60);
        double num = 2 * (currentHoop - LAUNCH_HEIGHT);
        double denom = dprime;
        double result = tanT - (num / denom);
        this.n = result;
        return result;
    }
    
   /**
     * calculate the launch velocity needed to launch the ball into the hoop
     * @return the launch velocity in ft./sec
     */
    protected double calculateLaunchVelocity() {
        double num = dprime * g;
        double sinAndCos = MathX.sin(60) + (n * MathX.cos(60));
        double denom = MathX.cos(60) * sinAndCos;
        double launchVelocity = MathX.sqrt(num / denom);
        return launchVelocity;
    }
    
    public void calculateInfo() {
        calculateR();
        calculateCorrection();
        calculateFinalR();
        calculateFinalD();
        calculateError();
        calculateN();
        calculateLaunchVelocity();
    }

    /**
     * push calculations to the console
     */
    public void pushInfo() {
        calculateInfo();
        System.out.println("P = " + p);
        System.out.println("R = " + R);        
        System.out.println("Correction = " + pprime);
        System.out.println("Final R = " + rprime);
        System.out.println("Final D = " + dprime);
        System.out.println("E = " + calculateError());
        System.out.println("N = " + n);
        System.out.println("V = " + calculateLaunchVelocity() + " ft./sec");
    }
    
    public void pushInfoToDashboard() {
        calculateInfo();
        msg.printLn("P = " + p);
        //msg.printLn("R = " + R);        
        msg.printLn("Correction = " + pprime);
        //msg.printLn("Final R = " + rprime);
        msg.printLn("Final D = " + dprime);
        //msg.printLn("E = " + calculateError());
        //msg.printLn("N = " + n);
        //msg.printLn("V = " + calculateLaunchVelocity() + " ft./sec");
    }
    
}