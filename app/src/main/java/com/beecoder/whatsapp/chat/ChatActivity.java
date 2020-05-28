package com.beecoder.whatsapp.chat;

import androidx.annotation.NonNull;
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
import com.beecoder.whatsapp.chat.Chat;
import com.beecoder.whatsapp.chat.ChatAdapter;
import com.beecoder.whatsapp.chatMessage.MessageActivity;
import com.beecoder.whatsapp.login.LoginActivity;
import com.beecoder.whatsapp.user.FindUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private ArrayList<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        getPermissions();
        loadChatList();
        getUserChatsFromDatabase();
        findViewById(R.id.fab_main).setOnClickListener(v -> findChat());
    }

    private void getUserChatsFromDatabase() {
        DatabaseReference userChats = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("chat");

        userChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        chats.add(new Chat(childSnapshot.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                }
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
        adapter = new ChatAdapter(chats);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::startChatting);
    }

    private void startChatting(int position) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatId",chats.get(position).getChatId());
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
        if (menuItem.getItemId() == R.id.log_out) {
            logOut();
        }
        return true;
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 101);
        }
    }
}
