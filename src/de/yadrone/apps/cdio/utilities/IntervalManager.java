package de.yadrone.apps.cdio.utilities;

public class IntervalManager
{
    private long startTime = 0;
    private long intervalTime = 0;

    public void start(long nanoTime)
    {
        startTime = System.nanoTime();
        intervalTime = nanoTime;
    }

    public boolean isIntervalReached()
    {
        return startTime + intervalTime < System.nanoTime();
    }

    public boolean isStarted(){
        return startTime > 0;
    }

    public void setIntervalTime(long nanotime)
    {
        this.intervalTime = nanotime;
    }

    public void reset()
    {
        startTime = 0;
        intervalTime = 0;
    }

}