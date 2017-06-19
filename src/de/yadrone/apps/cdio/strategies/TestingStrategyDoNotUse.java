package de.yadrone.apps.cdio.strategies;

import de.yadrone.apps.cdio.commands.ICommander;

/*
 * Created by thomas on 02/06/2017.
 */


import de.yadrone.base.ARDrone;

import java.awt.image.BufferedImage;

import application.Controller;
import application.DroneWrapper;

public class TestingStrategyDoNotUse extends AbstractStrategy
{
    private boolean isFlying = false;
    private boolean isMoving = false;
    private boolean isSpinning = false;
    private boolean isLanding = false;
    private boolean isHovering = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;


    private int spinCount = 0;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        if(!isFlying)
        {
            controller.log("Taking off");

            commander.takeoff();
            isFlying = true;
        }

        else if(!isHovering)
        {
            controller.log("Hover!");

            commander.hover(2000);
            this.isHovering = true;
        }

        else if(!isMovingUp)
        {
            controller.log("Moving up!");

            commander.up(60, 2000);
            this.isMovingUp = true;
            this.isHovering = false;
        }

        else if(!isMovingLeft)
        {
            controller.log("Moving left!");

            commander.left(10, 2000);
            this.isMovingLeft = true;
            this.isHovering = false;
        }

        else if(!isMovingRight)
        {
            controller.log("Moving right!");

            commander.right(10, 2000);
            this.isMovingRight = true;
            this.isHovering = false;
        }

        else if(!isMovingDown)
        {
            controller.log("Moving down!");

            commander.down(60, 1000);
            this.isMovingDown = true;
            this.isHovering = false;
        }

        /*else if(!isMoving)
        {
            controller.log("Forward command issued!");
            this.commander.forward(15, 3000);
            this.isMoving = true;
        }
        else if(!isHovering)
        {
            controller.log("Hover!");
            this.commander.hover(2000);
            this.isHovering = true;
        }
        else if(!isSpinning)
        {
            controller.log("Spinning!");
            this.commander.spinRight(100, 200);
            if(++spinCount % 15 == 0)
            {
                this.isSpinning = true;
                this.isHovering = false;
            }
        }*/

        else if(!this.isLanding)
        {
            controller.log("Landing!");

            commander.landing();
            this.isLanding = true;
        }

        return image;
    }
}