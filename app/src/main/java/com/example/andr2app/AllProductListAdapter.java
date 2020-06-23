package com.example.andr2app;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AllProductListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    int mResource;
    List<Product> mObjects;

    private FirebaseStorage mStorage;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    private String user_id;

    public AllProductListAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
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
        String userId = getItem(position).getUserId();
        Product p = new Product(name, price, url, id, userId);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.productAllNameView);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.productAllPriceView);
        ImageView productImageView = (ImageView) convertView.findViewById(R.id.productAllImageView);
        tvName.setText(name);
        tvPrice.setText("Price: " + p.getPriceFormatted());

        Glide.with(mContext).load(url).into(productImageView);


        Button viewProductButton = (Button) convertView.findViewById(R.id.productAllViewBtn);
        viewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to new activity with specified product
                goToIndividualProduct(position);
            }
        });
        return convertView;
    }

    void goToIndividualProduct(int position){
        Intent intent = new Intent(mContext.getApplicationContext(), ProductIndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Product product = mObjects.get(position);
        intent.putExtra("Product", product);

        mContext.startActivity(intent);
    }

}
