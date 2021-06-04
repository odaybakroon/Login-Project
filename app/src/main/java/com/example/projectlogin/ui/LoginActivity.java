package com.example.projectlogin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;

    private Button btnLogin, btnRegister;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initListener();
    }


    private void initView() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void initData() {
        Common.getToken(LoginActivity.this);
    }

    private void initListener() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validate())
                    return;

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle(R.string.login);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                String url = Common.BASE_URL + Common.LOGIN;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                // Display the first 500 characters of the response string.
                                Log.i(Common.TAG, "login:onResponse => " + response);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .putExtra(Common.EMAIL, edtEmail.getText().toString())
                                        .putExtra(Common.PASSWORD, edtPassword.getText().toString()));
                                finishAffinity();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Common.parseVolleyError(LoginActivity.this, "login", error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", edtEmail.getText().toString());
                        params.put("password", edtPassword.getText().toString());

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
        });
    }

    private boolean validate() {
        edtEmail.setError(null);
        edtPassword.setError(null);

        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError(getString(R.string.this_field_is_required));
            edtEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            edtEmail.setError(getString(R.string.please_enter_a_valid_email));
            edtEmail.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError(getString(R.string.this_field_is_required));
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }
}