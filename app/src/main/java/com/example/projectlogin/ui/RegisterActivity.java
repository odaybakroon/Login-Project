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

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFirstName, edtSecondName, edtEmail, edtPassword;

    private Button btnRegister, btnLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        edtFirstName = findViewById(R.id.edt_firstName);
        edtSecondName = findViewById(R.id.edt_secondName);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void initData() {

    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validate())
                    return;

                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle(R.string.register);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                String url = Common.BASE_URL + Common.ADD_NEW_USER;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                // Display the first 500 characters of the response string.
                                Log.i(Common.TAG, "Register:onResponse => " + response);
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                        .putExtra(Common.EMAIL, edtEmail.getText().toString())
                                        .putExtra(Common.PASSWORD, edtPassword.getText().toString()));
                                finishAffinity();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Common.parseVolleyError(RegisterActivity.this, "Register", error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("firstName", edtFirstName.getText().toString());
                        params.put("secondName", edtSecondName.getText().toString());
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
        edtFirstName.setError(null);
        edtSecondName.setError(null);
        edtEmail.setError(null);
        edtPassword.setError(null);

        if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
            edtFirstName.setError(getString(R.string.this_field_is_required));
            edtFirstName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(edtSecondName.getText().toString())) {
            edtSecondName.setError(getString(R.string.this_field_is_required));
            edtSecondName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(edtEmail.getText().toString())) {
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