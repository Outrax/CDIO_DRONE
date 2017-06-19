package de.yadrone.apps.cdio.strategies;


import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.opencv.CircleData;
import de.yadrone.apps.cdio.opencv.CircleDetector;
import de.yadrone.apps.cdio.opencv.CircleMat;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.apps.cdio.utilities.ImageUtils;

public class CalibrationCameraStrategy extends AbstractStrategy {

    private double distance = 2150;
    private double focalLength = 0;
    private double circleDiameter = 1000;
    private CircleDetector circleDetector = new CircleDetector();
    private long imageCount = 0;
    private double sum = 0;
    private int counter = 0;


    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander) {

        if((++imageCount % 2) != 0)
        {
            return image;
        }



        CircleMat mat = this.circleDetector.processImage(image);
        this.circleDetector.drawCircle(mat);
        CircleData circleData = new CircleData(mat.getCircles().get(0,0), this.distance);
        calibrateFocalLength(circleData, controller);

        //controller.next(Strategies.SEARCH);

        return ImageUtils.fromMatrix(mat.getFrame());
    }


    public void calibrateFocalLength(CircleData circleData, Controller controller) {

        double x = circleData.getX();
        double y = circleData.getY();
        double diameter = circleData.getDiameter();

        this.focalLength = (diameter * this.distance) / this.circleDiameter;
        this.sum += focalLength;

        if(++this.counter % 150 == 0)
        {
            System.out.println("Average focal length: " + this.sum/150d);
        }

        GlobalValues.cameraFocalLength = this.focalLength;

    }


}