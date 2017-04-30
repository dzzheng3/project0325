package com.dm.pricinggame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dm.pricinggame.R;
import com.dm.pricinggame.activity.model.HistoryModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linuxraju on 3/28/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {


    private Context context;
    private ArrayList<HistoryModel> historyItem;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> historyItem) {
        this.context = context;
        this.historyItem = historyItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hisory_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvLevel.setText(historyItem.get(position).getHisLevel());
        holder.tvMyInput.setText(historyItem.get(position).getHisUserInput());
        holder.tvProfit.setText(historyItem.get(position).getHisUserProfit());

    }

    @Override
    public int getItemCount() {
        return historyItem.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.tv_my_input)
        TextView tvMyInput;
        @BindView(R.id.tv_profit)
        TextView tvProfit;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


}

