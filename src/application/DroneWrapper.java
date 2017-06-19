package application;

/*
 * Created by thomas on 05/06/2017.
 */

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;

import java.util.HashMap;
import java.util.Map;

public class DroneWrapper
{
    private final static int MOVEMENT_MIN_SPEED = 0;
    private final static int MOVEMENT_MAX_SPEED = 15;

    private ARDrone        drone;
    private CommandManager commands;

    private Map<AltitudeListener, AltitudeListener> altitudeListeners = new HashMap<>();

    private boolean isTesting = false;

    public DroneWrapper(ARDrone drone, boolean isTesting)
    {
        this.drone    = drone;
        this.commands = drone.getCommandManager();

        this.isTesting = isTesting;
    }

    public boolean isTesting()
    {
        return this.isTesting;
    }

    public void setTestingState(boolean isTesting)
    {
        this.isTesting = isTesting;
    }

    public void start()
    {
        this.drone.start();
    }

    public void stop()
    {
        this.drone.stop();
    }

    public void takeoff()
    {
        if(this.isTesting)
        {
            return;
        }

        this.drone.takeOff();
    }

    public void landing()
    {
        if(this.isTesting)
        {
            return;
        }

        this.drone.landing();
    }

    /**
     * Commands the drone to hover in place.
     */
    public void hover()
    {
        if(this.isTesting)
        {
            return;
        }

        this.commands.hover();
    }

    /**
     * Stops the current command and freezes the drone at the current place.
     */
    public void freeze()
    {
        if(this.isTesting)
        {
            return;
        }

        this.commands.freeze();
    }

    /**
     * Commands the drone to move upwards.
     *
     * @param acceleration The upward acceleration in percentages.
     */
    public void up(int acceleration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.commands.up(acceleration);
    }

    /**
     * Commands the drone to move downwards.
     *
     * @param acceleration The downward acceleration in percentages.
     */
    public void down(int acceleration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.commands.down(acceleration);
    }

    public void forward(int speed)
    {
        if(this.isTesting)
        {
            return;
        }

        speed = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, speed);

        this.commands.forward(speed);
    }

    public void backward(int speed)
    {
        if(this.isTesting)
        {
            return;
        }

        speed = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, speed);

        this.commands.backward(speed);
    }

    public void right(int speed)
    {
        if(this.isTesting)
        {
            return;
        }

        speed = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, speed);

        this.commands.goRight(speed);
    }

    public void left(int speed)
    {
        if(this.isTesting)
        {
            return;
        }

        speed = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, speed);

        this.commands.goLeft(speed);
    }

    /**
     * Spins the drone around its vertical axis. Negative values make the drone turn left, while positive
     * values makes it turn right.
     *
     * @param speed The turn speed in a negative or positive value.
     */
    public void spin(int speed)
    {
        if(this.isTesting)
        {
            return;
        }

        if(speed < 0)
        {
            this.commands.spinLeft(Math.abs(speed));
        }
        else
        {
            this.commands.spinRight(speed);
        }
    }

    public void move(float lrspeed, float fbspeed, float vspeed, float acceleration)
    {
        if(this.isTesting)
        {
            return;
        }

        vspeed       = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, vspeed);
        acceleration = clamp(MOVEMENT_MIN_SPEED, MOVEMENT_MAX_SPEED, acceleration);

        this.commands.move(lrspeed, fbspeed, vspeed, acceleration);
    }

    public void setAltitudeListener(final AltitudeListener listener)
    {
        AltitudeListener wrapper = new AltitudeListener()
        {
            @Override
            public void receivedAltitude(int altitude)
            {
                if(altitude >= 100)
                {
                    altitude += 500;
                }

                listener.receivedAltitude(altitude);
            }

            @Override
            public void receivedExtendedAltitude(Altitude d)
            {
                listener.receivedExtendedAltitude(d);
            }
        };

        this.altitudeListeners.put(listener, wrapper);

        this.drone.getNavDataManager().addAltitudeListener(wrapper);
    }

    public void removeAltitudeListener(AltitudeListener listener)
    {
        if(!this.altitudeListeners.containsKey(listener))
        {
            System.out.println("Cannot remove altitude listener");
            return;
        }

        this.drone.getNavDataManager().removeAltitudeListener(this.altitudeListeners.get(listener));
    }

    private int clamp(int min, int max, int value)
    {
        if(value < min)
        {
            return min;
        }
        else if(value > max)
        {
            return max;
        }

        return value;
    }

    private float clamp(float min, float max, float value)
    {
        if(value < min)
        {
            return min;
        }
        else if(value > max)
        {
            return max;
        }

        return value;
    }
}
