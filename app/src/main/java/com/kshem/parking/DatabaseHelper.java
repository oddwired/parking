package com.kshem.parking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

/**
 * Created by kshem on 10/24/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH="/data/data/com.kshem.parking/databases/";
    private static String DB_NAME = "parking.db";
    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
        //DB_PATH = myContext.getFilesDir().getPath() + "/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() throws SQLiteException {

        SQLiteDatabase checkDB = null;

        String myPath = DB_PATH + DB_NAME;

        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch (SQLiteException sqle){
            //File databaseFile = new File(myPath);
            //checkDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
            //this.getReadableDatabase();
        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    public void openDataBase() throws SQLiteException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;

        try {
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteException sql){

            throw new Error("Unable to open database");

        }

    }

    public SQLiteDatabase getMyDataBase(){
        return myDataBase;
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.DELETE_LOCATIONS_TABLE);
        onCreate(db);
    }
}
