package de.yadrone.apps.cdio.data;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import de.yadrone.apps.cdio.data.PropertyHandler.PropertyLabel;



public class PropertyHandler 
{
    public enum PropertyLabel {
        DroneIP, MaxAltitude, MinAltitude, VideoFrameRate, Outdoor, Hull, PortalHeight, CameraConstant, LoggerLevel;
    }
    
    private final Properties prop = new Properties();
    private final String filepath;

    public PropertyHandler(String filepath, boolean createIfMissing) {
        this.filepath = filepath;
        
        if(createIfMissing) {
        }
            
    }
    
    public void loadProperties() throws FileNotFoundException, IOException {
       // try (InputStream input = new FileInputStream(filepath)) {
       //    prop.load(input);
        }
    
    
    public void saveProperties(String droneIP, Integer maxAltitude, Integer minAltitude, Integer VideoFrameRate, Boolean outdoor, Boolean hull, Integer PortalHeight, Double CameraConstant, Level loggerLevel) throws FileNotFoundException, IOException {
        try (OutputStream output = new FileOutputStream(filepath)) {
            prop.setProperty(PropertyLabel.DroneIP.name(), droneIP);
            prop.setProperty(PropertyLabel.MaxAltitude.name(), maxAltitude.toString());
            prop.setProperty(PropertyLabel.MinAltitude.name(), minAltitude.toString());
            prop.setProperty(PropertyLabel.VideoFrameRate.name(), VideoFrameRate.toString());
            prop.setProperty(PropertyLabel.Outdoor.name(), outdoor.toString());
            prop.setProperty(PropertyLabel.Hull.name(), hull.toString());
            prop.setProperty(PropertyLabel.PortalHeight.name(), PortalHeight.toString());
            prop.setProperty(PropertyLabel.CameraConstant.name(), CameraConstant.toString());
            prop.setProperty(PropertyLabel.LoggerLevel.name(), loggerLevel.getName());
            prop.store(output, "DFM drone client properties");
        }
    }

    public void saveCameraConstant(double cameraConstant) throws FileNotFoundException, IOException
    {
        try( OutputStream output = new FileOutputStream(filepath)){
            prop.setProperty(PropertyLabel.CameraConstant.name(), String.valueOf(cameraConstant));
            prop.store(output,"DFM drone client properties");
        }
    }

    public void save(PropertyLabel propLabel, Integer value) throws FileNotFoundException, IOException
    {
        try( OutputStream output = new FileOutputStream(filepath)){
            prop.setProperty(propLabel.name(), value.toString());
            prop.store(output,"DFM drone client properties");
        }
    }
    public String get(PropertyLabel propLabel) {
        final String value = prop.getProperty(propLabel.name(), "MISSING PROPERTY");
        
        if(value == null)
            propLabel.name();
        
        return value;
    }
    
}