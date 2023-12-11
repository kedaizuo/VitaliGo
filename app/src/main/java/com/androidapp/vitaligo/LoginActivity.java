package com.androidapp.vitaligo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageButton mainpageButton;
    private ImageButton historyButton;
    private ImageButton communityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mainpageButton = findViewById(R.id.btnNav1);
        historyButton = findViewById(R.id.btnNav2);
        communityButton = findViewById(R.id.btnNav3);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mainpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void login(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(txtEmail , txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "User name and " +
                        "Password match", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this , UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void goRegister(View view) {
        Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}

