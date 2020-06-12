package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AllItemsActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    private ListView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);

        productListView = findViewById(R.id.lvAllProducts);

        // Will change if viewing another person's items
        getSupportActionBar().setTitle("All products");

        loadAllProducts();
    }

    public List<Product> loadAllProducts(){
        List<Product> productsToBeShown = new ArrayList<>();
        CollectionReference ref = firebaseFirestore.collection("Users"); //all users
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { //when you get all users
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) { //for each user
                    String fetchedUserId = documentSnapshot.getId();

                    CollectionReference userProductsRef = firebaseFirestore.collection("Users/" + fetchedUserId + "/Products");
                    userProductsRef.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot product : queryDocumentSnapshots){
                                        if (product.exists()){
                                            String name = product.getString("name");
                                            double price = product.getDouble("price");
                                            String url = product.getString("photoUrl");
                                            String id = product.getId();
                                            String userId = product.getString("userId");
                                            Product p = new Product(name, price, url,id, userId);
                                            productsToBeShown.add(p);

                                        }else{
                                            Toast toast = Toast.makeText(getApplicationContext(), "Product error!",
                                                    Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();
                                        }
                                    }

                                    AllProductListAdapter adapter = new AllProductListAdapter(getApplicationContext(), R.layout.product_view_all, productsToBeShown);
                                    productListView.setAdapter(adapter);

                                }
                            });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error!",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });


        return productsToBeShown;
    }
}
