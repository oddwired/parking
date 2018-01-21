package com.kshem.parking;

/**
 * Created by kshem on 12/19/17.
 */

public class Constants {
    public static String CREATE_LOCATIONS_TABLE = "CREATE TABLE locations(" +
            "_id int, name TEXT, coordinates TEXT, rate TEXT, slots Text);";

    public static String DELETE_LOCATIONS_TABLE = "DROP IF EXISTS locations;";

    public static String CAR_PLATE = "car_plate";
    public static String DURATION = "duration";
    public static String LOCATION_ID = "location_id";

    public static int DEFAULT_DURATION = 30;
    public static int SUCCESS_RESULT_CODE = 0;
    public static int FAILED_RESULT_CODE = 1;

    public static String BASE_URL = "https://26d12e50.ngrok.io"; //TODO: Input server url here

    public static int TRANSACTION_STATUS_SUCCESS = 0;
    public static int TRANSACTION_STATUS_FAILURE = 1;
    public static int TRANSACTION_STATUS_PENDING = 2;
}
