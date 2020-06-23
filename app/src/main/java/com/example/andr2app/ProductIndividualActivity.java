package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.andr2app.notifications.APIService;
import com.example.andr2app.notifications.Client;
import com.example.andr2app.notifications.Data;
import com.example.andr2app.notifications.MyResponse;
import com.example.andr2app.notifications.NotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductIndividualActivity extends AppCompatActivity {

    private Product p;
    private String loggedUserId = "";

    private ImageView ivProduct;

    private TextView productName;
    private TextView productPrice;

    private TextView productUserName;
    private TextView productUserPhone;
    private Button btnNotification;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;


    //notification
    private APIService apiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_individual);

        Intent intent = getIntent();
        p = (Product) intent.getSerializableExtra("Product");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        ivProduct = findViewById(R.id.productIndividualView);
        productName = findViewById(R.id.productIndividualNameView);
        productPrice = findViewById(R.id.productIndividualPriceView);
        productUserName = findViewById(R.id.productIndividualUserName);
        productUserPhone = findViewById(R.id.productIndividualUserPhone);

        Glide.with(this).load(p.getPhotoUrl()).into(ivProduct);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        productName.setText(p.getName());
        productPrice.setText("Price: " + p.getPriceFormatted());
        loggedUserId = mAuth.getCurrentUser().getUid();
        btnNotification = findViewById(R.id.productIndividualNotify);

        setUserInfo();

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentUserMail = mAuth.getCurrentUser().getEmail();

                firebaseFirestore.document("Users/" + p.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                String token = task.getResult().getString("token");

                                notifyInterest(token, "A user has shown interest!", "User " + currentUserMail + " has shown interest in your " + p.getName() + ". Expect them to contact you soon!");

                            Toast toast = Toast.makeText(ProductIndividualActivity.this, "Message sent!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
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
        });
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

        void notifyInterest(String userToken, String title, String message){
            Data data = new Data(title, message);
            NotificationSender sender = new NotificationSender(data, userToken);
            apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if (response.code() == 200){
                        if (response.body().success != 1){
                            Toast toast = Toast.makeText(ProductIndividualActivity.this, "Failed!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    Toast toast = Toast.makeText(ProductIndividualActivity.this, "Message not sent!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            });
        }
}


