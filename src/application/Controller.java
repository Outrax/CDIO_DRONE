package application;


import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.gui.IConsole;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.video.ImageListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import de.yadrone.apps.cdio.strategies.AbstractStrategy;
import de.yadrone.apps.cdio.strategies.Strategies;

public class Controller implements ImageListener, Runnable, StateListener
{
    private final static long IDLE_TIMER_DELAY    = 6000L;
    private final static long IDLE_TIMER_INTERVAL = 500L;
    private final static long IDLE_TIMEOUT        = (long) 5E9;

    private IConsole    console;
    private ICommander  commander;
    private IARDrone    drone;

    private Map<Strategies, AbstractStrategy> strategies;
    private AbstractStrategy                  currentStrategy;

    private Timer timer;

    private long lastUpdated = System.nanoTime();

    private boolean          isIdle = false;
    private AbstractStrategy previous;

    private boolean isRunning   = false;
    private boolean isEmergency = false;

    public Controller(IConsole console, Map<Strategies, AbstractStrategy> strategies, ICommander commander)
    {
        this.console    = console;
        this.strategies = strategies;
        this.commander  = commander;
        this.drone      = commander.getDrone();

        if(strategies.size() < 1)
        {
            throw new IllegalStateException("No strategies are defined");
        }

        if(!this.ensureRequiredStrategiesAreDefined(strategies))
        {
            throw new IllegalStateException("Required strategies are not defined");
        }

        /*
         * Sets the initial strategy.
         */
        this.currentStrategy = strategies.get(Strategies.TAKEOFF);
        this.console.onStrategyChange(Strategies.TAKEOFF.toString());
    }

    private boolean ensureRequiredStrategiesAreDefined(Map<Strategies, AbstractStrategy> strategies)
    {
        return strategies.containsKey(Strategies.IDLE)    &&
               strategies.containsKey(Strategies.TAKEOFF) &&
               strategies.containsKey(Strategies.LANDING) &&
               strategies.containsKey(Strategies.EMERGENCY_LANDING);
    }

   
    private TimerTask createTimerTask()
    {
        final Controller controller = this;

        return new TimerTask()
        {
            @Override
            public void run()
            {
                if(lastUpdated + IDLE_TIMEOUT >= System.nanoTime())
                {
                    return;
                }

                controller.log("Idle timer reached");

                if(!controller.isIdle)
                {
                    controller.suspend();
                }

                controller.currentStrategy.run(null, controller, commander);
            }
        };
    }

    public void run()
    {
        if(this.drone == null)
        {
            this.log("Cannot set video listener when ARDrone is NULL.");

            return;
        }


    }

    /**
     * Stops the drone.
     */
    public void stop()
    {
        this.isRunning = false;
        this.timer.cancel();
        this.timer = null;
        this.drone.landing();
        this.next(Strategies.TAKEOFF);
    }

    public void start()
    {
        this.isRunning = true;

        if(!this.isEmergency)
        {
            drone.getCommandManager().emergency();
        }

        this.timer = new Timer("drone-idle-checker-thread");
        this.timer.scheduleAtFixedRate(createTimerTask(), IDLE_TIMER_DELAY, IDLE_TIMER_INTERVAL);
    }

    private void suspend()
    {
        this.log("Suspending current strategy due to missing image. Switching to idle");

        this.previous = this.currentStrategy;
        this.isIdle   = true;

        this.currentStrategy = this.strategies.get(Strategies.IDLE);
    }

    private void resume()
    {
        this.log("Resumed strategy after idle");

        this.currentStrategy = this.previous;
        this.isIdle = false;
    }

    @Override
    public void imageUpdated(BufferedImage buffer)
    {
        if(!this.isRunning || this.commander.isExecutingCommand())
        {
            this.console.setImage(this.insertCrosshair(buffer));
            this.console.repaint();

            return;
        }

        this.lastUpdated = System.nanoTime();

        if(this.isIdle)
        {
            this.resume();
        }

        BufferedImage image = this.currentStrategy.run(buffer, this, commander);

        if(image != null)
        {
            image = buffer;
        }

        this.console.setImage(this.insertCrosshair(image));
        this.console.repaint();
    }

    /**
     * Provides the next strategy and clears the extra fields of that strategy
     *
     * @param name The name of the strategy
     *
     * @return The next strategy instance
     */
    public AbstractStrategy next(Strategies name)
    {
        AbstractStrategy strategy = strategies.getOrDefault(name, null);

        if(strategy == null)
        {
            log("NO STRATEGY DEFINED FOR " + name);
            throw new IllegalStateException("Strategy " + name + " is not defined!");
        }

        strategy.reset();

        this.currentStrategy = strategy;
        this.console.onStrategyChange(strategy.getClass().getSimpleName());

        return strategy;
    }

    public void log(String message)
    {
        this.console.getOutputStream().println(message);
    }

    public void logException(Throwable throwable)
    {
        throwable.printStackTrace(this.console.getOutputStream());
    }

    private BufferedImage insertCrosshair(BufferedImage image)
    {
        if(image == null)
        {
            return null;
        }

        Graphics2D graphics = image.createGraphics();

        int width  = image.getWidth();
        int height = image.getHeight();

        graphics.setColor(Color.GREEN);

        /*
         * Draw position margins
         */
        int x1, y1;
        int x2, y2;
        int x3, y3;
        int x4, y4;

        int margin = (int) GlobalValues.getFizzyFactor();

        int centerX = width / 2;
        int centerY = height / 2;

        /*
         * Top-left
         */
        x1 = centerX - margin;
        y1 = centerY - margin;

        /*
         * Top-right
         */
        x2 = centerX + margin;
        y2 = centerY - margin;

        /*
         * Bottom-left
         */
        x3 = centerX - margin;
        y3 = centerY + margin;

        /*
         * Bottom-right
         */
        x4 = centerX + margin;
        y4 = centerY + margin;

        graphics.drawLine(x1, y1, x2, y2);  // top-left to top-right
        graphics.drawLine(x3, y3, x4, y4);  // bottom-left to bottom-right
        graphics.drawLine(x1, y1, x3, y3);  // top-left to bottom-left
        graphics.drawLine(x2, y2, x4, y4);  // top-right to bottom-right

        /*
         * Draw outer crosshairs
         */
        graphics.drawLine(width / 2, 0, width / 2, (height / 2) - margin);      // Vertical line before margin-box
        graphics.drawLine(width / 2, (height / 2) + margin, width / 2, height); // Vertical line after margin-box

        graphics.drawLine(0, height / 2, (width / 2) - margin, height / 2);     // Horizontal line before margin-box
        graphics.drawLine((width / 2) + margin, height / 2, width, height / 2); // Horizontal line after margin-box

        /*
         * Draw inner crosshairs
         */
        graphics.drawLine(width / 2, (height / 2) - 3, width / 2, (height / 2) + 3);
        graphics.drawLine((width / 2) - 3, height / 2, (width / 2) + 3, height / 2);

        graphics.dispose();

        return image;
    }

    @Override
    public void stateChanged(DroneState state)
    {
        this.isEmergency = state.isEmergency();
    }

    @Override
    public void controlStateChanged(ControlState state) { }
}