/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
/**
 *
 * @author Rajath
 * find targets
 */                                                     
public class ImageProcessing {

    ParticleAnalysisReport particles[];
    Physics imageCalculations;
    CriteriaCollection criteriaCollection = new CriteriaCollection();
    ParticleAnalysisReport bottomTarget, topTarget, middleTarget;

    public ImageProcessing() {
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH, 30, 400, false);
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT, 40, 400, false);
        imageCalculations = new Physics();
    }

    public void getTheParticles(AxisCamera camera)
            throws Exception {
        int erosionCount = 2;
        // true means connectivity 8, false means connectivity 4
        boolean connectivity8Or4 = false;

        particles = camera.getImage()
                //seperate the light and dark image
                .thresholdRGB(0, 42, 71, 255, 0, 255)
                .removeSmallObjects(connectivity8Or4, erosionCount)
                //fill the rectangles that were created
                .convexHull(connectivity8Or4)
                .particleFilter(criteriaCollection)
                .getOrderedParticleAnalysisReports();
        organizeParticles(particles,getTotalXCenter(particles),getTotalYCenter(particles));
    }

    public int getTotalXCenter(ParticleAnalysisReport[] particles) {
        int averageHeight = 0;

        if (particles.length == 0) {
            averageHeight = -1;
        } else {
            for (int i = 0; i < particles.length; i++) {
                averageHeight += particles[i].center_mass_x;
            }
            averageHeight /= particles.length;
        }
        return averageHeight;
    }

    public int getTotalYCenter(ParticleAnalysisReport[] particles) {
        int averageWidth = 0;

        if (particles.length == 0) {
            averageWidth = -1;
        } else {
            for (int i = 0; i < particles.length; i++) {
                averageWidth += particles[i].center_mass_y;
            }
            averageWidth /= particles.length;
        }
        return averageWidth;
    }

    public void organizeParticles(
            ParticleAnalysisReport[] particles,
            int centerMassHorizontal,
            int centerMassVertical) {
        double calculatedHeight;
        // the following values are in pixels
        double cameraOffset = 49;
        double bottomHeight = 38;
        double middleHeight = 71;
        double topHeight = 108;
        double errorRange = 3;
        String display = "";
        if (centerMassHorizontal == -1 || centerMassVertical == -1) {
            display += "No targets have been found\n";
        } else {
            display += particles.length + "Report"
                    + ((particles.length == 1)? "":"s") + "\n";
            for (int i = 0; i < particles.length; i++)
            {
                ParticleAnalysisReport particle = particles[i];
                display += particle.imageHeight + "\n";
                calculatedHeight =
                        imageCalculations.getHeight(
                            particle.imageHeight, particle.center_mass_y)
                        + cameraOffset;
                display += calculatedHeight + "\n";
                if (Math.abs(bottomHeight - calculatedHeight) < errorRange)
                {
                    display += "Bottom\n";
                    bottomTarget = particle;
                }
                else if (Math.abs(middleHeight - calculatedHeight) < errorRange)
                {
                    display += "Middle\n";
                    middleTarget = particle;
                }
                else if(Math.abs(topHeight - calculatedHeight) < errorRange)
                {
                    display += "Top\n";
                    topTarget = particle;
                }
            }
        }
        display += "----------------------\n";
        System.out.print(display);
    }
}
