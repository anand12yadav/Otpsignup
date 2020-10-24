package com.example.otpsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class activity_login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText countryCode;
    private EditText phoneNumber;
    private Button generateOtp;
    private ProgressBar loginprogress_bar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        countryCode = findViewById(R.id.countryCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        generateOtp = findViewById(R.id.generateOtp);
        loginprogress_bar = findViewById(R.id.loginprogress_bar);

        generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_Code = countryCode.getText().toString();
                String phone_Number = phoneNumber.getText().toString();
                String complete_phone_number = "+" + country_Code + phone_Number;
                if (country_Code.isEmpty() || phone_Number.isEmpty()) {
                    Toast.makeText(activity_login.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                } else {
                    loginprogress_bar.setVisibility(View.VISIBLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            activity_login.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(activity_login.this, "Please try again", Toast.LENGTH_SHORT).show();
                loginprogress_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent otpIntent = new Intent(activity_login.this, activity_otp.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                                finish();
                            }
                        },
                        10000
                );

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendUserToHome();
        }
        }

        private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity_login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                sendUserToHome();

                                //FirebaseUser user = task.getResult().getUser();
                                // ...
                            } else {

                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    Toast.makeText(activity_login.this, "Enter valid otp", Toast.LENGTH_SHORT).show();
                                }
                            }
                            loginprogress_bar.setVisibility(View.INVISIBLE);
                        }
                    });
        }

        public void sendUserToHome() {
            Intent homeIntent = new Intent(activity_login.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
    }
