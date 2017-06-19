package de.yadrone.apps.cdio.strategies;



import java.awt.*;
import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.opencv.CircleData;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.apps.cdio.utilities.IntervalManager;

public class PositionStrategy extends AbstractStrategy
{
    public final static String CIRCLE_DATA = "circle-data";

    private boolean isVerticallyPositioned = false;
    private boolean isHorizontallyPositioned = false;

    private int calls = 0;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        /*
         * Fetch circle data from previous strategy (search).
         */
        CircleData circle = (CircleData) this.fields.getOrDefault(CIRCLE_DATA, null);

        if (circle == null)
        {
            controller.log("Missing circle data!");
            controller.next(Strategies.SEARCH);

            return image;
        }

        this.calls++;

        /*
         * Calculate center of camera image.
         */
        Point imageCenterCoordinate = new Point(image.getWidth() / 2, image.getHeight() / 2);
        double fizzyFactor = GlobalValues.getFizzyFactor();

        if(!this.isVerticallyPositioned || this.calls % 4 == 0)
        {
            this.isVerticallyPositioned = this.verticalPositioning(commander, circle, imageCenterCoordinate, fizzyFactor);
        }
        else
        {
            this.isHorizontallyPositioned = this.horizontalPositioning(commander, circle, imageCenterCoordinate, fizzyFactor);
        }

        if(this.isHorizontallyPositioned && this.isVerticallyPositioned)
        {
            controller.log("YAY!");
            controller.next(Strategies.PENETRATE);

            /*
             * Now we are done, so reset values
             */
            this.calls = 0;
            this.isVerticallyPositioned = false;
            this.isHorizontallyPositioned = false;
        }
        else
        {
            controller.next(Strategies.SEARCH);
        }

        return image;
    }

    private boolean verticalPositioning(ICommander commander, CircleData circle, Point imageCenterCoordinate, double fizzyFactor)
    {
        int yDirectionComparison = this.determineDroneYPositionRelativeToCircleCenter(circle.getY(), imageCenterCoordinate.getY(), fizzyFactor);

        /*
         * If the y-axis comparison is equal to 0, the drone is looking right at the center of the circle.
         */
        if(yDirectionComparison == 0)
        {
            System.out.println("Drone is in the right height!");

            return true;
        }

        /*
         * If the y-axis comparison is negative the drone should fly up.
         */
        else if(yDirectionComparison > 0)
        {
            System.out.println("Drone is moving up");

            commander.up(25, 70);

            return false;
        }

        /*
         * If the y-axis comparison is positive the drone should fly down. We have already checked
         * for zero, so it can only be positive now.
         */
        else
        {
            System.out.println("Drone is moving down");

            commander.down(25, 70);

            return false;
        }
    }

    private boolean horizontalPositioning(ICommander commander, CircleData circle, Point imageCenterCoordinate, double fizzyFactor)
    {
            int xDirectionComparison = this.determineDroneXPositionRelativeToCircleCenter(circle.getX(), imageCenterCoordinate.getX(), fizzyFactor);

            if(xDirectionComparison == 0)
            {
                System.out.println("Circle is in the center of the image!");

                return true;
            }
            else if (xDirectionComparison < 0)
            {
                System.out.println("Circle is to the right!");
                System.out.println("Spinning right");

                commander.spinRight(5, 70);
            }
            else
            {
                System.out.println("Circle is to the left");
                System.out.println("Spinning left");

                commander.spinLeft(5, 70);
            }

        return false;
    }

    /**
     * Compares a drone y-coordinate relative to the image center coordinate.
     *
     * @param circleAxisCoordinate  Circle coordinate. This coordinate is stationary.
     * @param cameraCenter          Drone camera center coordinate
     * @param fizzyFactor           The margin of error
     *
     * @return Returns  1 if the drone should move up (y-axis).
     *                  0 if the drone is within the center plus/minus fizzy factor.
     *                 -1 if the drone should move down (y-axis).
     */
    private int determineDroneYPositionRelativeToCircleCenter(double circleAxisCoordinate, double cameraCenter, double fizzyFactor)
    {
        if(circleAxisCoordinate < (cameraCenter + fizzyFactor) && circleAxisCoordinate > (cameraCenter - fizzyFactor))
        {
            return 0;
        }
        else if(circleAxisCoordinate > (cameraCenter + fizzyFactor))
        {
            return -1;
        }

        return 1;
    }

    /**
     * Compares a drone x-coordinate relative to the image center coordinate.
     *
     * @param circleAxisCoordinate  Circle coordinate. This coordinate is stationary.
     * @param cameraCenter          Drone camera center coordinate
     * @param fizzyFactor           The margin of error
     *
     * @return Returns  1 if the drone should move right (x-axis).
     *                  0 if the drone is within the center plus/minus fizzy factor.
     *                 -1 if the drone should move left (x-axis).
     */
    private int determineDroneXPositionRelativeToCircleCenter(double circleAxisCoordinate, double cameraCenter, double fizzyFactor)
    {
        if(circleAxisCoordinate < (cameraCenter + fizzyFactor) && circleAxisCoordinate > (cameraCenter - fizzyFactor))
        {
            return 0;
        }
        if(circleAxisCoordinate > (cameraCenter + fizzyFactor))
        {
            return -1;
        }

        return 1;
    }


}