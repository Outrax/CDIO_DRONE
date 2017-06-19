package de.yadrone.apps.cdio.opencv;

public class CircleData
{
   private double x;
   private double y;
   private double radius;

   private double distance;

    public CircleData(double[] data, double distance)
    {
        this.x      = data[0];
        this.y      = data[1];
        this.radius = data[2];

        this.distance = distance;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getRadius()
    {
        return radius;
    }

    public double getDiameter()
    {
        return this.radius * 2;
    }

    public double getDistance()
    {
        return this.distance;
    }
}