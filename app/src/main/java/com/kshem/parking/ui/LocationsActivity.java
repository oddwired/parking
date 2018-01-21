package com.kshem.parking.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kshem.parking.DatabaseHelper;
import com.kshem.parking.R;
import com.kshem.parking.models.LocationModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.kshem.parking.Constants.FAILED_RESULT_CODE;
import static com.kshem.parking.Constants.SUCCESS_RESULT_CODE;

public class LocationsActivity extends AppCompatActivity {

    private EditText search;
    private ListView locations;
    private LocationsAdapter locationsAdapter;
    private ArrayList<LocationModel> locationModels;

    private DatabaseReference mFirebaseRef;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        locationModels = new ArrayList<>();
        locations = (ListView) findViewById(R.id.locations);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRef = mFirebaseInstance.getReference("locations");

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> location_items = (HashMap<String, Object>) dataSnapshot.getValue();

                locationModels.clear();

                for(String key : location_items.keySet()){
                    HashMap<String, Object> locationMap = (HashMap<String, Object>) location_items.get(key);

                    String name = (String) locationMap.remove("name");
                    String rate = (String) locationMap.remove("rate");
                    String coordinates = (String) locationMap.remove("coordinates");
                    String slots = (String) locationMap.remove("slots");
                    //Toast.makeText(LocationsActivity.this, key+name+coordinates+slots+rate, Toast.LENGTH_LONG).show();
                    LocationModel locationModel = new LocationModel(key, name, coordinates, slots,rate);
                    locationModels.add(locationModel);
                    Log.d("Firebase: Locations", key+name+coordinates+slots+rate);
                }

                locationsAdapter = new LocationsAdapter(LocationsActivity.this, locationModels);
                locations.setAdapter(locationsAdapter);

                /*for(Object location_item : location_items.values()){
                    HashMap<String, Object> locationMap = (HashMap<String, Object>) location_item;
                    String id = location_items.
                    String name = (String) locationMap.remove("name");
                    String rate = (String) locationMap.remove("rate");
                    String coordinates = (String) locationMap.remove("coordinate");
                    String slots = (String) locationMap.remove("slots");
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
