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
        imageCalc.setTargetPixels(imageCalc.middleLeftTarget,
                imageCalc.middleTargetLeftReport.center_mass_y,
                imageCalc.middleTargetLeftReport.boundingRectHeight);
        imageCalc.setTargetPixels(imageCalc.middleRightTarget,
                imageCalc.middleTargetRightReport.center_mass_y,
                imageCalc.middleTargetRightReport.boundingRectHeight);
        imageCalc.setTargetPixels(imageCalc.bottomTarget,
                imageCalc.bottomTargetReport.center_mass_y,
                imageCalc.bottomTargetReport.boundingRectHeight);
    }

    public void main() {

        if (controls.FOV_Top()) {
            imageCalc.setCurrentTarget(imageCalc.topTarget);
        } else if (controls.FOV_Bottom()) {
            imageCalc.setCurrentTarget(imageCalc.bottomTarget);
        } else if (controls.FOV_Left()) {
            imageCalc.setCurrentTarget(imageCalc.middleLeftTarget);
        } else if (controls.FOV_Right()) {
            imageCalc.setCurrentTarget(imageCalc.middleRightTarget);
        }

        if (imageCalc.currentTarget != null) {
            if (imageCalc.getDisparity(imageCalc.currentTarget) < imageCalc.disparityLimit) {
                System.out.println("Distance: " + imageCalc.getAverageDistance(imageCalc.currentTarget));
                System.out.println("Disparity = " + imageCalc.getDisparity(imageCalc.currentTarget));
            } else {
                System.out.println("Disparity = "
                        + imageCalc.getDisparity(imageCalc.currentTarget));
                System.out.println("TOO LARGE");
            }
        } else {
            System.out.println("No target supplied");
        }
    }

    public void loop() {
        if (cam.freshImage()) {
            try {
                init();
                main();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
