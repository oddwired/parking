package com.kshem.parking;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kshem.parking.models.LocationModel;
import com.kshem.parking.models.SlotModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kshem.parking.Constants.BASE_URL;

/**
 * Created by kshem on 12/27/17.
 */

public class SlotData {
    public interface SlotListener{
        void onSuccess(SlotModel slots);
        void onFailure();
    }

    private Context context;
    private SlotListener listener;
    private OkHttpClient client;
    private boolean status = false;
    private SlotModel slotModel;

    public SlotData(Activity activity){
        this.context = activity;
        this.client = new OkHttpClient();
    }

    public void setListener(SlotListener listener){
        this.listener = listener;
    }

    public void getOccupiedSlots(LocationModel locationModel){
        String url_parameters = "/get_slots?id=" + locationModel.getId(); //TODO: Enter url parameters
        String url_request = BASE_URL + url_parameters;
        Log.d("Location: slots url", url_request);
        final Request request = new Request.Builder().url(url_request).build();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Response response = client.newCall(request).execute();

                    status = response.isSuccessful();

                    if(status){
                        JSONArray jsonArray = new JSONArray(response.body().string());

                        slotModel = new SlotModel(jsonArray);


                    }else{

                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(status){
                    listener.onSuccess(slotModel);
                }else{
                    listener.onFailure();
                }
            }
        };

        task.execute();
    }
}
