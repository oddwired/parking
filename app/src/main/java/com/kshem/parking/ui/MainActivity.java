package com.kshem.parking.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kshem.parking.ConfirmTransaction;
import com.kshem.parking.Constants;
import com.kshem.parking.Duration;
import com.kshem.parking.ParkingLotData;
import com.kshem.parking.R;
import com.kshem.parking.SlotData;
import com.kshem.parking.models.LocationModel;
import com.kshem.parking.models.SlotModel;
import com.kshem.parking.mpesa.APIRequest;

import java.util.ArrayList;
import java.util.Calendar;

import static com.kshem.parking.Constants.CAR_PLATE;
import static com.kshem.parking.Constants.DEFAULT_DURATION;
import static com.kshem.parking.Constants.DURATION;
import static com.kshem.parking.Constants.LOCATION_ID;
import static com.kshem.parking.Constants.SUCCESS_RESULT_CODE;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private TextView duration, end_time, cost;
    private TextView parking_lot, car_details, info;
    private EditText inputPhone;
    private LinearLayout select_parking_lot, enter_details;
    private Button next;
    private ImageView subtract, add;
    private Duration mDuration;
    private String car_plate;
    private String location_id;
    private int rate = 0;
    private LocationModel locationModel;

    private SharedPreferences sp;
    private ProgressDialog pd;

    private final int LOCATION_REQUEST = 1;
    private final int DETAILS_REQUEST = 2;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-1.286056, 36.826042);

    private final String TAG = "MAPS:";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;

    private ParkingLotData parkingLotData;

    private APIRequest apiRequest;
    private int transaction_status = 2;
    private String merchant_id = "null";

    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ConfirmTransaction confirmTransaction = new ConfirmTransaction();
            transaction_status = confirmTransaction.confirmTransaction(merchant_id);
            if(transaction_status == Constants.TRANSACTION_STATUS_PENDING){
                handler.postDelayed(runnable, 3000);
            }else if(transaction_status == Constants.TRANSACTION_STATUS_FAILURE){
                showProcessFailure();
            }else if(transaction_status == Constants.TRANSACTION_STATUS_SUCCESS){
                completeProcess();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        duration = (TextView) findViewById(R.id.txtDuration);
        end_time = (TextView) findViewById(R.id.txtEndTime);
        cost = (TextView) findViewById(R.id.txtCost);
        parking_lot = (TextView) findViewById(R.id.txtLocation);
        car_details = (TextView) findViewById(R.id.txtCar_details);
        info = (TextView) findViewById(R.id.info);
        select_parking_lot = (LinearLayout) findViewById(R.id.choose_location);
        enter_details = (LinearLayout) findViewById(R.id.enter_car_details);
        subtract = (ImageView) findViewById(R.id.subtract);
        add = (ImageView) findViewById(R.id.add);
        next = (Button) findViewById(R.id.next);
        inputPhone = (EditText) findViewById(R.id.phone);

        pd = new ProgressDialog(MainActivity.this);
        handler = new Handler();

        getAccessToken();

        //getParkingLots();

        mDuration = new Duration();

        initializeSharedPreferences();

        duration.setText(mDuration.getDuration());

        next.setEnabled(false);
        enter_details.setClickable(false);

        select_parking_lot.setClickable(true);
        select_parking_lot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationsActivity.class);
                startActivityForResult(intent, LOCATION_REQUEST);
            }
        });


        enter_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarDetailsActivity.class);
                startActivityForResult(intent, DETAILS_REQUEST);
            }
        });

        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDuration.getHours() == 0 && mDuration.getMinutes() <= 30){
                    //Do nothing
                }else{
                    mDuration.addMinutes(-30);
                    updateCost();
                }
                duration.setText(mDuration.getDuration());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDuration.getHours() == 24){
                    //Do nothing
                }else{
                    mDuration.addMinutes(30);
                    updateCost();
                }
                duration.setText(mDuration.getDuration());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*pd.setMessage("Confirming availability of parking slots...");
                pd.show();
                SlotData slotData = new SlotData(MainActivity.this);
                slotData.setListener(new SlotData.SlotListener() {
                    @Override
                    public void onSuccess(SlotModel slots) {
                        pd.dismiss();
                        if(slots.getSlots().size() == 0){
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("There are no available parking slots at the moment." +
                                            "Please try again later or find another parking lot.")
                                    .setCancelable(false)
                                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }else{
                            if(TextUtils.isEmpty(inputPhone.getText())){
                                inputPhone.setError("Required");
                            }else{
                                apiRequest.setRequestListener(new APIRequest.APIRequestListener() {
                                    @Override
                                    public void onSuccess(String merchant_id) {
                                        pd.setMessage("Confirming transaction. Please wait...");
                                        pd.show();

                                        MainActivity.this.merchant_id = merchant_id;
                                        handler.post(runnable);

                                    }

                                    @Override
                                    public void onFailure() {
                                        showProcessFailure();
                                    }
                                });
                                int amount = mDuration.getDurationInMinutes() * Integer.parseInt(locationModel.getRate());
                                String access_token = sp.getString("access_token", "");
                                String phone = inputPhone.getText().toString();
                                apiRequest.STKPush(String.valueOf(amount), access_token, phone);

                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        pd.dismiss();
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Network connection error. Please try again!")
                                .setCancelable(true)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });

                slotData.getOccupiedSlots(locationModel);*/

            }
        });
    }

    private void initializeSharedPreferences(){
        sp = getSharedPreferences(getString(R.string.sp_name), Context.MODE_PRIVATE);

        if(!sp.contains(Constants.CAR_PLATE)){
            mDuration.addMinutes(DEFAULT_DURATION);
        }else{
            mDuration.addMinutes(sp.getInt(DURATION, DEFAULT_DURATION));
            car_plate = sp.getString(CAR_PLATE, "");
            location_id = sp.getString(LOCATION_ID, "");
        }
    }

    private void getParkingLots(){
        parkingLotData = new ParkingLotData(this);
        parkingLotData.setListener(new ParkingLotData.NewDataListener() {
            @Override
            public void onReceive() {

            }

            @Override
            public void onFailure() {
                new AlertDialog.Builder(MainActivity.this).setMessage("Unable to fetch data")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(true).show();
            }
        });

        parkingLotData.getParkingLots();
    }
    private void getAccessToken(){
        apiRequest = new APIRequest(MainActivity.this);
        apiRequest.setAccessTokenListener(new APIRequest.AccessTokenListener() {
            @Override
            public void onReceive(String accessToken, String expiry) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, Integer.parseInt(expiry));

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("access_token", accessToken);
                editor.putString("expires", calendar.getTime().toString());
                editor.apply();
            }

            @Override
            public void onFailure() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Please make sure you have data connection!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                apiRequest.fetchAccessToken();
                            }
                        }).setCancelable(false).show();
            }
        });

        apiRequest.fetchAccessToken();
    }

    private void completeProcess(){
        pd.dismiss();
        //TODO: DO completion of the booking process
    }

    private void showProcessFailure(){
        pd.dismiss();
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("An error occurred while processing your request. Please try again.")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOCATION_REQUEST:
                if(SUCCESS_RESULT_CODE != resultCode){

                }else{
                    String id = data.getStringExtra("location_id");
                    String name = data.getStringExtra("location_name");
                    String coordinates = data.getStringExtra("location_coordinates");
                    String rate = data.getStringExtra("location_rate");
                    String slots = data.getStringExtra("slots");
                    this.rate = Integer.parseInt(rate);

                    LocationModel locationModel = new LocationModel(id, name, coordinates, slots, rate);
                    this.locationModel = locationModel;

                    LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                    updateCost();
                    parking_lot.setText(name);

                    enter_details.setClickable(true);
                }
                break;
            case DETAILS_REQUEST:
                if(SUCCESS_RESULT_CODE != resultCode){

                }else{
                    car_plate = data.getStringExtra("car_plate");
                    car_details.setText(car_plate);

                    next.setEnabled(true);
                }
                break;
        }
    }

    private void updateCost(){
        int total_cost = mDuration.getDurationInMinutes() * rate;
        cost.setText(String.valueOf(total_cost));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-1.286056, 36.826042);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void getDeviceLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            LatLng current_position = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_position, DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
