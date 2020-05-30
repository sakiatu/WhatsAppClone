package com.beecoder.whatsapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.beecoder.whatsapp.R;
import com.beecoder.whatsapp.chatMessage.MessageActivity;
import com.beecoder.whatsapp.user.Contact;
import com.beecoder.whatsapp.user.UserAdapter;
import com.beecoder.whatsapp.utils.CountryToPhonePrefix;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;

public class FindUserActivity extends AppCompatActivity {
    private UserAdapter adapter;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ProgressBar progressBar;
    private String uID;
    private ArrayList<String> chatKeys = new ArrayList<>();
    private Boolean hasChat = Boolean.TRUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        uID = FirebaseAuth.getInstance().getUid();
        loadUserList();
        adapter.setOnItemClickListener(this::chatWithSelected);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        while (true) {
            if (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                Log.i("contact name", "onCreate: " + name);
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                try {
                    if (!String.valueOf(number.charAt(0)).equals("+"))
                        number = getCountryISO() + number;
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    number = number.replace("(", "");
                    number = number.replace(")", "");
                } catch (Exception ignored) {
                }
                setContactToList(new Contact(name, number));
            } else {
                progressBar.setVisibility(View.GONE);
                break;
            }
        }
    }

    private void loadUserList() {
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_selectContact);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserAdapter(contacts);
        recyclerView.setAdapter(adapter);
    }

    private void chatWithSelected(int position) {
        Query userChatQuery = FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("chat");
        userChatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chatKey : dataSnapshot.getChildren()) {
                        chatKeys.add(chatKey.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Query receiverChatQuery = FirebaseDatabase.getInstance().getReference().child("users").child(contacts.get(position).getUid()).child("chat");
        receiverChatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot chatKey : dataSnapshot.getChildren()) {
                        ++i;
                        if (chatKeys.contains(chatKey.getKey())) {
                            //if true then there was a previous communication history between chats
                            String chatId = chatKey.getKey();
                            startWithChatId(chatId);
                            return;
                        }
                        if (i == dataSnapshot.getChildrenCount()) {
                            //if true then there was no communication history between chats
                            startChat(position);
                        }
                    }
                } else {
                    startChat(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setContactToList(Contact contact) {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        Query checkNumber = users.orderByChild("number").equalTo(contact.getNumber());
        checkNumber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        contact.setUid(childDataSnapshot.getKey());
                    }
                    contacts.add(contact);
                    adapter.notifyItemInserted(contacts.size() - 1);
                    Log.i("contact name", "onDataChange: " + contact.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getCountryISO() {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(this.TELEPHONY_SERVICE);

        String iso = telephonyManager.getNetworkCountryIso();
        if (iso != null && !iso.equals("")) {
            return CountryToPhonePrefix.getPhone(iso);
        }
        return "";
    }

    private void startChat(int position) {
        String chatId = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("users").child(contacts.get(position).getUid()).child("chat").child(chatId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("users").child(contacts.get(position).getUid()).child("chat").child(chatId).setValue(true);

        startWithChatId(chatId);
    }

    private void startWithChatId(String chatId) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
        finish();
    }
}
