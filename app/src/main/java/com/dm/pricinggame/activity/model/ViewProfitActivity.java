package com.dm.pricinggame.activity.model;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
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
import com.dm.pricinggame.adapter.HistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewProfitActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.p_load_history)
    ProgressBar pLoadHistory;
    @BindView(R.id.tv_error_message)
    TextView errorText;
    private ProgressDialog progressDialog;
    private ArrayList<HistoryModel> hisToryItem;
    private RecyclerView.Adapter adapter;
    private PreferenceHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profit);
        helper = new PreferenceHelper(ViewProfitActivity.this);
        hisToryItem = new ArrayList<>();
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewProfitActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        callViewProfitTask();
    }

    private void ShowDialogue(String mes, final String auth) {
        progressDialog = new ProgressDialog(ViewProfitActivity.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(mes);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(ViewProfitActivity.this).cancelAll(auth);
            }
        });
    }

    private void setViewProfitRecycler(ArrayList<HistoryModel> hisToryItem) {
        adapter = new HistoryAdapter(ViewProfitActivity.this, hisToryItem);
        //recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }

    private void callViewProfitTask() {
        if (Api.isInNetwork(ViewProfitActivity.this)) {
            Logger.e("player_RE_game_Id",helper.getString(AppText.PLAYER_RE_GAME_ID,"N/A"));
            ViewProfitTask(helper.getString(AppText.PLAYER_RE_GAME_ID,"N/A"),helper.getString(AppText.PLAYER_ID,"N/A"));
        } else {
            progressDialog.dismiss();
            recyclerView.setVisibility(View.GONE);
            errorText.setText(ViewProfitActivity.this.getString(R.string.error_no_internet));
            errorText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Game View Profit Task
     */
    private void ViewProfitTask(final String playerGameId, final String user_id) {
        ShowDialogue("Submitting value...,", "ViewProfitTask");
        Logger.e("gameId",""+playerGameId);
        String url = Api.submitValueUrl + "?user_id=" + user_id + "&player_game_id=" + playerGameId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("ViewProfitTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        JSONArray allMessages = responseJson.getJSONArray("data");
                        hisToryItem.clear();
                        //update the adapter, saving the last known size
                        //  int curSize = adapter == null ? 0 : adapter.getItemCount();
                        if (allMessages.length() > 0) {
                            for (int i = 0; i < allMessages.length(); i++) {
                                JSONObject his = allMessages.getJSONObject(i);
                                HistoryModel hisModel = new HistoryModel(his.getString("id"), his.getString("player_game_id"),
                                        his.getString("user_input"), his.getString("user_score"), his.getString("level"));
                                /*
                                *         "id": 1,
                                          "player_game_id": 1,
                                          "user_id": 1,
                                          "user_input": 5,getString
                                          "user_score": 5,
                                          "level": 1
                                * */
                                hisToryItem.add(hisModel);
                            }

                            setViewProfitRecycler(hisToryItem);


                        }
                    } else {
                        Toast.makeText(ViewProfitActivity.this, "ViewProfitTask error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("ViewProfitTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("ViewProfitTask error login", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(ViewProfitActivity.this, ViewProfitActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(ViewProfitActivity.this, ViewProfitActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ViewProfitActivity.this, ViewProfitActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ViewProfitActivity.this, ViewProfitActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ViewProfitActivity.this, ViewProfitActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
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

        final RequestQueue queue = Volley.newRequestQueue(ViewProfitActivity.this);
        request.setTag("ViewProfitTask");
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
