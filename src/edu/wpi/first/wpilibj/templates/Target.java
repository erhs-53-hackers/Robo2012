/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author Nick
 */
public class Target {
    
    public static final double cameraHeight = 54;
    
    public double topHeight;
    public double middleHeight;
    public double bottomHeight;
    
    public double centerOfMass_y;
    public double topPixelValue;
    public double bottomPixelValue;
    
    public Target(double middleHeight) {
        this.middleHeight = middleHeight - cameraHeight;
        this.topHeight = this.middleHeight + 9;
        this.bottomHeight = this.middleHeight - 9;
    }
    
    public void setPixelValues(int centerOfMass_y, int targetHeight_pixels) {
        this.centerOfMass_y = centerOfMass_y;
        this.topPixelValue = centerOfMass_y - (targetHeight_pixels / 2);
        this.bottomPixelValue = centerOfMass_y + (targetHeight_pixels / 2);
    }
 
}
