package de.yadrone.apps.cdio.opencv;

import org.opencv.core.Mat;


 public class CircleMat
{
    private Mat circles;
    private Mat frame;

    /**
     * Constructs a CircleMat object
     * @param circles The matrix that contains the circles
     * @param frame The matrix that contains the image
     */
    public CircleMat(Mat circles, Mat frame)
    {
        this.circles = circles;
        this.frame = frame;
    }

    /**
     * Counts the number of circles in the circle frame.
     * @return The number of circles
     */
    public int numberOfCircles()
    {
        return (circles.rows() == 0) ? 0 : circles.cols();
    }

    /**
     * Returns the matrix that contains the circles
     * @return The matrix that contains the circles
     */
    public Mat getCircles()
    {
        return circles;
    }

    /**
     * Returns the matrix that contains the image
     * @return The matrix that contains the image
     */
    public Mat getFrame()
    {
        return frame;
    }
}
