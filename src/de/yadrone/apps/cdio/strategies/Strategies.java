package de.yadrone.apps.cdio.strategies;

public enum Strategies
{
    TAKEOFF,
    SEARCH,
    POSITION,
    VERIFY,
    PENETRATE,
    LANDING,
    EMERGENCY_LANDING,
    IDLE,


    CALIBRATE_CAMERA,
    CALIBRATE_MAGNETOMETER,


    TESTING;

    @Override
    public String toString() {
        switch(this) {
            case TAKEOFF: return "TAKEOFF";
            case SEARCH: return "SEARCH";
            case POSITION: return "POSITION";
            case VERIFY: return "VERIFY";
            case PENETRATE: return "PENETRATE";
            case LANDING: return "LANDING";
            case EMERGENCY_LANDING: return "EMERGENCY_LANDING";
            case IDLE: return "IDLE";
            case CALIBRATE_CAMERA: return "CALIBRATE_CAMERA";
            case CALIBRATE_MAGNETOMETER: return "CALIBRATE_MAGNETOMETER";
            case TESTING: return "TESTING";
            default: return "UNKNOWN";
        }
    }
}