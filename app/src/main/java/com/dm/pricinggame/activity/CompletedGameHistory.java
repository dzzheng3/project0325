package com.dm.pricinggame.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.dm.pricinggame.activity.model.Player;
import com.dm.pricinggame.activity.model.raGameModel;
import com.dm.pricinggame.adapter.AvailableGameAdapter;
import com.dm.pricinggame.adapter.RunningGameAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompletedGameHistory extends AppCompatActivity {
    @BindView(R.id.game_runnning_recycler_view)
    RecyclerView gameRunnningRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ProgressDialog progressDialog;


    private RunningGameAdapter adapter;
    private AvailableGameAdapter availableGameAdapter;
    private ArrayList<raGameModel> gameArray;
    private ArrayList<raGameModel> avaiGameArray;
    private PreferenceHelper helper;
    private GridLayoutManager mLayoutManager, nLayoutManager;
    private int gameId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_game_history);

        //Data is coming from questionList adapater
        Intent intent = getIntent();
        if (intent != null) {
            Logger.e("detail intent", " not null");
            gameId = intent.getIntExtra(AppText.GAME_ID, 0);
            Logger.e("detail intent completd", "" + gameId);
        }
        ButterKnife.bind(this);
        helper = new PreferenceHelper(CompletedGameHistory.this);

        toolBarSetUp();
        callGameTask();
    }

    /**
     * Toolbar setup method
     */
    private void toolBarSetUp() {
        /**
         * Menu item click listner
         * */
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_completed_game:
                                break;
                        }
                        return true;
                    }
                });
    }
    private void ShowDialogue(String mes, final String auth) {
        progressDialog = new ProgressDialog(CompletedGameHistory.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(mes);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(CompletedGameHistory.this).cancelAll(auth);
            }
        });
    }

    private void callGameTask() {
        if (Api.isInNetwork(CompletedGameHistory.this)) {
            CompletedGameTask(String.valueOf(gameId), helper.getString(AppText.PLAYER_ID, "N/A"));
        } else {
            Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setRunningGameRecycler(ArrayList<raGameModel> GameItem) {
        adapter = new RunningGameAdapter(CompletedGameHistory.this, GameItem,true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CompletedGameHistory.this, LinearLayoutManager.VERTICAL, false);
        gameRunnningRecyclerView.setLayoutManager(layoutManager);
        gameRunnningRecyclerView.setVisibility(View.VISIBLE);
        gameRunnningRecyclerView.setAdapter(adapter);
    }
    private void CompletedGameTask(final String GameId, final String user_id) {
        ShowDialogue("Loading Game...,", "CompletedGameTask");
        Logger.e("gameId", "" + GameId);
        String url = Api.completedGameUrl + "?user_id=" + user_id + "&game_id=" + GameId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("CompletedGameTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        JSONArray allMessages = responseJson.getJSONArray("data");
                        gameArray = new ArrayList<>();
                        //update the adapter, saving the last known size
                        //  int curSize = adapter == null ? 0 : adapter.getItemCount();
                        if (allMessages.length() > 0) {
                            for (int i = 0; i < allMessages.length(); i++) {
                                JSONObject his = allMessages.getJSONObject(i);
                                JSONObject pla = his.getJSONObject("player");
                                Player player = new Player(pla.getString("id"), pla.getString("username"), pla.getString("email"));
                                raGameModel hisModel = new raGameModel(his.getString("id"), his.getString("current_level"), player, his.getString("created_at"));
                                gameArray.add(hisModel);
                            }

                            setRunningGameRecycler(gameArray);


                        }
                    } else {
                        Toast.makeText(CompletedGameHistory.this, "No Running Game Available", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("CompletedGameTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("CompletedGameTask error", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(CompletedGameHistory.this, CompletedGameHistory.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

        };

        final RequestQueue queue = Volley.newRequestQueue(CompletedGameHistory.this);
        request.setTag("CompletedGameTask");
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
