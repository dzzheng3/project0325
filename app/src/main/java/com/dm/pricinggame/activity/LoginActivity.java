package com.dm.pricinggame.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dm.pricinggame.R;
import com.dm.pricinggame.activity.helper.Api;
import com.dm.pricinggame.activity.helper.AppText;
import com.dm.pricinggame.activity.helper.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView createAccText;
    private EditText userEmail, userPassword;
    private Button btnLogiin;

    ProgressDialog progressDialog;
    PreferenceHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new PreferenceHelper(LoginActivity.this);
        if (helper.getBoolean(AppText.IS_LOGIN, false)) {
            startActivity(new Intent(LoginActivity.this, GameListActivity.class));
            finish();
        }
        initView();

        progressDialog = new ProgressDialog(LoginActivity.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(LoginActivity.this).cancelAll("login_task");
            }
        });
    }

    private void initView() {
        createAccText = (TextView) findViewById(R.id.link_signup);
        createAccText.setOnClickListener(LoginActivity.this);
        userEmail = (EditText) findViewById(R.id.input_email);
        userPassword = (EditText) findViewById(R.id.input_password);
        btnLogiin = (Button) findViewById(R.id.btn_login);
        btnLogiin.setOnClickListener(LoginActivity.this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.link_signup:
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
                break;
            case R.id.btn_login:
                LoginTask();

<<<<<<< HEAD
                //This should open for while connecting with server
                if (!userEmail.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty()) {
                    String user_email = userEmail.getText().toString();
                    String user_password = userPassword.getText().toString();

                    if (Api.isInNetwork(LoginActivity.this)) {
                        //LoginTask();
                        loginTask(user_email, user_password);
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
=======
>>>>>>> fdc06e79a7dc4fa55f3b6a3e1124aaf949942c7d

                //This should open for while connecting with server
//                if (!userEmail.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty()) {
//                    String user_email = userEmail.getText().toString();
//                    String user_password = userPassword.getText().toString();
//
//                    if (Api.isInNetwork(LoginActivity.this)) {
//                        loginTask(user_email, user_password);
//                    } else {
//                        Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(LoginActivity.this, "Please Enter User and password", Toast.LENGTH_SHORT).show();
//                }

                break;

        }

    }

    private void LoginTask() {
        startActivity(new Intent(this, GameListActivity.class));

    }

    private void loginTask(final String userEmail, final String password) {
        String url = Api.loginUrl+"?email="+userEmail+"&pass="+password;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                com.dm.pricinggame.activity.helper.Logger.e("loginTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {

                        JSONObject userDetail = responseJson.getJSONObject("data");
                        helper.edit().putString(AppText.PLAYER_ID, userDetail.getString("id")).commit();
                        helper.edit().putString(AppText.NAME_PLAYER, userDetail.getString("username")).commit();
                        helper.edit().putString(AppText.EMAIL, userDetail.getString("email")).commit();
                        helper.edit().putString(AppText.ROLE, userDetail.getString("role")).commit();
                        helper.edit().putBoolean(AppText.IS_LOGIN, true).commit();
                        Toast.makeText(LoginActivity.this,"Login sucessfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, GameListActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,"Login Error ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    com.dm.pricinggame.activity.helper.Logger.e("loginTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                com.dm.pricinggame.activity.helper.Logger.e("loginTask error login", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("username", userEmail);
//                map.put("password", password);
//                com.dm.pricinggame.activity.helper.Logger.e("loginTask post params123", userEmail + " : " + password);
//                return map;
//            }
        };

        final RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        request.setTag("login_task");
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                progressDialog.dismiss();
            }
        });
        queue.add(request);

        progressDialog.show();
    }
}
