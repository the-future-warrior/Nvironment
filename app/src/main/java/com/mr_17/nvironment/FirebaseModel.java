package com.mr_17.nvironment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseModel
{
    public static String node_users = "Users",
            node_uid = "uid",
            node_firstName = "firstName",
            node_lastName = "lastName",
            node_joiningDate = "joiningDate";

    public static DatabaseReference databaseRef_root = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference databaseRef_users = databaseRef_root.child(node_users);

    public static StorageReference storageRef_root = FirebaseStorage.getInstance().getReference();
}