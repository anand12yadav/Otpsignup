package com.example.otpsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class activity_otp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String mAuthVerification;
    private EditText mOtp;
    private Button verifyOtp;
    private ProgressBar otpprogress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mAuthVerification=getIntent().getStringExtra("AuthCredentials");

        mOtp=findViewById(R.id.otp);
        verifyOtp=findViewById(R.id.verifyOtp);
        otpprogress_bar=findViewById(R.id.otpprogress_bar);

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp=mOtp.getText().toString();
                if(otp.isEmpty()){
                    Toast.makeText(activity_otp.this,"Enter otp first",Toast.LENGTH_SHORT).show();

                }else {
                    otpprogress_bar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential( mAuthVerification,otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity_otp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            sendUserToHome();

                            //FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(activity_otp.this,"Enter valid otp",Toast.LENGTH_SHORT).show();
                            }
                        }
                        otpprogress_bar.setVisibility(View.INVISIBLE);
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser!=null){
            sendUserToHome();
        }
    }

    public void sendUserToHome(){
        Intent homeIntent=new Intent(activity_otp.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}