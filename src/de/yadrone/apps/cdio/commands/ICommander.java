package de.yadrone.apps.cdio.commands;
import de.yadrone.base.IARDrone;

public interface ICommander
{
    public boolean isExecutingCommand();

    public boolean isTesting();

    public void setTesting(boolean isTesting);

    public void reset();

    public void takeoff();

    public void landing();

    public void forward(int speed, int duration);

    public void backwards(int speed, int duration);

    public void left(int speed, int duration);

    public void right(int speed, int duration);

    public void up(int speed, int duration);

    public void down(int speed, int duration);

    public void spinLeft(int speed, int duration);

    public void spinRight(int speed, int duration);

    public void hover(int duration);

    public IARDrone getDrone();

    public void calibrateMagnetometer();
}