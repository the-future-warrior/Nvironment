package com.mr_17.nvironment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText firstName, lastName, userEmail, userPassword, confirmUserPassword;
    private TextView loginLink;
    private Spinner standard;

    //private FirebaseAuth myAuth;
    //private DatabaseReference rootRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loginLink = findViewById(R.id.login_link);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToActivity(LoginActivity.class, false);
            }
        });
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