package utilities;

public class Calculation {
    public static float getCompassRotation(float deviceOrientation) {
        return 360 - (float) Math.toDegrees(deviceOrientation);
    }

    public static float getAngle(double lat1, double long1, double lat2, double long2) {
        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);

        double dLong = (long2 - long1);

        double y = Math.sin(dLong) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLong);
        double angle = Math.atan2(y, x);

        return (float) (Math.toDegrees(angle) + 360) % 360;
    }


}
