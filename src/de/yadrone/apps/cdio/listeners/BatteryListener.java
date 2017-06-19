package de.yadrone.apps.cdio.listeners;

import de.yadrone.apps.cdio.gui.Controller;

public class BatteryListener implements de.yadrone.base.navdata.BatteryListener
{
    private final Controller controller;

    public BatteryListener(Controller controller) {
        this.controller = controller;
    }
    
    @Override
    public void batteryLevelChanged(int i) {
        controller.updateBatteryDisplay(i);
    }

    @Override
    public void voltageChanged(int i) { }
}