package com.mr_17.nvironment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseModel
{
    public static String node_users = "Users",
            node_uid = "uid",
            node_name = "name",
            node_firstName = "firstName",
            node_lastName = "lastName",
            node_movementName = "movementName",
            node_joiningDate = "joiningDate",
            node_posts = "Posts",
            node_description = "description",
            node_videoUrl = "videoUrl";

    public static DatabaseReference databaseRef_root = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference databaseRef_users = databaseRef_root.child(node_users);
    public static DatabaseReference databaseRef_posts = databaseRef_root.child(node_posts);

    public static StorageReference storageRef_root = FirebaseStorage.getInstance().getReference();
    public static StorageReference storageRef_posts = storageRef_root.child(node_posts);
}