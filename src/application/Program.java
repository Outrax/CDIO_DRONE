package application;

import de.yadrone.apps.cdio.commands.Commander;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.gui.ButtonType;
import de.yadrone.apps.cdio.gui.Console;
import de.yadrone.apps.cdio.gui.IConsole;
import de.yadrone.apps.cdio.listeners.ButtonClickListener;
import de.yadrone.apps.cdio.strategies.*;
import de.yadrone.base.ARDrone;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import org.opencv.core.Core;

import java.util.HashMap;
import java.util.Map;

public class Program
{
    public final static int INITIAL_TAKEOFF_ALTITUDE = 1800;

    private Thread       droneThread;
    private Controller   droneController;
    private ICommander   commander;

    public static void main(String[] args)
    {
        new Program();
    }

    private Program()
    {
        this.initialize();
    }

    private void initialize()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ARDrone  drone   = new ARDrone();
        IConsole console = new Console("All Directions Autonomous Drone Madafaking System");

        this.commander = new Commander(drone, false);
        this.droneController = new Controller(console, loadStrategies(), this.commander);

        console.display();
        drone.start();

        this.setupConsoleListeners(drone, console);
        this.setupConsoleStatusListeners(drone, console);

        if(this.commander.isTesting())
        {
            console.setIsTestingEnabled(true);

            console.getOutputStream().println("[DEBUG] DRONE IS STARTED IN TESTING MODE");
        }

        drone.getVideoManager().addImageListener(droneController);
        drone.getNavDataManager().addStateListener(droneController);

        this.droneThread = new Thread(this.droneController);
        droneThread.start();
    }

    private Map<Strategies, AbstractStrategy> loadStrategies()
    {
        Map<Strategies, AbstractStrategy> strategies = new HashMap<>();

        /*
         * Testing strategies. Does nothing serious :)
         */
        strategies.put(Strategies.TESTING,           new TestingStrategyDoNotUse());
        strategies.put(Strategies.EMERGENCY_LANDING, new EmergencyLanding());

        /*
         * Takeoff uses the desired altitude in mm.
         */
        strategies.put(Strategies.TAKEOFF, new TakeOffStrategy(INITIAL_TAKEOFF_ALTITUDE));
        strategies.put(Strategies.IDLE,    new IdleStrategy());
        strategies.put(Strategies.SEARCH,  new SearchStrategy());
        strategies.put(Strategies.VERIFY,  new VerifyStrategy());
        strategies.put(Strategies.POSITION, new PositionStrategy());
        strategies.put(Strategies.PENETRATE, new PenetrateStrategy());
        strategies.put(Strategies.LANDING, new LandingStrategy());

        /*
         * Calibrations
         */
        strategies.put(Strategies.CALIBRATE_CAMERA, new CalibrationCameraStrategy());
        strategies.put(Strategies.CALIBRATE_MAGNETOMETER, new CalibrateMagnetometerStrategy());

        return strategies;
    }

    private void setupConsoleListeners(final ARDrone drone, IConsole console)
    {
        /*
         * Setup the callback for starting the drone thread.
         */
        console.addButtonListener(ButtonType.START, new ButtonClickListener()
        {
            @Override
            public void onClick(IConsole console)
            {
                droneController.start();

                console.println("Starting drone");
            }
        });

        /*
         * Setup the callback for stopping the drone thread (done indirectly by stopping the drone controller logic).
         */
        console.addButtonListener(ButtonType.STOP, new ButtonClickListener()
        {
            @Override
            public void onClick(IConsole console)
            {
                droneController.stop();

                console.println("Stopping drone");
            }
        });

        /*
         * Setup the callback for switching testing state.
         */
        console.addButtonListener(ButtonType.TESTING, new ButtonClickListener()
        {
            @Override
            public void onClick(IConsole console)
            {
                if(commander.isTesting())
                {
                    commander.setTesting(false);
                    console.println("[DEBUG] DISABLED TESTING MODE");

                    return;
                }

                commander.setTesting(true);
                console.println("[DEBUG] ENABLED TESTING MODE");
            }
        });

        console.addButtonListener(ButtonType.EMERGENCY, new ButtonClickListener()
        {
            @Override
            public void onClick(IConsole console)
            {
                drone.stop();
                droneController.stop();
            }
        });
    }

    private void setupConsoleStatusListeners(ARDrone drone, final IConsole console)
    {
        drone.getNavDataManager().addBatteryListener(new BatteryListener()
        {
            @Override
            public void batteryLevelChanged(int percentage)
            {
                console.onBatteryChange(percentage);
            }

            @Override
            public void voltageChanged(int vbat_raw) {}
        });

        drone.getNavDataManager().addAltitudeListener(new AltitudeListener()
        {
            @Override
            public void receivedAltitude(int altitude)
            {
                console.onAltitudeChange((altitude));
            }

            @Override
            public void receivedExtendedAltitude(Altitude d) {}
        });
    }

}