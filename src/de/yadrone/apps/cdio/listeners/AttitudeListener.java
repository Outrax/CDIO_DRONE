package de.yadrone.apps.cdio.listeners;

import de.yadrone.apps.cdio.gui.Controller;

public class AttitudeListener implements de.yadrone.base.navdata.AttitudeListener
{
//    private final Controller controller;

    public AttitudeListener(Controller controller) {
//        this.controller = controller;
    }
    
    @Override
    public void attitudeUpdated(float pitch, float roll, float yaw) {
//        controller.updateNavigationDisplay(pitch, roll, yaw);
    }
    
    @Override
    public void attitudeUpdated(float pitch, float roll) { }
    
    @Override
    public void windCompensation(float pitch, float roll) { }
}