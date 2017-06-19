package de.yadrone.apps.cdio.listeners;

import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;

public class ErrorListener implements IExceptionListener
{
    @Override
    public void exeptionOccurred(ARDroneException e) {
        System.out.println("ErrorListener");
    }
}