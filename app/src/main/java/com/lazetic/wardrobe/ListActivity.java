package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lazetic.wardrobe.Adapter.MyAdapter;
import com.lazetic.wardrobe.Adapter.RecyclerviewOnClickListener;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.Wardrobe;
import com.lazetic.wardrobe.models.WardrobeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListActivity extends AppCompatActivity implements RecyclerviewOnClickListener {

    DBHelper dbHelper;
    ImageButton back;
    FloatingActionButton toTopFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        String name = GlobalFunctions.getUserName(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(name + "'s Wardrobe");

        dbHelper = new DBHelper(this);
        toTopFab = findViewById(R.id.toTopFab);

        List<Wardrobe> wardrobeList = data(dbHelper);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(wardrobeList, this, this));

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(ListActivity.this, HomeActivity.class);
            startActivity(i);
        });

        if (recyclerView.getScrollState() > 0) {
            toTopFab.setVisibility(View.VISIBLE);
        }

        toTopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                toTopFab.setVisibility(View.GONE);
            }
        });
    }

    private List<Wardrobe> data(DBHelper dbHelper) {
        List<Wardrobe> wardrobe = new ArrayList<>();
        Cursor cursor = dbHelper.getAllWardrobe();
        cursor.moveToFirst();
        int id = cursor.getColumnIndex("id");
        int name = cursor.getColumnIndex("name");
        int color = cursor.getColumnIndex("color");
        int category = cursor.getColumnIndex("category");
        int image = cursor.getColumnIndex("image");
        do {
            wardrobe.add(new Wardrobe(cursor.getString(name),
                    cursor.getString(color),
                    WardrobeCategory.getByName(cursor.getString(category).toUpperCase()),
                    GlobalFunctions.getImage(cursor.getBlob(image)),
                    cursor.getInt(id)));
        } while (cursor.moveToNext());
        return wardrobe;
    }

    @Override
    public void recyclerviewClick(Wardrobe wardrobe) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("title", wardrobe.getName());
        intent.putExtra("id", wardrobe.getId());
        intent.putExtra("color", wardrobe.getColor());
        intent.putExtra("category", wardrobe.getCategory().getName());
        intent.putExtra("image", GlobalFunctions.getBytes(wardrobe.getImage()));
        intent.putExtra("search", 0);
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
        switch (item.getItemId())
        {
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
                intent.putExtra("email",email);
                dbHelper.logout();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}