package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
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

import com.lazetic.wardrobe.Adapter.PostsAdapter;
import com.lazetic.wardrobe.Adapter.PostsRecyclerviewOnClickListener;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.Posts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostsActivity extends AppCompatActivity implements PostsRecyclerviewOnClickListener {

    DBHelper dbHelper;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String name = GlobalFunctions.getUserName(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(name+"'s posts");

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        });
        dbHelper = new DBHelper(this);

        List<Posts> postsList = data(dbHelper.getAllPosts());
        RecyclerView recyclerView = findViewById(R.id.postsRecycler);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PostsAdapter(postsList, this, this));
    }

    private List<Posts> data(Cursor c) {
        List<Posts> posts = new ArrayList<>();
        c.moveToFirst();
        int name = c.getColumnIndex("outfit_name");
        int location = c.getColumnIndex("location_name");
        int date = c.getColumnIndex("date");
        int image = c.getColumnIndex("image");
        int lat = c.getColumnIndex("lat");
        int lng = c.getColumnIndex("lat");
        do {
            if(c.getCount() == 0){
                posts.add(new Posts(null,"No posts yet","","",0,0));
            }else {
                posts.add(new Posts(GlobalFunctions.getImage(c.getBlob(image)),
                        c.getString(name),
                        c.getString(location),
                        c.getString(date),
                        c.getDouble(lat),
                        c.getDouble(lng)));
            }

        } while (c.moveToNext());
        c.close();
        return posts;
    }

    @Override
    public void postsRecyclerviewClick(Posts posts) {

        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("outfit_name", posts.getName());
        intent.putExtra("location_name", posts.getLocation());
        intent.putExtra("date", posts.getDate());
        intent.putExtra("image", GlobalFunctions.getBytes(posts.getIcon()));
        intent.putExtra("lat", posts.getLat());
        intent.putExtra("lng", posts.getLng());
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