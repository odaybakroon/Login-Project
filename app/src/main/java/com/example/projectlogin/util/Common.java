package com.example.projectlogin.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Common {
    public static final String BASE_URL = "https://mcc-users-api.herokuapp.com/";
    public static final String ADD_NEW_USER = "add_new_user";
    public static final String LOGIN = "login";
    public static final String ADD_REG_TOKEN = "add_reg_token";
    public static final String TAG = "ttt";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static String FCM_TOKEN;


    public static void parseVolleyError(Context context, String requestName, VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            JSONObject res = new JSONObject(responseBody);
            int statusCode = res.getInt("statusCode");
            String msg = res.getString("msg");
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            Log.e(Common.TAG, requestName + ":onErrorResponse => statusCode = " + statusCode + ", msg = " + msg);

        } catch (JSONException ignored) {
        }
    }


    public static void getToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(Common.TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.i(Common.TAG, "Fetching FCM registration token success" + token);
                    Common.FCM_TOKEN = token;
                });
    }
}
