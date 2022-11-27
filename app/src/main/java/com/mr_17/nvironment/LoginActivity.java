package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText userEmail, userPassword;
    private TextView signupLink, forgotPasswordLink;

    private FirebaseAuth myAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields(); // initializing all elements

        // checking clicks respective buttons and text
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserTolLogin();
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword(v);
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToActivity(SignUpActivity.class, true);
            }
        });
    }

    private void InitializeFields()
    {
        loginButton = findViewById(R.id.login_button);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        signupLink = findViewById(R.id.signup_link);
        forgotPasswordLink = findViewById(R.id.forgot_password);

        loadingBar = new ProgressDialog(this);

        myAuth = FirebaseAuth.getInstance();
    }

    private void AllowUserTolLogin()
    {
        // storing the email and password as String
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        // checking that none of the fields are empty and giving appropriate toasts
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Email required...", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password required...", Toast.LENGTH_LONG).show();
        }
        else
        {
            // setting up the loading bar
            loadingBar.setTitle(("Logging In"));
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            // attempting to sign in using firebase
            myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        // sending user to main activity on successful sign in
                        SendToActivity(MainActivity.class, false);
                        //Toast.makeText(LoginActivity.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        // otherwise displaying the error message
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void ForgotPassword(View v)
    {
        // edit text for the alert dialog
        EditText resetEmail = new EditText(v.getContext());
        resetEmail.setHint("Email Address");

        // setting up the alert dialog
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("Reset Password ?");
        passwordResetDialog.setMessage("Enter your e-mail to receive the reset link.");
        passwordResetDialog.setView(resetEmail);

        // creating functionality of the "yes" button
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // getting the email text from the edit text
                String mail = resetEmail.getText().toString();
                // checking for empty edit text
                if(TextUtils.isEmpty(mail))
                {
                    Toast.makeText(LoginActivity.this, "Email required...", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // resetting the password using firebase
                    myAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Reset link has been sent to your e-mail.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // displaying a toast on failure
                            String message = e.getMessage();
                            Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        // creating the "no" button
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        passwordResetDialog.create().show();

    }

    public void ShowHidePass(View v)
    {
        if (v.getId() == R.id.show_hide_pass)
        {
            // displaying the password as it is hidden
            if (userPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_open); //setting the drawable
                userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            // hiding the password as it is displayed
            else
            {
                ((ImageView) (v)).setImageResource(R.drawable.eye_closed); //setting the drawable
                userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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