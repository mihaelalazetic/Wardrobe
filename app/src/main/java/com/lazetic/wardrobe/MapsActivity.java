package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lazetic.wardrobe.databinding.ActivityMapsBinding;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    TextView at;
    ImageButton back;
    Button post;
    Spinner chooseOutfit;
    SearchView searchView;
    Marker marker;
    ImageView imagePost;
    DBHelper dbHelper;
    private byte[] img;
    Address address;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = GlobalFunctions.getUserName(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Objects.requireNonNull(getActionBar()).setTitle(name + ", write a post...");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        dbHelper = new DBHelper(this);

        searchView = findViewById(R.id.idSearchView);
        at = findViewById(R.id.at);
        imagePost = findViewById(R.id.imagePost);

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(MapsActivity.this, HomeActivity.class);
            startActivity(i);
        });

        post = findViewById(R.id.post);

        chooseOutfit = findViewById(R.id.chooseOutfit);
        List<String> favsList = allFavs();
        favsList.add(0, "");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, favsList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseOutfit.setAdapter(dataAdapter);
        chooseOutfit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!chooseOutfit.getSelectedItem().equals("")) {
                    String name = chooseOutfit.getSelectedItem().toString();
                    img = dbHelper.getHistoryImageByFavName(name);
                    Bitmap bitmap = GlobalFunctions.getImage(img);
                    imagePost.setImageBitmap(bitmap);
                    imagePost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                if (!Objects.equals(marker, null)) {
                    marker.remove();
                }
                location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                // on below line we are creating and initializing a geo coder.
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try {
                    // on below line we are getting location from the
                    // location name and adding that location to address list.
                    addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList.size() == 0) {
                        addressList = geocoder.getFromLocationName("Skopje", 1);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // on below line we are getting the location
                // from our list a first position.
                address = addressList.get(0);
                String fff = "at " + location + ", " + address.getAddressLine(0);
                at.setText(fff);
                // on below line we are creating a variable for our location
                // where we will add our locations latitude and longitude.
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // on below line we are adding marker to that position.
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                // below line is to animate camera to that position.
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        post.setOnClickListener(view -> {
            if (at.getText().toString().equals("at")) {
                snack("Please enter location!", view, R.color.Red, R.drawable.warning);
            } else if (chooseOutfit.getSelectedItem().toString().equals("")) {
                snack("Please select an outfit!", view, R.color.Red, R.drawable.warning);
            } else {
               if( dbHelper.insertPost(chooseOutfit.getSelectedItem().toString(),
                       location + " " + address.getAddressLine(0),
                        img, address.getLatitude(), address.getLongitude())) {
                   snack("Posted!", view, R.color.Green, R.drawable.check);
                   Intent i = new Intent(this, MapsActivity.class);
                   startActivity(i);
               }else {
                   snack("Something went wrong! Try again!", view, R.color.Red, R.drawable.warning);
               }
            }
        });
    }

    private void snack(String text, View view, int color, int emoji) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), color));
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(emoji, 0, 0, 0);
        textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
        snackbar.setAction("Action", null).show();
    }

    private List<String> allFavs() {
        List<String> favs = new ArrayList<>();
        Cursor c = dbHelper.getJustFavorites();
        c.moveToFirst();
        int name = c.getColumnIndex("name");
        do {
            if(c.getCount()!=0)
                favs.add(c.getString(name));
        } while (c.moveToNext());
        c.close();
        return favs;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLngBounds bounds = new LatLngBounds(new LatLng(39, 14), new LatLng(47, 31));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        String name = GlobalFunctions.getUserName(this);
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_favorite:
                intent = new Intent(this, HistoryActivity.class);
                intent.putExtra("what", "f");
                startActivity(intent);
                return true;
            case R.id.action_camera:
                intent = new Intent(this, UploadPhotoActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                return true;
            case R.id.action_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(this, MainActivity.class);
                String email = GlobalFunctions.getUserEmail(this);
                intent.putExtra("email", email);
                dbHelper.logout();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}