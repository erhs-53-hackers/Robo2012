/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.camera.AxisCamera;

/**
 *
 * @author Nick
 */
public class Main {

    ImageCalculations imageCalc = new ImageCalculations();
    Joystick stick = new Joystick(1);
    Controls controls = new Controls(stick);
    AxisCamera cam = AxisCamera.getInstance();
    double distance;

    public void init() throws Exception {
        imageCalc.getTheParticles(cam);
        imageCalc.setTargetPixels(imageCalc.topTarget,
                imageCalc.topTargetReport.center_mass_y,
                imageCalc.topTargetReport.boundingRectHeight);
        imageCalc.setTargetPixels(imageCalc.topTarget,
                imageCalc.topTargetReport.center_mass_y,
                imageCalc.topTargetReport.boundingRectHeight);
        imageCalc.setTargetPixels(imageCalc.middleTarget,
                imageCalc.middleTargetLeftReport.center_mass_y,
                imageCalc.middleTargetLeftReport.boundingRectHeight);
        imageCalc.setTargetPixels(imageCalc.bottomTarget,
                imageCalc.bottomTargetReport.center_mass_y,
                imageCalc.bottomTargetReport.boundingRectHeight);
    }

    public double returnDistanceToTarget() {
        distance = (imageCalc.getDistance(
                imageCalc.currentTarget.bottomHeight,
                imageCalc.currentTarget.bottomPixelValue)
                + imageCalc.getDistance(imageCalc.currentTarget.topHeight,
                imageCalc.currentTarget.topPixelValue)) / 2;
        double disparity = imageCalc.getDisparity(
                imageCalc.currentTarget.bottomHeight,
                imageCalc.currentTarget.bottomPixelValue,
                imageCalc.currentTarget.topHeight,
                imageCalc.currentTarget.topPixelValue);
        if (disparity < imageCalc.disparityLimit) {
            return distance;
        } else {
            System.out.println("Disparity = " + disparity + ". Too high");
            return -1;
        }
    }

    public void main() {
        if (controls.FOV_Top()) {
            imageCalc.setCurrentTarget(imageCalc.topTarget);
        }
        if (controls.FOV_Bottom()) {
            imageCalc.setCurrentTarget(imageCalc.bottomTarget);
        }
        if (controls.FOV_Left() || controls.FOV_Right()) {
            imageCalc.setCurrentTarget(imageCalc.middleTarget);
        }
        if (imageCalc.currentTarget != null) {
            System.out.println("Distance: " + returnDistanceToTarget());
        } else {
            System.out.println("Cannot determine distance");
        }
    }
}
