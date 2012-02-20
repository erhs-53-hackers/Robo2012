/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
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

    public void getTheParticles(AxisCamera cam) throws Exception {
        ColorImage colorImg = cam.getImage(); //get image from the camera
        BinaryImage binImg = colorImg.thresholdRGB(0, 42, 71, 255, 0, 255);//seperate the light and dark image
        colorImg.free();
        BinaryImage clnImg = binImg.removeSmallObjects(false, 2);//remove the small objects 
        binImg.free();
        BinaryImage convexHullImg = clnImg.convexHull(false);//fill the rectangles that were created
        clnImg.free();
        BinaryImage filteredImg = convexHullImg.particleFilter(criteriaCollection);//
        convexHullImg.free();
        particles = filteredImg.getOrderedParticleAnalysisReports();
        filteredImg.free();
        
    }
    
    public static ParticleAnalysisReport getTopMost(ParticleAnalysisReport[] parts) {
        ParticleAnalysisReport p = parts[0];
        for (int i = 0; i < parts.length; i++) {
            if (p.center_mass_y < parts[i].center_mass_y) {
                p = parts[i];
            }
        }
        return p;
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
