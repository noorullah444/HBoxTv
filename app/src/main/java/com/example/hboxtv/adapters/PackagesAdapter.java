package com.example.hboxtv.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hboxtv.R;
import com.example.hboxtv.model.Package;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.ViewHolder> {
    private static final String TAG = PackagesAdapter.class.getSimpleName();
    private final List<Package> packageList;
    private final List<Package> selectedPackagesList = new ArrayList<>();
    private final Context context;
    private OnPackageSelectListener clickListener;

    //Set the values
    Set<String> set = new HashSet<String>();
    Boolean[] checkedStatus;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PackagesAdapter(Context context, List<Package> packages, Boolean[] checkedStatus/*, OnCategoryClickListener listener*/) {
        this.context = context;
        this.packageList = packages;
        this.checkedStatus = checkedStatus;
        selectedPackagesList.addAll(packageList);
        Log.d(TAG, "PackagesAdapter: selected: "+ selectedPackagesList.size());
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
            holder.checkBoxPackage.setChecked(checkedStatus[position]);

            prefs = context.getSharedPreferences("status", Context.MODE_PRIVATE);
            editor = prefs.edit();

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
                        editor.putStringSet("key", set);
                        editor.apply();
                        savePosCheck(position, true);
                    }
                    else {
                        selectedPackagesList.remove(mPackage);
                        editor.putStringSet("key", set);
                        editor.apply();
                        savePosCheck(position, false);
                    }
                    clickListener.OnPackageSelect(selectedPackagesList);
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

    private void savePosCheck(int position, boolean state) {
        checkedStatus[position] = state;
        String key = Integer.toString(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }
}
