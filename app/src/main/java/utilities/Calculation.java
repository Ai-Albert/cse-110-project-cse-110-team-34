package utilities;

import java.util.Random;

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
