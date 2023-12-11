package com.androidapp.vitaligo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCommunityActivity extends AppCompatActivity {
    private ListView postsListView;
    private List<CommunityItem> userPosts;
    private CommunityItemAdapter adapter;
    private ImageButton historyButton;
    private ImageButton mainPageButton;
    private ImageButton userpageButton;
    private ImageButton communityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_own_community);

        postsListView = findViewById(R.id.lvCommunityData);
        userPosts = new ArrayList<>();
        adapter = new CommunityItemAdapter(this, userPosts);
        postsListView.setAdapter(adapter);

        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);
        communityButton = findViewById(R.id.btnNav3);


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCommunityActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserCommunityActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
            Query postsQuery = databaseReference.orderByChild("postInformation/publisherID").equalTo(userId);

            postsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userPosts.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot postInfoSnapshot = postSnapshot.child("postInformation");
                        CommunityItem post = postInfoSnapshot.getValue(CommunityItem.class);
                        if (post != null) {
                            userPosts.add(post);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }

        postsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CommunityItem selectedItem = userPosts.get(position);
                showDeleteConfirmationDialog(selectedItem);
                return true;
            }
        });

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommunityItem selectedItem = userPosts.get(position);
                openPostDetails(selectedItem);
            }
        });

    }

    private void openPostDetails(CommunityItem item) {
        Intent intent = new Intent(UserCommunityActivity.this, UserCommunityDetailsActivity.class);
        intent.putExtra("topic", item.getTopic());
        intent.putExtra("content", item.getContent());
        intent.putExtra("author", item.getPublisher());
        intent.putExtra("authorID", item.getPublisherID());
        intent.putExtra("postID", item.getPostID());
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog(CommunityItem item) {
        new AlertDialog.Builder(UserCommunityActivity.this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post and all its comments?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(item);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(CommunityItem item) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.child(item.getPostID()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserCommunityActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();
                userPosts.remove(item);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(UserCommunityActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
