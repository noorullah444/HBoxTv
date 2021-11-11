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
import com.example.hboxtv.model.Channel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private static final String TAG = ChannelAdapter.class.getSimpleName();
    private final List<Channel> channelList;
    private final Context context;
    private OnChannelClickListener clickListener;
    private int row_index = 0;

    public ChannelAdapter(Context context, List<Channel> channels/*, OnCategoryClickListener listener*/) {
        this.context = context;
        this.channelList = channels;
//        this.clickListener = listener;
    }

    public void setOnItemClickListener(OnChannelClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChannelAdapter.ViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        if (channel != null) {
            Log.d(TAG, "onBindViewHolder: seriesName: "+ channel.getName());
            Log.d(TAG, "onBindViewHolder: seriesCover: " + channel.getStreamIcon());

            Glide.with(context).load(channel.getStreamIcon()).into(holder.ivChannelIcon);
            holder.tvChannelName.setText(channel.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, "Category Id: " + category.getCategoryId(), Toast.LENGTH_SHORT).show();
                    clickListener.OnChannelClick(channel.getName(), channel.getChannelId(), channel.getStreamType(), channel.getContainerExtension());

                    row_index = position;
                    notifyDataSetChanged();
                }
            });

            if(row_index == position){
                holder.itemView.setBackgroundColor(Color.parseColor("#CC2E3180"));
                holder.ivPlayIcon.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#B3444444"));
                holder.ivPlayIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (channelList.size() == 0) ? 0 : channelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChannelIcon;
        ImageView ivPlayIcon;
        TextView tvChannelName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChannelIcon = itemView.findViewById(R.id.iv_channel_icon);
            ivPlayIcon = itemView.findViewById(R.id.iv_play_icon);
            tvChannelName = itemView.findViewById(R.id.tv_channel_name);
        }
    }

    public interface OnChannelClickListener{
        void OnChannelClick(String channelName, String channelId, String streamType, String extension);
    }
}
