package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button btnRegister;
    private TextView textGoBackToLogin;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress_circular);
        emailField = findViewById(R.id.textEmail);
        passwordField = findViewById(R.id.textPwrd);
        confirmPasswordField = findViewById(R.id.textConfirmPwrd);

        btnRegister = findViewById(R.id.registerBtn);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        textGoBackToLogin = findViewById(R.id.textGoBackToLogin);
        textGoBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void register() {
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();
        final String passwordConfirm = confirmPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (password.equals(passwordConfirm)) {
                progressBar.setVisibility(View.VISIBLE);
                // Register
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            sendToMainActivity();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            String errorMessage = task.getException().getMessage();
                            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + errorMessage,
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }

                    }
                });
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Passwords do not match",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Non-filled in fields or incorrect email",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
