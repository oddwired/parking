package com.kshem.parking;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import com.kshem.parking.models.LocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kshem.parking.Constants.BASE_URL;

/**
 * Created by kshem on 12/20/17.
 */

public class ParkingLotData {
    public interface NewDataListener{
        void onReceive();
        void onFailure();
    }

    private Context context;
    private NewDataListener listener;
    private OkHttpClient client;
    private ArrayList<LocationModel> locationModels;
    private boolean status = false;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase localDb;

    public ParkingLotData(Activity activity){
        this.context = activity;
        this.client = new OkHttpClient();
        this.locationModels = new ArrayList<>();
    }

    public void setListener(NewDataListener listener){
        this.listener = listener;
    }

    public void getParkingLots(){
        String url_parameters = "/get_locations"; //TODO: Enter url parameters
        String url_request = BASE_URL + url_parameters;

        final Request request = new Request.Builder().url(url_request).build();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Response response = client.newCall(request).execute();

                    status = response.isSuccessful();

                    if(status){
                        JSONArray jsonArray = new JSONArray(response.body().string());

                        for(int i = 0; i < jsonArray.length(); i++){
                            LocationModel locationModel = new LocationModel(jsonArray.getJSONObject(i));
                            locationModels.add(locationModel);

                        }
                        updateDatabase();
                        listener.onReceive();
                    }else{
                        listener.onFailure();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        task.execute();
    }

    private void updateDatabase(){
        databaseHelper = new DatabaseHelper(context);

        try {
            databaseHelper.createDataBase();
        }catch (IOException ioe){
            throw new Error("Unable to create database");
        }

        try{
            databaseHelper.openDataBase();
        }catch (SQLiteException sqle){
            throw new Error(sqle.getMessage());
        }

        localDb = databaseHelper.getMyDataBase();

        localDb.execSQL("DELETE FROM locations WHERE 1;");

        for(LocationModel locationModel : locationModels){
            ContentValues values = new ContentValues();
            values.put("_id", locationModel.getId());
            values.put("name", locationModel.getName());
            values.put("coordinates", locationModel.getCoordinates());
            values.put("rate", locationModel.getRate());

            long success = localDb.insert("locations", null, values);

            if(success == -1){
                throw new Error("Unable to update database");
            }
        }

        localDb.close();
        databaseHelper.close();
    }
}
