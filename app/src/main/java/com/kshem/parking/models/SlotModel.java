package com.kshem.parking.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kshem on 12/27/17.
 */

public class SlotModel {
    private ArrayList<Integer> slots;

    public SlotModel(ArrayList<Integer> slots){
        this.slots = slots;
    }

    public SlotModel(JSONArray data){
        slots = new ArrayList<>();
        try {
            for(int i = 0; i < data.length(); i++){
                slots.add(data.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getSlots() {
        return slots;
    }
}
