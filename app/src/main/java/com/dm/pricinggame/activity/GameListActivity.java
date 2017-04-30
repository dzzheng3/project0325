package com.dm.pricinggame.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dm.pricinggame.R;
import com.dm.pricinggame.activity.helper.Api;
import com.dm.pricinggame.activity.helper.Logger;
import com.dm.pricinggame.activity.helper.PreferenceHelper;
import com.dm.pricinggame.activity.model.GameModel;
import com.dm.pricinggame.adapter.GameAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameListActivity extends AppCompatActivity {

    @BindView(R.id.game_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_message)
    SwipeRefreshLayout refreshMessage;
    @BindView(R.id.progress_infinite)
    ProgressBar progressInfinite;
    @BindView(R.id.tv_error_message)
    TextView errorText;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /*
    * Variable declaration
    * */
    private RecyclerView.Adapter adapter;
    private ArrayList<GameModel> gameArray;
    private PreferenceHelper helper;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        helper = new PreferenceHelper(GameListActivity.this);
        gameArray = new ArrayList<>();
        ButterKnife.bind(this);
        toolBarSetUp();
        // The number of Columns
        mLayoutManager = new GridLayoutManager(GameListActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(GameListActivity.this, LinearLayoutManager.VERTICAL, false);
        //recyclerView.setLayoutManager(layoutManager);
        refreshMessage.setEnabled(false);
        callGameTask();
        //fakeData();

    }

    /**
     * Toolbar setup method
     * */
    private void toolBarSetUp() {
        /**
         * Menu item click listner
         * */
        toolbar.setTitle("");
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
                            case R.id.action_logout:
                                SharedPreferences.Editor editor = helper.edit();
                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(GameListActivity.this, LoginActivity.class));
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }


    private void setMessageRecycler(ArrayList<GameModel> GameItem) {
        adapter = new GameAdapter(GameListActivity.this, GameItem);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }

    private void callGameTask() {
        if (Api.isInNetwork(GameListActivity.this)) {
            GameTask();
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GameListActivity.this.getString(R.string.error_no_internet));
            errorText.setVisibility(View.VISIBLE);
        }
    }

    public void GameTask() {
        String url = Api.listOfGameUrl;
        Logger.e("GameTask url", url);
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e("GameTask response", response);
                        try {
                            if (response.length() > 0) {
                            JSONArray resObj = new JSONArray(response);
                            gameArray.clear();
                            //update the adapter, saving the last known size
                            //  int curSize = adapter == null ? 0 : adapter.getItemCount();
                                for (int i = 0; i < resObj.length(); i++) {
                                    JSONObject eachMessage = resObj.getJSONObject(i);
                                    GameModel msgModel = new GameModel(eachMessage.getInt("id"),
                                            eachMessage.getString("name"), eachMessage.getString("description"), eachMessage.getString("type"));
                                    gameArray.add(msgModel);
                                }

                                setMessageRecycler(gameArray);


                            } else {

                                recyclerView.setVisibility(View.GONE);
                                errorText.setText("No Data Available");
                                errorText.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            Logger.e("GameTask json_ex", e.getMessage() + "");
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Logger.e("GameTask onErrorResponse", error.getMessage() + "");

                try {
                    Logger.e("GameTask error response", new String(error.networkResponse.data));
                    JSONObject errorObj = new JSONObject(new String(error.networkResponse.data));
                    Toast.makeText(GameListActivity.this, errorObj.getString("message"), Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    errorText.setText(errorObj.getString("message"));
                    errorText.setVisibility(View.VISIBLE);


                } catch (Exception e) {
                    Logger.e("GameTask error ex", e.getMessage() + "");
                    errorText.setText("");
                    if (error instanceof NetworkError) {
                        errorText.setText(GameListActivity.this.getString(R.string.error_no_internet));
                    } else if (error instanceof ServerError) {
                        errorText.setText(GameListActivity.this.getString(R.string.error_server));
                    } else if (error instanceof AuthFailureError) {
                        errorText.setText(GameListActivity.this.getString(R.string.error_authFailureError));
                    } else if (error instanceof ParseError) {
                        errorText.setText(GameListActivity.this.getString(R.string.error_parse_error));
                    } else if (error instanceof TimeoutError) {
                        errorText.setText(GameListActivity.this.getString(R.string.error_time_out));
                    }

                    errorText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
            }
        }) {
/*            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "2238d5e3dba0b843782ab11c97a345d376f0cd95");
                return headers;
            }*/
        };

        final RequestQueue queue = Volley.newRequestQueue(GameListActivity.this);

        request.setTag("GameTask");
        request.setRetryPolicy(policy);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                progressBar.setVisibility(View.GONE);
            }
        });
        queue.add(request);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
