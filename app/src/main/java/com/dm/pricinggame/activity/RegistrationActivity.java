package com.dm.pricinggame.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.dm.pricinggame.activity.helper.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView createAccText;
    private PreferenceHelper helper;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        helper = new PreferenceHelper(RegistrationActivity.this);
        initView();

        progressDialog = new ProgressDialog(RegistrationActivity.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(RegistrationActivity.this).cancelAll("RegisterTask");
            }
        });
    }

    private void initView() {
        createAccText = (TextView) findViewById(R.id.link_login);
        createAccText.setOnClickListener(RegistrationActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link_login:
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
                break;

        }
    }

    private void RegisterTask(final String name,final String userEmail, final String password) {
        String url = Api.loginUrl;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                com.dm.pricinggame.activity.helper.Logger.e("RegisterTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {

/*                        JSONObject userDetail = responseJson.getJSONObject("user_data");
                        helper.edit().putString(AppText.PLAYER_ID, userDetail.getString("member_id")).commit();
                        helper.edit().putString(AppText.NAME_PLAYER, userDetail.getString("Name")).commit();
                        helper.edit().putString(AppText.EMAIL, userDetail.getString("email")).commit();
                        helper.edit().putString(AppText.PASSWORD, userDetail.getString("Password")).commit();
                        helper.edit().putBoolean(AppText.IS_LOGIN, true).commit();*/
                        Toast.makeText(RegistrationActivity.this, responseJson.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, responseJson.getString("message") + "", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    com.dm.pricinggame.activity.helper.Logger.e("RegisterTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                com.dm.pricinggame.activity.helper.Logger.e("RegisterTask error login", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("email",userEmail);
                map.put("password", password);
                com.dm.pricinggame.activity.helper.Logger.e("RegisterTask post params123", name + " : " + password);
                return map;
            }
        };

        final RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
        request.setTag("RegisterTask");
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
