package de.yadrone.apps.cdio.strategies;



import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.apps.cdio.utilities.IntervalManager;

import static de.yadrone.apps.cdio.strategies.Strategies.LANDING;

public class PenetrateStrategy extends AbstractStrategy
{
    private boolean hasPenetrated = false;

    private final static int FORWARD_SPEED = 12;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(!hasPenetrated)
        {
            controller.log("CHARGE!");

            commander.forward(FORWARD_SPEED, 3000);

            /*
             * For testing purpose. Should be just ++
             */
            GlobalValues.nextGateInSequence = GlobalValues.nextGateInSequence == 1 ? 5 : 6;

            controller.next(Strategies.SEARCH);

            this.hasPenetrated = true;
        }

        return image;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.hasPenetrated = false;
    }
}