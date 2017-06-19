package de.yadrone.apps.cdio.commands;
import de.yadrone.base.IARDrone;

import java.util.Timer;
import java.util.TimerTask;

public class Commander implements ICommander
{
    private final long TAKEOFF_TIME   = 4000;
    private final long CALIBRATION_TIME = 6000;
    private final long EXECUTION_TIME = 200;

    private IARDrone drone;

    private Timer     timer;
    private TimerTask task;

    private boolean isExecutingCommand = false;
    private boolean isCalibrating      = false;
    private boolean isTakingOff        = false;
    private boolean isTesting          = false;

    public Commander(IARDrone drone, boolean isTesting)
    {
        this.drone     = drone;
        this.isTesting = isTesting;
        this.timer     = new Timer();
    }



    @Override
    public boolean isExecutingCommand()
    {
        return this.isExecutingCommand || isTakingOff || isCalibrating;
    }

    @Override
    public boolean isTesting()
    {
        return this.isTesting;
    }

    @Override
    public void setTesting(boolean isTesting)
    {
        this.isTesting = isTesting;
    }

    @Override
    public void reset()
    {
        this.drone.reset();
    }

    @Override
    public void takeoff()
    {
        if(this.isTesting)
        {
            return;
        }

        this.isTakingOff = true;
        this.drone.takeOff();

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                isTakingOff = false;
            }
        }, TAKEOFF_TIME);
    }

    @Override
    public void landing()
    {
        if(this.isTesting)
        {
            return;
        }

        this.drone.landing();
    }

    @Override
    public void forward(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.forward();
            }
        }, duration);
    }

    @Override
    public void backwards(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.backward();
            }
        }, duration);
    }

    @Override
    public void left(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.goLeft();
            }
        }, duration);
    }

    @Override
    public void right(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.goRight();
            }
        }, duration);
    }

    @Override
    public void up(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.up();
            }
        }, duration);
    }

    @Override
    public void down(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.down();
            }
        }, duration);
    }

    @Override
    public void spinLeft(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.spinLeft();
            }
        }, duration);
    }

    @Override
    public void spinRight(final int speed, int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.setSpeed(speed);
                drone.spinRight();
            }
        }, duration);
    }

    @Override
    public void hover(int duration)
    {
        if(this.isTesting)
        {
            return;
        }

        this.execute(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.hover();
            }
        }, duration);
    }

    @Override
    public IARDrone getDrone()
    {
        return this.drone;
    }

    @Override
    public void calibrateMagnetometer()
    {
        this.isCalibrating = true;
        this.drone.getCommandManager().calibrateMagnetometer();

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                isCalibrating = false;
            }
        }, CALIBRATION_TIME);
    }

    private void execute(TimerTask task, long duration)
    {
        this.task = task;
        this.isExecutingCommand = true;

        /*
         * Schedule the task
         */
        this.timer.scheduleAtFixedRate(task, 0, EXECUTION_TIME);

        /*
         * Schedule a hover command for when the task is done
         */
        this.timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                drone.hover();
                Commander.this.task.cancel();

                isExecutingCommand = false;
            }
        }, duration);
    }
}