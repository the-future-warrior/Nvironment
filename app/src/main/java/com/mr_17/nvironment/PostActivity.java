package com.mr_17.nvironment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private FirebaseAuth myAuth;
    private String currentUserID;
    private DatabaseReference rootRef;

    private TextView warningMsg, acceptedMsg;
    private EditText name, movementName, description;
    private Button attachButton, uploadButton;
    private Uri videoUri;
    private ProgressDialog progressDialog;

    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        InitializeFields();
        SetName();

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseVideo();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(PostActivity.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                UploadVideo();
            }
        });
    }

    private void InitializeFields()
    {
        myAuth = FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();
        rootRef = FirebaseModel.databaseRef_root;

        warningMsg = findViewById(R.id.warning_message);
        acceptedMsg = findViewById(R.id.accepted_message);
        name = findViewById(R.id.name);
        movementName = findViewById(R.id.movement_name);
        description = findViewById(R.id.description);
        attachButton = findViewById(R.id.attach_button);
        uploadButton = findViewById(R.id.upload_button);
    }

    private void SetName()
    {
        rootRef.child(FirebaseModel.node_users).child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    name.setText(snapshot.child(FirebaseModel.node_firstName).getValue().toString() + " " + snapshot.child(FirebaseModel.node_lastName).getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // choose a video from phone storage
    private void ChooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 5);
    }

    // startActivityForResult is used to receive the result, which is the selected video.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            warningMsg.setVisibility(View.INVISIBLE);
            acceptedMsg.setVisibility(View.VISIBLE);
            attachButton.setVisibility(View.INVISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            /*progressDialog.setTitle("Uploading...");
            progressDialog.show();
            UploadVideo();*/
        }
    }

    private String GetFileType(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void UploadVideo()
    {
        if (videoUri != null)
        {
            time = String.valueOf(System.currentTimeMillis());
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseModel.storageRef_posts.child(currentUserID).child(time + "." + GetFileType(videoUri)); //FirebaseStorage.getInstance().getReference("Posts/" + currentUserID + "." + GetFileType(videoUri));
            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    UploadData(downloadUri);
                    // Video uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Post Uploaded!", Toast.LENGTH_SHORT).show();

                    warningMsg.setVisibility(View.VISIBLE);
                    acceptedMsg.setVisibility(View.INVISIBLE);
                    attachButton.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.INVISIBLE);
                    description.setText("");
                    videoUri = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
        else
        {
            warningMsg.setVisibility(View.VISIBLE);
            acceptedMsg.setVisibility(View.INVISIBLE);
            attachButton.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.INVISIBLE);
        }
    }

    private void UploadData(String videoUrl)
    {
        String name_ = name.getText().toString();
        String movement_Name = movementName.getText().toString();
        String description_ = description.getText().toString();

        if (TextUtils.isEmpty(name_)) {
            Toast.makeText(this, "Name Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(movement_Name)) {
            Toast.makeText(this, "Movement Name Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(description_)) {
            Toast.makeText(this, "Description Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(videoUrl)) {
            Toast.makeText(this, "Video Required...", Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String, String> map = new HashMap<>();
            map.put(FirebaseModel.node_uid, currentUserID);
            map.put(FirebaseModel.node_name, name_);
            map.put(FirebaseModel.node_movementName, movement_Name);
            map.put(FirebaseModel.node_description, description_);
            map.put(FirebaseModel.node_videoUrl, videoUrl);

            rootRef.child(FirebaseModel.node_posts).child(currentUserID).child(time).setValue(map);
        }
    }
}