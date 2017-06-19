package de.yadrone.apps.cdio.listeners;

import de.yadrone.apps.cdio.gui.Controller;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GUIWindowListener extends WindowAdapter
{
    private final Controller controller;

    public GUIWindowListener(Controller controller) {
        this.controller = controller;
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("\n--- GUI Closed ---");
        controller.getDrone().stop();
        System.exit(0);
    }
}