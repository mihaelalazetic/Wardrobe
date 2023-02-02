package com.lazetic.wardrobe;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lazetic.wardrobe.databinding.ActivityPostDetailsBinding;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PostDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityPostDetailsBinding binding;
    ImageButton back;
    DBHelper dbHelper;
    double lat,lng;
    String location_name;
    TextView i_wore;
    ImageView imagePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = GlobalFunctions.getUserName(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
//        getActionBar().setTitle(name + "'s Post");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        dbHelper = new DBHelper(this);

//        back = findViewById(R.id.back);
//        back.setOnClickListener(view -> {
//            Intent i = new Intent(this, PostsActivity.class);
//            startActivity(i);
//        });

        Intent intent = getIntent();
        String outfit_name = intent.getStringExtra("outfit_name");
        location_name = intent.getStringExtra("location_name");
        String date = intent.getStringExtra("date");
        Bitmap bm = GlobalFunctions.getImage(intent.getByteArrayExtra("image"));
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);

//        i_wore = findViewById(R.id.i_wore);
//        imagePost = findViewById(R.id.imagePost);
//        String text = "I wore " + outfit_name + ", at " + location_name+", on the date "+ date +".";
//        i_wore.setText(text);
//        imagePost.setImageBitmap(bm);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(PostDetailsActivity.this);
        try {
            addressList = geocoder.getFromLocationName(location_name, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        LatLngBounds bounds = new LatLngBounds(new LatLng(39, 14), new LatLng(47, 31));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }
}