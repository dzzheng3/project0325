package com.dm.pricinggame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                MainActivity.this, R.array.select_price, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectGuess.setAdapter(adapter);
    }

    @OnClick(R.id.bt_play_game)
    public void onBtPlayGameClicked() {
    }

    @OnClick(R.id.bt_search)
    public void onBtSearchClicked() {
    }
}
