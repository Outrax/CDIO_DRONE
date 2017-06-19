package de.yadrone.apps.cdio.opencv;


import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.apps.cdio.utilities.ImageUtils;

import java.awt.image.BufferedImage;

public class CircleDetector {

    private double focalLength = GlobalValues.cameraFocalLength;
    private double circleDiameter = 1000;

    /**
     * Returns a CircleMat object. See {@link de.yadrone.apps.cdio.opencv.CircleMat}
     * @param image The image to process
     * @return A new CircleMat object
     */
    public CircleMat processImage(BufferedImage image)
    {
        Mat frame = ImageUtils.toMatrix(image);
        Mat circle = detectCircle(frame);

        return new CircleMat(circle, frame);

    }

    /**
     * Processes the image by creating a new matrix and finding any circles in the frame.
     * @param frame The matrix containing the image to process
     * @return A matrix containing the circles in the image matrix
     */
    private Mat detectCircle(Mat frame) {

        Mat grayMat = new Mat();

        Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_RGB2GRAY);

		/* reduce the noise so we avoid false circle detection */
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(9, 9), 2, 2);

        // accumulator value
        double dp = 1.2d;
        // minimum distance between the center coordinates of detected circle in pixels
        double minDist = 500;

        // min and max radii (set these values as you desire)
        int minRadius = 0, maxRadius = 0;

        // param1 = gradient value used to handle edge detection
        // param2 = Accumulator threshold value for the
        // cv2.CV_HOUGH_GRADIENT method.
        // The smaller the threshold is, the more circle will be
        // detected (including false circle).
        // The larger the threshold is, the more circle will
        // potentially be returned.


        double param1 = 70, param2 = 100;

		/* create a Mat object to store the circle detected */
        Mat circle = new Mat();

		/* find the circle in the image */
        Imgproc.HoughCircles(grayMat, circle,
                Imgproc.HOUGH_GRADIENT, dp, minDist, param1,
                param2, minRadius, maxRadius);

        return circle;
    }

    /**
     * Calculates the distance to a circle
     * @param circleCoordinates A double array containing the x, y and radius of the circle
     * @return The distance to the circle
     */
    public double calculateDistance(double[] circleCoordinates)
    {
        //double[] circleCoordinates = circles.get(0, 0);
        double x        = circleCoordinates[0];
        double y        = circleCoordinates[1];
        double diameter = circleCoordinates[2] * 2;

        double calculatedDistance = (this.circleDiameter * this.focalLength) / (diameter);

        System.out.println("distance to object " + calculatedDistance + " cm");

        return calculatedDistance;
    }

    /**
     * Draws a circle in the image matrix
     * TODO: Update doc, if we draw more than one
     * @param circleMat The CircleMat object with the circles and the image
     * @return The updated image as a matrix
     */
    public Mat drawCircle(CircleMat circleMat) {

        Mat circle = circleMat.getCircles();
        Mat frame = circleMat.getFrame();

        //for (int i = 0; i < circleMat.numberOfCircles(); i++) {

        /*  get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
           (x,y) are the coordinates of the circle's center */

        // double[] circleCoordinates = circle.get(0,i);
        double[] circleCoordinates = circle.get(0, 0);
        double x = circleCoordinates[0];
        double y = circleCoordinates[1];
        int radius = (int) circleCoordinates[2];


        /* draw the circles found on the image */

        Point center = new Point(x, y);

        /* circle's outline */
        Imgproc.circle(
                frame,
                center,
                radius,
                new Scalar(0, 255, 0),
                4
        );

        /* circle's center outline */
        Imgproc.rectangle(
                frame,
                new Point(x - 5, y - 5),
                new Point(x + 5, y + 5),
                new Scalar(0, 128, 255),
                1
        );

        /* Show distance to circle */

        double distance = calculateDistance(circle.get(0, 0));
        Point label = new Point (frame.width()-300, frame.height()-50);

        Imgproc.putText(frame, distance + " cm", label, Core.FONT_HERSHEY_PLAIN, 4.0 ,new Scalar(255,255,0));

        //}


        return frame;
    }

}