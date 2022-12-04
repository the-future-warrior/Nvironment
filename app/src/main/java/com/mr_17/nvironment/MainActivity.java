package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth myAuth;
    private androidx.appcompat.widget.Toolbar toolbar;
    private CardView postButton, contributeButton, supportUsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeFields();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToActivity(PostActivity.class, true);
            }
        });

        supportUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToActivity(SupportUsActivity.class, true);
            }
        });

        contributeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToActivity(PaymentActivity.class, true);
            }
        });
    }

    private void InitializeFields()
    {
        myAuth = FirebaseAuth.getInstance();
        postButton = findViewById(R.id.post_activity_card);
        contributeButton = findViewById(R.id.contribute_card);
        supportUsButton = findViewById(R.id.support_us_card);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.home_nav_log_out)
                {
                    // setting up the alert dialog
                    final AlertDialog.Builder confirmLogoutDialog = new AlertDialog.Builder(MainActivity.this);
                    confirmLogoutDialog.setTitle("Confirm Logout");
                    confirmLogoutDialog.setMessage("Are sure you want to logout?");

                    // creating functionality of the "yes" button
                    confirmLogoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myAuth.signOut();
                            SendToActivity(LoginActivity.class, false);
                        }
                    });

                    // creating the "no" button
                    confirmLogoutDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    confirmLogoutDialog.create().show();
                }
                return true;
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