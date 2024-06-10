package com.example.apptrail;

public class Trail {
    private int id;
    private String title;
    private double distance;
    private long time;
    private double speed;

    public Trail(int id, String title, double distance, long time, double speed) {
        this.id = id;
        this.title = title;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getDistance() { return distance; }
    public long getTime() { return time; }
    public double getSpeed() { return speed; }
}
