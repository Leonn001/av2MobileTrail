package com.example.apptrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TrilhasDB extends SQLiteOpenHelper {

    private static final String DATABASE = "trilha_database";
    private static final int VERSION = 1;

    private static final String TABLE_WAYPOINTS = "waypoints";
    private static final String TABLE_TRAILS = "trails";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ALTITUDE = "altitude";

    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_SPEED = "speed";

    public TrilhasDB(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createWaypointsTable = "CREATE TABLE " + TABLE_WAYPOINTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_LATITUDE + " NUMERIC NOT NULL, " +
                COLUMN_LONGITUDE + " NUMERIC NOT NULL, " +
                COLUMN_ALTITUDE + " NUMERIC NOT NULL);";

        String createTrailsTable = "CREATE TABLE " + TABLE_TRAILS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DISTANCE + " REAL NOT NULL, " +
                COLUMN_TIME + " INTEGER NOT NULL, " +
                COLUMN_SPEED + " REAL NOT NULL);";

        db.execSQL(createWaypointsTable);
        db.execSQL(createTrailsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAILS);
        onCreate(db);
    }

    public void registrarWaypoint(Waypoint waypoint) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, waypoint.getLatitude());
        values.put(COLUMN_LONGITUDE, waypoint.getLongitude());
        values.put(COLUMN_ALTITUDE, waypoint.getAltitude());
        getWritableDatabase().insert(TABLE_WAYPOINTS, null, values);
    }

    public ArrayList<Waypoint> recuperarWaypoints() {
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        String[] columns = {COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ALTITUDE};
        Cursor cursor = getWritableDatabase().query(TABLE_WAYPOINTS, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Waypoint waypoint = new Waypoint();
            waypoint.setId(cursor.getLong(0));
            waypoint.setLatitude(cursor.getDouble(1));
            waypoint.setLongitude(cursor.getDouble(2));
            waypoint.setAltitude(cursor.getDouble(3));
            waypoints.add(waypoint);
        }
        cursor.close();
        return waypoints;
    }

    public void apagarTrilha() {
        getWritableDatabase().execSQL("DELETE FROM " + TABLE_WAYPOINTS);
    }

    public void addTrail(String title, double distance, long time, double speed) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DISTANCE, distance);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_SPEED, speed);
        getWritableDatabase().insert(TABLE_TRAILS, null, values);
    }

    public ArrayList<Trail> getAllTrails() {
        ArrayList<Trail> trails = new ArrayList<>();
        String[] columns = {COLUMN_ID, COLUMN_TITLE, COLUMN_DISTANCE, COLUMN_TIME, COLUMN_SPEED};
        Cursor cursor = getWritableDatabase().query(TABLE_TRAILS, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME));
            double speed = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SPEED));
            trails.add(new Trail(id, title, distance, time, speed));
        }
        cursor.close();
        return trails;
    }
}
