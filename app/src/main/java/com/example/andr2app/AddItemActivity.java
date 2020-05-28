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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddItemActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference userCollection;

    private Toolbar mainToolbar;

    private EditText productName;
    private EditText productPrice;

    private ImageView productImage;
    private Button btnPost;
    private Button btnCancel;
    private ProgressBar progressBar;

    private String user_id;
    private Uri mainImageURI = null;
    private boolean imageChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userCollection = firebaseFirestore.collection("Users");

        productName = findViewById(R.id.txtProductName);
        productPrice = findViewById(R.id.txtProductPrice);
        productImage = findViewById(R.id.imageProduct);

        btnCancel = findViewById(R.id.btnCancelProduct);
        btnPost = findViewById(R.id.btnPostProduct);

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Add new item");

        user_id = mAuth.getCurrentUser().getUid();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If build is later or equal to marshmallow, permission are required
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Permission Denied",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        ActivityCompat.requestPermissions(AddItemActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentToMainActivity();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToUser();
            }
        });

    }

    private void addProductToUser(){
        String name = productName.getText().toString();
        double price = Double.valueOf(productPrice.getText().toString());

        Product product = new Product(name, price, "none");

        userCollection.document(user_id).collection("Products").add(product);
    }

    private void startImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AddItemActivity.this);
    }


    private void sentToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
