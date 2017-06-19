package de.yadrone.apps.cdio.gui;

import de.yadrone.apps.cdio.listeners.BatteryListener;
import de.yadrone.base.IARDrone;
import de.yadrone.apps.cdio.logic.AILogic;
import de.yadrone.apps.cdio.logic.CommandQueue;
import de.yadrone.apps.cdio.data.PropertyHandler;
import de.yadrone.apps.cdio.data.PropertyHandler.PropertyLabel;
import de.yadrone.apps.cdio.listeners.CameraSwitchListener;
import de.yadrone.apps.cdio.listeners.GUIWindowListener;
import de.yadrone.apps.cdio.listeners.VideoListener;
import de.yadrone.apps.cdio.logic.Commander;
import de.yadrone.apps.cdio.data.Config;
import de.yadrone.apps.cdio.data.HSVHandler;
import de.yadrone.apps.cdio.utilities.OpenCVUtils.ImageAnalyticsModel;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.JFrame;


public class Controller 
{
    public final PropertyHandler propHandler;
    public final HSVHandler hsvHandler;
    private final MenuPanel menu;
    private final JFrame window;
    private final VideoPanel video;
    private final AILogic droneLogic;
    
    public final CommandQueue cmdQ;
    protected final IARDrone drone;
    
    public Controller(IARDrone drone, PropertyHandler propHandler, HSVHandler hsvHandler) {
        this.drone = drone;
        this.propHandler = propHandler;
        this.hsvHandler = hsvHandler;
        
        cmdQ = new CommandQueue(this, new Commander(this));
        droneLogic = new AILogic(this, cmdQ);
        
        video = new VideoPanel(this);
        video.setSize((int) (640 * 1.5), (int) (360 * 1.5));
        video.addVideoListener(new VideoListener(this));
        video.addCameraSwitchListener(new CameraSwitchListener(this));
        
        menu = new MenuPanel(this);
        menu.addVideoPanel(video);
        menu.addBatteryListener(new BatteryListener(this));
        
        window = new JFrame("Killer Drone");
        window.addWindowListener(new GUIWindowListener(this));
        window.setSize(1200, 600);
        window.setJMenuBar(new GUIMenuBar(this));
        window.setContentPane(menu);
        window.setVisible(true);
        
        setBusy(true);
        cmdQ.clearQueue();
        cmdQ.start(Config.CMDQ_TIMEOUT);
        setBusy(false);
    }
    
    public void setColorOffset(Integer[] colorOffset) {
        MenuPanel.colorOffset = colorOffset;
    }
    
    public void setBusy(boolean busy) {
        menu.updateBusy(busy);
    }
    
    public String getProperty(PropertyLabel propLabel) {
        return propHandler.get(propLabel);
    }

    public IARDrone getDrone() {
        return drone;
    }
    
    public BufferedImage getVideoFrame() {
        return video.imageRaw;
    }

    public void updateImage(BufferedImage image) {
        video.updateImage(image);
    }

    public void updateBatteryDisplay(int batteryLevel) {
        menu.updateBatteryDisplay(batteryLevel);
    }

    public void updateDistanceDisplay(double distance) {
        menu.updateDistanceDisplay(distance);
    }

    public void updateLastCMDDisplay(String command) {
        menu.updateLastCMDDisplay(command);
        updateLogDisplay("CMD - " + command);
    }
    
    public void updateLogDisplay(String message) {
        menu.updateLogger(message);
    }

    public void updateNavigationDisplay(float pitch, float roll, float yaw) {
        menu.updateNavigationDisplay(pitch, roll, yaw);
    }
    
    protected void computeFlight(ImageAnalyticsModel imageAnalytics) {
        droneLogic.compute(imageAnalytics);
    }
}