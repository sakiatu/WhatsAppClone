package com.beecoder.whatsapp.chatMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.beecoder.whatsapp.R;
import com.beecoder.whatsapp.chat.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private String chatId;
    private EditText message_edt;
    private Button send;
    private DatabaseReference chatDB;
    private ArrayList<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chatId = getIntent().getStringExtra("chatId");
        Log.i("TAG", "onCreate: " + chatId);
        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);

        message_edt = findViewById(R.id.message);
        send = findViewById(R.id.send);
        send.setOnClickListener(v -> sendMessage());
        loadMessages();
        getChatMessages();

    }

    private void loadMessages() {
        recyclerView = findViewById(R.id.recycler_message);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
    }

    private void getChatMessages() {

        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = "", creator = "";
                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("creator").getValue() != null) {
                        creator = dataSnapshot.child("creator").getValue().toString();
                    }
                    messages.add(new Message(dataSnapshot.getKey(), creator, message));
                    layoutManager.scrollToPosition(messages.size() - 1);
                    adapter.notifyDataSetChanged();
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
        if (!message_edt.getText().toString().isEmpty()) {
            DatabaseReference newMsgDB = chatDB.push();
            Map newMsgMap = new HashMap();
            newMsgMap.put("text", message_edt.getText().toString());
            newMsgMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMsgDB.updateChildren(newMsgMap);
        }
        message_edt.setText(null);
    }
}
