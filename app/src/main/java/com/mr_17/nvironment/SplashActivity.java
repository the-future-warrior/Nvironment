package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth myAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        InitializeFields();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent intent;
                if(currentUser == null)
                {
                    intent = new Intent(SplashActivity.this, SignUpActivity.class);

                }
                else
                {
                    CheckJoining();
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private void InitializeFields()
    {
        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser();
    }

    private void CheckJoining()
    {
        FirebaseModel.databaseRef_users.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child(FirebaseModel.node_joiningDate).exists()))
                {
                    FirebaseModel.databaseRef_users.child(currentUser.getUid()).child(FirebaseModel.node_joiningDate).setValue(currentUser.getMetadata().getCreationTimestamp());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}