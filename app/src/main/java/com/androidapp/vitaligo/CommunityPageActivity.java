package com.androidapp.vitaligo;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class CommunityPageActivity extends AppCompatActivity {
    ImageButton mainPageButton;
    ImageButton historyButton;
    ImageButton userpageButton;
    FloatingActionButton fab;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_page);


        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);

        listView = findViewById(R.id.lvCommunityData);


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityPageActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(CommunityPageActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(CommunityPageActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fab = findViewById(R.id.fab);
        if(currentUser == null){
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPageActivity.this);
                    View dialogView = LayoutInflater.from(CommunityPageActivity.this).inflate(R.layout.dialog_custom, null);
                    EditText input_Topic = dialogView.findViewById(R.id.dialog_input_Topic);
                    EditText input_Content = dialogView.findViewById(R.id.dialog_input_Content);
                    builder.setView(dialogView);

                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String topic_text = input_Topic.getText().toString();
                            String content_text = input_Content.getText().toString();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                            String postId = databaseReference.push().getKey();
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = auth.getCurrentUser();

                            if (currentUser != null) {
                                String authorId = currentUser.getUid();
                                String authorName = currentUser.getDisplayName();
                                CommunityItem post = new CommunityItem(authorName, topic_text, content_text, authorId, postId);
                                databaseReference.child(postId).child("postInformation").setValue(post);
                                displayCommunityData();
                            } else {
                                Toast.makeText(CommunityPageActivity.this, "You need to login to post.", Toast.LENGTH_SHORT).show();
                            }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommunityItem item = (CommunityItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(CommunityPageActivity.this, DetailActivity.class);
                intent.putExtra("topic", item.getTopic());
                intent.putExtra("content", item.getContent());
                intent.putExtra("author", item.getPublisher());
                intent.putExtra("authorID", item.getPublisherID());
                intent.putExtra("postID", item.getPostID());
                startActivity(intent);

            }
        });
        displayCommunityData();
    }

    private void displayCommunityData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CommunityItem> items = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot postInfoSnapshot = postSnapshot.child("postInformation");

                    CommunityItem post = postInfoSnapshot.getValue(CommunityItem.class);
                    if (post != null) {
                        items.add(post);
                    }
                }
                CommunityItemAdapter adapter = new CommunityItemAdapter(CommunityPageActivity.this, items);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
