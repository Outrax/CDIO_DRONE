package de.yadrone.apps.cdio.strategies;

/*
 * Created by thomas on 05/06/2017.
 */


import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;

public class EmergencyLanding extends AbstractStrategy
{
    private boolean isLanding = false;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(!this.isLanding)
        {
            commander.landing();

            this.isLanding = true;
        }

        return image;
    }
}