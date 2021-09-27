package com.example.hboxtv.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hboxtv.R;
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.example.hboxtv.model.Package;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.ViewHolder> {
    private static final String TAG = PackagesAdapter.class.getSimpleName();
    private final List<Package> packageList;
    private final List<Package> selectedPackagesList = new ArrayList<>();
    private final Context context;
    private OnPackageSelectListener clickListener;

    public PackagesAdapter(Context context, List<Package> packages/*, OnCategoryClickListener listener*/) {
        this.context = context;
        this.packageList = packages;
        selectedPackagesList.addAll(packageList);
//        this.clickListener = listener;
    }

    public void setOnItemClickListener(OnPackageSelectListener listener) {
        clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_package, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PackagesAdapter.ViewHolder holder, int position) {
        Package mPackage = packageList.get(position);
        if (mPackage != null) {
            holder.tvPackageName.setText(mPackage.getPackageName());
            // select all package at first
            holder.checkBoxPackage.setChecked(true);

            /*holder.tvPackageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.OnPackageSelect(mPackage);
                }
            });*/

            holder.checkBoxPackage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        selectedPackagesList.add(mPackage);
                        clickListener.OnPackageSelect(selectedPackagesList);
                    }
                    else {
                        selectedPackagesList.remove(mPackage);
                        clickListener.OnPackageSelect(selectedPackagesList);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (packageList.size() == 0) ? 0 : packageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxPackage;
        TextView tvPackageName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxPackage = itemView.findViewById(R.id.checkbox_package);
            tvPackageName = itemView.findViewById(R.id.tv_package_name);
        }
    }

    public interface OnPackageSelectListener {
        void OnPackageSelect(List<Package> list);
    }
}
