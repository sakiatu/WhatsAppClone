package com.beecoder.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectUserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_selectContact);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserAdapter(contacts);
        recyclerView.setAdapter(adapter);

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


    private void setContactToList(Contact contact) {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        Query checkNumber = users.orderByChild("number").equalTo(contact.getNumber());
        checkNumber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
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
}
