package de.yadrone.apps.cdio.strategies;



import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;

public class IdleStrategy extends AbstractStrategy
{
    private long imageCount = 0;

    @Override
    public BufferedImage run(BufferedImage buff, Controller controller, ICommander commander)
    {
        imageCount++;

        if(imageCount % 5 == 0)
        {
            commander.hover(0);
        }

        return buff;
    }
}