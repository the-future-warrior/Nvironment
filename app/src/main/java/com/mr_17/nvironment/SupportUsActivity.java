package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SupportUsActivity extends AppCompatActivity {

    private FirebaseAuth myAuth;
    private String currentUserID;
    private DatabaseReference rootRef;

    private EditText fullName, email, thoughts;
    private Button submitButton;

    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        InitializeFields();
        SetNameAndEmail();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadData();
            }
        });
    }

    private void InitializeFields()
    {
        myAuth = FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();
        rootRef = FirebaseModel.databaseRef_root;

        fullName = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        thoughts = findViewById(R.id.thoughts);
        submitButton = findViewById(R.id.submit_button);
    }

    private void SetNameAndEmail()
    {
        rootRef.child(FirebaseModel.node_users).child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    fullName.setText(snapshot.child(FirebaseModel.node_firstName).getValue().toString() + " " + snapshot.child(FirebaseModel.node_lastName).getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        email.setText(myAuth.getCurrentUser().getEmail());
    }

    private void UploadData()
    {
        String name_ = fullName.getText().toString();
        String email_ = email.getText().toString();
        String thoughts_ = thoughts.getText().toString();

        if (TextUtils.isEmpty(name_)) {
            Toast.makeText(this, "Full Name Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(email_)) {
            Toast.makeText(this, "Email Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(thoughts_)) {
            Toast.makeText(this, "Thoughts Required...", Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String, String> map = new HashMap<>();
            map.put(FirebaseModel.node_uid, currentUserID);
            map.put(FirebaseModel.node_name, name_);
            map.put(FirebaseModel.node_email, email_);
            map.put(FirebaseModel.node_thoughts, thoughts_);

            time = String.valueOf(System.currentTimeMillis());

            FirebaseModel.databaseRef_supports.child(currentUserID).child(time).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SupportUsActivity.this, "Submitted Succesfully!", Toast.LENGTH_SHORT).show();
                    thoughts.setText("");
                    SendToActivity(ThankYouActivity.class, false);
                }
            });
        }
    }

    private void SendToActivity(Class<? extends Activity> activityClass, boolean backEnabled)
    {
        Intent intent = new Intent(this, activityClass);

        if (!backEnabled)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        if (!backEnabled)
            finish();
    }
}