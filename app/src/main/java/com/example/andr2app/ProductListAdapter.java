package com.example.andr2app;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProductListAdapter extends ArrayAdapter<Product> {

    private Context mContext;
    int mResource;
    List<Product> mObjects;

    private FirebaseStorage mStorage;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    private String user_id;

    public ProductListAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position).getName();
        double price = getItem(position).getPrice();
        String url = getItem(position).getPhotoUrl();
        String id = getItem(position).getId();

        Product p = new Product(name, price, url, id);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.productNameView);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.productPriceView);
        ImageView productImageView = (ImageView) convertView.findViewById(R.id.productImageView);
        Button deleteProductButton = (Button) convertView.findViewById(R.id.productDeleteBtn);
        Button editProductButton = (Button) convertView.findViewById(R.id.productEditBtn);

        tvName.setText(name);
        tvPrice.setText("Price: €" + String.valueOf(price));

        Glide.with(mContext).load(url).into(productImageView);

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(position);
            }
        });
        editProductButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Product p = mObjects.get(position);
                Intent intent = new Intent( mContext, EditItemActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", p.getId());
                mContext.startActivity(intent);

            }
        });

        return convertView;
    }

    void deleteProduct(int position){
        Product selectedProduct = mObjects.get(position);
        String selectedId = selectedProduct.getId();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedProduct.getPhotoUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //remove from db
                mDatabase.collection("Users/"+ user_id + "/Products").document(selectedId).delete();

                //notification
                Toast toast = Toast.makeText(mContext, "Product deleted",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                //remove from list
                mObjects.remove(position);
                notifyDataSetChanged();
            }
        });
    }

}
