package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN_GOOGLE = 123;
    private FirebaseAuth mAuth;
    private Button btnLoginGoogle;
    private Button btnLoginEmailPassword;
    private Button btnFinalizeLoginEmailPassword;
    private EditText emailField;
    private EditText passwordField;
    private TextView textGoToRegister;
    private ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progress_circular);
        emailField = findViewById(R.id.textEmail);
        passwordField = findViewById(R.id.textPwrd);

        textGoToRegister = findViewById(R.id.textRegister);
        textGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLoginGoogle = findViewById(R.id.loginGoogle);
        btnLoginEmailPassword = findViewById(R.id.loginEmailPassword);
        btnFinalizeLoginEmailPassword = findViewById(R.id.finalizeEmailPasswordLogin);

        btnLoginEmailPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInEmailPasswordFields();
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequestAndSignIn();
            }
        });

        btnFinalizeLoginEmailPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signInEmailPassword();
            }
        });


    }

    private void createRequestAndSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInGoogle();
    }

    private void showSignInEmailPasswordFields() {
        btnLoginGoogle.setVisibility(View.INVISIBLE);
        btnLoginEmailPassword.setVisibility(View.INVISIBLE);
        btnFinalizeLoginEmailPassword.setVisibility(View.VISIBLE);
        passwordField.setVisibility(View.VISIBLE);
        emailField.setVisibility(View.VISIBLE);
        textGoToRegister.setVisibility(View.VISIBLE);
    }

    private void signInEmailPassword() {
        String loginEmail = emailField.getText().toString();
        String loginPassword = passwordField.getText().toString();

        // Login user
        if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        sentToMainActivity();
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
            Toast toast = Toast.makeText(getApplicationContext(), "Incorrect password and/or email",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    private void signInGoogle() {
        progressBar.setVisibility(View.VISIBLE);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast toast = Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            sentToMainActivity();

                        } else {
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

    private void sentToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
