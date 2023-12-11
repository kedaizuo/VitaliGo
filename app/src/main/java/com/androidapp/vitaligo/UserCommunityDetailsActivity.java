package com.androidapp.vitaligo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCommunityDetailsActivity extends AppCompatActivity {
    private List<String> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private TextView tvTopic;
    private TextView tvContent;
    private TextView tvAuthor;

    private ImageButton historyButton;
    private ImageButton mainPageButton;
    private ImageButton userpageButton;
    private ImageButton communityButton;
    private Button deletePostButton;

    private String topic;
    private String content;
    private String author;
    private String authorID;
    private String postID;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_own_post_detail);

        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);
        communityButton = findViewById(R.id.btnNav3);

        listView = findViewById(R.id.lvComments);

        deletePostButton = findViewById(R.id.DeletePost);
        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCommunityDetailsActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityDetailsActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityDetailsActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityDetailsActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        content = intent.getStringExtra("content");
        author = intent.getStringExtra("author");
        authorID = intent.getStringExtra("authorID");
        postID = intent.getStringExtra("postID");

        tvTopic = findViewById(R.id.tvPostTitle);
        tvContent = findViewById(R.id.tvPostContent);
        tvAuthor = findViewById(R.id.tvPostAuthor);

        tvTopic.setText(topic);
        tvContent.setText(content);
        tvAuthor.setText(author);

        displayPostData();


    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post and all its comments?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePostAndComments();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayPostData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        String postId = postID;  // 当前帖子的ID

        databaseReference.child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CommentItem> items = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    CommentItem comment = commentSnapshot.getValue(CommentItem.class);
                    if (comment != null) {
                        items.add(comment);
                    }
                }
                CommentAdapter adapter = new CommentAdapter(UserCommunityDetailsActivity.this, items);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 处理可能发生的错误
            }
        });
    }

    private void deletePostAndComments() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.child(postID).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserCommunityDetailsActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UserCommunityDetailsActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(this, UserCommunityActivity.class);
        startActivity(intent);
        finish();
    }

}
