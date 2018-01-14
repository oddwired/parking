package com.kshem.parking.mpesa;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kshem on 12/27/17.
 */

public class APIRequest {
    public interface APIRequestListener{
        void onSuccess(String merchant_id);
        void onFailure();
    }

    public interface AccessTokenListener{
        void onReceive(String accessToken, String expiry);
        void onFailure();
    }


    private String password;
    private String timestamp;
    private String phone;
    private String accountReference;

    private Context context;
    private OkHttpClient client;
    private APIRequestListener requestListener;
    private AccessTokenListener accessTokenListener;
    private String access_token;
    private String expires_in;
    private boolean status = false;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String merchant_request_id;
    private String checkout_id;
    private String response_code;

    public APIRequest(Activity activity){
        this.context = activity;
        this.client = new OkHttpClient();
        this.timestamp = Utils.getTimestamp();
        this.password = Utils.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, this.timestamp );
    }

    public void setRequestListener(APIRequestListener listener){
        this.requestListener = listener;
    }

    public void setAccessTokenListener(AccessTokenListener listener){
        this.accessTokenListener = listener;
    }

    public void fetchAccessToken(){
        String appKeySecret = Config.CONSUMER_KEY + ":" + Config.CONSUMER_SECRET;
        try {
            byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
            String auth = Base64.encodeToString(bytes, Base64.NO_WRAP);

            final Request request = new Request.Builder()
                    .url(Config.TOKEN_URL)
                    .addHeader("authorization", "Basic " + auth)
                    .addHeader("Accept", "application/json")
                    .build();

            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        Response response = client.newCall(request).execute();

                        status = response.isSuccessful();
                        if(status){
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            access_token = jsonObject.getString("access_token");
                            expires_in = jsonObject.getString("expires_in");
                        }else{
                            //TODO: Retry fetching the access key
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(status){
                        accessTokenListener.onReceive(access_token, expires_in);
                    }else{
                        accessTokenListener.onFailure();
                    }

                }
            };

            task.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void STKPush(String amount, String accessToken, String phone){
        this.phone = Utils.sanitizePhoneNumber(phone);
        JSONObject requestParameters = new JSONObject();
        String response_parameters = "phone=" + phone;

        try {
            requestParameters.put("BusinessShortCode", Config.BUSINESS_SHORT_CODE);
            requestParameters.put("Password",this.password);
            requestParameters.put("Timestamp",this.timestamp);
            requestParameters.put("TransactionType",Config.TRANSACTION_TYPE);
            requestParameters.put("Amount", amount);
            requestParameters.put("PartyA", this.phone);
            requestParameters.put("PartyB",Config.BUSINESS_SHORT_CODE);
            requestParameters.put("PhoneNumber",this.phone);
            requestParameters.put("CallBackURL", Config.CALLBACKURL + response_parameters);
            requestParameters.put("AccountReference", this.timestamp);
            requestParameters.put("TransactionDesc", Config.TRANSACTION_DESC);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, requestParameters.toString());

        final Request request = new Request.Builder().addHeader("authorization", "Bearer "+ accessToken)
                .addHeader("content-type", "application/json")
                .post(requestBody)
                .url(Config.STKPUSH_PROCESS_URL)
                .build();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Response response = client.newCall(request).execute();
                    status = response.isSuccessful();

                    if(status){
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        merchant_request_id = jsonObject.getString("MerchantRequestID");
                        checkout_id = jsonObject.getString("CheckoutRequestID");
                        response_code = jsonObject.getString("ResponseCode");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(status && Integer.parseInt(response_code) == 0){
                    requestListener.onSuccess(merchant_request_id);
                }else{
                    requestListener.onFailure();
                }
            }
        };

        task.execute();
    }

    private void allocate(){

    }

}
