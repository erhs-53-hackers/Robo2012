package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

/**
 *
 * @author Nick
 */
public class ImageCalculations {

    ParticleAnalysisReport particles[];
    ImageProcessing imageProcessing = new ImageProcessing();
    CriteriaCollection criteriaCollection = new CriteriaCollection();
    ParticleAnalysisReport bottomTargetReport, topTargetReport,
            middleTargetLeftReport, middleTargetRightReport;
    Target  topTarget,
            middleLeftTarget, middleRightTarget,
            bottomTarget,
            currentTarget;
    Messager msg = new Messager();
    // camera imaging values
    final double FOV_d = 35.25; // field of view, degrees
    final double FOV_p = 480; // field of view, pixels
    final double CFOV_p = FOV_p / 2; // center of field of view, pixels
    final double cameraElevationAngle = 12; // degrees
    final double disparityLimit = 100;

    public ImageCalculations() {
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH, 30, 400, false);
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT, 40, 400, false);
        bottomTarget = new Target((2 + (1 / 3.0)) * 12);
        middleLeftTarget = new Target((5 + (1 / 12.0)) * 12);
        middleRightTarget = new Target((5 + (1 / 12.0)) * 12);
        topTarget = new Target((8 + (1 / 6.0)) * 12);
    }

    private double pixelsToDegrees(double pixels) {
        return (pixels * FOV_d) / FOV_p;
    }

    private double degreesToPixels(double degrees) {
        return (degrees * FOV_p) / FOV_d;
    }

    public void setTargetPixels(
            Target target, int centerMassY, int pixelHeight) {
        target.setPixelValues(centerMassY, pixelHeight);
    }

    private double getElevationAngle(double targetPixelValue) {
        return pixelsToDegrees(CFOV_p - targetPixelValue)
                + cameraElevationAngle;
    }

    public double getDistance(double targetHeight, double targetPixelValue) {
        return targetHeight / (MathX.tan(getElevationAngle(targetPixelValue)));
    }
    
    public double getAverageDistance(Target target) {
        double distance1 = getDistance(target.topHeight, target.topPixelValue);
        double distance2 = getDistance(target.bottomHeight, target.bottomPixelValue);
        return (distance1 + distance2) / 2;
    }

    public double getDisparity(double targetHeight_b, double pixel_b,
            double targetHeight_t, double pixel_t) {
        double disparity = MathX.abs(getDistance(targetHeight_t, pixel_t)
                - getDistance(targetHeight_b, pixel_b));
        return disparity;
    }
    
    public double getDisparity(Target target) {
        return getDisparity(target.bottomHeight, target.bottomPixelValue,
                target.topHeight, target.topPixelValue);
    }

    public void setCurrentTarget(Target selectedTarget) {
        currentTarget = selectedTarget;
    }

    public void setTargets(ParticleAnalysisReport[] particles) {
        topTargetReport = getTopMost(particles);
        bottomTargetReport = getBottomMost(particles);
    }

    public static ParticleAnalysisReport getTopMost(
            ParticleAnalysisReport[] particles) {
        ParticleAnalysisReport greatest = particles[0];
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];

            if (particle.center_mass_y > greatest.center_mass_y) {
                greatest = particle;
            }
        }
        return greatest;
    }

    public static ParticleAnalysisReport getBottomMost(
            ParticleAnalysisReport[] particles) {
        ParticleAnalysisReport lowest = particles[0];
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];

            if (particle.center_mass_y < lowest.center_mass_y) {
                lowest = particle;
            }
        }
        return lowest;
    }

    public static ParticleAnalysisReport getRightMost(
            ParticleAnalysisReport[] particles) {
        ParticleAnalysisReport rightistTarget = particles[0];
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];

            if (particle.center_mass_x > rightistTarget.center_mass_x) {
                rightistTarget = particle;
            }
        }
        return rightistTarget;
    }

    public static ParticleAnalysisReport getLeftMost(
            ParticleAnalysisReport[] particles) {
        ParticleAnalysisReport leftistTarget = particles[0];
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];

            if (particle.center_mass_x < leftistTarget.center_mass_x) {
                leftistTarget = particle;
            }
        }
        return leftistTarget;
    }

    public void getTheParticles(AxisCamera camera) throws Exception {
        int erosionCount = 2;
        // true means use connectivity 8, false means connectivity 4
        boolean connectivity8Or4 = false;
        ColorImage colorImage;
        BinaryImage binaryImage;
        BinaryImage cleanImage;
        BinaryImage convexHullImage;
        BinaryImage filteredImage;

        colorImage = camera.getImage();
        //seperate the light and dark image
        binaryImage = colorImage.thresholdRGB(0, 42, 71, 255, 0, 255);
        cleanImage = binaryImage.removeSmallObjects(
                connectivity8Or4, erosionCount);
        //fill the rectangles that were created
        convexHullImage = cleanImage.convexHull(connectivity8Or4);
        filteredImage = convexHullImage.particleFilter(criteriaCollection);
        particles = filteredImage.getOrderedParticleAnalysisReports();
        colorImage.free();
        binaryImage.free();
        cleanImage.free();
        convexHullImage.free();
        filteredImage.free();
    }

    public void organizeTheParticles(ParticleAnalysisReport[] particles) {
        bottomTargetReport = null;
        topTargetReport = null;
        middleTargetLeftReport = null;
        middleTargetRightReport = null;
        ParticleAnalysisReport midLeftTargetTemp = null,
                midRightTargetTemp = null;
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];
            double minDisparity = Double.POSITIVE_INFINITY;
            int target = 1;
            for (int j = 1; j < 4; j++) {
                double currentDisparity =
                        getDisparity(
                            currentTarget.bottomHeight,
                            currentTarget.bottomPixelValue,
                            currentTarget.topHeight,
                            currentTarget.topPixelValue);

                if (currentDisparity < minDisparity) {
                    minDisparity = currentDisparity;
                    target = j;
                }
            }

            if (target == 1 && minDisparity < disparityLimit) {
                topTargetReport = particle;
                msg.printOnLn("Top target found",
                        DriverStationLCD.Line.kUser4);
            } else if (target == 2 && minDisparity < disparityLimit) {
                if (midLeftTargetTemp == null) {

                    midLeftTargetTemp = particle;
                    msg.printOnLn("left target found",
                            DriverStationLCD.Line.kUser5);

                } else {

                    midRightTargetTemp = particle;
                    msg.printOnLn("right target found",
                            DriverStationLCD.Line.kUser5);
                }
            } else if (target == 3 && minDisparity < disparityLimit) {
                bottomTargetReport = particle;
                msg.printOnLn("bottom target found",
                        DriverStationLCD.Line.kUser4);
            } else {
                msg.printOnLn("OMFG i cant find any targets",
                        DriverStationLCD.Line.kUser5);
            }
        }

        ParticleAnalysisReport[] targets;
        ParticleAnalysisReport p;

        if (midLeftTargetTemp != null && midRightTargetTemp != null) {
            targets = new ParticleAnalysisReport[]{
                midLeftTargetTemp, midRightTargetTemp};
            middleTargetRightReport = getRightMost(targets);
            middleTargetLeftReport = getLeftMost(targets);
        } else {
            if (topTarget != null && midRightTargetTemp != null) {
                targets = new ParticleAnalysisReport[]{
                    topTargetReport, midRightTargetTemp};
                p = getRightMost(targets);
                if (p == midRightTargetTemp) {
                    middleTargetRightReport = midRightTargetTemp;
                } else {
                    middleTargetLeftReport = midRightTargetTemp;
                }
            } else if (topTarget != null && midLeftTargetTemp != null) {
                targets = new ParticleAnalysisReport[]{
                    topTargetReport, midLeftTargetTemp};
                p = getLeftMost(targets);
                if (p == midRightTargetTemp) {
                    middleTargetLeftReport = midLeftTargetTemp;
                } else {
                    middleTargetRightReport = midLeftTargetTemp;
                }
            } else if (bottomTarget != null && midRightTargetTemp != null) {
                targets = new ParticleAnalysisReport[]{
                    bottomTargetReport, midRightTargetTemp};
                p = getRightMost(targets);
                if (p == midRightTargetTemp) {
                    middleTargetRightReport = midRightTargetTemp;
                } else {
                    middleTargetLeftReport = midRightTargetTemp;
                }
            } else if (bottomTarget != null && midLeftTargetTemp != null) {
                targets = new ParticleAnalysisReport[]{
                    bottomTargetReport, midLeftTargetTemp};
                p = getLeftMost(targets);
                if (p == midRightTargetTemp) {
                    middleTargetLeftReport = midLeftTargetTemp;
                } else {
                    middleTargetRightReport = midLeftTargetTemp;
                }
            } else {
                msg.printOnLn("Not 2 targets!!!", DriverStationLCD.Line.kMain6);
            }
        }
    }
}
