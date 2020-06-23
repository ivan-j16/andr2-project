package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText profileName;
    private Button btnCancelEdit;
    private Button btnEditProfile;
    private Uri mainImageURI = null;
    private ProgressBar progressBar;
    private String user_id;
    private boolean imageChanged = false;
    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.imageUser);
        btnEditProfile = findViewById(R.id.editBtn);
        btnCancelEdit = findViewById(R.id.cancelBtn);
        profileName = findViewById(R.id.textName);
        progressBar = findViewById(R.id.progress_circular);
        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);

        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Edit profile");

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        getAccountInformation();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If build is later or equal to marshmallow, permission are required
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Permission Denied",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {
                        startImagePicker();
                    }
                }
                else {
                    startImagePicker();
                }
            }
        });

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentToMainActivity();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAccount();
            }
        });
    }


    private void getAccountInformation() {
        // Get username and profile image from firestore
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String phone = task.getResult().getString("phone");

                        profileName.setText(name);
                        editTextCarrierNumber.setText(phone);

                        Glide.with(ProfileActivity.this).load(image).into(profileImage);

                        // Set the image to the loaded one from the database
                        mainImageURI = Uri.parse(image);

                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Data does not exist",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                }
                else {
                    String error = task.getException().getMessage();
                    Toast toast = Toast.makeText(getApplicationContext(), "Firestore retrieve error: " + error,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    private void editAccount() {
        final String username = profileName.getText().toString();
        final String phoneNumber = ccp.getFullNumberWithPlus();

        progressBar.setVisibility(View.VISIBLE);

        if (imageChanged) {
            if (!TextUtils.isEmpty(username) && mainImageURI != null) {


                final StorageReference image_path = storageReference.child("profile_images")
                        .child(user_id + ".jpg");

                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    storeFirestoreData(uri, username, phoneNumber);
                                }
                            });
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);

                            String error = task.getException().getLocalizedMessage();
                            Toast toast = Toast.makeText(getApplicationContext(), "Image error: " + error,
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                });

            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Name and/or profile image missing.",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
        else {
            storeFirestoreData(null, username, phoneNumber);
        }
    }

    private void storeFirestoreData(Uri uri, String username, String phoneNumber) {

        String downloadUri;
        if (uri == null) {
            downloadUri = mainImageURI.toString();
        }
        else{
            downloadUri = uri.toString();
        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("image", downloadUri);
        userMap.put("phone", phoneNumber);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "User settings updated",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();


                    sentToMainActivity();
                }
                else {
                    String error = task.getException().getMessage();
                    Toast toast = Toast.makeText(getApplicationContext(), "Firestore error: " + error,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    private void startImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProfileActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);
                imageChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast toast = Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
    }

    private void sentToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
