package com.yj.planrun;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FollowingAccountsAdapter extends RecyclerView.Adapter<FollowingAccountsAdapter.ViewHolder> {

    private ArrayList<String> mFollowingAccountsList;

    public FollowingAccountsAdapter(ArrayList<String> followingAccountsList) {
        mFollowingAccountsList = followingAccountsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String account = mFollowingAccountsList.get(position);
        holder.accountName.setText(account);
    }

    @Override
    public int getItemCount() {
        return mFollowingAccountsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accountName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.account_name);
        }
    }
}