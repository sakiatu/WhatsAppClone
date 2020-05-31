package com.beecoder.whatsapp.chatMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beecoder.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_INTENT = 101;
    private String chatId;
    private EditText message_edt;
    private Button send;
    private ImageButton addMedia;
    private DatabaseReference chatDB;
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Media> medias = new ArrayList<>();
    private RecyclerView textMessageListView, mediaListView;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapter messageAdapter;
    private MediaAdapter mediaAdapter;
    private String chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chatId = getIntent().getStringExtra("chatId");
        chatName = getIntent().getStringExtra("chatName");

        setToolbar();

        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);

        message_edt = findViewById(R.id.message);
        send = findViewById(R.id.send);
        addMedia = findViewById(R.id.addMedia);
        send.setOnClickListener(v -> sendMessage());
        addMedia.setOnClickListener(v -> openGallery());
        loadMessages();
        loadMedias();
        getChatMessages();

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        title.setText(chatName);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    medias.add(new Media(data.getData().toString()));
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        String uri = data.getClipData().getItemAt(i).getUri().toString();
                        medias.add(new Media(uri));
                        Log.i("media", "onActivityResult: " + medias.get(i).getUri());
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadMedias() {
        mediaListView = findViewById(R.id.recycler_media);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mediaListView.setHasFixedSize(true);
        mediaListView.setLayoutManager(layoutManager);
        mediaAdapter = new MediaAdapter(this, medias);
        mediaListView.setAdapter(mediaAdapter);
        mediaAdapter.setOnItemClickListener(null);
        mediaAdapter.setOnItemClickListener(position -> showImage(medias.get(position).getUri()));
    }

    private void loadMessages() {
        textMessageListView = findViewById(R.id.recycler_message);
        layoutManager = new LinearLayoutManager(this);
        textMessageListView.setHasFixedSize(true);
        textMessageListView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messages, this);
        textMessageListView.setAdapter(messageAdapter);
        messageAdapter.setOnItemClickListener(position -> showImage(messages.get(position).getImageUrl()));
    }

    private void showImage(String url) {
        if (url != null) {
            Intent intent = new Intent(this, ImageViewerActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    private void getChatMessages() {

        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = "", creator = "";
                    if (dataSnapshot.child("creator").getValue() != null) {
                        creator = dataSnapshot.child("creator").getValue().toString();
                    }
                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                        messages.add(new Message(dataSnapshot.getKey(), creator, message));
                        messageAdapter.notifyDataSetChanged();
                    }

                    if (dataSnapshot.child("media").getChildrenCount() > 0) {
                        for (DataSnapshot mediaSnapshot :
                                dataSnapshot.child("media").getChildren()) {
                            messages.add(new Message(dataSnapshot.getKey(), creator, "", mediaSnapshot.getValue().toString()));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    textMessageListView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {
        if (!message_edt.getText().toString().isEmpty() | !medias.isEmpty()) {
            Map newMsgMap = new HashMap();
            String messageId;
            newMsgMap.put("creator", FirebaseAuth.getInstance().getUid());

            DatabaseReference newMsgDB;
            ArrayList<String> mediaIds = new ArrayList();
            messageId = chatDB.push().getKey();
            newMsgDB = chatDB.child(messageId);


            if (!message_edt.getText().toString().isEmpty()) {
                newMsgMap.put("text", message_edt.getText().toString());
                newMsgDB.updateChildren(newMsgMap);
                message_edt.setText(null);
            }
            if (!medias.isEmpty()) {
                for (int i = 0; i < medias.size(); i++) {
                    String mediaId = newMsgDB.child("media").push().getKey();
                    mediaIds.add(mediaId);

                    StorageReference filePath =
                            FirebaseStorage.getInstance().getReference()
                                    .child("chat")
                                    .child(chatId)
                                    .child(messageId)
                                    .child(mediaId);
                    UploadTask uploadTask = filePath.putFile(Uri.parse(medias.get(i).getUri()));

                    int finalI = i;
                    DatabaseReference finalNewMsgDB = newMsgDB;
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                            newMsgMap.put("/media/" + mediaIds.get(finalI) + "/", uri.toString());

                            if (finalI == mediaIds.size() - 1) {
                                finalNewMsgDB.updateChildren(newMsgMap);
                            }
                        });
                    });
                }
                medias.clear();
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }
}
