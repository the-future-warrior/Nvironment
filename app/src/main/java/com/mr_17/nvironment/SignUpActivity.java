package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText firstName, lastName, userEmail, userPassword, confirmUserPassword;
    private TextView loginLink;

    private FirebaseAuth myAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitializeFields();

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToActivity(LoginActivity.class, false);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void InitializeFields()
    {
        myAuth = FirebaseAuth.getInstance();
        signUpButton = findViewById(R.id.signup_button);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        userEmail = findViewById(R.id.signup_email);
        userPassword = findViewById(R.id.signup_password);
        confirmUserPassword = findViewById(R.id.signup_confirm_password);
        loginLink = findViewById(R.id.login_link);

        loadingBar = new ProgressDialog(this);
    }

    private void CreateNewAccount()
    {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = confirmUserPassword.getText().toString();
        String first_Name = firstName.getText().toString();
        String last_Name = lastName.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Password Confirmation Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(first_Name)) {
            Toast.makeText(this, "First Name Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(last_Name)) {
            Toast.makeText(this, "Last Name Required...", Toast.LENGTH_LONG).show();
        }
        else if(!(password.equals(confirmPassword)))
        {
            Toast.makeText(this, "Passwords Mismatch...", Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating a new account for you...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            myAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                String currentUserID = myAuth.getCurrentUser().getUid();

                                HashMap<String, String> profileMap = new HashMap<>();
                                profileMap.put(FirebaseModel.node_uid, currentUserID);
                                profileMap.put(FirebaseModel.node_firstName, first_Name);
                                profileMap.put(FirebaseModel.node_lastName, last_Name);
                                profileMap.put(FirebaseModel.node_joiningDate, String.valueOf(myAuth.getCurrentUser().getMetadata().getCreationTimestamp()));

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                FirebaseModel.databaseRef_users.child(currentUserID).setValue(""); // test this
                                //rootRef.child("Programs").child("subTopics").child("VIII").setValue("");
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                FirebaseModel.databaseRef_users.child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        SendToActivity(LoginActivity.class, false);
                                        Toast.makeText(SignUpActivity.this, "Account Created Successfully.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                });
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    public void ShowHidePass(View v)
    {
        if (v.getId() == R.id.show_hide_pass)
        {
            if (userPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_open);
                userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_closed);
                userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        else if(v.getId() == R.id.show_hide_confirm_pass)
        {
            if (confirmUserPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_open);
                confirmUserPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_closed);
                confirmUserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
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