package com.example.dashview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_dash_view, null, false);
        return new DashViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        DashViewHolder dashViewHolder = (DashViewHolder) viewHolder;
        dashViewHolder.dash_view.setDatas(0,1);
        dashViewHolder.dash_view.setCurrentValue(0.6f);
        if (position == 1){
            dashViewHolder.dash_view.setDangerValue(0.5f);
            dashViewHolder.dash_view.setWarningValue(0.8f);
        }else if (position == 3){
            dashViewHolder.dash_view.setDangerValue(0.6f);
            dashViewHolder.dash_view.setWarningValue(0);
        }else if (position == 6){

            dashViewHolder.dash_view.setDangerValue(0);
            dashViewHolder.dash_view.setWarningValue(0.6f);
        }else{
            dashViewHolder.dash_view.setDangerValue(0);
            dashViewHolder.dash_view.setWarningValue(0);
        }

        dashViewHolder.dash_view.setTextInfo("出水瞬时流量","国标：1-60","09-09 12:33:20");
        dashViewHolder.dash_view.startRender();

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    private class DashViewHolder extends RecyclerView.ViewHolder {
        public DashView dash_view;

        public DashViewHolder(@NonNull View itemView) {
            super(itemView);
            dash_view = itemView.findViewById(R.id.dash_view);
        }
    }
}
