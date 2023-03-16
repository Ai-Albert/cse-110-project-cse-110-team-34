package utilities;

import java.util.Random;

public class Calculation {
    public static float getCompassRotation(float deviceOrientation) {
        return 360 - (float) Math.toDegrees(deviceOrientation);
    }

    public static float getAngle(double lat1, double long1, double lat2, double long2) {
        double dLat = lat2 - lat1;
        double dLng = long1 - long2;

        double radsFromX = Math.atan2(dLat, dLng);
        double degsFromN = Math.toDegrees(radsFromX) - 90;

        return (float) degsFromN;
    }

    public static float getDistance(double lat1, double long1, double lat2, double long2) {
        double dLat = lat2 - lat1;
        double dLng = long1 - long2;
        return (float) Math.sqrt(dLat * dLat + dLng * dLng) * 69;
    }
}
