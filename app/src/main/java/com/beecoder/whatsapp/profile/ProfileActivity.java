package com.beecoder.whatsapp.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beecoder.whatsapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int PICK_PROFILE_PICTURE_INTENT = 123;
    private ImageView profileImage;
    private String userId;
    private String url;
    private StorageReference filePath;
    private DatabaseReference userIdRef;
    private ProgressBar progressBar;
    private TextView profileName,profileNumber;
    private Button editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_picture);
        progressBar = findViewById(R.id.progressBar);
        profileName = findViewById(R.id.profile_name);
        profileNumber = findViewById(R.id.profile_number);
        editName = findViewById(R.id.edit_name);
        editName.setOnClickListener(v->showEditDialog());

        userId = FirebaseAuth.getInstance().getUid();
        filePath = FirebaseStorage.getInstance().getReference().child("profilePictures").child(userId);
        userIdRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        getProfileInfo();
        userIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    url = dataSnapshot.child("profilePicture").getValue(String.class);
                    Glide.with(ProfileActivity.this).load(url).circleCrop().placeholder(R.drawable.icon_people).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(v -> showImage());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
        EditText name = layout.findViewById(R.id.edit_name_edt);
        builder.setView(layout);

        builder.setPositiveButton("Change", (a, b) -> changeProfileName(name.getText().toString()))
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void changeProfileName(String name) {
        userIdRef.child("name").setValue(name);
        userIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    profileName.setText(dataSnapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_image_view, null);
        ImageView picture = layout.findViewById(R.id.dialog_image);
        builder.setView(layout);
        Glide.with(this).load(url).fitCenter().placeholder(R.drawable.icon_photo).into(picture);
        builder.setPositiveButton("Change", (a, b) -> openGallery())
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_PROFILE_PICTURE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PROFILE_PICTURE_INTENT) {
                Log.i(TAG, "onActivityResult: " + "like it\t" + data.getData());
                changeProfilePicture(data.getData());
            }
        }
    }

    private void changeProfilePicture(Uri contentUri) {
        progressBar.setVisibility(View.VISIBLE);
        if (contentUri != null)
            filePath.putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
                Log.i(TAG, "contentUri: \n" + contentUri);
                filePath.getDownloadUrl().addOnSuccessListener(url -> {
                    Log.i(TAG, "url:\n " + url);
                    Map map = new HashMap();
                    map.put("profilePicture", url.toString());
                    userIdRef.updateChildren(map);
                    progressBar.setVisibility(View.GONE);
                });
            });
    }
    private void getProfileInfo() {
        userIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profileNumber.setText(dataSnapshot.child("number").getValue(String.class));
                    profileName.setText(dataSnapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}