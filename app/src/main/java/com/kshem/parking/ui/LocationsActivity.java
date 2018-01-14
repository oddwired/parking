package com.kshem.parking.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kshem.parking.DatabaseHelper;
import com.kshem.parking.R;
import com.kshem.parking.models.LocationModel;

import java.io.IOException;
import java.util.ArrayList;

import static com.kshem.parking.Constants.FAILED_RESULT_CODE;
import static com.kshem.parking.Constants.SUCCESS_RESULT_CODE;

public class LocationsActivity extends AppCompatActivity {

    private EditText search;
    private ListView locations;
    private SQLiteDatabase parkingDatabase;
    private DatabaseHelper sqLiteOpenHelper;
    private LocationsAdapter locationsAdapter;
    private ArrayList<LocationModel> locationModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        locationModels = new ArrayList<>();
        locations = (ListView) findViewById(R.id.locations);

        sqLiteOpenHelper = new DatabaseHelper(this);

        try {
            sqLiteOpenHelper.createDataBase();
        }catch (IOException ioe){
            throw new Error("Unable to create database");
        }

        try{
            sqLiteOpenHelper.openDataBase();
        }catch (SQLiteException sqle){
            throw new Error(sqle.getMessage());
        }

        parkingDatabase = sqLiteOpenHelper.getMyDataBase();

        Cursor data = parkingDatabase.rawQuery("SELECT * FROM locations", null);
        while(data.moveToNext()){
            String id = String.valueOf(data.getInt(0));
            String name = data.getString(1);
            String coordinates = data.getString(2);
            String rate = data.getString(3);
            String slots = data.getString(4);

            LocationModel locationModel = new LocationModel(id, name, coordinates, slots,rate);
            locationModels.add(locationModel);

        }

        parkingDatabase.close();
        sqLiteOpenHelper.close();

        locationsAdapter = new LocationsAdapter(this, locationModels);
        locations.setAdapter(locationsAdapter);

        locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationModel locationModel = locationModels.get(position);

                getIntent().putExtra("location_id", locationModel.getId());
                getIntent().putExtra("location_name", locationModel.getName());
                getIntent().putExtra("location_coordinates", locationModel.getCoordinates());
                getIntent().putExtra("slots", locationModel.getSlots());
                getIntent().putExtra("location_rate", locationModel.getRate());

                setResult(SUCCESS_RESULT_CODE, getIntent());
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(FAILED_RESULT_CODE);
        super.onBackPressed();
    }
}
