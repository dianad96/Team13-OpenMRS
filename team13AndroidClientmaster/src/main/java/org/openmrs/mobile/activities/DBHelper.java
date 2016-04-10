package org.openmrs.mobile.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 08-Apr-16.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "UserData.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "healthData";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_CALORIES_BURNED = "calories_burned";
    public static final String COLUMN_ACTIVE_MINS = "active_mins";
    public static final String COLUMN_HEART_RATE= "heart_rate";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date TEXT," +
            "steps TEXT," +
            "distance TEXT," +
            "floor TEXT," +
            "calories TEXT," +
            "calories_burned TEXT," +
            "active_mins TEXT," +
            "heart_rate TEXT" + ")";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String getSteps(int id){
        id = id + 1; // Database ID doesn't start from 0, it starts from 1.
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_STEPS + " FROM " + TABLE_NAME + " WHERE id = " + id + ";", null);
        if( cursor.moveToFirst() )
            return cursor.getString(0);
        else
            return null;
    }


    public GraphData getHealthData(int id){
        String[] column = { COLUMN_ID, COLUMN_DATE, COLUMN_STEPS, COLUMN_DISTANCE, COLUMN_FLOOR, COLUMN_CALORIES, COLUMN_CALORIES_BURNED, COLUMN_ACTIVE_MINS, COLUMN_HEART_RATE };
        id = id + 1; // Database ID doesn't start from 0, it starts from 1.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = " + id + ";", null);
//        Cursor cursor = db.query(TABLE_NAME, column, COLUMN_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        Log.d("DBHelper", "column count = " + cursor.getColumnCount() + " count = " + cursor.getCount());
        if(cursor.moveToFirst()) {
            Log.d("DBHelper", "column date index = " + cursor.getColumnIndex(COLUMN_DATE) + " column heartRate index = " + cursor.getColumnIndex(COLUMN_HEART_RATE) +
                    " column steps index = " + cursor.getColumnIndex(COLUMN_STEPS) + " column dist index = " + cursor.getColumnIndex(COLUMN_DISTANCE)
                    + " column floor index = " + cursor.getColumnIndex(COLUMN_FLOOR) + " column calories index = " + cursor.getColumnIndex(COLUMN_CALORIES)
                    + " column calories index = " + cursor.getColumnIndex(COLUMN_CALORIES_BURNED) + " column activeMins index = " + cursor.getColumnIndex(COLUMN_ACTIVE_MINS));
            GraphData graphData = new GraphData();
            graphData.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            graphData.setSteps(cursor.getString(cursor.getColumnIndex(COLUMN_STEPS)));
            graphData.setDistance(cursor.getString(cursor.getColumnIndex(COLUMN_DISTANCE)));
            graphData.setFloor(cursor.getString(cursor.getColumnIndex(COLUMN_FLOOR)));
            graphData.setCalories(cursor.getString(cursor.getColumnIndex(COLUMN_CALORIES)));
            graphData.setCaloriesBurned(cursor.getString(cursor.getColumnIndex(COLUMN_CALORIES_BURNED)));
            graphData.setActiveMinutes(cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVE_MINS)));
            graphData.setHeartRate(cursor.getString(cursor.getColumnIndex(COLUMN_HEART_RATE)));

            return graphData;
        }

        return null;
    }

    public boolean insertHealthData(String date, String steps, String distance, String floor, String calories, String caloriesBurned,
                                    String activeMins, String heartRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_STEPS, steps);
        contentValues.put(COLUMN_DISTANCE, distance);
        contentValues.put(COLUMN_FLOOR, floor);
        contentValues.put(COLUMN_CALORIES, calories);
        contentValues.put(COLUMN_CALORIES_BURNED, caloriesBurned);
        contentValues.put(COLUMN_ACTIVE_MINS, activeMins);
        contentValues.put(COLUMN_HEART_RATE, heartRate);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public void updateHealthData (Integer id, String date, String steps, String distance, String floor, String calories, String caloriesBurned,
        String activeMins, String heartRate) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_STEPS, steps);
        contentValues.put(COLUMN_DISTANCE, distance);
        contentValues.put(COLUMN_FLOOR, floor);
        contentValues.put(COLUMN_CALORIES, calories);
        contentValues.put(COLUMN_CALORIES_BURNED, caloriesBurned);
        contentValues.put(COLUMN_ACTIVE_MINS, activeMins);
        contentValues.put(COLUMN_HEART_RATE, heartRate);

        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id+1)});
        db.close();
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public ArrayList<String> getHealthData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> stringArrayList = new ArrayList<String>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DATE  + " FROM " + TABLE_NAME + ";", null);
        if(cursor != null)
            cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            stringArrayList.add(cursor.getString(0));
            cursor.moveToNext();
        }

        return stringArrayList;
    }
}
