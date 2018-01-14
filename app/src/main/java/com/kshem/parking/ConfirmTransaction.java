package com.kshem.parking;

import android.content.Context;
import android.os.AsyncTask;

import com.kshem.parking.mpesa.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kshem.parking.Constants.BASE_URL;

/**
 * Created by kshem on 12/28/17.
 */

public class ConfirmTransaction {
    private OkHttpClient client;
    private boolean status = false;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int transaction_status;

    public ConfirmTransaction(){
        this.client = new OkHttpClient();
    }

    public int confirmTransaction(String merchant_request_id){
        String url_request = BASE_URL + "/confirm_transaction";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("MerchantRequestID", merchant_request_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        final Request request = new Request.Builder()
                .url(url_request)
                .post(requestBody)
                .build();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Response response = client.newCall(request).execute();
                    status = response.isSuccessful();
                    if(status){
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        transaction_status = jsonResponse.getInt("status");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };

        task.execute();

        if (status){
            return transaction_status;
        }else{
            return Constants.TRANSACTION_STATUS_PENDING;
        }
    }
}
