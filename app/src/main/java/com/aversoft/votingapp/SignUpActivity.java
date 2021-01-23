package com.aversoft.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aversoft.votingapp.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class SignUpActivity extends AppCompatActivity {

    TextView tvMessage, tvLogIn;
    EditText etName, etNId, etPass, etConPass;
    Button btnSignUp;
    ProgressBar pb;
    LinearLayout layout;
    FirebaseDatabase fireBaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        BiometricManager manager = BiometricManager.from(getApplicationContext());
        switch(manager.canAuthenticate()){

            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "Sensor access Okay!", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                tvMessage.setText("No Hardware");
                btnSignUp.setVisibility(View.GONE);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                tvMessage.setText("Unavailable Hardware");
                btnSignUp.setVisibility(View.GONE);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                tvMessage.setText("Nothing happened");
                btnSignUp.setVisibility(View.GONE);
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(getApplicationContext());

        BiometricPrompt prompt = new BiometricPrompt(SignUpActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                layout.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);

                String name = etName.getText().toString();
                String nId = etNId.getText().toString();
                String pass = etPass.getText().toString();
                String conPass = etConPass.getText().toString();

                if(name.isEmpty() || nId.isEmpty() || pass.isEmpty() || conPass.isEmpty()){
                    tvMessage.setText("Please Fill all Fields!!");
                    layout.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    return;
                }

                if(!pass.equals(conPass)){
                    tvMessage.setText("Please provide same password!!");
                    layout.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    return;
                }

                registerUser(name, nId, pass);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Registration")
                .setDescription("Use your fingerprint to sign up!")
                .setNegativeButtonText("Cancel")
                .build();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt.authenticate(promptInfo);
            }
        });
    }

    private void registerUser(String name, String nId, String pass) {
        DatabaseReference pushRef = reference.child("Users").push();
        pushRef.setValue(new User(pushRef.getKey(), name, nId, pass));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void init(){
        tvMessage = findViewById(R.id.tv_sign_message);
        tvLogIn = findViewById(R.id.tv_log_in);
        etName = findViewById(R.id.et_sign_name);
        etNId = findViewById(R.id.et_sign_national_id);
        etPass = findViewById(R.id.et_sign_password);
        etConPass = findViewById(R.id.et_sign_con_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        pb = findViewById(R.id.pb_sign);
        layout = findViewById(R.id.layout_main);
        fireBaseDatabase = FirebaseDatabase.getInstance();
        reference = fireBaseDatabase.getReference("Voting App");
    }
}