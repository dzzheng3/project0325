package com.dm.pricinggame.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dm.pricinggame.MainActivity;
import com.dm.pricinggame.R;
import com.dm.pricinggame.activity.helper.AppText;
import com.dm.pricinggame.activity.helper.PreferenceHelper;
import com.dm.pricinggame.activity.model.ViewProfitActivity;
import com.dm.pricinggame.activity.model.raGameModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linuxraju on 3/29/17.
 */

public class RunningGameAdapter extends RecyclerView.Adapter<RunningGameAdapter.ViewHolder> {

    private Context context;
    private ArrayList<raGameModel> gameModels;
    boolean check;
    PreferenceHelper helper;

    public RunningGameAdapter(Context context, ArrayList<raGameModel> gameItem, boolean check) {
        this.context = context;
        this.gameModels = gameItem;
        this.check = check;
        helper = new PreferenceHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gameName.setText("Created By: " + gameModels.get(position).getPlayer().getpUserName());
        holder.createdDate.setText("Created Date: " + gameModels.get(position).getCreatedDate());

    }

    @Override
    public int getItemCount() {
        return gameModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.game_name)
        TextView gameName;
        @BindView(R.id.msg_layout)
        RelativeLayout msgLayout;
        @BindView(R.id.created_date)
        TextView createdDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (check == true) {
                Intent intent1 = new Intent(context.getApplicationContext(), ViewProfitActivity.class);
                helper.edit().putString(AppText.PLAYER_RE_GAME_ID, gameModels.get(getLayoutPosition()).getPlayerGameId()).commit();
                context.startActivity(intent1);
            } else {
                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                intent.putExtra(AppText.PLAYER_GAME_ID, gameModels.get(getLayoutPosition()).getPlayerGameId());
                intent.putExtra(AppText.CURRENT_LEVEL, gameModels.get(getLayoutPosition()).getCurrent_level());
                intent.putExtra(AppText.ARE_ON_GOING_GAME, true);
                context.startActivity(intent);
            }
        }
    }


}
