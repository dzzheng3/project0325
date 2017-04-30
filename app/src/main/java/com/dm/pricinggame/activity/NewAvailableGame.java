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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.dm.pricinggame.MainActivity;
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
import butterknife.OnClick;

public class NewAvailableGame extends AppCompatActivity {

    @BindView(R.id.game_runnning_recycler_view)
    RecyclerView gameRunnningRecyclerView;
    @BindView(R.id.game_available_recycler_view)
    RecyclerView gameAvailableRecyclerView;
    @BindView(R.id.bt_create_new_game)
    Button btCreateNewGame;
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
        setContentView(R.layout.activity_new_available_game);
        //Data is coming from questionList adapater
        Intent intent = getIntent();
        if (intent != null) {
            Logger.e("detail intent", " not null");
            gameId = intent.getIntExtra(AppText.GAME_ID, 0);
            Logger.e("detail intent", "" + gameId);
        }
        ButterKnife.bind(this);
        helper = new PreferenceHelper(NewAvailableGame.this);
        toolBarSetUp();

        callGameTask();
        callAvailableGameTask();
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
                                Intent intent = new Intent(NewAvailableGame.this, CompletedGameHistory.class);
                                intent.putExtra(AppText.GAME_ID, gameId);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void ShowDialogue(String mes, final String auth) {
        progressDialog = new ProgressDialog(NewAvailableGame.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(mes);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(NewAvailableGame.this).cancelAll(auth);
            }
        });
    }

    private void setRunningGameRecycler(ArrayList<raGameModel> GameItem) {
        adapter = new RunningGameAdapter(NewAvailableGame.this, GameItem,false);
        //mLayoutManager = new GridLayoutManager(NewAvailableGame.this, 2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NewAvailableGame.this, LinearLayoutManager.VERTICAL, false);
        gameRunnningRecyclerView.setLayoutManager(layoutManager);
        //gameRunnningRecyclerView.setLayoutManager(mLayoutManager);
        gameRunnningRecyclerView.setVisibility(View.VISIBLE);
        gameRunnningRecyclerView.setAdapter(adapter);
    }

    private void setAvailableGameRecycler(ArrayList<raGameModel> GameItem) {
        availableGameAdapter = new AvailableGameAdapter(NewAvailableGame.this, GameItem);
        //nLayoutManager = new GridLayoutManager(NewAvailableGame.this, 2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NewAvailableGame.this, LinearLayoutManager.VERTICAL, false);
        gameAvailableRecyclerView.setLayoutManager(layoutManager);
        gameAvailableRecyclerView.setVisibility(View.VISIBLE);
        gameAvailableRecyclerView.setAdapter(availableGameAdapter);
    }

    private void callGameTask() {
        if (Api.isInNetwork(NewAvailableGame.this)) {
            RunningGameTask(String.valueOf(gameId), helper.getString(AppText.PLAYER_ID, "N/A"));
        } else {
            Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void callAvailableGameTask() {
        if (Api.isInNetwork(NewAvailableGame.this)) {
            AvailableGameTask(String.valueOf(gameId));
        } else {
            Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    private void RunningGameTask(final String GameId, final String user_id) {
        //ShowDialogue("Loading Game...,", "RunningGameTask");
        Logger.e("gameId", "" + GameId);
        String url = Api.runningGameUrl + "?user_id=" + user_id + "&game_id=" + GameId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("RunningGameTask response", response);

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
                        Toast.makeText(NewAvailableGame.this, "No Running Game Available", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("RunningGameTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("RunningGameTask error", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
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

        final RequestQueue queue = Volley.newRequestQueue(NewAvailableGame.this);
        request.setTag("RunningGameTask");
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                // progressDialog.dismiss();
            }
        });
        queue.add(request);

        // progressDialog.show();
    }

    private void AvailableGameTask(final String GameId) {
        ShowDialogue("Loading Game...,", "AvailableGameTask");
        Logger.e("gameId", "" + GameId);
        String url = Api.AvailableGameUrl + "?game_id=" + GameId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("AvailableGameTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        JSONArray allMessages = responseJson.getJSONArray("data");
                        avaiGameArray = new ArrayList<>();
                        //update the adapter, saving the last known size
                        //  int curSize = adapter == null ? 0 : adapter.getItemCount();
                        if (allMessages.length() > 0) {
                            for (int i = 0; i < allMessages.length(); i++) {
                                JSONObject his = allMessages.getJSONObject(i);
                                JSONObject pla = his.getJSONObject("player");
                                Player player = new Player(pla.getString("id"), pla.getString("username"), pla.getString("email"));
                                raGameModel hisModel = new raGameModel(his.getString("id"), his.getString("current_level"), player, his.getString("created_at"));
                                /*
                                  "status": "success",
                              "data": [
                                {
                                  "id": 1,
                                  "game_id": 1,
                                  "user_id": 1,
                                  "current_level": 1,
                                  "status": "active",
                                  "player": {
                                    "id": 1,
                                    "username": null,
                                    "email": "mainalipuk@wtf.com",
                                    "role": null
                                  }
                                }*/
                                avaiGameArray.add(hisModel);
                            }
                            //setRunningGameRecycler(avaiGameArray);
                            setAvailableGameRecycler(avaiGameArray);


                        }
                    } else {
                        Toast.makeText(NewAvailableGame.this, "No Joinable Game Available", Toast.LENGTH_SHORT).show();
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

                Logger.e("AvailableGameTask error", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(NewAvailableGame.this, NewAvailableGame.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
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

        final RequestQueue queue = Volley.newRequestQueue(NewAvailableGame.this);
        request.setTag("AvailableGameTask");
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

    @OnClick(R.id.bt_create_new_game)
    public void onViewClicked() {
        Intent intent = new Intent(NewAvailableGame.this, MainActivity.class);
        intent.putExtra(AppText.GAME_ID, gameId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_completed_game) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        callGameTask();
        callAvailableGameTask();
    }
}
