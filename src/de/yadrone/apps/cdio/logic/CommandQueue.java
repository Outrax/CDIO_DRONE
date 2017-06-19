package de.yadrone.apps.cdio.logic;

import de.yadrone.apps.cdio.gui.Controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;

public class CommandQueue implements Runnable
{
    /** True if the drone is flying */
    private boolean droneFlying = false;
    
    private final Controller controller;
    /** Command handler class that contains instructions for executing commands */
    private final Commander commandHandler;
    
    /** Determines if the execution thread should run */
    private boolean running = false;
    /** Thread sleep time when queue is empty in miliseconds */
    private int timeout = 0;
    /** Thread to execute the commands */
    private Thread commandThread;
    
    /** Blocks incomming commands if thread is executing other commands */
    private boolean busy = false;
    /** Blocks all incomming commands if larger than 0 */
    private int block = 0;
    /** Queue off all commands to be executed. The command will be removed after execution */
    private final LinkedList<CommandModel> commandQueue = new LinkedList();

    /**
     * Queue off all commands to be executed. The command will be removed after execution.
     * <br> Uses FIFO (First In = First Out) queue method.
     * @param controller
     *      Controller class
     * @param commandHandler
     *      Command handler class that contains instructions for executing commands
     */
    public CommandQueue(Controller controller, Commander commandHandler) {
        this.controller = controller;
        this.commandHandler = commandHandler;
    }
    
    /**
     * List of possible commands to be executed
     */
    public enum Command {
        /** Start flight */
        TakeOff, 
        /** Stop flight correctly */ 
        Land, 
        /** Ascent */
        MoveUp,
        /** Descent */
        MoveDown, 
        /** Fly left */
        MoveLeft,
        /** Fly right */
        MoveRight, 
        /** Spin left */
        SpinLeft,
        /** Spin right */
        SpinRight,
        /** Move forward */
        Forward,
        /** Move Backward */
        Backward,
        /** Stop drone in flight <b>(WARNING: WILL CRASH DOWN TO EARTH!)</b> */
        Kill, 
        /** Stand still in air */
        Hover, 
        /** Flash LEDs */
        LED, 
        /** Take off, hover for 3 seconds and land again */
        Test;
    }
    
    /**
     * List of flags for adding commands to the queue
     */
    public enum PushType {
        /** Clears queue before adding the command */
        Instant,
        /** Ignore busy flag */
        IgnoreBusy,
        /** Blocks other commands from being added to the queue untill this command has been executed */
        Block,
        /** Ignore Block flag */
        IgnoreBlock;
    }
    
    /**
     * Adds a command to the command queue. The command wil be removed after executione
     * <br>By default a command will not be added to the queue if the thread is marked as busy or blocked.
     * Add push type flags to avoid this.
     * @param command
     *      Command to be executed
     * @param speed
     *      The speed of flight for the given command in milimeters per second. Set -1 for null value
     * @param duration
     *     The duration that the command should be done for in miliseconds. Set -1 for null value
     * @param pushTypes
     *      List of flags for adding commands to the queue
     * @return
     *      Returns <code>true</code> if the command was succefully add to the queue
     */
    public boolean add(Command command, int speed, int duration, PushType... pushTypes) {
        ArrayList<PushType> pushFlags = new ArrayList();
        
        if(pushTypes != null)
            if(pushTypes.length > 0)
                pushFlags.addAll(Arrays.asList(pushTypes));
        
        if(block < 1 || pushFlags.contains(PushType.IgnoreBlock)) {
            if(!busy || pushFlags.contains(PushType.IgnoreBusy)) {
                if(pushFlags.contains(PushType.Instant))
                    this.clearQueue();
                
                if(pushFlags.contains(PushType.Block))
                    block++;
                
                return commandQueue.add(new CommandModel(command, speed, duration, pushTypes));
            }
        }
        return false;
    }
    
    /**
     * Get all commands in the queue
     * @return 
     *      <code>ArrayList</code> of all commands in the queue
     */
    public ArrayList<Command> getQueuedCommands() {
        ArrayList<Command> queuedCommands = new ArrayList<>();
        
        for (CommandModel cmd : commandQueue)
            queuedCommands.add(cmd.cmd);
        
        return queuedCommands;
    }
    
    /**
     * Get the number of commands in the queue
     * @return 
     *      Count of commands in the queue
     */
    public int getQueueSize() {
        return commandQueue.size();
    }
    
    /**
     * Checks id the queue is empty
     * @return 
     *      Returns <code>true</code> if the queue is empty
     */
    public boolean isQueueEmpty() {
        return commandQueue.isEmpty();
    }
    
