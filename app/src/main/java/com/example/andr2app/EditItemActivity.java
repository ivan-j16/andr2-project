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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditItemActivity extends AppCompatActivity {

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
    //private ProgressBar progressBar;

    private String product_id;
    private String user_id;

    private Uri mainImageURI = null;
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userCollection = firebaseFirestore.collection("Users");

        productName = findViewById(R.id.txtProductNameEdit);
        productPrice = findViewById(R.id.txtProductPriceEdit);
        productImage = findViewById(R.id.imageProductEdit);

        btnCancel = findViewById(R.id.btnCancelProductEdit);
        btnPost = findViewById(R.id.btnPostProductEdit);

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Edit product");

        Intent intent = getIntent();

        product_id = intent.getStringExtra("id");
        user_id = mAuth.getCurrentUser().getUid();

        loadProduct();

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

                        ActivityCompat.requestPermissions(EditItemActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                sentToItemActivity();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduct();
            }
        });
    }

    private void loadProduct(){
// Get username and profile image from firestore
        firebaseFirestore.collection("Users/" + user_id + "/Products").document(product_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               // progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("photoUrl");
                        double price = task.getResult().getDouble("price");

                        productName.setText(name);
                        productPrice.setText(String.valueOf(price));

                        Glide.with(EditItemActivity.this).load(image).into(productImage);

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

    private void editProduct(){
        final String name = productName.getText().toString();
        final double price = Double.valueOf(productPrice.getText().toString());

        if (imageChanged) {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(String.valueOf(price)) && mainImageURI != null) {


                final StorageReference image_path = storageReference.child("product_images")
                        .child(product_id + ".jpg");

                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    storeFirestoreData(uri, name, price);
                                }
                            });
                        }
                        else {

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
            storeFirestoreData(null, name, price);
        }
    }

    private void storeFirestoreData(Uri uri, String name, double price) {

        String downloadUri;
        if (uri == null) {
            downloadUri = mainImageURI.toString();
        }
        else{
            downloadUri = uri.toString();
        }

        DocumentReference editedProduct =  firebaseFirestore.collection("Users/" + user_id + "/Products").document(product_id);
        editedProduct.update(
                "name", name,
                "photoUrl", downloadUri,
                "price", price
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Product updated",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    sentToItemActivity();
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

    private void sentToItemActivity() {
        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditItemActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                productImage.setImageURI(mainImageURI);
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
}
