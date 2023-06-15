package com.yj.planrun;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FollowingAccountsAdapter extends RecyclerView.Adapter<FollowingAccountsAdapter.ViewHolder> {

    private ArrayList<String> mFollowingAccountsList;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(String nickname);
    }
    public FollowingAccountsAdapter(ArrayList<String> followingAccountsList, OnItemClickListener listener) {
        mFollowingAccountsList = followingAccountsList;
        this.listener = listener;
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
        String nickname = mFollowingAccountsList.get(position);
        holder.bind(nickname, listener);
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

        public void bind(final String nickname, final OnItemClickListener listener) {
            // 닉네임을 뷰에 표시하는 작업
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(nickname); // 클릭 이벤트 리스너 호출
                }
            });
        }
    }
}