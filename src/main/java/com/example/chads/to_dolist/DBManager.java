package com.example.chads.to_dolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by gboursiquot on 2/26/2018.
 */

public class DBManager extends SQLiteOpenHelper {
    //Static Variables

    //Database version, creates a new database when changed
    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "todo_list";
    private static final String TABLE_NAME = "task";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_COMPLETED = "completed";

    public DBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_NAME + " (";
        CREATE_TASK_TABLE += KEY_ID + " INTEGER PRIMARY KEY, ";
        CREATE_TASK_TABLE += KEY_TITLE + " TEXT, ";
        CREATE_TASK_TABLE += KEY_DESCRIPTION + " TEXT, ";
        CREATE_TASK_TABLE += KEY_DATE + " TEXT, ";
        CREATE_TASK_TABLE += KEY_PRIORITY + " TINYINT, ";
        CREATE_TASK_TABLE += KEY_COMPLETED + " BOOLEAN)";

        db.execSQL(CREATE_TASK_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask (Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, (Byte) null);
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_PRIORITY, task.getPriority());
        values.put(KEY_COMPLETED, task.isCompleted());
        Log.d("task created: ", task.toString());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> tasks = new ArrayList<Task>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                Task t = new Task();
                t.setId(Integer.parseInt(cursor.getString(0)));
                t.setTitle(cursor.getString(1));
                t.setDescription(cursor.getString(2));
                t.setDate(cursor.getString(3));
                t.setPriority(Integer.parseInt(cursor.getString(4)));
                t.setCompleted(cursor.getInt(5)>0);

                Log.d("Task #" + cursor.getString(0), t.toString());
                tasks.add(t);
            }while(cursor.moveToNext());
        }

        return tasks;
    }

    public Task getTaskById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_DATE, KEY_PRIORITY, KEY_COMPLETED},
                KEY_ID  + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);

        Task t = new Task();
        Log.d("Test", Integer.toString(cursor.getCount()));

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            t.setId(Integer.parseInt(cursor.getString(0)));
            t.setTitle(cursor.getString(1));
            t.setDescription(cursor.getString(2));
            t.setDate(cursor.getString(3));
            t.setPriority(Integer.parseInt(cursor.getString(4)));
            t.setCompleted(cursor.getInt(5)>0);
        }

        return t;
    }

    public void setPriority(int id, int priority){
        SQLiteDatabase db = this.getWritableDatabase();

        //array to pass to query (associative array)
        ContentValues values = new ContentValues();
        values.put(KEY_PRIORITY, priority);

        //query to update the database
        db.update(
                TABLE_NAME,
                values,
                KEY_ID +" = ? ",
                    new String[]{String.valueOf(id)});
        db.close();
    }

    public void setCompleted(int id, Boolean completed){
        SQLiteDatabase db = this.getWritableDatabase();

        //array to pass to query (associative array)
        ContentValues values = new ContentValues();
        values.put(KEY_COMPLETED, completed);

        //query to update the database
        db.update(
                TABLE_NAME,
                values,
                KEY_ID +" = ? ",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d("setCompleted: ", completed.toString());
    }

    public void deleteTask (Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        //query to delete from database
        db.delete(
                TABLE_NAME,
                KEY_ID +" = ? ",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }
}
