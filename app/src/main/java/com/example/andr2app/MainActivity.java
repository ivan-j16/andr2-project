package com.example.andr2app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private Toolbar mainToolbar;
    private FloatingActionButton addItemBtn;
    public double m_latitude;
    public double m_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private SensorManager sensorManager;
    private Sensor sensor;
    private int steps;
    private TextView stepCounterTextView;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            // If no user is logged in, send to login
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.bringToFront();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Better OLX");

        addItemBtn = findViewById(R.id.add_item_btn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewItemIntent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(addNewItemIntent);
            }
        });
        GetLocationPermission();
        // step counter:
        stepCounterTextView = findViewById(R.id.stepCounter_tv);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // putting peoples profiles on the map
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getUsersPhotos();
        SetFirestoreListener();
        updateToken();
    }

    void updateToken(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newToken = FirebaseInstanceId.getInstance().getToken();
        //Token token = new Token(newToken);

        //FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("token").setValue(token);
        DocumentReference ref = firebaseFirestore.document("Users/" + user.getUid());
        ref.update("token", newToken);
    }

    private void SetFirestoreListener()
    {
        CollectionReference ref = firebaseFirestore.collection("Users");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("FAIL!!!!", "Listen failed.", e);
                    return;
                }

                if (snapshot != null) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "Users moving",
//                            Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    toast.show();
                    getUsersPhotos();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Fail updating map",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    Dictionary<String,Marker> userMarkers = new Hashtable<String, Marker>();
    public void ClearCurrentPhotos(String imageURL)
    {
       if(userMarkers.get(imageURL) != null)
       {
        userMarkers.get(imageURL).remove();
        userMarkers.remove(imageURL);
       }
//int counter = 0;
//            String bitmapToRemove=null;
//            Marker markerToRemove=null;
//                for (String url:
//                        imagesURL) {
//
//                    if(url == imageURL)
//                    {
//                        bitmapToRemove = url;
//                        markerToRemove = markersOnMap.get(counter);
//                        markerToRemove.remove();
//                    }
//                    counter++;
//                }
//                markersOnMap.remove(markerToRemove);
//                imagesURL.remove(bitmapToRemove);
    }
    private void getUsersPhotos() {

        CollectionReference ref = firebaseFirestore.collection("Users");

        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String image = documentSnapshot.get("image").toString();
                    String name = documentSnapshot.get("name").toString();
                    String latitude = documentSnapshot.get("latitude").toString();
                    String longitude = documentSnapshot.get("longitude").toString();
                    SetUserLocationMarkers setLocations = new SetUserLocationMarkers();
                    setLocations.execute(image, name, latitude, longitude);

                }

            }
        });
    }


    class SetUserLocationMarkers extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        String userName;
        Double userLatitude;
        Double userLongitude;
        Bitmap bitmapImage;

        @Override
        protected Void doInBackground(String... params) {
            userName = params[1];
            userLatitude = Double.parseDouble(params[2]);
            userLongitude = Double.parseDouble(params[3]);
            Uri mainImageURI = Uri.parse(params[0]);
            URL imageURL = null;
            try {
                imageURL = new URL(mainImageURI.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                bitmapImage = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 100, 100, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(userLatitude, userLongitude));
            markerOptions.title(userName);
            markerOptions.snippet(""); // products count
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmapImage));
            Marker marker = mMap.addMarker(markerOptions);
            ClearCurrentPhotos(userName);
            userMarkers.put(userName,marker);
        }
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[0] == 1.0f) {
                steps++;
            }
            stepCounterTextView.setText("Steps: " + Integer.toString(steps));

        }
    };

    @Override
    public void onResume() {
        sensorManager.registerListener(mSensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        GetLocationPermission();
    }

    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d("msg: ", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //other markers with photos:
        final LatLng MELBOURNE = new LatLng(latLng.latitude, +latLng.longitude);
    }

    public void statusLocationCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }
    public void OnLocationChangeInit()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d("TAPAK NE RABOTI", "statusLocationCheck: NOT WORKING");
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                SaveLocationOfUser(String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
        });
    }
    public void SaveLocationOfUser(String lng, String lat) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("latitude", lat);
        userMap.put("longitude", lng);

        firebaseFirestore.collection("Users").document(user.getUid()).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "User settings updated",
//                            Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    toast.show();
                } else {
                    String error = task.getException().getMessage();
                    Toast toast = Toast.makeText(getApplicationContext(), "Firestore error: " + error,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your location seems to be disabled, would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void GetDeviceLocationAndZoom()
    {
        Log.d("msg: ", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d("message:", "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                                if(currentLocation != null)
                                {
                                    m_latitude = currentLocation.getLatitude();
                                    m_longitude = currentLocation.getLongitude();
                                    SaveLocationOfUser(String.valueOf(currentLocation.getLongitude()), String.valueOf(currentLocation.getLatitude()));
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    18);
                                    OnLocationChangeInit();
                                }
                                else
                                {
                                    statusLocationCheck();
                                }
                        }else{
                            Log.d("msg:", "onComplete: current location is null");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("eception: ", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    public void GetLocationPermission(){
        Log.d("message", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            GetDeviceLocationAndZoom();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                signOut();
                return true;
            case R.id.nav_profile:
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            case R.id.nav_items:
                Intent itemsIntent = new Intent(getApplicationContext(), ItemActivity.class);
                startActivity(itemsIntent);
                return true;
            case R.id.nav_all_items:
                Intent all_items_intent = new Intent(getApplicationContext(), AllItemsActivity.class);
                startActivity(all_items_intent);
                return true;
            default:
                Toast.makeText(getApplicationContext(), "Navigation error",
                        Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    private void signOut() {
        mAuth.signOut();
        GoogleSignIn.getClient(
                getApplicationContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
