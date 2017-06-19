package de.yadrone.apps.cdio.strategies;



import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;

public abstract class AbstractStrategy
{
    protected Map<String, Object> fields = new HashMap<>();

    public abstract BufferedImage run(BufferedImage image, Controller controller, ICommander commander);

    /**
     * Clears the extra fields of the strategy
     */
    public void reset()
    {
        fields.clear();
    }

    /**
     * Adds an extra field to the strategy
     * @param key - The key for the field
     * @param value - The value of the field
     */
    public void addField(String key, Object value)
    {
        fields.put(key, value);
    }
}