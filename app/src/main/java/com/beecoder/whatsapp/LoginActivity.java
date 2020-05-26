package com.beecoder.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText countryCode_edt, phoneNumber_edt;
    private CountryCodePicker picker;
    private String countryCode;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logIn();

        countryCode_edt = findViewById(R.id.countryCode);
        phoneNumber_edt = findViewById(R.id.phoneNumber);
        picker = findViewById(R.id.countryCodePicker);
        nextBtn = findViewById(R.id.next);

        countryCode = "+" + picker.getDefaultCountryCode();
        countryCode_edt.setText(countryCode);

        picker.setOnCountryChangeListener(this::setCountryCode);

        nextBtn.setOnClickListener(v -> verifyPhoneNumber());
    }

    private void logIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void verifyPhoneNumber() {
        String phoneNumber = countryCode_edt.getText().toString() + phoneNumber_edt.getText().toString();
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);

    }

    private void setCountryCode() {
        countryCode = "+" + picker.getSelectedCountryCode();
        countryCode_edt.setText(countryCode);
        phoneNumber_edt.requestFocus();
    }
}
