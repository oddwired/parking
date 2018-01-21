package com.kshem.parking.mpesa;

import static com.kshem.parking.Constants.BASE_URL;

/**
 * Created by kshem on 12/27/17.
 */

public class Config {
    public static final String CONSUMER_KEY = "T5Qn16N54we8zF56cgtHROxpMON5coWX";
    public static final String CONSUMER_SECRET = "yetpjpz2YBFsjSZW";

    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379";
    public static final String CALLBACKURL = BASE_URL + "/result?";

    public static final String TOKEN_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String STKPUSH_PROCESS_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
    public static final String TRANSACTION_DESC = "Parking lot payment";
}
