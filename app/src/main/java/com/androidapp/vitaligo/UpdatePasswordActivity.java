package com.androidapp.vitaligo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText previousPasswordField, newPasswordField;
    private Button updatePasswordButton;
    private FirebaseAuth firebaseAuth;

    private ImageButton historyButton;
    private ImageButton mainPageButton;
    private ImageButton userpageButton;
    private ImageButton communityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        previousPasswordField = findViewById(R.id.previousPassword);
        newPasswordField = findViewById(R.id.NewPassword);
        updatePasswordButton = findViewById(R.id.update);

        historyButton = findViewById(R.id.btnNav2);
        mainPageButton = findViewById(R.id.btnNav1);
        userpageButton = findViewById(R.id.btnNav4);
        communityButton = findViewById(R.id.btnNav3);


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdatePasswordActivity.this, HistoryPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdatePasswordActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userpageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdatePasswordActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        communityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdatePasswordActivity.this, CommunityPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String previousPassword = previousPasswordField.getText().toString();
                String newPassword = newPasswordField.getText().toString();

                if (!previousPassword.isEmpty() && !newPassword.isEmpty()) {
                    changeUserPassword(previousPassword, newPassword);
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeUserPassword(String oldPassword, String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(UpdatePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, UserPageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(UpdatePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "Authentication failed. Check your old password.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToUserPage(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        startActivity(intent);
        finish();
    }
}
