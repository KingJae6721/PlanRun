package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private String contentUid,contentId;
    private EditText comment_edit_message;
    private TextView textView;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private CommentRecyclerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        contentUid = getIntent().getStringExtra("contentUid");
        contentId = getIntent().getStringExtra("contentId");
        comment_edit_message = findViewById(R.id.comment_edit_message);

        RecyclerView commentRecyclerView = findViewById(R.id.comment_recyclerview);
        adapter = new CommentRecyclerviewAdapter();
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.detail_text);
        imageView = findViewById(R.id.detail_image);

        //이미지랑 텍스트 띄우기

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
                                        textView.setText(text);
                                        textView.setVisibility(View.VISIBLE);
                                    } else {
                                        textView.setVisibility(View.GONE);
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

        //댓글
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
                            comments.add(snapshot.toObject(ContentDTO.Comment.class));
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

            CustomViewHolder(View itemView) {
                super(itemView);
                commentTextView = itemView.findViewById(R.id.commentviewitem_textview_comment);
                profileTextView = itemView.findViewById(R.id.commentviewitem_textview_profile);
                profileImageView = itemView.findViewById(R.id.commentviewitem_imageview_profile);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            customViewHolder.commentTextView.setText(comments.get(position).getComment());
            customViewHolder.profileTextView.setText(comments.get(position).getUserId());


            FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(comments.get(position).getUid())
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
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }
}