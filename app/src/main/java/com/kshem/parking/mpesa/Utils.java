package com.kshem.parking.mpesa;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kshem on 12/27/17.
 */

public class Utils {

    public static String getTimestamp(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return timeStamp;
    }
    public static  String sanitizePhoneNumber(String phone) {

        if(phone.equals("")){
            return "";
        }

        if (phone.length() < 11 & phone.startsWith("0")) {
            String p = phone.replaceFirst("^0", "254");
            return p;
        }
        if(phone.length() == 13 && phone.startsWith("+")){
            String p = phone.replaceFirst("^+", "");
            return p;
        }
        return phone;
    }
    public static String getPassword(String businessShortCode, String passkey, String timestamp){
        String str = businessShortCode + passkey + timestamp;
        //encode the password to Base64
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }
}
