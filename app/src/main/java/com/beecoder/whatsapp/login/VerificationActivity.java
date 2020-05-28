package com.beecoder.whatsapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beecoder.whatsapp.chat.ChatActivity;
import com.beecoder.whatsapp.R;
import com.beecoder.whatsapp.user.Contact;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private Button verify_btn;
    private EditText code_edt;
    private String verificationCode;
    private String phoneNumber;
    private TextView remainingText;
    private int seconds = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verify_btn = findViewById(R.id.verify);
        code_edt = findViewById(R.id.verificationCode);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        verify_btn.setOnClickListener(v -> verifyCode(code_edt.getText().toString()));
        remainingText = findViewById(R.id.remainingTime);
        startCounting();
        verifyPhoneNumber();
    }

    private void startCounting() {
        new Handler().postDelayed(() -> {
            if (seconds != 0) {
                String string = "Verify within " + seconds + " seconds";
                remainingText.setText(string);
                seconds--;
                startCounting();
            } else {
                remainingText.setText("timeout!!!");
            }
        }, 1000);
    }

    private void verifyPhoneNumber() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                code_edt.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            finish();//this activity no longer needed
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).
                addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        saveUserDataInDatabase();
                        Intent intent=new Intent(VerificationActivity.this, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                        Toast.makeText(VerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataInDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userDatabase.setValue(new Contact("sakib",phoneNumber));
    }
}
