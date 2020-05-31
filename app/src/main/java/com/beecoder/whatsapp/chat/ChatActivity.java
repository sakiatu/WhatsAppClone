package com.beecoder.whatsapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.beecoder.whatsapp.R;
import com.beecoder.whatsapp.chatMessage.MessageActivity;
import com.beecoder.whatsapp.login.LoginActivity;
import com.beecoder.whatsapp.profile.ProfileActivity;
import com.beecoder.whatsapp.user.FindUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private ArrayList<Chat> chats = new ArrayList<>();
    private DatabaseReference users;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        users = FirebaseDatabase.getInstance().getReference().child("users");
        userId = FirebaseAuth.getInstance().getUid();

        setupToolbar();
        getPermissions();
        loadChatList();
        getUserChatsFromDatabase();
        findViewById(R.id.fab_main).setOnClickListener(v -> findChat());
    }

    private void getUserChatsFromDatabase() {
        DatabaseReference userChats = users.child(userId).child("chat");

        userChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String chatId = childSnapshot.getKey();

                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot uID : dataSnapshot.getChildren()) {
                                        //sender and receiver have same chatId , we need only the other user
                                        boolean isSenderUid = uID.getKey().equals(userId);
                                        Boolean hasChatId = uID.child("chat").child(chatId).getValue(Boolean.class);
                                        if (hasChatId == null)
                                            hasChatId = false; //if there is chat missing then it will produce null

                                        if (!isSenderUid && hasChatId) {
                                            String chatName = uID.child("name").getValue(String.class);
                                            String chatIcon = uID.child("profilePicture").getValue(String.class);
                                            if (!(chats.contains(new Chat(chatIcon,chatName, chatId)))) {
                                                Chat chat = new Chat(chatIcon,chatName, chatId);
                                                chats.add(chat);
                                                adapter.notifyDataSetChanged();
                                                setLastMessage(chatId, chat);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setLastMessage(String chatId, Chat chat) {
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("text").getValue() != null) {
                        chat.setLastChatMsg(dataSnapshot.child("text").getValue(String.class));
                        adapter.notifyDataSetChanged();
                    }
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

    private void loadChatList() {
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        recyclerView = findViewById(R.id.chatList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this,chats);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::startChatting);
    }

    private void startChatting(int position) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatId", chats.get(position).getChatId());
        intent.putExtra("chatName",chats.get(position).getChatName());
        startActivity(intent);
    }

    private void findChat() {
        startActivity(new Intent(this, FindUserActivity.class));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.log_out:
                logOut();
                break;
            case R.id.profile:
                openProfile();
                break;
        }
        return true;
    }

    private void openProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 101);
        }
    }
}
