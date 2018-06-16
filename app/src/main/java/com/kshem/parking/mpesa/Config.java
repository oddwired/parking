package com.kshem.parking.mpesa;

import static com.kshem.parking.Constants.BASE_URL;

/**
 * Created by kshem on 12/27/17.
 */

public class Config {
    public static final String CONSUMER_KEY = "<your consumer key>";
    public static final String CONSUMER_SECRET = "<your consumer secret>";

    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "<passkey>";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379";
    public static final String CALLBACKURL = BASE_URL + "/result?";

    public static final String TOKEN_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String STKPUSH_PROCESS_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
    public static final String TRANSACTION_DESC = "Parking lot payment";
}
