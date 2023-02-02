package com.lazetic.wardrobe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    ImageView imageView;
    ImageButton back;
    LinearLayout nameLayout;
    DBHelper dbHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Article Details");
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("title");
        String color = intent.getStringExtra("color");
        String category = intent.getStringExtra("category");
        byte[] image = intent.getByteArrayExtra("image");
        int search = intent.getIntExtra("search",0);

        imageView = findViewById(R.id.top);
        back = findViewById(R.id.back);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);

        back.setOnClickListener(view -> {
            if (search == 1 ){
                Intent i = new Intent(DetailsActivity.this, SearchActivity.class);
                startActivity(i);
            }else if (search == 0 ) {
                Intent i = new Intent(DetailsActivity.this, ListActivity.class);
                startActivity(i);
            }
        });

        imageView.setImageBitmap(GlobalFunctions.getImage(image));


        nameLayout.addView(addLL("Name:", name, 40));
        nameLayout.addView(addLL("Category:", category, 25));
        nameLayout.addView(addLL("Color:", color, 25));


    }

    LinearLayout addLL(String title, String text, int size) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(size);
        t.setTextColor(Color.BLACK);
        t.setLayoutParams(dim);
        ll.addView(t);

        TextView txt = new TextView(this);
        txt.setText(text);
        txt.setTextSize(size);
        txt.setLayoutParams(dim);
        txt.setTextColor(Color.BLACK);


        if (title.equals("Color:")) {
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.color, 0);
            for (Drawable drawable : txt.getCompoundDrawables()) {
                if (drawable != null) {
                    String[] colorNames = getResources().getStringArray(R.array.colorNames);
                    for (int i = 0; i < colorNames.length; i++) {
                        if (text.equals(colorNames[i])) {
                            TypedArray ta = getResources().obtainTypedArray(R.array.colors);
                            int colorToUse = ta.getResourceId(i, 0);
                            drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(colorToUse), PorterDuff.Mode.SRC_IN));
                            break;
                        }
                    }
                }
            }
        }
        ll.addView(txt);

        return ll;
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