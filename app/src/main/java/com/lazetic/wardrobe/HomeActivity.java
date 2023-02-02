package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    TextView emptyWardrobe, emptyHistory, emptyFavorites, textMyWardrobe, textMyHistory, textMyFavorites;
    Button newOutfitButton,viewPosts;
    DBHelper dbHelper;
    LinearLayout wardrobe, history, favorites;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Intent intent = getIntent();
//        String name = intent.getStringExtra("name");
        String name = GlobalFunctions.getUserName(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome, " + name + "!");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(i);
        });

        wardrobe = findViewById(R.id.wardrobe);
        history = findViewById(R.id.history);
        favorites = findViewById(R.id.favorites);
        emptyWardrobe = findViewById(R.id.emptyWardrobe);
        emptyHistory = findViewById(R.id.emptyHistory);
        emptyFavorites = findViewById(R.id.emptyFavorites);
        textMyWardrobe = findViewById(R.id.textMyWardrobe);
        textMyHistory = findViewById(R.id.textMyHistory);
        textMyFavorites = findViewById(R.id.textMyFavorites);
        viewPosts= findViewById(R.id.viewPosts);

        dbHelper = new DBHelper(this);

        home(dbHelper.getWardrobe(), "View Wardrobe", wardrobe, ListActivity.class, emptyWardrobe);
        home(dbHelper.getHistory(), "View History", history, HistoryActivity.class, emptyHistory);
        home(dbHelper.getFavorites4(), "View Favorites", favorites, HistoryActivity.class, emptyFavorites);


        newOutfitButton = findViewById(R.id.generate);
        newOutfitButton.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, CombinationsActivity.class);
            startActivity(i);
        });

        textMyWardrobe.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, ListActivity.class);
            startActivity(i);
        });

        textMyHistory.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, HistoryActivity.class);
            i.putExtra("what", "h");
            startActivity(i);
        });

        textMyFavorites.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, HistoryActivity.class);
            i.putExtra("what", "f");
            startActivity(i);
        });
        viewPosts.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, PostsActivity.class);
            startActivity(i);
        });
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
                intent = new Intent(HomeActivity.this, HistoryActivity.class);
                intent.putExtra("what", "f");
                startActivity(intent);
                return true;
            case R.id.action_camera:
                intent = new Intent(HomeActivity.this, UploadPhotoActivity.class);
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
    void home(Cursor c, String buttonText, LinearLayout ll, Class<?> cls, TextView tv) {
        if (c.getCount() <= 0) {
            tv.setVisibility(View.VISIBLE);
        } else {
            c.moveToFirst();
            int id = c.getColumnIndex("image");
            do {
                ll.addView(addImage(c.getBlob(id)));
            } while (c.moveToNext());
            Button viewButton = addButton(buttonText, R.drawable.more, 1);
            ll.addView(viewButton);
            viewButton.setOnClickListener(view -> {
                Intent i = new Intent(HomeActivity.this, cls);
                if (buttonText.equals("View History")) {
                    i.putExtra("what", "h");
                } else if (buttonText.equals("View Favorites")) {
                    i.putExtra("what", "f");
                } else {
                    i.putExtra("what", "w");
                }
                startActivity(i);
            });
        }
        c.close();
    }

    ImageView addImage(byte[] src) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(650, 650);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(GlobalFunctions.getImage(src));
        imageView.setPaddingRelative(55, 55, 55, 55);
        return imageView;
    }

    Button addButton(String text, int icon, int id) {
        Button button = new Button(this);
        button.setText(text);
        button.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setId(id);
        button.setBackground(AppCompatResources.getDrawable(this, R.drawable.grad));
        button.setTextColor(getResources().getColor(R.color.white));
        button.setPadding(20, 10, 20, 10);
        return button;
    }

}