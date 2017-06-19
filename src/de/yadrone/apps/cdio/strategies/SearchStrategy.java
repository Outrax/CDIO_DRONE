package de.yadrone.apps.cdio.strategies;

import com.google.zxing.*;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.opencv.CircleData;
import de.yadrone.apps.cdio.opencv.CircleDetector;
import de.yadrone.apps.cdio.opencv.CircleMat;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.apps.cdio.utilities.ImageUtils;

import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class SearchStrategy extends AbstractStrategy
{
    private CircleDetector circleDetector = new CircleDetector();

    private Result scanResult;
    private int QRCount = 0;
    private int imageCount = 0;
    private int count = 0;


    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(GlobalValues.nextGateInSequence == 6)
        {
            controller.next(Strategies.LANDING);

            return image;
        }

        if(++imageCount % 2 == 0)
        {
            return image;
        }

        CircleMat mat = this.circleDetector.processImage(image);

        if(mat.numberOfCircles() > 0)
        {
            this.circleDetector.drawCircle(mat);

            System.out.println("Found circle!!!");

            next(commander, controller, mat);
        }
        else
        {
            commander.spinRight(10, 70);
        }

        return ImageUtils.fromMatrix(mat.getFrame());
    }

    private void next(ICommander commander, Controller controller, CircleMat circle)
    {

        double[] circleCoordinates = circle.getCircles().get(0, 0);
        controller.next(Strategies.POSITION).addField(PositionStrategy.CIRCLE_DATA, new CircleData(circleCoordinates, this.circleDetector.calculateDistance(circleCoordinates)));
    }

    //Imgcodecs.imwrite(System.getProperty("user.dir") + "/src/de/yadrone/apps/cdio/resources/test1.jpg", mat.getFrame());
}