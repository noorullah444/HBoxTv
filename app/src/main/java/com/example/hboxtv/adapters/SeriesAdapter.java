package com.example.hboxtv.adapters;

import android.content.Context;
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
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.example.hboxtv.model.Series;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private static final String TAG = SeriesAdapter.class.getSimpleName();
    private final List<Series> seriesList;
    private final Context context;
    private OnSeriesClickListener clickListener;

    public SeriesAdapter(Context context, List<Series> series/*, OnCategoryClickListener listener*/) {
        this.context = context;
        this.seriesList = series;
//        this.clickListener = listener;
    }

    public void setOnItemClickListener(OnSeriesClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_series, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SeriesAdapter.ViewHolder holder, int position) {
        Series series = seriesList.get(position);
        if (series != null) {
            Log.d(TAG, "onBindViewHolder: seriesName: "+ series.getSeriesName());
            Log.d(TAG, "onBindViewHolder: seriesCover: " + series.getCover());

            Glide.with(context).load(series.getCover()).into(holder.ivSeriesCover);
            holder.tvSeriesName.setText(series.getSeriesName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, "Category Id: " + category.getCategoryId(), Toast.LENGTH_SHORT).show();
                    clickListener.OnSeriesClick(series.getSeriesId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (seriesList.size() == 0) ? 0 : seriesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeriesName;
        ImageView ivSeriesCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeriesName = itemView.findViewById(R.id.tv_series_name);
            ivSeriesCover = itemView.findViewById(R.id.iv_series_cover);
        }
    }

    public interface OnSeriesClickListener{
        void OnSeriesClick(String seriesId);
    }
}
