package com.example.hboxtv.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hboxtv.R;
import com.example.hboxtv.model.Epg.Epg;
import com.example.hboxtv.model.Epg.EpgResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EpgAdapter extends RecyclerView.Adapter<EpgAdapter.ViewHolder> {
    private static final String TAG = EpgAdapter.class.getSimpleName();
    private final List<Epg> epgList;
    private final Context context;

    public EpgAdapter(Context context, List<Epg> list) {
        this.context = context;
        this.epgList = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_epg, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EpgAdapter.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        Epg epg = epgList.get(pos);
        if (epg != null) {
            Log.d(TAG, "onBindViewHolder---: epg title: "+ epg.getTitle());

            holder.tvTitle.setText("Title: "+epg.getTitle());
            holder.tvStartTime.setText("Start: "+epg.getStart());
            holder.tvEndTime.setText("Stop: "+epg.getStop());
        }else
            Log.d(TAG, "onBindViewHolder---: epg is null");

    }

    @Override
    public int getItemCount() {
        return (epgList.size() == 0) ? 0 : epgList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvStartTime;
        TextView tvEndTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_channel_title);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
        }
    }
}
