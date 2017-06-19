package de.yadrone.apps.cdio.strategies;


import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.base.ARDrone;

import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;

public class LandingStrategy extends AbstractStrategy
{
    private boolean isLanding = false;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(!isLanding)
        {
            commander.landing();

            this.isLanding = true;

            controller.log("[DEBUG] LANDING STRATEGY: Drone landing");
            controller.next(Strategies.TAKEOFF);
            controller.stop();
        }

        return image;
    }
}