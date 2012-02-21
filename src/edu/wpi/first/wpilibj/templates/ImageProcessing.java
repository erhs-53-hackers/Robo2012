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
    int numberOfDegreesInVerticalFieldOfView = 33;
    final int numberOfPixelsVerticalInFieldOfView = 480;
    final int numberOfPixelsHorizontalInFieldOfView = 640;
    /*TODO instead of calculating the top target's height and then
     * writing down the result of your calculation,
     * code the calculation itself.  the compiler will generate the same
     * bytecode.  the only difference will be clearer code.  for one target,
     * this doesn't make much difference, but for 3 it will be clearer.
     * and there will be code repetition, which is a chance to refactor.
     */
    final int heightToTheTopOfTheTopTarget = 118;
    final int heightToBottomOfTopTarget = 100;
    /*TODO none of these three should be global varaibles
     * instead, use parameters, local variables and return values
     */
    int PixelsFromLevelToBottomOfTopTarget = 0;
    int PixelsFromLevelToTopOfTopTarget = 0;
    boolean isLookingAtTopTarget = false;

    int cameraHeight = 49;

    public ImageProcessing() {
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH, 30, 400, false);
        criteriaCollection.addCriteria(
                MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT, 40, 400, false);
        imageCalculations = new Physics(true);
    }

    public void getPixelsFromLevelToBottomOfTopTarget(
            ParticleAnalysisReport particle) {
        /*TODO take into account the fact that level is not
         * always at the bottom of the field of view
         */
        /*TODO don't use global variable PixelsFromLevelToBottomOfTopTarget,
         * instead use local variables, return values, and parameters
         */
        PixelsFromLevelToBottomOfTopTarget =
                numberOfPixelsVerticalInFieldOfView - particle.center_mass_y
                - (particle.boundingRectHeight / 2);
    }

    public void getPixelsFromLevelToTopOfTopTarget(
            ParticleAnalysisReport particle) {
        /*TODO take into account the fact that level is not
         * always at the bottom of the field of view
         */
        /*TODO don't use global variable PixelsFromLevelToBottomOfTopTarget,
         * instead use local variables, return values, and parameters
         */
        PixelsFromLevelToTopOfTopTarget =
                numberOfPixelsVerticalInFieldOfView - particle.center_mass_y
                - (particle.boundingRectHeight / 2);
    }

    public double getPhi(int PixelsFromLevelToTopOfTopTarget) {
        /* TODO: consider simply returning the calculated value,
         * instead of assigning it to a variable and then returning that
         * variable
         */
        double phi =
                (PixelsFromLevelToTopOfTopTarget
                    / numberOfPixelsVerticalInFieldOfView)
                * numberOfDegreesInVerticalFieldOfView;
        return phi;
    }

    /* this is a good function.  nice and short, easy to reason about,
     * no non-constant global usage.
     */
    public double getTheta(int PixelsFromLevelToBottomOfTopTarget) {
        /* TODO: consider simply returning the calculated value,
         * instead of assigning it to a variable and then returning that
         * variable
         */
        double theta =
                (PixelsFromLevelToBottomOfTopTarget /
                    numberOfPixelsVerticalInFieldOfView)
                * numberOfDegreesInVerticalFieldOfView;

        return theta;
    }

    /* this is a really good function.  nice and short,
     * takes a parameter for the values it uses, doesn't use a bad global,
     * doesn't set a bad global, and returns the calculated value.
     * it's really easy to see exactly what it does and reason about its
     * correctness.
     *
     * kudos.
     */
    public double getHypotneuse1(double angle) {
        double opposite1 = heightToTheTopOfTheTopTarget - cameraHeight;
        double hypotneuse_1 =
                opposite1
                / MathX.sin(getPhi(PixelsFromLevelToTopOfTopTarget));
        return hypotneuse_1;
    }

    public double getHypotneuse0(double angle) {
        double opposite0 = heightToBottomOfTopTarget - cameraHeight;
        double hypotneuse_0 = opposite0
                / MathX.sin(getTheta(PixelsFromLevelToBottomOfTopTarget));
        return hypotneuse_0;
    }

    /* this is nice and short.
     */
    public boolean isLookingAtTopTarget() {
        /*TODO don't use the globals PixelsFromLevelToTopOfTopTarget
         * instead, those should be parameters, or the functions that generate
         * those values should be called from here and the results saved
         * to local variables.  same with PixelsFromLevelToBottomOfTopTarget.
         */
        double adjacent1 =
                MathX.cos(getPhi(PixelsFromLevelToTopOfTopTarget))
                * getHypotneuse1(getPhi(PixelsFromLevelToTopOfTopTarget));
        double adjacent0 =
                MathX.cos(getTheta(PixelsFromLevelToBottomOfTopTarget))
                * getHypotneuse0(getTheta(PixelsFromLevelToBottomOfTopTarget));
        /* TODO remove these print statements after testing */
        System.out.println("Adjacent0 : " + adjacent0);
        System.out.println("Adjacent1 : " + adjacent1);
        /* TODO turn the next 5 lines into 1 line.
         */
        if (adjacent0 == adjacent1) {
            return true;
        } else {
            return false;
        }
    }

    public void idTopTarget(ParticleAnalysisReport[] particles) {
        for (int i = 0; i < particles.length; i++) {
            ParticleAnalysisReport particle = particles[i];

            getPixelsFromLevelToTopOfTopTarget(particle);
            getPixelsFromLevelToBottomOfTopTarget(particle);

            /*
             * TODO change this so a variable isn't set and not used.
             */
            isLookingAtTopTarget = isLookingAtTopTarget();
        }
    }

    public void getTheParticles(AxisCamera camera) throws Exception {
        int erosionCount = 2;
        // true means use connectivity 8, true means connectivity 4
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
        organizeParticles(particles, getTotalXCenter(particles),
                getTotalYCenter(particles));

        colorImage.free();
        binaryImage.free();
        cleanImage.free();
        convexHullImage.free();
        filteredImage.free();
    }

    public static ParticleAnalysisReport getTopMost(
            ParticleAnalysisReport[] particles) {
        ParticleAnalysisReport particle = particles[0];
        for (int i = 0; i < particles.length; i++) {
            if (particle.center_mass_y < particles[i].center_mass_y) {
                particle = particles[i];
            }
        }
        return particle;
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
                    + ((particles.length == 1) ? "" : "s") + "\n";
            for (int i = 0; i < particles.length; i++) {
                ParticleAnalysisReport particle = particles[i];
                display += particle.imageHeight + "\n";
                calculatedHeight =
                        imageCalculations.getHeight(
                        particle.imageHeight, particle.center_mass_y)
                        + cameraOffset;
                display += calculatedHeight + "\n";
                if (Math.abs(bottomHeight - calculatedHeight) < errorRange) {
                    display += "Bottom\n";
                    bottomTarget = particle;
                } else if (Math.abs(middleHeight - calculatedHeight)
                        < errorRange) {
                    display += "Middle\n";
                    middleTarget = particle;
                } else if (Math.abs(topHeight - calculatedHeight)
                        < errorRange) {
                    display += "Top\n";
                    topTarget = particle;
                }
            }
        }
        display += "----------------------\n";
        System.out.print(display);
    }
}
