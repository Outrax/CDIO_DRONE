package de.yadrone.apps.cdio.listeners;

public interface StatusChangeListener
{
    public void onBatteryChange(int percentage);

    public void onAltitudeChange(int altitude);

    public void onStrategyChange(String name);
}