package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.lazetic.wardrobe.Adapter.MyAdapter;
import com.lazetic.wardrobe.Adapter.RecyclerviewOnClickListener;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.Wardrobe;
import com.lazetic.wardrobe.models.WardrobeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements RecyclerviewOnClickListener {
    DBHelper dbHelper;
    SearchView searchView;
    ImageButton back;
    Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DBHelper(this);

        String name = GlobalFunctions.getUserName(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(name + "'s wardrobe search!");
        searchView = findViewById(R.id.idSearchView);
        back = findViewById(R.id.back);
        categorySpinner = findViewById(R.id.categorySpinner);

        List<String> categories = WardrobeCategory.getWardrobeCategories();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                List<Wardrobe> wardrobeList = cat(dbHelper);
                RecyclerView recyclerView = findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MyAdapter(wardrobeList, getBaseContext(), SearchActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        back.setOnClickListener(view -> {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String search = searchView.getQuery().toString();
                List<Wardrobe> wardrobeList = data(search, SearchActivity.this.getCurrentFocus().getRootView(), dbHelper);
                RecyclerView recyclerView = findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MyAdapter(wardrobeList, getBaseContext(), SearchActivity.this));
                if (wardrobeList.size() == 1) {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String search = searchView.getQuery().toString();
                List<Wardrobe> wardrobeList = data(search, SearchActivity.this.getCurrentFocus().getRootView(), dbHelper);
                RecyclerView recyclerView = findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MyAdapter(wardrobeList, getBaseContext(), SearchActivity.this));
                if (wardrobeList.size() == 1) {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                }
                // To hide the keyboard
//                InputMethodManager imm = (InputMethodManager)SearchActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getRootView().getWindowToken(), 0);
                return false;
            }

        });
    }

    private List<Wardrobe> cat(DBHelper dbHelper) {
        List<Wardrobe> wardrobeList = new ArrayList<>();
        Cursor c = dbHelper.getWardrobeByCategory(categorySpinner.getSelectedItem().toString());
        if (c.moveToFirst()) {
            int name = c.getColumnIndex("name");
            int color = c.getColumnIndex("color");
            int category = c.getColumnIndex("category");
            int image = c.getColumnIndex("image");
            int id = c.getColumnIndex("id");
            do {
                wardrobeList.add(new Wardrobe(c.getString(name),
                        c.getString(color),
                        WardrobeCategory.getByName(c.getString(category).toUpperCase()),
                        GlobalFunctions.getImage(c.getBlob(image)),
                        c.getInt(id)));
            } while (c.moveToNext());
        }
        return wardrobeList;
    }

    private List<Wardrobe> data(String search, View view, DBHelper dbHelper) {
        List<Wardrobe> wardrobeList = new ArrayList<>();
        if (search.trim().length() == 0) {
            Cursor cursor = dbHelper.getAllWardrobe();
            cursor.moveToFirst();
            int id = cursor.getColumnIndex("id");
            int name = cursor.getColumnIndex("name");
            int color = cursor.getColumnIndex("color");
            int category = cursor.getColumnIndex("category");
            int image = cursor.getColumnIndex("image");
            do {
                wardrobeList.add(new Wardrobe(cursor.getString(name),
                        cursor.getString(color),
                        WardrobeCategory.getByName(cursor.getString(category).toUpperCase()),
                        GlobalFunctions.getImage(cursor.getBlob(image)),
                        cursor.getInt(id)));
            } while (cursor.moveToNext());
            snack("Showing whole wardrobe!", view, R.color.myBlue, R.drawable.list);
            cursor.close();
        } else {
            wardrobeList = new ArrayList<>();
            Cursor c = dbHelper.wardrobeQuery(search);
            if (c.moveToFirst() && c.getCount() != 0) {
                c.moveToFirst();
                int id = c.getColumnIndex("id");
                int name = c.getColumnIndex("name");
                int color = c.getColumnIndex("color");
                int category = c.getColumnIndex("category");
                int image = c.getColumnIndex("image");
                do {
                    wardrobeList.add(new Wardrobe(c.getString(name),
                            c.getString(color),
                            WardrobeCategory.getByName(c.getString(category).toUpperCase()),
                            GlobalFunctions.getImage(c.getBlob(image)),
                            c.getInt(id)));
                } while (c.moveToNext());
                snack("Showing results for search '" + search + "'", view, R.color.myPurple, R.drawable.list);
                c.close();
            } else {
                snack("No results for search '" + search + "'", view, R.color.Orange, R.drawable.warning);
            }

        }
        return wardrobeList;
    }

    private void snack(String text, View view, int color, int emoji) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), color));
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(emoji, 0, 0, 0);
        textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
        snackbar.setAction("Action", null).show();
    }

    @Override
    public void recyclerviewClick(Wardrobe wardrobe) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("title", wardrobe.getName());
        intent.putExtra("id", wardrobe.getId());
        intent.putExtra("color", wardrobe.getColor());
        intent.putExtra("category", wardrobe.getCategory().getName());
        intent.putExtra("image", GlobalFunctions.getBytes(wardrobe.getImage()));
        intent.putExtra("search", 1);
        startActivity(intent);
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