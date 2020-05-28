package com.example.andr2app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProductListAdapter extends ArrayAdapter<Product> {

    private Context mContext;
    int mResource;

    public ProductListAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position).getName();
        double price = getItem(position).getPrice();
        String url = getItem(position).getPhotoUrl();

        Product p = new Product(name, price, url);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.productNameView);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.productPriceView);

        tvName.setText(name);
        tvPrice.setText("Price: €" + String.valueOf(price));

        return convertView;
    }
}
