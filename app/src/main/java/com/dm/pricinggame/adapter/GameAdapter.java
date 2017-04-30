package com.dm.pricinggame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dm.pricinggame.R;
import com.dm.pricinggame.activity.model.GameModel;
import com.dm.pricinggame.eventbusinterface.ChooseGame;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linuxraju on 3/27/17.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GameModel> gameModels;
    ChooseGame chooseGame;

    public GameAdapter(Context context, ArrayList<GameModel> gameItem) {
        this.context = context;
        this.gameModels = gameItem;
        chooseGame = (ChooseGame)context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_adapter_new_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gameName.setText(gameModels.get(position).getGameName());

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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chooseGame.selectGame(getLayoutPosition(),gameModels.get(getLayoutPosition()).getGameId());
//            Intent intent = new Intent(context.getApplicationContext(), DecisionGameActivity.class);
//            intent.putExtra(AppText.GAME_ID, gameModels.get(getLayoutPosition()).getGameId());
//            context.startActivity(intent);
        }
    }


}
