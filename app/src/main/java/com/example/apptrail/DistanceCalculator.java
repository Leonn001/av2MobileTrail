package com.example.apptrail;

import android.location.Location;

public class DistanceCalculator {
    private static final int EARTH_RADIUS_METERS = 6371000;

    public static double calculateDistance(double startLatitude, double startLongitude,
                                           double endLatitude, double endLongitude) {
        double dLat = Math.toRadians(endLatitude - startLatitude);
        double dLon = Math.toRadians(endLongitude - startLongitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLatitude)) * Math.cos(Math.toRadians(endLatitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    public static double calculateDistance(Location startLocation, Location endLocation) {
        return calculateDistance(startLocation.getLatitude(), startLocation.getLongitude(),
                endLocation.getLatitude(), endLocation.getLongitude());
    }
}
