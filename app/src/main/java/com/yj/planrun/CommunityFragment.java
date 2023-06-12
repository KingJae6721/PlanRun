package com.yj.planrun;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommunityFragment extends Fragment {
    private DatabaseReference mDatabaseRef;
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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");

        RelativeLayout club_btn = view.findViewById(R.id.club_btn);
        ImageView add_post = view.findViewById(R.id.add_post);
        ImageView search = view.findViewById(R.id.search);

        Toolbar community_toolbar = view.findViewById(R.id.community_toolbar);

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        club_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ClubFragment clubFragment = new ClubFragment();
                transaction.replace(R.id.community_layout, clubFragment);
                community_toolbar.setVisibility(View.GONE);
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

        //검색
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });

        return view;
    }
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search")
                .setMessage("Enter nickname:")
                .setView(R.layout.dialog_search)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog alertDialog = (AlertDialog) dialogInterface;
                        EditText editTextNickname = alertDialog.findViewById(R.id.editTextUserId);
                        String nickname = editTextNickname.getText().toString().trim();
                        showUserProfileByNickname(nickname);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showUserProfileByNickname(String nickname) {
        mDatabaseRef.child("UserAccount")
                .orderByChild("nickname")
                .equalTo(nickname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 사용자를 찾았을 때의 처리
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String uid = userSnapshot.getKey();
                                // 해당 사용자 정보를 사용하여 마이페이지로 이동
                                showMypageFragment(uid, nickname);
                            }
                        } else {
                            // 사용자를 찾지 못했을 때의 처리
                            Toast.makeText(getActivity(), "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                        Toast.makeText(getActivity(), "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showMypageFragment(String uid, String nickname) {
        MypageFragment mypageFragment = new MypageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("destinationUid", uid);
        bundle.putString("userId", nickname);
        mypageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
    }


    private void showUserProfile(String uid) {
        // 사용자 프로필을 보여주는 코드 작성
        // 예를 들어, 사용자의 프로필 이미지와 닉네임을 표시하는 등의 작업을 수행할 수 있습니다.
        // 이 부분은 원하는 방식으로 UI를 구성하시면 됩니다.
    }
    /*
    private void searchUser(String userId) {
        firestore.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String destinationUid = documentSnapshot.getId();

                        MypageFragment mypageFragment = new MypageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("destinationUid", destinationUid);
                        bundle.putString("userId", userId);
                        mypageFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.containers, mypageFragment)
                                .commit();
                    } else {
                        // 유저를 찾지 못한 경우에 대한 처리
                        Toast.makeText(getActivity(), "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // 검색 중 에러가 발생한 경우에 대한 처리
                    Toast.makeText(getActivity(), "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
       */


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
            ImageView imageViewContent,imageViewProfile;
            TextView explainTextView;
            TextView favoriteCounterTextView;

            CustomViewHolder(View itemView) {
                super(itemView);
                profileTextView = itemView.findViewById(R.id.detailviewitem_profile_textview);
                imageViewContent = itemView.findViewById(R.id.detailviewitem_imageview_content);
                explainTextView = itemView.findViewById(R.id.detailviewitem_explain_textview);
                favoriteCounterTextView = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
                imageViewProfile = itemView.findViewById(R.id.detailviewitem_profile_image);
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

            // 프로필 이미지를 눌렀을 경우 이벤트처리
            viewHolder.imageViewProfile.setOnClickListener(v -> {
                MypageFragment mypageFragment = new MypageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("destinationUid", contentDTO.getUid());
                bundle.putString("userId", contentDTO.getUserId());
                mypageFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
            });

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