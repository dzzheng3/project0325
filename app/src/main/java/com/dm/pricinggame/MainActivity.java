package com.dm.pricinggame;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.dm.pricinggame.activity.helper.Api;
import com.dm.pricinggame.activity.helper.AppText;
import com.dm.pricinggame.activity.helper.Logger;
import com.dm.pricinggame.activity.helper.PreferenceHelper;
import com.dm.pricinggame.activity.model.ViewProfitActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sp_select_guess)
    Spinner spSelectGuess;
    @BindView(R.id.bt_play_game)
    Button btPlayGame;
    @BindView(R.id.bt_search)
    Button btSearch;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.bt_check_next_level)
    Button btn_next_level;
    @BindView(R.id.show_waiting_status)
    TextView tv_waiting;
    private int gameId;
    private String playerGameId;
    private PreferenceHelper helper;
    private ProgressDialog progressDialog;
    private boolean checkDialogue = false;
    private String user_input, currentLevel;
    private boolean areOneGoingGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new PreferenceHelper(MainActivity.this);

        //Data is coming from questionList adapater
        Intent intent = getIntent();
        if (intent != null) {
            Logger.e("detail intent", " not null");
            gameId = intent.getIntExtra(AppText.GAME_ID, 0);
            Logger.e("detail intent", "" + gameId);
        }


        Intent intent1 = getIntent();
        if (intent1 != null) {
            Logger.e("detail intent", " not null");
            playerGameId = intent1.getStringExtra(AppText.PLAYER_GAME_ID);
            Logger.e("player_RE_game_Id1", "" + playerGameId);
            helper.edit().putString(AppText.PLAYER_RE_GAME_ID, String.valueOf(playerGameId)).commit();
            currentLevel = intent1.getStringExtra(AppText.CURRENT_LEVEL);
            areOneGoingGame = intent1.getBooleanExtra(AppText.ARE_ON_GOING_GAME, false);
            Logger.e("detail intent", "" + gameId);
        }
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                MainActivity.this, R.array.select_price, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectGuess.setAdapter(adapter);


        if (Api.isInNetwork(MainActivity.this)) {
            if (areOneGoingGame == true) {
                tvLevel.setText("Level: " + currentLevel);
                //btPlayGame.setEnabled(false);
            } else {
                GameInitializeTask(String.valueOf(gameId), helper.getString(AppText.PLAYER_ID, "N/A"));
            }

        } else {
            Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }


    }

    public String getTimeDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void ShowDialogue(String mes, final String auth) {
        progressDialog = new ProgressDialog(MainActivity.this);//R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(mes);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Volley.newRequestQueue(MainActivity.this).cancelAll(auth);
            }
        });
    }

    @OnClick(R.id.bt_play_game)
    public void onBtPlayGameClicked() {
        if (!spSelectGuess.getSelectedItem().toString().equalsIgnoreCase("Select Price")) {
            if (Api.isInNetwork(MainActivity.this)) {
                if (areOneGoingGame == true) {
                    GamePlayTask(String.valueOf(playerGameId),
                            currentLevel, spSelectGuess.getSelectedItem().toString().trim(),
                            helper.getString(AppText.PLAYER_ID, "N/A"));
                } else {
                    GamePlayTask(helper.getString(AppText.PLAYER_RE_GAME_ID, "N/A"),
                            helper.getString(AppText.GAME_LEVEL, "N/A"), spSelectGuess.getSelectedItem().toString().trim(),
                            helper.getString(AppText.PLAYER_ID, "N/A"));
                }
            } else {
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please select price", Toast.LENGTH_SHORT).show();
        }

       // startActivity(new Intent(MainActivity.this,PlayingGameActivity.class));
    }

    @OnClick(R.id.bt_search)
    public void onBtSearchClicked() {
        Logger.e("player_RE_game_Id", helper.getString(AppText.PLAYER_RE_GAME_ID, "N/A"));
        startActivity(new Intent(MainActivity.this, ViewProfitActivity.class));
    }

    /**
     * Game Initilization  Task
     */
    private void GameInitializeTask(final String gameId, final String user_id) {
        ShowDialogue("Loading Gmae...,", "GameInitializeTask");
        String url = Api.GameInitializeUrl + "?user_id=" + user_id + "&game_id=" + gameId;//&created_at="+getTimeDate();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("GameInitializeTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    progressDialog.setMessage(responseJson.getString("message"));
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        JSONObject userDetail = responseJson.getJSONObject("data");
                        helper.edit().putString(AppText.PLAYER_RE_GAME_ID, userDetail.getString("id")).commit();
                        helper.edit().putString(AppText.GAME_LEVEL, userDetail.getString("current_level")).commit();
                        tvLevel.setText("Level: " + userDetail.getString("current_level"));
                        tv_waiting.setText("Submit Your first Score");
                        tv_waiting.setVisibility(View.VISIBLE);
                        // btn_next_level.setEnabled(false);
                        Toast.makeText(MainActivity.this, "Game Inialized sucessfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Game Inialization error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("GameInitializeTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("GameInitializeTask error login", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
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

        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        request.setTag("GameInitializeTask");
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
     * Game Play Task
     */
    private void GamePlayTask(final String playerGameId, final String level, final String score, final String user_id) {
        ShowDialogue("Submitting value...,", "GamePlayTask");
        String url = Api.submitValueUrl;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("GamePlayTask response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
//                        JSONObject userDetail = responseJson.getJSONObject("data");
//                        helper.edit().putString(AppText.GAME_LEVEL, userDetail.getString("new-level")).commit();
//                        tvLevel.setText("Level: " + userDetail.getString("new-level"));
                        Toast.makeText(MainActivity.this, "Score Submitted sucessfully", Toast.LENGTH_SHORT).show();
                        //btn_next_level.setEnabled(true);
                        //btPlayGame.setEnabled(false);
                    } else {
                        /* {"status":"error","message":"Already submitted"}*/
                        Toast.makeText(MainActivity.this, responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("GamePlayTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("GamePlayTask error ", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("player_game_id", playerGameId);
                map.put("user_id", user_id);
                map.put("level", level);
                map.put("user_input", score);
                Logger.e("values", playerGameId + "/" + level + "/");
                return map;
            }
        };

        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        request.setTag("GamePlayTask");
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
     * Game Next Level  Task
     */
    private void GameNextLevel(final String playerGameId, final String plaLevel) {
        ShowDialogue("Changing Level...,", "GameNextLevel");
        String url = Api.nextLevelUrl + "?player_level=" + plaLevel + "&player_game_id=" + playerGameId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Logger.e("GameNextLevel response", response);

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.getString("status").equalsIgnoreCase("success")) {
                        /*
                    {"status":"success","data":{"level":2}}
                        */
                        JSONObject userDetail = responseJson.getJSONObject("data");
//                        helper.edit().putString(AppText.GAME_LEVEL, userDetail.getString("new-level")).commit();
//                        tvLevel.setText("Level: " + userDetail.getString("new-level"));

                        if (areOneGoingGame == true) {
                            currentLevel = userDetail.getString("level");
                        } else {
                            helper.edit().putString(AppText.GAME_LEVEL, userDetail.getString("level")).commit();
                        }
                        tvLevel.setText("Level: " + userDetail.getString("level"));
                        Toast.makeText(MainActivity.this, "Level Changed sucessfully", Toast.LENGTH_SHORT).show();
                        tv_waiting.setText("Level Changed sucessfully");
                        tv_waiting.setVisibility(View.VISIBLE);
                        //btPlayGame.setEnabled(true);
                    } else {
                        tv_waiting.setText("Waiting for other player to submit");
                        //btPlayGame.setEnabled(false);
                        tv_waiting.setVisibility(View.VISIBLE);

                        Toast.makeText(MainActivity.this, "Waiting", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //catch
                    Logger.e("GameInitializeTask json ex", e.getMessage() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work

                Logger.e("GameInitializeTask error login", error.getMessage() + "");
                if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_authFailureError), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_parse_error), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_time_out), Toast.LENGTH_SHORT).show();
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

        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        request.setTag("GameInitializeTask");
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

    @OnClick(R.id.bt_check_next_level)
    public void onViewClicked() {
        if (Api.isInNetwork(MainActivity.this)) {
            if (areOneGoingGame == true) {
                Logger.e("next_data", "" + playerGameId + "/" + currentLevel);
                GameNextLevel(String.valueOf(playerGameId),currentLevel);
            } else {
                Logger.e("next_data1", "" + helper.getString(AppText.PLAYER_RE_GAME_ID, "N/A") + "/" + helper.getString(AppText.GAME_LEVEL, "N/A"));
                GameNextLevel(helper.getString(AppText.PLAYER_RE_GAME_ID, "N/A"), helper.getString(AppText.GAME_LEVEL, "N/A"));
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
