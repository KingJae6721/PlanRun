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

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_mypage, container, false);
        TextView mypage_nickname = fragmentView.findViewById(R.id.mypage_nickname);
        mypage_nickname.setText(DataLoadingActivity.nickname);

        Button edit_profile = (Button) fragmentView.findViewById(R.id.edit_profile);
        ImageView setting = fragmentView.findViewById(R.id.setting);
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
                        mypage_nickname.setText(nickname);
                        list_of_posts.setText(nickname + "게시물");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }

        RecyclerView recyclerView = fragmentView.findViewById(R.id.account_reyclerview);
        recyclerView.setAdapter(new UserFragmentRecyclerViewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return fragmentView;
    }

    private class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
        private boolean isMyPage; // 사용자 페이지 여부를 판단하는 플래그

        UserFragmentRecyclerViewAdapter() {
            firestore.collection("images").whereEqualTo("uid", uid).addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                if (querySnapshot == null) return;
                contentDTOs.clear();
                for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                    ContentDTO contentDTO = snapshot.toObject(ContentDTO.class);
                    contentDTO.setDocumentId(snapshot.getId()); // documentId 설정 추가
                    contentDTOs.add(contentDTO);
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
                //fragmentView.findViewById(R.id.account_tv_post_count).setText(String.valueOf(contentDTOs.size()));
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

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
            ImageView imageView;
            TextView textView;

            CustomViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_mypage_image);
                textView = itemView.findViewById(R.id.item_mypage_text);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 클릭된 아이템의 position을 얻어옵니다.
                    ContentDTO contentDTO = contentDTOs.get(position);
                    // 상세 페이지로 이동하는 동작 수행
                    moveToDetailPage(contentDTO);
                }
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


            private void moveToDetailPage(ContentDTO contentDTO) {
                // 상세 페이지로 이동하는 인텐트를 생성합니다.
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                // 필요한 데이터를 인텐트에 추가합니다. 예를 들어, contentDTO의 ID를 전달할 수 있습니다.
                intent.putExtra("contentId", contentDTO.getDocumentId());
                startActivity(intent);
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


}