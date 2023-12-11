package com.androidapp.vitaligo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private List<String> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private TextView tvTopic;
    private TextView tvContent;
    private TextView tvAuthor;

    private ImageButton historyButton;
    private ImageButton mainPageButton;
    private ImageButton userpageButton;
    private ImageButton communityButton;

    private String topic;
    private String content;
    private String author;
    private String authorID;
    private String postID;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_details);

        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);
        communityButton = findViewById(R.id.btnNav3);

        listView = findViewById(R.id.lvComments);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(DetailActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(DetailActivity.this, CommunityPageActivity.class);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    View dialogView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.dialog_custom_comment, null);
                    EditText input_Comments = dialogView.findViewById(R.id.dialog_input_Comments);
                    builder.setView(dialogView);

                    builder.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String commentText = input_Comments.getText().toString();
                            submitComment(commentText);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }

        displayPostData();
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
                CommentAdapter adapter = new CommentAdapter(DetailActivity.this, items);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void submitComment(String commentText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        String postId = postID;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();
            CommentItem newComment = new CommentItem(userID, commentText);
            databaseReference.child(postId).child("comments").push().setValue(newComment);
        } else {

        }
    }
}
