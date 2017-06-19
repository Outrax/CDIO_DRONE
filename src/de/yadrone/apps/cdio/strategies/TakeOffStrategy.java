package de.yadrone.apps.cdio.strategies;


import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.utilities.IntervalManager;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;

import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;

public class TakeOffStrategy extends AbstractStrategy implements AltitudeListener
{
    private int desiredAltitude = -1;
    private int counter = 0;

    private boolean shouldContinue = false;
    private boolean isInitialized = false;

    private Controller controller;

    /**
     * Creates a TakeOffStrategy that starts the drone and continues,
     * when the drone has reached the desired altitude.
     *
     * @param desiredAltitude The desired altitude in mm
     */
    public TakeOffStrategy(int desiredAltitude)
    {
        this.desiredAltitude = desiredAltitude;
    }

    /**
     * @inheritDoc
     */
    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(this.controller == null)
        {
            this.controller = controller;
        }

        if(!this.isInitialized)
        {
            this.initialize(commander);
        }

        if(!shouldContinue)
        {
            /*
             * Safeguard for when the altitude listener is not working
             */
            if(++counter % 7 == 0)
            {
                controller.log("Safeguarding takeoff");

                shouldContinue = true;
            }

            commander.up(50, 500);
        }
        else
        {
            commander.hover(500);
            commander.getDrone().getNavDataManager().removeAltitudeListener(this);

            controller.next(Strategies.SEARCH);
            controller.log("Takeoff completed");
        }

        return image;
    }

    @Override
    public void receivedAltitude(int altitude)
    {
        if(altitude >= desiredAltitude && !shouldContinue)
        {
            controller.log("Received desired altitude: " + altitude);

            shouldContinue = true;
        }
    }

    /**
     * AltitudeListener
     */
    @Override
    public void receivedExtendedAltitude(Altitude d) { }

    /**
     * @inheritDoc
     */
    @Override
    public void reset()
    {
        super.reset();

        this.isInitialized = false;
        this.shouldContinue = false;
        this.counter = 0;
    }

    private void initialize(ICommander commander)
    {
        this.isInitialized = true;

        commander.getDrone().getNavDataManager().addAltitudeListener(this);
        commander.takeoff();

        controller.log("[DEBUG] TAKEOFF STRATEGY: Drone taking off");
    }

}