package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

    private RequestQueue req;

    private String url = "https://fcm.googleapis.com/fcm/send";

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

        FirebaseMessaging.getInstance().subscribeToTopic(p.getId());
        btnNotification = findViewById(R.id.productIndividualNotify);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    notifyInterest();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loggedUserId = mAuth.getCurrentUser().getUid();

        req = Volley.newRequestQueue(this);

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

        void notifyInterest() throws JSONException {

            JSONObject mainObj = new JSONObject();

            mainObj.put("to", "/topics/"+p.getId());

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", "A user is interested in your item!");
            notificationObject.put("body", "User " + loggedUserId + " will contact you about your " + p.getName());

            mainObj.put("notification", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Notification sent!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error with notification",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();

                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAoWuvyQg:APA91bFimKsHznbT5e2oCJJRTI_trT675ELfhRRqiCtxnJ4xEkNosd-I5NKwAoyLzIN9npt2L9jX3FdFkqI32xz192EgQe09pfoTvn3ZPNOEaf3i_Sqr6LElz-wpl93_Re0bSm0ktjil");
                    return header;
                }
            };

            req.add(request);
        }


}


