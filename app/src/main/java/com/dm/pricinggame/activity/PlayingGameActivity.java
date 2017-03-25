package com.dm.pricinggame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.dm.pricinggame.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/3/25.
 */
public class PlayingGameActivity extends Activity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_value)
    EditText etValue;
    @BindView(R.id.bt_submit)
    Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playinggame);
        ButterKnife.bind(this);
        toolbar.setTitle("Playing");

    }

    @OnClick(R.id.bt_submit)
    public void onViewClicked() {
        toolbar.setSubtitle("Waiting another player...");
        SystemClock.sleep(1500);
        startActivity(new Intent(this,ResultActivity.class));
    }
}
