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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private final List<Channel> channelList;
    private final Context context;
    private OnMoviesClickListener clickListener;

    public MoviesAdapter(Context context, List<Channel> channels/*, OnCategoryClickListener listener*/) {
        this.context = context;
        this.channelList = channels;
//        this.clickListener = listener;
    }

    public void setOnItemClickListener(OnMoviesClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MoviesAdapter.ViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        if (channel != null) {
            Log.d(TAG, "onBindViewHolder: movieName: "+ channel.getName());
            Log.d(TAG, "onBindViewHolder: movieCover: " + channel.getStreamIcon());

            Glide.with(context).load(channel.getStreamIcon()).into(holder.ivMovieCover);
            holder.tvMovieName.setText(channel.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, "Category Id: " + category.getCategoryId(), Toast.LENGTH_SHORT).show();
                    clickListener.OnMovieClick(channel.getChannelId(), channel.getStreamType(), channel.getContainerExtension());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (channelList.size() == 0) ? 0 : channelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName;
        ImageView ivMovieCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tv_movie_name);
            ivMovieCover = itemView.findViewById(R.id.iv_movie_cover);
        }
    }

    public interface OnMoviesClickListener{
        void OnMovieClick(String channelId, String streamType, String extension);
    }
}
