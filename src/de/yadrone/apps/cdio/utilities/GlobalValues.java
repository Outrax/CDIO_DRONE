package de.yadrone.apps.cdio.utilities;
public class GlobalValues
{
    public static int nextGateInSequence = 1;
    public static double cameraFocalLength = 525.5;

    public static double getFizzyFactor()
    {
        switch(nextGateInSequence)
        {
            case 1:
            case 2:
                return GlobalConstants.POSITION_FIZZY_FACTOR_100;
            case 3:
            case 4:
                return GlobalConstants.POSITION_FIZZY_FACTOR_90;
            case 5:
            case 6:
                return GlobalConstants.POSITION_FIZZY_FACTOR_80;
            default:
                return GlobalConstants.POSITION_FIZZY_FACTOR_80;
        }
    }
}