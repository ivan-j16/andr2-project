package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.andr2app.notifications.APIService;
import com.example.andr2app.notifications.Client;
import com.example.andr2app.notifications.Data;
import com.example.andr2app.notifications.MyResponse;
import com.example.andr2app.notifications.NotificationSender;
import com.example.andr2app.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        updateToken();
        setUserInfo();

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentUserName = mAuth.getCurrentUser().getEmail();

                FirebaseDatabase.getInstance().getReference().child("Users").child(p.getUserId()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userToken = dataSnapshot.getValue(String.class);
                        notifyInterest(userToken, "A user has shown interest!", "User " + currentUserName + " has shown interest in your " + p.getName() + ". Expect them to contact you soon!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProductIndividualActivity.this, "Message not sent but different!", Toast.LENGTH_LONG);
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
                            Toast.makeText(ProductIndividualActivity.this, "Failed!", Toast.LENGTH_LONG);
                        }
                        if(response.body().success == 1){
                            Toast.makeText(ProductIndividualActivity.this, "Message sent!", Toast.LENGTH_LONG);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    Toast.makeText(ProductIndividualActivity.this, "Message not sent!", Toast.LENGTH_LONG);

                }
            });

        }

        void updateToken(){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String newToken = FirebaseInstanceId.getInstance().getToken();
            Token token = new Token(newToken);
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).setValue(token);
        }


}


