package com.example.hboxtv.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hboxtv.R;
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceResponse;

import org.jetbrains.annotations.NotNull;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private final CategoryByDeviceResponse categories;
    private final Context context;

    public CategoryAdapter(Context context, CategoryByDeviceResponse response) {
        this.context = context;
        this.categories = response;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.getResponse().get(position);
        if (category != null) {
            Log.d(TAG, "onBindViewHolder: categories: "+ category.getCategoryName());
            holder.tvCategoryName.setText(category.getCategoryName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Category Id: " + category.getCategoryId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (categories.getResponse().size() == 0) ? 0 : categories.getResponse().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
