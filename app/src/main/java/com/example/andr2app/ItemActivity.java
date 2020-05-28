package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1beta1.ListCollectionIdsRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference productCollection;

    private String user_id;

    private ListView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();
        productCollection = firebaseFirestore.collection("Users/" + user_id + "/Products");

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);

        productListView = findViewById(R.id.lvProducts);

        // Will change if viewing another person's items
        getSupportActionBar().setTitle("Your items");
        loadProducts();

    }

    private List<Product> loadProducts(){

        List<Product> productsToBeShown = new ArrayList<>();
       productCollection.get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot product : queryDocumentSnapshots){
                            if (product.exists()){
                                String name = product.getString("name");
                                double price = product.getDouble("price");
                                String url = product.getString("photoUrl");
                                Product p = new Product(name, price, url);
                                productsToBeShown.add(p);

                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Product error!",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }

                        ProductListAdapter adapter = new ProductListAdapter(getApplicationContext(), R.layout.product_view, productsToBeShown);
                        productListView.setAdapter(adapter);

                   }
               })
               .addOnFailureListener(new OnFailureListener() {
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
