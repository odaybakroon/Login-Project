package com.example.projectlogin.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectlogin.R;
import com.example.projectlogin.util.Common;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {
        if (getIntent() == null || !getIntent().hasExtra(Common.EMAIL) || !getIntent().hasExtra(Common.PASSWORD)) {
            finish();
            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(R.string.add_update_registration_token);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = Common.BASE_URL + Common.ADD_REG_TOKEN;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        // Display the first 500 characters of the response string.
                        Log.i(Common.TAG, "add_reg_token:onResponse => " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Common.parseVolleyError(MainActivity.this, "add_reg_token", error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", getIntent().getStringExtra(Common.EMAIL));
                params.put("password", getIntent().getStringExtra(Common.PASSWORD));
                params.put("reg_token", Common.FCM_TOKEN);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void initListener() {

    }
}