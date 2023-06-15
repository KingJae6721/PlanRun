package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private String contentUid, contentId;
    private EditText comment_edit_message;
    private TextView explainTextView;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private CommentRecyclerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        contentUid = getIntent().getStringExtra("contentUid");
        contentId = getIntent().getStringExtra("contentId");
        comment_edit_message = findViewById(R.id.comment_edit_message);

        RecyclerView commentRecyclerView = findViewById(R.id.comment_recyclerview);
        adapter = new CommentRecyclerviewAdapter();
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();

        //콘텐트형식 item_detail로 찍기
        LayoutInflater layoutInflater=getLayoutInflater();
        View additionalView = layoutInflater.inflate(R.layout.item_detail, null);
        LinearLayout linearLayout =findViewById(R.id.detail_image);

        imageView = additionalView.findViewById(R.id.detailviewitem_imageview_content);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        TextView profileTextView;
        ImageView imageViewContent,imageViewProfile,imageComment;
        TextView explainTextView;
        TextView favoriteCounterTextView;

            profileTextView = additionalView.findViewById(R.id.detailviewitem_profile_textview);

            explainTextView = additionalView.findViewById(R.id.detailviewitem_explain_textview);
            favoriteCounterTextView = additionalView.findViewById(R.id.detailviewitem_favoritecounter_textview);
            imageViewProfile = additionalView.findViewById(R.id.detailviewitem_profile_image);
            imageComment = additionalView.findViewById(R.id.detailviewitem_comment_imageview);

        linearLayout.addView(additionalView);
        // 이미지와 텍스트 표시
        if (contentId != null) {
            // Firestore에서 해당 문서를 가져오는 코드 추가
            firestore.collection("images").document(contentId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                ContentDTO contentDTO = documentSnapshot.toObject(ContentDTO.class);
                                if (contentDTO != null) {
                                    String imageUrl = contentDTO.getImageUrl();
                                    String text = contentDTO.getExplain();
                                    profileTextView.setText(contentDTO.getUserId());
                                    // 이미지와 텍스트를 화면에 표시
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Glide.with(CommentActivity.this)
                                                .load(imageUrl)
                                                .apply(RequestOptions.centerCropTransform())
                                                .into(imageView);
                                        imageView.setVisibility(View.VISIBLE);
                                    } else {
                                        imageView.setVisibility(View.GONE);
                                    }

                                    if (text != null && !text.isEmpty()) {
                                        explainTextView.setText(text);
                                        explainTextView.setVisibility(View.VISIBLE);
                                    } else {
                                        explainTextView.setVisibility(View.GONE);
                                    }

                                    // Explain of content
                                    explainTextView.setText(contentDTO.getExplain());

                                    // Likes
                                    favoriteCounterTextView.setText("Likes " + contentDTO.getFavoriteCount());
                                    explainTextView.setText(contentDTO.getExplain());

                                    // This code is when the button is clicked
                                    //좋아요버튼 막아놓음
                                    //additionalView.findViewById(R.id.detailviewitem_favorite_imageview).setOnClickListener(v -> favoriteEvent(position));

                                    // 프로필 이미지를 눌렀을 경우 이벤트처리
                                    imageViewProfile.setOnClickListener(v -> {
                                        MypageFragment mypageFragment = new MypageFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("destinationUid", contentDTO.getUid());
                                        bundle.putString("userId", contentDTO.getUserId());
                                        mypageFragment.setArguments(bundle);
                                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
                                    });

                                    // Comment 버튼 클릭 이벤트
                                    imageComment.setOnClickListener(v -> {

                                        comment_edit_message.requestFocus();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                                    });

                                    // This code is when the page is loaded
                                    ImageView favoriteImageView = additionalView.findViewById(R.id.detailviewitem_favorite_imageview);
                                    Map<String, Boolean> favorites = contentDTO.getFavorites();
                                    if (favorites != null && favorites.containsKey(contentUid)) {
                                        // This is like status
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite);
                                    } else {
                                        // This is unlike status
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 문서 가져오기 실패 처리
                        }
                    });
        }

        // 댓글 작성 버튼 클릭 이벤트 처리
        findViewById(R.id.comment_btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = comment_edit_message.getText().toString();
                if (!comment.isEmpty()) {
                    ContentDTO.Comment commentData = new ContentDTO.Comment();
                    commentData.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    commentData.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    commentData.setComment(comment);
                    commentData.setTimestamp(System.currentTimeMillis());

                    FirebaseFirestore.getInstance()
                            .collection("images")
                            .document(contentUid)
                            .collection("comments")
                            .document()
                            .set(commentData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    comment_edit_message.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 댓글 추가 실패 처리

                                }
                            });
                }
            }
        });
    }


    private class CommentRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO.Comment> comments;

        CommentRecyclerviewAdapter() {
            comments = new ArrayList<>();

            FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid)
                    .collection("comments")
                    .orderBy("timestamp")
                    .addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                        comments.clear();
                        if (querySnapshot == null) return;

                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            ContentDTO.Comment comment = snapshot.toObject(ContentDTO.Comment.class);
                            comment.setDocumentId(snapshot.getId());    //삭제를 위해
                            comments.add(comment);
                        }
                        notifyDataSetChanged();
                    });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomViewHolder(view);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView commentTextView;
            TextView profileTextView;
            ImageView profileImageView;

            ImageView imageViewContent,imageViewProfile,imageComment;
            TextView explainTextView;
            TextView favoriteCounterTextView;

            CustomViewHolder(View itemView) {
                super(itemView);
                commentTextView = itemView.findViewById(R.id.commentviewitem_textview_comment);
                profileTextView = itemView.findViewById(R.id.commentviewitem_textview_profile);
                profileImageView = itemView.findViewById(R.id.commentviewitem_imageview_profile);
                explainTextView = itemView.findViewById(R.id.detailviewitem_explain_textview);
                favoriteCounterTextView = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
                imageViewContent = itemView.findViewById(R.id.detailviewitem_imageview_content);
                imageViewProfile = itemView.findViewById(R.id.detailviewitem_profile_image);
                imageComment =itemView.findViewById(R.id.detailviewitem_comment_imageview);

                // 댓글 롱 클릭 이벤트 처리(댓글 삭제)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteComment(position);
                            return true;
                        }
                        return false;
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            ContentDTO.Comment comment = comments.get(position);

            customViewHolder.commentTextView.setText(comment.getComment());
            customViewHolder.profileTextView.setText(comment.getUserId());

            FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(comment.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String url = task.getResult().getString("image");
                            Glide.with(customViewHolder.itemView.getContext())
                                    .load(url)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(customViewHolder.profileImageView);
                        }
                    });

            customViewHolder.itemView.setTag(comment.getDocumentId());  //삭제를 위해
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private void deleteComment(int position) {
            ContentDTO.Comment comment = comments.get(position);

            FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid)
                    .collection("comments")
                    .document(comment.getDocumentId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 댓글 삭제 성공 처리
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 댓글 삭제 실패 처리
                        }
                    });
        }
    }
    private void favoriteEvent(int position) {
        DocumentReference tsDoc = firestore.collection("images").document(contentUid);
        firestore.runTransaction(transaction -> {
            ContentDTO contentDTO = transaction.get(tsDoc).toObject(ContentDTO.class);

            if (contentDTO.getFavorites().containsKey(contentUid)) {
                // When the button is clicked
                contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() - 1);
                contentDTO.getFavorites().remove(contentUid);
            } else {
                // When the button is not clicked
                contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() + 1);
                contentDTO.getFavorites().put(contentUid, true);
            }

            transaction.set(tsDoc, contentDTO);
            return null;
        });
    }
}