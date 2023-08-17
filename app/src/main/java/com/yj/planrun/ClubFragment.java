package com.yj.planrun;

import android.Manifest;
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

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Map;

public class ClubFragment extends Fragment {
    private SlidingUpPanelLayout slidingPanel;
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore firestore;
    private EditText editTextNickname;
    private String uid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_club, container, false);
        View commuLayout = inflater.inflate(R.layout.fragment_community, container, false);
        RelativeLayout post_btn = view.findViewById(R.id.post_btn);
        Toolbar club_toolbar = view.findViewById(R.id.club_toolbar);
        Toolbar community_toolbar = commuLayout.findViewById(R.id.community_toolbar);
        ImageView addClub_btn = view.findViewById(R.id.add_club);
        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RecyclerView recyclerView = view.findViewById(R.id.detailviewfragment_recyclerview);
        recyclerView.setAdapter(new ClubFragment.DetailViewRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");
        RelativeLayout club_btn = view.findViewById(R.id.club_btn);
        ImageView add_club = view.findViewById(R.id.add_club);
        ImageView search = view.findViewById(R.id.search);

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        addClub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreateClubActivity.class);
                startActivity(intent);
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityFragment communityFragment = new CommunityFragment();
                transaction.replace(R.id.club_layout, communityFragment);
                club_toolbar.setVisibility(View.GONE);
                community_toolbar.setVisibility(View.VISIBLE);
                transaction.commit();
            }
        });
        return view;
    }

    /*private void showUserProfileByNickname(String nickname) {
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
    }*/
   /* private void showMypageFragment(String uid, String nickname) {
        MypageFragment mypageFragment = new MypageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("destinationUid", uid);
        bundle.putString("userId", nickname);
        mypageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
    }*/


   /* private void showUserProfile(String uid) {
        // 사용자 프로필을 보여주는 코드 작성
        // 예를 들어, 사용자의 프로필 이미지와 닉네임을 표시하는 등의 작업을 수행할 수 있습니다.
        // 이 부분은 원하는 방식으로 UI를 구성하시면 됩니다.
    }*/
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




    private class DetailViewRecyclerViewAdapter extends RecyclerView.Adapter<ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder> {
        private ArrayList<ClubDTO> clubDTOs = new ArrayList<>();
        private ArrayList<String> contentUidList = new ArrayList<>();

        public DetailViewRecyclerViewAdapter() {
            firestore.collection("club")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                        if (querySnapshot != null) { // querySnapshot이 null인 경우 예외 처리
                            clubDTOs.clear();
                            contentUidList.clear();
                            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                                ClubDTO item = snapshot.toObject(ClubDTO.class);
                                clubDTOs.add(item);
                                contentUidList.add(snapshot.getId());
                            }
                            // 업로드 시간 순으로 정렬
                            //Collections.reverse(clubDTOs);
                            notifyDataSetChanged();
                        }
                    });
        }

        @NonNull
        @Override
        public ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_info, parent, false);
            return new ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder(view);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView clubNameTextView;
            ImageView imageViewProfile;
            TextView explainTextView, clubinfoviewitem_textview_member , clubinfoviewitem_textview_post, clubinfoviewitem_textview_registered;
            TextView memberCounterTextView;

            CustomViewHolder(View itemView) {
                super(itemView);
                clubNameTextView = itemView.findViewById(R.id.clubinfoviewitem_textview_name);
                explainTextView = itemView.findViewById(R.id.clubinfoviewitem_textview_comment);
                memberCounterTextView = itemView.findViewById(R.id.clubinfoviewitem_textview_member);
                imageViewProfile = itemView.findViewById(R.id.commentviewitem_imageview_profile);
                clubinfoviewitem_textview_registered = itemView.findViewById(R.id.clubinfoviewitem_textview_registered);
                clubinfoviewitem_textview_member = itemView.findViewById(R.id.clubinfoviewitem_textview_member);
                clubinfoviewitem_textview_post = itemView.findViewById(R.id.clubinfoviewitem_textview_post);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder holder, int position) {
            ClubDTO clubDTO = clubDTOs.get(position);
            ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder viewHolder = (ClubFragment.DetailViewRecyclerViewAdapter.CustomViewHolder) holder;
            View view = viewHolder.itemView;

            // UserId
            viewHolder.clubNameTextView.setText(clubDTO.getClubName());

            // Check if an image is available
            if (clubDTO.getImageUrl() != null && !clubDTO.getImageUrl().isEmpty()) {
                // Image is available, show the ImageView

                Glide.with(viewHolder.itemView.getContext())
                        .load(clubDTO.getImageUrl())
                        .into(viewHolder.imageViewProfile);
            } else {
                // Image is not available, hide the ImageView

            }
            // Image
            Glide.with(viewHolder.itemView.getContext())
                    .load(clubDTO.getImageUrl())
                    .into(viewHolder.imageViewProfile);




            // Explain of content
            viewHolder.explainTextView.setText(clubDTO.getExplain());

            // Likes
            viewHolder.memberCounterTextView.setText("회원 " + clubDTO.getMember_count());
            viewHolder.explainTextView.setText(clubDTOs.get(position).getExplain());

            // This code is when the button is clicked
           // view.findViewById(R.id.detailviewitem_favorite_imageview).setOnClickListener(v -> favoriteEvent(position));

           /* // 프로필 이미지를 눌렀을 경우 이벤트처리
            viewHolder.imageViewProfile.setOnClickListener(v -> {
                MypageFragment mypageFragment = new MypageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("destinationUid", clubDTO.getUid());
                bundle.putString("userId", clubDTO.getUserId());
                mypageFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
            });
*/



        }

        @Override
        public int getItemCount() {
            return clubDTOs.size();
        }




     /*   private void favoriteEvent(int position) {
            DocumentReference tsDoc = firestore.collection("images").document(contentUidList.get(position));
            firestore.runTransaction(transaction -> {
                ClubDTO clubDTO = transaction.get(tsDoc).toObject(ClubDTO.class);

                if (clubDTO.getFavorites().containsKey(uid)) {
                    // When the button is clicked
                    clubDTO.setFavoriteCount(clubDTO.getFavoriteCount() - 1);
                    clubDTO.getFavorites().remove(uid);
                } else {
                    // When the button is not clicked
                    clubDTO.setFavoriteCount(clubDTO.getFavoriteCount() + 1);
                    clubDTO.getFavorites().put(uid, true);
                }

                transaction.set(tsDoc, clubDTO);
                return null;
            });
        }*/
 }


}
