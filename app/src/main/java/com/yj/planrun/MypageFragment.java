package com.yj.planrun;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import android.Manifest;

import com.kakao.sdk.user.UserApiClient;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MypageFragment extends Fragment {

    private View fragmentView;
    private FirebaseFirestore firestore;
    private String uid;
    private FirebaseAuth auth;
    private String currentUserUid;
    private DatabaseReference mDatabaseRef;
    public static final int PICK_PROFILE_FROM_ALBUM = 10;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_mypage, container, false);
        TextView mypage_nickname = fragmentView.findViewById(R.id.mypage_nickname);
        mypage_nickname.setText(DataLoadingActivity.nickname);

        Button edit_profile = (Button) fragmentView.findViewById(R.id.edit_profile);
        ImageView setting = fragmentView.findViewById(R.id.setting);
        TextView followingCountTextView = fragmentView.findViewById(R.id.account_tv_following_count);


        followingCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowlistActivity.class);
                intent.putExtra("userUid", uid);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });


        //fragmentView = view;
        uid = getArguments().getString("destinationUid");
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserUid = auth.getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        followingCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowlistActivity.class);
                intent.putExtra("userUid", uid);
                startActivity(intent);
            }
        });

        if (uid.equals(currentUserUid)) {
            // MyPage
            Button add_post = fragmentView.findViewById(R.id.add_post);
            add_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // OtherUserPage
            Button follow = (Button) fragmentView.findViewById(R.id.add_post);
            TextView list_of_posts = fragmentView.findViewById(R.id.list_of_posts);
            follow.setText(getString(R.string.follow));
            setting.setVisibility(View.GONE);
            edit_profile.setVisibility(View.GONE);
            //버튼 크기 늘리기
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
            params.setMargins(33, 0, 33, 0); // 양옆에 16dp의 마진 설정
            follow.setLayoutParams(params);
            mDatabaseRef.child("UserAccount").child(uid).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nickname = dataSnapshot.getValue(String.class);
                    if (nickname != null) {
                        DataLoadingActivity.nickname=nickname;
                        mypage_nickname.setText(nickname);
                        list_of_posts.setText(nickname + "게시물");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

            //팔로우
            fragmentView.findViewById(R.id.add_post).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestFollow();
                }
            });
        }

        RecyclerView recyclerView = fragmentView.findViewById(R.id.account_reyclerview);
        recyclerView.setAdapter(new UserFragmentRecyclerViewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        getFollowerAndFollowing();
        return fragmentView;
    }

    private class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
        private ArrayList<String> contentUidList = new ArrayList<>();

        public UserFragmentRecyclerViewAdapter() {
            firestore.collection("images").whereEqualTo("uid", uid).addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                if (querySnapshot == null) return;
                contentDTOs.clear();
                contentUidList.clear(); // contentUidList 초기화

                for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                    ContentDTO contentDTO = snapshot.toObject(ContentDTO.class);
                    contentDTO.setDocumentId(snapshot.getId()); // documentId 설정 추가
                    contentDTOs.add(contentDTO);
                    contentUidList.add(snapshot.getId()); // contentUidList에 고유 식별자 추가
                }

                // 올린 시간 순으로 정렬
                Collections.sort(contentDTOs, new Comparator<ContentDTO>() {
                    @Override
                    public int compare(ContentDTO o1, ContentDTO o2) {
                        return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                    }
                });

                TextView postCountTextView = fragmentView.findViewById(R.id.account_tv_post_count);
                postCountTextView.setText(String.valueOf(contentDTOs.size()));

                // contentUidList 사용 예시
                for (String uid : contentUidList) {
                    // contentUidList에서 각 고유 식별자를 활용하는 작업 수행
                }

                notifyDataSetChanged();
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
            int itemWidth = screenWidth / 3;
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypage, parent, false);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, itemWidth));
            return new CustomViewHolder(itemView);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            ImageView imageView;
            TextView textView;

            CustomViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_mypage_image);
                textView = itemView.findViewById(R.id.item_mypage_text);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 삭제할 아이템의 position을 얻어옴
                    ContentDTO contentDTO = contentDTOs.get(position);
                    // 삭제 동작 수행
                    if (uid.equals(currentUserUid)) {
                        // Mypage의 경우 게시물 삭제 가능
                        deleteItem(contentDTO);
                    }
                }
                return true;
            }

            private void deleteItem(ContentDTO contentDTO) {
                // Firestore에서 해당 아이템을 삭제
                String documentId = contentDTO.getDocumentId(); // Firestore 문서 ID를 얻어옴
                firestore.collection("images").document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Firestore에서 삭제 성공
                                Toast.makeText(getContext(), "아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                // RecyclerView에서 아이템 제거
                                int position = contentDTOs.indexOf(contentDTO);
                                if (position != -1) {
                                    contentDTOs.remove(position);
                                    notifyItemRemoved(position);
                                }
                                // Storage에서 사진 파일 삭제
                                String imageUrl = contentDTO.getImageUrl();
                                if (imageUrl != null) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                // Storage에서 사진 파일 삭제 성공
                                            })
                                            .addOnFailureListener(e -> {
                                                // Storage에서 사진 파일 삭제 실패
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Firestore에서 삭제 실패
                                Toast.makeText(getContext(), "아이템 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                // RecyclerView에서 아이템 제거
                int position = contentDTOs.indexOf(contentDTO);
                if (position != -1) {
                    contentDTOs.remove(position);
                    notifyItemRemoved(position);
                }
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder viewHolder = (CustomViewHolder) holder;
            ContentDTO contentDTO = contentDTOs.get(position);

           if (contentDTO.getImageUrl() != null) {
               viewHolder.imageView.setVisibility(View.VISIBLE);
               viewHolder.textView.setVisibility(View.GONE);
               Glide.with(holder.itemView.getContext())
                       .load(contentDTO.getImageUrl())
                       .apply(RequestOptions.centerCropTransform())
                       .into(viewHolder.imageView);
           } else {
               viewHolder.imageView.setVisibility(View.GONE);
               viewHolder.textView.setVisibility(View.VISIBLE);
               viewHolder.textView.setText(contentDTO.getExplain());
           }


            viewHolder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(
                        getContext(), CommentActivity.class);
                intent.putExtra("contentId", contentUidList.get(position));   //사진과 텍스트
                intent.putExtra("contentUid", contentUidList.get(position));    //댓글
                startActivity(intent);
            });
            /*
            // 아이템을 클릭했을 때 디테일 페이지로 이동
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("contentId", contentDTO.getDocumentId());
                    view.getContext().startActivity(intent);
                }
            });
            */
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }

    private void getFollowerAndFollowing() {
        firestore.collection("users").document(uid).addSnapshotListener((documentSnapshot, firebaseFirestoreException) -> {
            if (documentSnapshot == null) return;
            FollowDTO followDTO = documentSnapshot.toObject(FollowDTO.class);
            if (followDTO != null) {
                Integer followingCount = followDTO.getFollowingCount();
                if (followingCount != null && followingCount != 0) {
                    TextView followingCountTextView = (TextView) fragmentView.findViewById(R.id.account_tv_following_count);
                    followingCountTextView.setText(String.valueOf(followingCount));
                }
                Integer followerCount = followDTO.getFollowerCount();
                if (followerCount != null) {
                    TextView followerCountTextView = (TextView) fragmentView.findViewById(R.id.account_tv_follower_count);
                    followerCountTextView.setText(String.valueOf(followerCount));
                    if (followDTO.getFollowers().containsKey(currentUserUid)) {
                        Button addPostButton = (Button) fragmentView.findViewById(R.id.add_post);
                        if (getActivity() != null) {
                            addPostButton.setText(getActivity().getString(R.string.follow_cancel));
                            addPostButton.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.green), PorterDuff.Mode.MULTIPLY);
                        }
                    } else {
                        if (!uid.equals(currentUserUid)) {
                            Button addPostButton = (Button) fragmentView.findViewById(R.id.add_post);
                            if (getActivity() != null) {
                                addPostButton.setText(getActivity().getString(R.string.follow));
                                addPostButton.getBackground().setColorFilter(null);
                            }
                        }
                    }
                }
            }
        });
    }

    private void requestFollow() {
        // My nickname
        String myNickname = DataLoadingActivity.nickname;

        // Get the nickname of the user I want to follow
        mDatabaseRef.child("UserAccount").child(uid).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userNickname = dataSnapshot.getValue(String.class);

                // Save data to my account
                DocumentReference tsDocFollowing = firestore.collection("users").document(currentUserUid);
                firestore.runTransaction(transaction -> {
                    FollowDTO followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO.class);
                    if (followDTO == null) {
                        followDTO = new FollowDTO();
                        followDTO.setFollowingCount(1);
                        followDTO.getFollowings().put(uid, true);
                        followDTO.getFollowingNicknames().put(uid, userNickname);  // Save the nickname

                        transaction.set(tsDocFollowing, followDTO);
                        return null;
                    }

                    if (followDTO.getFollowings().containsKey(uid)) {
                        // It removes following third person when a third person follows me
                        followDTO.setFollowingCount(followDTO.getFollowingCount() - 1);
                        followDTO.getFollowings().remove(uid);
                        followDTO.getFollowingNicknames().remove(uid);  // Remove the nickname
                    } else {
                        // It adds following third person when a third person does not follow me
                        followDTO.setFollowingCount(followDTO.getFollowingCount() + 1);
                        followDTO.getFollowings().put(uid, true);
                        followDTO.getFollowingNicknames().put(uid, userNickname);  // Save the nickname
                    }
                    transaction.set(tsDocFollowing, followDTO);
                    return null;
                });

                // Save data to third account
                DocumentReference tsDocFollower = firestore.collection("users").document(uid);
                firestore.runTransaction(transaction -> {
                    FollowDTO followDTO = transaction.get(tsDocFollower).toObject(FollowDTO.class);
                    if (followDTO == null) {
                        followDTO = new FollowDTO();
                        followDTO.setFollowerCount(1);
                        followDTO.getFollowers().put(currentUserUid, true);
                        followDTO.getFollowerNicknames().put(currentUserUid, myNickname);  // Save my nickname

                        transaction.set(tsDocFollower, followDTO);
                        return null;
                    }

                    if (followDTO.getFollowers().containsKey(currentUserUid)) {
                        // It cancels my follower when I follow a third person
                        followDTO.setFollowerCount(followDTO.getFollowerCount() - 1);
                        followDTO.getFollowers().remove(currentUserUid);
                        followDTO.getFollowerNicknames().remove(currentUserUid);  // Remove my nickname
                    } else {
                        // It adds my follower when I don't follow a third person
                        followDTO.setFollowerCount(followDTO.getFollowerCount() + 1);
                        followDTO.getFollowers().put(currentUserUid, true);
                        followDTO.getFollowerNicknames().put(currentUserUid, myNickname);  // Save my nickname
                    }
                    transaction.set(tsDocFollower, followDTO);
                    return null;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}