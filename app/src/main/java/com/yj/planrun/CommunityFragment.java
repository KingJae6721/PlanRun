package com.yj.planrun;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommunityFragment extends Fragment {
    private FirebaseFirestore firestore;
    private String uid;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RecyclerView recyclerView = view.findViewById(R.id.detailviewfragment_recyclerview);
        recyclerView.setAdapter(new DetailViewRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RelativeLayout club_btn = view.findViewById(R.id.club_btn);
        ImageView add_post = view.findViewById(R.id.add_post);
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        club_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ClubFragment clubFragment = new ClubFragment();
                transaction.replace(R.id.community_layout, clubFragment);
                transaction.commit();
            }
        });

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private class DetailViewRecyclerViewAdapter extends RecyclerView.Adapter<DetailViewRecyclerViewAdapter.CustomViewHolder> {
        private ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
        private ArrayList<String> contentUidList = new ArrayList<>();

        public DetailViewRecyclerViewAdapter() {
            firestore.collection("images")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                        if (querySnapshot != null) { // querySnapshot이 null인 경우 예외 처리
                            contentDTOs.clear();
                            contentUidList.clear();
                            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                                ContentDTO item = snapshot.toObject(ContentDTO.class);
                                contentDTOs.add(item);
                                contentUidList.add(snapshot.getId());
                            }
                            // 업로드 시간 순으로 정렬
                            //Collections.reverse(contentDTOs);
                            notifyDataSetChanged();
                        }
                    });
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
            return new CustomViewHolder(view);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView profileTextView;
            ImageView imageViewContent;
            TextView explainTextView;
            TextView favoriteCounterTextView;

            CustomViewHolder(View itemView) {
                super(itemView);
                profileTextView = itemView.findViewById(R.id.detailviewitem_profile_textview);
                imageViewContent = itemView.findViewById(R.id.detailviewitem_imageview_content);
                explainTextView = itemView.findViewById(R.id.detailviewitem_explain_textview);
                favoriteCounterTextView = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            ContentDTO contentDTO = contentDTOs.get(position);
            CustomViewHolder viewHolder = (CustomViewHolder) holder;
            View view = viewHolder.itemView;

            // UserId
            viewHolder.profileTextView.setText(contentDTO.getUserId());

            // Check if an image is available
            if (contentDTO.getImageUrl() != null && !contentDTO.getImageUrl().isEmpty()) {
                // Image is available, show the ImageView
                viewHolder.imageViewContent.setVisibility(View.VISIBLE);
                Glide.with(viewHolder.itemView.getContext())
                        .load(contentDTO.getImageUrl())
                        .into(viewHolder.imageViewContent);
            } else {
                // Image is not available, hide the ImageView
                viewHolder.imageViewContent.setVisibility(View.GONE);
            }
            /*
            // Image
            Glide.with(viewHolder.itemView.getContext())
                    .load(contentDTO.getImageUrl())
                    .into(viewHolder.imageViewContent);

             */

            // Explain of content
            viewHolder.explainTextView.setText(contentDTO.getExplain());

            // Likes
            viewHolder.favoriteCounterTextView.setText("Likes " + contentDTO.getFavoriteCount());
            viewHolder.explainTextView.setText(contentDTOs.get(position).getExplain());

            // This code is when the button is clicked
            view.findViewById(R.id.detailviewitem_favorite_imageview).setOnClickListener(v -> favoriteEvent(position));

            // This code is when the page is loaded
            ImageView favoriteImageView = view.findViewById(R.id.detailviewitem_favorite_imageview);
            Map<String, Boolean> favorites = contentDTO.getFavorites();
            if (favorites != null && favorites.containsKey(uid)) {
                // This is like status
                favoriteImageView.setImageResource(R.drawable.ic_favorite);
            } else {
                // This is unlike status
                favoriteImageView.setImageResource(R.drawable.ic_favorite_border);
            }
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }


        private void favoriteEvent(int position) {
            DocumentReference tsDoc = firestore.collection("images").document(contentUidList.get(position));
            firestore.runTransaction(transaction -> {
                ContentDTO contentDTO = transaction.get(tsDoc).toObject(ContentDTO.class);

                if (contentDTO.getFavorites().containsKey(uid)) {
                    // When the button is clicked
                    contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() - 1);
                    contentDTO.getFavorites().remove(uid);
                } else {
                    // When the button is not clicked
                    contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() + 1);
                    contentDTO.getFavorites().put(uid, true);
                }

                transaction.set(tsDoc, contentDTO);
                return null;
            });
        }
    }
}