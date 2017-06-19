package de.yadrone.apps.cdio.data;

import de.yadrone.base.command.VideoCodec;
import java.util.logging.Level;

public class Config 
{

    protected final static String DEFAULT_DRONE_IP = "192.168.1.1";
    protected final static int DEFAULT_MAX_ALTITUDE = 2750; // in mm
    protected final static int DEFAULT_MIN_ALTITUDE = 1500; // in mm
    protected final static int DEFAULT_VIDEO_FRAMERATE = 15;
    protected final static boolean DEFAULT_OUTDOOR = false;
    protected final static boolean DEFAULT_HULL = true;
    protected final static int DEFAULT_PORTAL_HEIGHT = 850; //in mm
    protected final static double DEFAULT_CAMERA_CONSTANT = 1.61806;
    protected final static Level DEFAULT_LOGGER_LEVEL = Level.INFO;
    public static int PORTAL_HEIGHT = DEFAULT_PORTAL_HEIGHT;
    public static double CAMERA_CONSTANT = DEFAULT_CAMERA_CONSTANT;
    public final static VideoCodec VIDEO_CODEC = VideoCodec.H264_360P;
    public final static int CMDQ_TIMEOUT = 200; //in ms
}