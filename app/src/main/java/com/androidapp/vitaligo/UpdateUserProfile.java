package com.androidapp.vitaligo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdateUserProfile extends AppCompatActivity {

    private ImageButton historyButton;
    private ImageButton mainPageButton;
    private ImageButton userpageButton;
    private ImageButton communityButton;
    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user);

        usernameEditText = findViewById(R.id.username);


        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);
        communityButton = findViewById(R.id.btnNav3);


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateUserProfile.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdateUserProfile.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdateUserProfile.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdateUserProfile.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void update(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String newUsername = usernameEditText.getText().toString();

            if (!newUsername.isEmpty()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();
                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User profile updated.", Toast.LENGTH_SHORT).show();

                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("username", newUsername);
                        usersRef.child(user.getUid()).updateChildren(updates);

                        updatePostsPublisherName(user.getUid(), newUsername);

                        Intent intent = new Intent(this, UserPageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }

    }

    public void backToUserPage(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void updatePostsPublisherName(String userId, String newUsername) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot postInfoSnapshot = postSnapshot.child("postInformation");
                    CommunityItem post = postInfoSnapshot.getValue(CommunityItem.class);
                    if (post != null && post.getPublisherID().equals(userId)) {
                        postSnapshot.child("postInformation").child("publisher").getRef().setValue(newUsername);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Updating posts publisher name failed: ", databaseError.toException());
            }
        });
    }
}
