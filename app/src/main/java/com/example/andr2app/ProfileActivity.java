package com.example.andr2app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    private TextView infoUser;
    private ImageView image;
    private Button btn_logout;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null && googleSignInAccount == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        infoUser = findViewById(R.id.infoUser);
        image = findViewById(R.id.imageUser);
        btn_logout = findViewById(R.id.logout);

        if(googleSignInAccount != null){
            // If the signed in user is from a Gmail account
            infoUser.setText(googleSignInAccount.getDisplayName());

//            String photo = String.valueOf(signInAccount.getPhotoUrl());
//            Picasso.with(getApplicationContext()).load(photo).into(image);
//            Picasso.
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        GoogleSignIn.getClient(
                getApplicationContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
