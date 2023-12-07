package com.androidapp.vitaligo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class UserPageActivity extends AppCompatActivity {
    ImageButton mainpageButton;
    ImageButton communityButton;
    ImageButton historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        communityButton = findViewById(R.id.btnNav3);
        historyButton = findViewById(R.id.btnNav2);
        mainpageButton = findViewById(R.id.btnNav1);

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
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MapsActivity.class));
        Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
        finish();
    }
}
