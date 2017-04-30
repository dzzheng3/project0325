package com.dm.pricinggame.decisiongame;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.dm.pricinggame.activity.helper.Logger;
import com.dm.pricinggame.activity.helper.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DecisionGameActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.btn_yes)
    Button btnYes;
    @BindView(R.id.btn_no)
    Button btnNo;
    @BindView(R.id.tv_result)
    TextView tvResult;
    private int initialValue = 50;
    int value;
    int nextValue;
    boolean check = false;

    /*Variable declaration*/
    int lastPaid = 0;
    int CurrentValue;
    int lastNotPaid;
    private ProgressDialog progressDialog;
    private PreferenceHelper helper;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_game);
        ButterKnife.bind(this);
        helper = new PreferenceHelper(DecisionGameActivity.this);

        //Data is coming from  GameList Activity
        Intent intent = getIntent();
        if (intent != null) {
            Logger.e("detail intent", " not null");
            gameId = intent.getIntExtra(AppText.GAME_ID, 0);
            Logger.e("detail intent", "" + gameId);
        }
        if (Api.isInNetwork(DecisionGameActivity.this)) {
            DecisionGInitTask(String.valueOf(gameId));
        } else {
            Toast.makeText(DecisionGameActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick({R.id.btn_yes, R.id.btn_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                lastPaid = CurrentValue;
                CurrentValue = (CurrentValue + lastNotPaid) / 2;
                tvQuestion.setText("Do you want to pay $" + CurrentValue + " for this goods");
                break;
            case R.id.btn_no:
                lastNotPaid = CurrentValue;
                CurrentValue = (CurrentValue + lastPaid) / 2;
                tvQuestion.setText("Do you want to pay $" + CurrentValue + " for this goods");
                break;
        }
        if((lastNotPaid-lastPaid)<=1){
            tvResult.setText(String.valueOf(CurrentValue));
            if (Api.isInNetwork(DecisionGameActivity.this)) {
                DecisionSubmitTask(String.valueOf(CurrentValue),helper.getString(AppText.PLAYER_ID,"N/A"));
            } else {
                Toast.makeText(DecisionGameActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ShowDialogue(String mes, final String auth) {
        progressDialog = new ProgressDialog(DecisionGameActivity.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(mes);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(DecisionGameActivity.this).cancelAll(auth);
            }
        });
    }

    /**
     * Game Initilization  Task
     */
    private void DecisionGInitTask(final String gameId) {
        ShowDialogue("Loading Gmae...,", "DecisionGInitTask");
        String url = Api.decisionGameUrl + "?game_id="+gameId;//?user_id=" + user_id + "&game_id=" + gameId;//&created_at="+getTimeDate();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("DecisionGInitTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        JSONObject userDetail = responseJson.getJSONObject("data");
                        /*
                          {"status":"success","data":{"item":"beer","value":"50"}}
                        * */
                        CurrentValue = Integer.parseInt(userDetail.getString("value"));
                        lastNotPaid = 2 * CurrentValue;
                        tvQuestion.setText("Do you want to pay $" + CurrentValue + " for this "+userDetail.getString("item"));
//                        helper.edit().putString(AppText.PLAYER_RE_GAME_ID, userDetail.getString("id")).commit();
                        Toast.makeText(DecisionGameActivity.this, "Game Inialized sucessfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DecisionGameActivity.this, "Game Inialization error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("DecisionGInitTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("DecisionGInitTask error", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("user_id", user_id);
//                map.put("game_id", gameId);
//                return map;
//            }
        };

        final RequestQueue queue = Volley.newRequestQueue(DecisionGameActivity.this);
        request.setTag("DecisionGInitTask");
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

    /**
     * Game Final value Submission  Task
     */
    private void DecisionSubmitTask(final String score, final String user_id) {
        ShowDialogue("Loading Gmae...,", "DecisionSubmitTask");
        String url = Api.submitDecisionValue;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("DecisionSubmitTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(DecisionGameActivity.this, "Final price Submiteed sucessfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DecisionGameActivity.this, "Final price not Submiteed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("DecisionGInitTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("DecisionGInitTask error", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(DecisionGameActivity.this, DecisionGameActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", user_id);
                map.put("user_input", score);
                return map;
            }
        };

        final RequestQueue queue = Volley.newRequestQueue(DecisionGameActivity.this);
        request.setTag("DecisionGInitTask");
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
