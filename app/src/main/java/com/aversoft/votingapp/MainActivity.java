package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aversoft.votingapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView tvMessage, tvSignUp;
    EditText etId, etPass;
    Button btnLogIn;
    FirebaseDatabase firebase;
    DatabaseReference reference;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize every view items
        init();

        //Check FingerPrint sensor position by calling a biometricManager
        BiometricManager manager = BiometricManager.from(getApplicationContext());
        switch(manager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "Sensor Okay!", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                tvMessage.setText("No Hardware");
                btnLogIn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                tvMessage.setText("Unavailable Hardware");
                btnLogIn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                tvMessage.setText("Nothing happened");
                btnLogIn.setVisibility(View.GONE);
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(getApplicationContext());

        //Task after accessing fingerprint
        BiometricPrompt prompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String nId = etId.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                if(nId.isEmpty() || pass.isEmpty()){
                    tvMessage.setText("Please provide your credential!");
                    return;
                }

                //Log in as admin
                if(nId.equals("1234567890") && pass.equals("admin@1234")){
                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                    finish();
                }

                //Get other users
                getMySelf(nId, pass);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        //Show the Dialog
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("LogIn")
                .setDescription("Use your fingerprint to log in")
                .setNegativeButtonText("Cancel")
                .build();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt.authenticate(promptInfo);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

    }

    private void getMySelf(String nId, String pass) {
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                   User user =  data.getValue(User.class);
                   if(user.getnId().equals(nId) && user.getPass().equals(pass)){
                       sp.edit().putString("userId", user.getId()).apply();
                       startActivity(new Intent(getApplicationContext(), VoteActivity.class));
                       finish();
                   }
                }
                tvMessage.setText("Invalid Credential");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        tvMessage = findViewById(R.id.tv_message);
        btnLogIn = findViewById(R.id.btn_log_in);
        tvSignUp = findViewById(R.id.tv_sign_up);
        etId = findViewById(R.id.et_national_id);
        etPass = findViewById(R.id.et_password);
        firebase = FirebaseDatabase.getInstance();
        reference= firebase.getReference("Voting App");
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
    }
}