    /**
     * Clear all commands in queue (ignores the current command)
     */
    public void clearQueue() {
        commandQueue.clear();
        block = 0;
    }
    
    /**
     * Starts the command execution thread
     * @param timeoutDuration 
     *      Thread sleep time when queue is empty
     */
    public void start(int timeoutDuration) {
        timeout = timeoutDuration;
        
        commandThread = new Thread(this);
        commandThread.setDaemon(true);
        commandThread.run();
    }
    
    /**
     * Stops the command execution thread correctly
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Forces the command execution thread to stop <b>(WARNING: MAY RESULT IN CONFLICTS!)</b>
     * <br>Clears the command queue
     */
    public void kill() {
        commandThread.stop();
        running = false;
        busy = false;
    }
    
    /**
     * Checks if the command execution thread is running
     * @return 
     *      Returns <code>true</code> if the command execution thread is running
     */
    public boolean isRunning() {
        return commandThread.isAlive();
    }
    
    /**
     * 
     * @return 
     */
    public boolean isDroneFlying() {
        return droneFlying;
    }

    @Override
    public void run() {
        running = true;
        
        while(running) {
            if(!commandQueue.isEmpty()) {
                busy = true;
                final CommandModel cmd = commandQueue.remove();
                
                switch(cmd.cmd) {
                    case TakeOff: {
                        droneFlying = true;
                        commandHandler.takeOff(); break;
                    }
                    case Land: {
                        commandHandler.land();
                        droneFlying = false; break;
                    }
                    case Kill: {
                        commandHandler.kill();
                        droneFlying = false; break;
                    }
                    case Hover: {
                        commandHandler.hover(cmd.duration);
                        break;
                    }
                    case SpinLeft: {
                        commandHandler.rotate(-cmd.speed, cmd.duration);
                        break;
                    }
                    case SpinRight: {
                        commandHandler.rotate(cmd.speed, cmd.duration);
                        break;
                    }
                    case MoveUp: {
                        commandHandler.moveVertival(cmd.speed, cmd.duration);
                        break;
                    }
                    case MoveDown: {
                        commandHandler.moveVertival(-cmd.speed, cmd.duration);
                        break;
                    }
                    case MoveLeft: {
                        commandHandler.moveHorizontal(-cmd.speed, cmd.duration);
                        break;
                    }
                    case MoveRight: {
                        commandHandler.moveHorizontal(cmd.speed, cmd.duration);
                        break;
                    }
                    case Forward: {
                        commandHandler.move(cmd.speed, cmd.duration);
                        break;
                    }
                     case Backward: {
                        commandHandler.move(-cmd.speed, cmd.duration);
                        break;
                    }
                    case LED: {
//                        commandHandler.animateLEDs(cmd.duration);
                        break;
                    }
                    case Test: {
                        droneFlying = true;
                        commandHandler.takeOffAndLand(3000);
                        droneFlying = false; break;
                    }
                    default: {
                        break;
                    }
                }
                
                if(cmd.hasBlockFlag())
                    block--;
            }
            else
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) { }
            
            if(block < 0)
                block = 0;
            
            busy = false;
        }
    }
    
    /**
     * Data class for commands in the command queue
     */
    private class CommandModel
    {
        /** Command to be executed */
        protected final Command cmd;
        /** The speed of flight for the given command in milimeters per second. Set -1 for null value */
        protected final int speed;
        /** The duration that the command should be done for in miliseconds. Set -1 for null value */
        protected final int duration;
        /** List of flags for adding commands to the queue */
        protected final PushType[] pushFlags;

        /**
         * Data object for commands in the command queue
         * @param cmd
         *      Command to be executed
         * @param speed
         *      The speed of flight for the given command in milimeters per second. Set -1 for null value
         * @param duration
         *      The duration that the command should be done for in miliseconds. Set -1 for null value
         * @param pushTypes 
         *      List of flags for adding commands to the queue
         */
        public CommandModel(Command cmd, int speed, int duration, PushType... pushTypes) {
            if(cmd == null)
                throw new NullPointerException("Command must be set! Command=" + cmd);
            
            this.cmd = cmd;
            this.speed = speed;
            this.duration = duration;
            
            if(pushTypes == null)
                this.pushFlags = new PushType[0];
            else
                this.pushFlags = pushTypes;
        }
        
        /**
         * Checks if the command object contains the block flag
         * @return 
         *      Returns <code>true</code> if the flag exist
         */
        public boolean hasBlockFlag() {
            for (PushType pushFlag : pushFlags) {
                if(pushFlag.equals(PushType.Block))
                    return true;
            }
            
            return false;
        }
    }
}