package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazetic.wardrobe.Adapter.FavRecyclerviewOnClickListener;
import com.lazetic.wardrobe.Adapter.FavoritesAdapter;
import com.lazetic.wardrobe.models.Combination;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity implements FavRecyclerviewOnClickListener {

    DBHelper dbHelper;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String name = GlobalFunctions.getUserName(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        String what = intent.getStringExtra("what");
        List<Combination> list = new ArrayList<>();
        if (what.equals("h")){
            list = history(dbHelper);
            Objects.requireNonNull(getSupportActionBar()).setTitle(name + "'s History");
        } else if (what.equals("f")){
            list = fav(dbHelper);
            Objects.requireNonNull(getSupportActionBar()).setTitle(name + "'s Favorites");
        }else {
            Intent i = new Intent(HistoryActivity.this, HomeActivity.class);
            startActivity(i);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FavoritesAdapter(list, this, this));

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(HistoryActivity.this, HomeActivity.class);
            startActivity(i);
        });
    }

    private List<Combination> history(DBHelper dbHelper) {
        List<Combination> combo = new ArrayList<>();
        Cursor cursor = dbHelper.getAllHistory();
        cursor.moveToFirst();
        int top = cursor.getColumnIndex("topImg");
        int bottom = cursor.getColumnIndex("bottomImg");
        int shoe = cursor.getColumnIndex("shoeImg");
        int acc = cursor.getColumnIndex("accessoryImg");
        int date = cursor.getColumnIndex("date");
        do {
            combo.add(new Combination(Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(top)), 120, 140, false),
                    Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(bottom)), 120, 140, false),
                            Bitmap.createScaledBitmap( GlobalFunctions.getImage(cursor.getBlob(shoe)), 120, 140, false),
                                    Bitmap.createScaledBitmap( GlobalFunctions.getImage(cursor.getBlob(acc)), 120, 140, false),
                    cursor.getString(date)));
        } while (cursor.moveToNext());
        return combo;
    }

    private List<Combination> fav(DBHelper dbHelper) {
        List<Combination> combo = new ArrayList<>();
        Cursor cursor = dbHelper.getAllFavorites();
        cursor.moveToFirst();
        int top = cursor.getColumnIndex("topImg");
        int bottom = cursor.getColumnIndex("bottomImg");
        int shoe = cursor.getColumnIndex("shoeImg");
        int acc = cursor.getColumnIndex("accessoryImg");
        int date = cursor.getColumnIndex("date");

        Cursor names = dbHelper.getAllFavoritesNames();
        names.moveToFirst();
        int name = names.getColumnIndex("name");
        int id = names.getColumnIndex("id");
        List<Pair> n = new ArrayList<>();
        if (names.getCount() != 0){
            do {
                n.add(new Pair(names.getString(name),names.getInt(id)));
            } while (names.moveToNext());
            names.close();
        }
        int i=0;
        if (cursor.getCount() != 0) {
            do {
                combo.add(new Combination(n.get(i).getName(), Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(top)), 120, 140, false),
                        Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(bottom)), 120, 140, false),
                        Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(shoe)), 120, 140, false),
                        Bitmap.createScaledBitmap(GlobalFunctions.getImage(cursor.getBlob(acc)), 120, 140, false),
                        cursor.getString(date), n.get(i).getId()));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        } else {
                combo.add(new Combination("No favorites yet",null,null,null,null,""));
        }
        return combo;
    }

    @Override
    public void favRecyclerviewClick(Combination combination) {
        Intent intent = new Intent(this, FavDetailsActivity.class);
        intent.putExtra("top", GlobalFunctions.getBytes(combination.getTop()));
        intent.putExtra("bottom", GlobalFunctions.getBytes(combination.getBottom()));
        intent.putExtra("shoe", GlobalFunctions.getBytes(combination.getShoe()));
        intent.putExtra("acc", GlobalFunctions.getBytes(combination.getAccessory()));
        intent.putExtra("date", combination.getDate());
        intent.putExtra("id", combination.getId());
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