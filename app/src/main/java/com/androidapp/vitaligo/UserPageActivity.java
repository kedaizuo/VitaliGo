package com.androidapp.vitaligo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserPageActivity extends AppCompatActivity {
    ImageButton mainpageButton;
    ImageButton communityButton;
    ImageButton historyButton;
    ImageButton userOwnCommunityButton;
    ImageButton updateProfileButton;
    ImageButton updatePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        TextView sampleTextView = findViewById(R.id.sampleText);
        TextView sampleTextViewUsername = findViewById(R.id.sampleTextUserName);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        sampleTextView.setText(currentUser.getEmail());


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    sampleTextViewUsername.setText(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
            }
        });

        communityButton = findViewById(R.id.btnNav3);
        historyButton = findViewById(R.id.btnNav2);
        mainpageButton = findViewById(R.id.btnNav1);
        userOwnCommunityButton = findViewById(R.id.rightImageButton);
        updateProfileButton = findViewById(R.id.leftImageButton);
        updatePasswordButton = findViewById(R.id.leftbuttomImageButton);
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPageActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserPageActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserPageActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userOwnCommunityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPageActivity.this, UserCommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPageActivity.this, UpdateUserProfile.class);
                startActivity(intent);
                finish();
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPageActivity.this, UpdatePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MapsActivity.class));
        Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
        finish();
    }
}
