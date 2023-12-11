package com.androidapp.vitaligo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageButton mainpageButton;
    private ImageButton historyButton;
    private ImageButton communityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        mainpageButton = findViewById(R.id.btnNav1);
        historyButton = findViewById(R.id.btnNav2);
        communityButton = findViewById(R.id.btnNav3);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mainpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void register(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText username = findViewById(R.id.username);
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        String txtUsername = username.getText().toString();

        //Initialize authentication variable
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(txtUsername)
                                .build();
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                            HashMap<String, String> userMap = new HashMap<>();
                                            userMap.put("username", txtUsername);
                                            userMap.put("email", txtEmail);
                                            databaseReference.child(firebaseUser.getUid()).setValue(userMap);

                                            Intent intent = new Intent(RegisterActivity.this, UserPageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to update user profile", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void backToLoginPage(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
