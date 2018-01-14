package com.kshem.parking.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kshem on 12/19/17.
 */

public class LocationModel {
    private String id;
    private String name;
    private String coordinates;
    private double latitude;
    private double longitude;
    private String slots;
    private String rate;

    public LocationModel(String id, String name, String coordinates,
                         String slots,
                         String rate){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.slots = slots;
        this.rate = rate;

        List<String> values = Arrays.asList(coordinates.split(","));
        double latitude = Double.valueOf(values.get(0));
        double longitude = Double.valueOf(values.get(1));

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationModel(JSONObject data){
        try {
            this.id = data.getString("id");
            this.name = data.getString("name");
            this.coordinates = data.getString("coordinates");
            this.rate = data.getString("rate");
            this.slots = data.getString("slots");

            List<String> values = Arrays.asList(coordinates.split(","));
            double latitude = Double.valueOf(values.get(0));
            double longitude = Double.valueOf(values.get(1));

            this.latitude = latitude;
            this.longitude = longitude;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String num_slots) {
        this.slots = num_slots;
    }
}
