package com.mr_17.nvironment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView signupLink, forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupLink = findViewById(R.id.signup_link);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToActivity(SignUpActivity.class, true);
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