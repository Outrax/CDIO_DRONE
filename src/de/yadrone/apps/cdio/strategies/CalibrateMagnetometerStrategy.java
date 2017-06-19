package de.yadrone.apps.cdio.strategies;

import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.Commander;
import de.yadrone.apps.cdio.commands.ICommander;

public class CalibrateMagnetometerStrategy extends AbstractStrategy
{
    private boolean isCalibrated = false;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(!this.isCalibrated)
        {
            commander.calibrateMagnetometer();

            this.isCalibrated = true;
        }
        else
        {
            controller.next(Strategies.LANDING);
        }

        return image;
    }
}