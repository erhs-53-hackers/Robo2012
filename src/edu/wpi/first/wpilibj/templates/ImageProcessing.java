/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
/**
 *
 * @author Rajath
 *
 */
public class ImageProcessing {

    ParticleAnalysisReport party[];
    ParticleAnalysisReport topT;
    ParticleAnalysisReport bottomT;
    ParticleAnalysisReport leftT;
    ParticleAnalysisReport rightT;
    CriteriaCollection cc = new CriteriaCollection();

    public ImageProcessing() {
        cc.addCriteria(MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH, 30, 400, false);
        cc.addCriteria(MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT, 40, 400, false);
    }

    public ParticleAnalysisReport[] getTheParticles(AxisCamera cam) throws Exception {
        ColorImage colorImg = cam.getImage(); //get image from the camera
        BinaryImage binImg = colorImg.thresholdRGB(0, 42, 71, 255, 0, 255);//seperate the light and dark image
        colorImg.free();
        BinaryImage clnImg = binImg.removeSmallObjects(false, 2);//remove the small objects 
        binImg.free();
        BinaryImage convexHullImg = clnImg.convexHull(false);//fill the rectangles that were created
        clnImg.free();
        BinaryImage filteredImg = convexHullImg.particleFilter(cc);//
        convexHullImg.free();
        party = filteredImg.getOrderedParticleAnalysisReports();
        filteredImg.free();
        //orginizeParticles(party, getTotalXCenter(party), getTotalYCenter(party));
        return party;
    }
    public int getTarget(ParticleAnalysisReport part) {
        return part.boundingRectWidth/2;
    }
    
    public static ParticleAnalysisReport getTopmost(ParticleAnalysisReport[] parts) {
        ParticleAnalysisReport p = parts[0];
        for(int i=0;i<parts.length;i++) {
            if(p.center_mass_y < parts[i].center_mass_y) {
                p = parts[i];
            }
        }
        
        return p;        
    }
    
    
        public int[] getImgWidthArry(ParticleAnalysisReport[] part) {
       int[] array = new int[part.length];
        for (int i = 0; i < part.length; i++) {
            ParticleAnalysisReport r = part[i];
            array[i] = r.boundingRectHeight;           
       }
        return array;
    }
        
    public int getTotalXCenter(ParticleAnalysisReport[] part) {
        int avgHeight = 0;
        for (int i = 0; i < part.length; i++) {
            ParticleAnalysisReport r = part[i];
            avgHeight = avgHeight + r.center_mass_x;
        }
        if (part.length == 0) {
            return 1234567890;
        } else {
            avgHeight = avgHeight / part.length;
            return avgHeight;
        }
    }
    
    public int getTotalYCenter(ParticleAnalysisReport[] part) {
        int avgHeight = 0;
        for (int i = 0; i < part.length; i++) {
            ParticleAnalysisReport r = part[i];
            avgHeight = avgHeight + r.center_mass_y;
        }
        if (part.length == 0) {
            return 1234567890;
        } else {
            avgHeight = avgHeight / part.length;
            return avgHeight;
        }
    }

    public int getCurrentXCenter(ParticleAnalysisReport part) {
        return part.center_mass_x;
    }

    public int getCurrentYCenter(ParticleAnalysisReport part) {
        return part.center_mass_y;
    }

    public String orginizeParticles(ParticleAnalysisReport[] part, int cMx, int cMy) {
        String s = " ";
        if (cMx == 1234567890 || cMy == 1234567890) {
            s = "No targets have been found";
            return s;
        } else {
            System.out.println(part.length + " Report(s)");
            for (int i = 0; i < part.length; i++) {
                ParticleAnalysisReport r = part[i];
                
                if (r.particleArea > 15000) {
                    topT = r;
                    System.out.println(i + ": " + r.particleArea);
                    if (r.center_mass_x > cMx) {
                        rightT = r;
                        System.out.println("right target found");
                    } else if (r.center_mass_x < cMx) {
                        leftT = r;
                        System.out.println("left target found");
                    }

                    if (r.center_mass_y < cMy) {
                        topT = r;
                        System.out.println("Top");
                    } else if (r.center_mass_y > cMy) {
                        bottomT = r;
                        System.out.println("Bottom");
                    }
                }               
            }
            System.out.println("----------------------");
            return s;
        }
    }
}
