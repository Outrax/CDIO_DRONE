package de.yadrone.apps.cdio.gui;

import java.awt.image.BufferedImage;
import java.io.PrintStream;

import de.yadrone.apps.cdio.listeners.ButtonClickListener;
import de.yadrone.apps.cdio.listeners.StatusChangeListener;

public interface IConsole extends StatusChangeListener
{
    /**
     * Shows the display and requests focus from the OS.
     */
    public void display();

    /**
     * Redirects Java system standard and error
     * output to the console.
     */
    public void redirectSystemOutput();

    /**
     * Sets the next image to display in the video feed.
     *
     * @param image
     */
    public void setImage(BufferedImage image);

    /**
     * Forces the console to redraw the next video frame.
     */
    public void repaint();

    /**
     * Prints a message in the console on a new line.
     */
    public void println(String message);

    /**
     * Adds a click listener on the specified button type.
     *
     * @param type     A specific button in the console to listen for click events on.
     * @param listener A listener implementation.
     */
    public void addButtonListener(ButtonType type, ButtonClickListener listener);

    /**
     * @return Returns the underlying output stream used in
     *         the console.
     */
    public PrintStream getOutputStream();

    public void setIsTestingEnabled(boolean state);
}