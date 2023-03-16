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

    public static String getRandomUID(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
