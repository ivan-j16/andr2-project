package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductIndividualActivity extends AppCompatActivity {

    private Product p;
    private String userName = "";
    private String userPhone = "";

    private ImageView ivProduct;

    private TextView productName;
    private TextView productPrice;

    private TextView productUserName;
    private TextView productUserPhone;
    private Button btnNotification;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_individual);

        Intent intent = getIntent();
        p = (Product) intent.getSerializableExtra("Product");

        ivProduct = findViewById(R.id.productIndividualView);
        productName = findViewById(R.id.productIndividualNameView);
        productPrice = findViewById(R.id.productIndividualPriceView);
        productUserName = findViewById(R.id.productIndividualUserName);
        productUserPhone = findViewById(R.id.productIndividualUserPhone);

        Glide.with(this).load(p.getPhotoUrl()).into(ivProduct);

        productName.setText(p.getName());
        productPrice.setText("Price: " + p.getPriceFormatted());

        btnNotification = findViewById(R.id.productIndividualNotify);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               notifyInterest();
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();

        setUserInfo();
    }


    void setUserInfo(){

        DocumentReference docRef = firebaseFirestore.collection("Users").document(p.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        productUserName.setText( "Posted by: " + document.getString("name"));
                        productUserPhone.setText( "Phone: " + document.getString("phone"));
                    }
                }
            }
        });
        }

        void notifyInterest(){
        
        }
}
